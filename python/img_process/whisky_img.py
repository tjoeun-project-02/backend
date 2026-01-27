import os

# ✅ 모델 소스 체크(허깅페이스 연결 확인) 우회
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"

import cv2
import numpy as np
import logging
import re
from paddleocr import PaddleOCR

# ✅ ultralytics(YOLO) = torch 의존이라 윈도우에서 종종 WinError 127로 터짐
#    -> YOLO가 안 되면 전체 이미지 OCR로라도 돌아가게 "옵션 처리"
try:
    from ultralytics import YOLO
    YOLO_AVAILABLE = True
except Exception as e:
    YOLO_AVAILABLE = False
    YOLO_IMPORT_ERROR = str(e)

# -----------------------------------------------------------
# [설정] 로그 제어
# -----------------------------------------------------------
logging.getLogger("ppocr").setLevel(logging.ERROR)


def clean_text(text):
    """중복 체크 및 검색 엔진 최적화를 위한 텍스트 정규화"""
    return re.sub(r"[^a-zA-Z0-9]", "", str(text)).lower()


def run_whisky_ocr(image_path):
    """
    이미지에서 위스키 라벨 텍스트를 추출하여 데이터 리스트로 반환합니다.
    - YOLO 사용 가능하면 bottle ROI를 먼저 뽑아 OCR
    - YOLO 사용 불가하면 전체 이미지로 OCR
    """

    # ---------------------------
    # 0) 입력 경로 체크
    # ---------------------------
    if not image_path or not isinstance(image_path, str):
        print("❌ image_path가 비어있습니다. 예: ./test_img.webp")
        return []

    if not os.path.exists(image_path):
        print(f"❌ 파일이 존재하지 않습니다: {image_path}")
        return []

    # ---------------------------
    # 1) PaddleOCR 초기화
    # ---------------------------
    # 너가 쓰던 형태 유지
    # (PaddleOCR 버전에 따라 show_log 옵션이 없어서 ValueError 난 적 있음)
    ocr = PaddleOCR(lang="en", use_angle_cls=True)

    # ---------------------------
    # 2) 이미지 로드
    # ---------------------------
    original_cv = cv2.imread(image_path)
    if original_cv is None:
        print(f"❌ 이미지를 로드할 수 없습니다: {image_path}")
        return []

    h_img, w_img = original_cv.shape[:2]

    # ---------------------------
    # 3) YOLO로 Bottle ROI 찾기 (가능할 때만)
    # ---------------------------
    bottles = []

    if YOLO_AVAILABLE:
        yolo_path = "./yolov8n.pt"
        yolo_model = YOLO(yolo_path if os.path.exists(yolo_path) else "yolov8n.pt")

        yolo_results = yolo_model(original_cv, verbose=False)

        for r in yolo_results:
            for box in r.boxes:
                if int(box.cls) == 39:  # Bottle
                    x1, y1, x2, y2 = box.xyxy[0].cpu().numpy().astype(int)

                    # 영역 10% 확장 (글자 잘림 방지)
                    pw, ph = int((x2 - x1) * 0.1), int((y2 - y1) * 0.1)
                    x1, y1 = max(0, x1 - pw), max(0, y1 - ph)
                    x2, y2 = min(w_img, x2 + pw), min(h_img, y2 + ph)

                    bottles.append([x1, y1, x2, y2])
    else:
        print("⚠️ YOLO 미사용: ultralytics/torch 로딩 실패로 전체 이미지 OCR로 진행합니다.")
        print(f"⚠️ YOLO import 에러: {YOLO_IMPORT_ERROR}")

    # bottle 못 찾으면 전체 이미지로 fallback
    if not bottles:
        bottles = [[0, 0, w_img, h_img]]

    raw_results = []

    # ---------------------------
    # 4) 각 병 영역(ROI)별 OCR 수행
    # ---------------------------
    for idx, (bx1, by1, bx2, by2) in enumerate(bottles):
        roi = original_cv[by1:by2, bx1:bx2]

        # 전처리: 원본 + 반전(대비 케이스 대응)
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
        inv = cv2.cvtColor(cv2.bitwise_not(gray), cv2.COLOR_GRAY2BGR)

        for label, img_check in [("Original", roi), ("Inverted", inv)]:
            result = ocr.ocr(img_check)

            if result is None or len(result) == 0:
                continue

            ocr_data = result[0]

            # ✅ 딕셔너리 구조 대응 (PaddleX 계열 환경에서 나올 수 있음)
            if isinstance(ocr_data, dict) and "rec_texts" in ocr_data:
                texts = ocr_data.get("rec_texts", [])
                scores = ocr_data.get("rec_scores", [])
                boxes = ocr_data.get("dt_polys", [])

                for i in range(len(texts)):
                    if i < len(scores) and scores[i] > 0.5 and i < len(boxes):
                        y_coords = [p[1] for p in boxes[i]]
                        height = max(y_coords) - min(y_coords)

                        raw_results.append(
                            {
                                "text": texts[i],
                                "conf": float(scores[i]),
                                "size": float(height),
                                "norm_key": clean_text(texts[i]),
                            }
                        )

            # ✅ 리스트 구조 대응 (일반 PaddleOCR)
            elif isinstance(ocr_data, list):
                for line in ocr_data:
                    # line: [coords, (text, conf)]
                    coords, (text, conf) = line[0], line[1]
                    if conf > 0.5:
                        y_coords = [p[1] for p in coords]
                        height = max(y_coords) - min(y_coords)

                        raw_results.append(
                            {
                                "text": text,
                                "conf": float(conf),
                                "size": float(height),
                                "norm_key": clean_text(text),
                            }
                        )

    # ---------------------------
    # 5) 데이터 정제 (중복 제거 및 정렬)
    #   - 신뢰도 높은 순 정렬 -> 중복 제거 -> 글자 크기(중요도) 순 최종 정렬
    # ---------------------------
    raw_results.sort(key=lambda x: x["conf"], reverse=True)

    unique_dict = {}
    for item in raw_results:
        key = item["norm_key"]
        if len(key) < 2:
            continue
        if key not in unique_dict:
            unique_dict[key] = item

    # 최종 결과: 글자 크기가 큰 것이 보통 브랜드명/숙성년수이므로 크기순 정렬
    final_results = sorted(unique_dict.values(), key=lambda x: x["size"], reverse=True)

    return final_results


if __name__ == "__main__":
    # ✅ 여기 비우면 또 에러나니까 기본값 넣어둠
    target_img = "./test_img.webp"

    results = run_whisky_ocr(target_img)

    # 백엔드 로그 출력용
    print(f"\n>>> 추출된 위스키 키워드 ({len(results)}건):")
    for r in results:
        print(f"[{r['text']}] - 크기: {int(r['size'])}, 신뢰도: {r['conf']:.2f}")

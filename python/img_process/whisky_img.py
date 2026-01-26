import cv2
import numpy as np
import os
import logging
import re
from paddleocr import PaddleOCR
from ultralytics import YOLO

# -----------------------------------------------------------
# [설정] 환경 변수 및 로그 제어
# -----------------------------------------------------------
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"
logging.getLogger("ppocr").setLevel(logging.ERROR)

def clean_text(text):
    """중복 체크 및 검색 엔진 최적화를 위한 텍스트 정규화"""
    return re.sub(r'[^a-zA-Z0-9]', '', str(text)).lower()

def run_whisky_ocr(image_path):
    """
    이미지에서 위스키 라벨 텍스트를 추출하여 데이터 리스트로 반환합니다.
    """
    # 1. 모델 로드
    yolo_path = "/yolov8n.pt"
    yolo_model = YOLO(yolo_path if os.path.exists(yolo_path) else 'yolov8n.pt')
    
    # PaddleOCR 초기화 (백엔드용이므로 시각화 옵션 등은 기본값 유지)
    ocr = PaddleOCR(use_textline_orientation=True, lang='en', ocr_version='PP-OCRv4')

    original_cv = cv2.imread(image_path)
    if original_cv is None: 
        print(f"❌ 이미지를 로드할 수 없습니다: {image_path}")
        return []

    # 2. YOLOv8로 Bottle 탐색
    yolo_results = yolo_model(original_cv, verbose=False)
    bottles = []
    h_img, w_img = original_cv.shape[:2]

    for r in yolo_results:
        for box in r.boxes:
            if int(box.cls) == 39: # Bottle
                x1, y1, x2, y2 = box.xyxy[0].cpu().numpy().astype(int)
                # 영역 10% 확장 (글자 잘림 방지)
                pw, ph = int((x2-x1)*0.1), int((y2-y1)*0.1)
                x1, y1 = max(0, x1-pw), max(0, y1-ph)
                x2, y2 = min(w_img, x2+pw), min(h_img, y2+ph)
                bottles.append([x1, y1, x2, y2])

    if not bottles:
        bottles = [[0, 0, w_img, h_img]]

    raw_results = []

    # 3. 각 병 영역(ROI)별로 OCR 수행
    for idx, (bx1, by1, bx2, by2) in enumerate(bottles):
        roi = original_cv[by1:by2, bx1:bx2]
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
        inv = cv2.cvtColor(cv2.bitwise_not(gray), cv2.COLOR_GRAY2BGR)
        
        for label, img_check in [("Original", roi), ("Inverted", inv)]:
            result = ocr.ocr(img_check)
            if result is None or len(result) == 0: continue
            
            ocr_data = result[0]
            
            # 딕셔너리 구조 대응 (사용자 환경 PaddleX 등)
            if isinstance(ocr_data, dict) and 'rec_texts' in ocr_data:
                texts, scores, boxes = ocr_data['rec_texts'], ocr_data['rec_scores'], ocr_data['dt_polys']
                for i in range(len(texts)):
                    if scores[i] > 0.5:
                        y_coords = [p[1] for p in boxes[i]]
                        height = max(y_coords) - min(y_coords)
                        raw_results.append({
                            'text': texts[i], 'conf': scores[i], 'size': height, 
                            'norm_key': clean_text(texts[i])
                        })
            
            # 리스트 구조 대응 (일반 PaddleOCR)
            elif isinstance(ocr_data, list):
                for line in ocr_data:
                    coords, (text, conf) = line[0], line[1]
                    if conf > 0.5:
                        y_coords = [p[1] for p in coords]
                        height = max(y_coords) - min(y_coords)
                        raw_results.append({
                            'text': text, 'conf': conf, 'size': height, 
                            'norm_key': clean_text(text)
                        })

    # 4. 데이터 정제 (중복 제거 및 정렬)
    # 신뢰도 높은 순 정렬 -> 중복 제거 -> 글자 크기(중요도) 순 최종 정렬
    raw_results.sort(key=lambda x: x['conf'], reverse=True)
    unique_dict = {}
    for item in raw_results:
        key = item['norm_key']
        if len(key) < 2: continue
        if key not in unique_dict:
            unique_dict[key] = item
    
    # 최종 결과: 글자 크기가 큰 것이 보통 브랜드명/숙성년수이므로 크기순 정렬
    final_results = sorted(unique_dict.values(), key=lambda x: x['size'], reverse=True)

    return final_results

if __name__ == "__main__":
    target_img = ""
    results = run_whisky_ocr(target_img)
    
    # 백엔드 로그 출력용
    print(f"\n>>> 추출된 위스키 키워드 ({len(results)}건):")
    for r in results:
        print(f"[{r['text']}] - 크기: {int(r['size'])}, 신뢰도: {r['conf']:.2f}")
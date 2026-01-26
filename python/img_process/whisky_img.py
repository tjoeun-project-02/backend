import cv2
import numpy as np
import os
import platform
import logging
import re
from paddleocr import PaddleOCR
from ultralytics import YOLO
from PIL import Image, ImageDraw, ImageFont

# -----------------------------------------------------------
# [설정] 환경 변수 및 로그 제어
# -----------------------------------------------------------
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"
logging.getLogger("ppocr").setLevel(logging.ERROR)

def get_optimal_font(size=20):
    system_os = platform.system()
    if system_os == "Darwin": font_path = "/System/Library/Fonts/Supplemental/Arial.ttf"
    elif system_os == "Windows": font_path = "C:/Windows/Fonts/arial.ttf"
    else: font_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
    try:
        return ImageFont.truetype(font_path, size) if os.path.exists(font_path) else ImageFont.load_default()
    except:
        return ImageFont.load_default()

def clean_text(text):
    """중복 체크를 위한 정규화"""
    return re.sub(r'[^a-zA-Z0-9]', '', str(text)).lower()

def run_whisky_ocr(image_path):
    print(">>> 모델 로딩 중...")
    yolo_path = "/Users/ljw/Desktop/whisky_assistant/yolov8n.pt"
    yolo_model = YOLO(yolo_path if os.path.exists(yolo_path) else 'yolov8n.pt')
    
    # PaddleOCR 초기화 (기존 성공 코드 설정 반영)
    ocr = PaddleOCR(use_textline_orientation=True, lang='en', ocr_version='PP-OCRv4')

    original_cv = cv2.imread(image_path)
    if original_cv is None: 
        print(f"❌ 이미지를 로드할 수 없습니다: {image_path}")
        return

    # 1. YOLOv8로 Bottle 탐색
    print(">>> YOLOv8: 병(Bottle) 영역 탐색 시작...")
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
        print("⚠️ 병이 감지되지 않았습니다. 전체 영역을 분석합니다.")
        bottles = [[0, 0, w_img, h_img]]

    raw_results = []

    # 2. 각 병 영역(ROI)별로 OCR 수행 (기존 성공 로직 적용)
    for idx, (bx1, by1, bx2, by2) in enumerate(bottles):
        roi = original_cv[by1:by2, bx1:bx2]
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
        inv = cv2.cvtColor(cv2.bitwise_not(gray), cv2.COLOR_GRAY2BGR)
        
        print(f">>> ROI {idx+1} 분석 중...")
        for label, img_check in [("Original", roi), ("Inverted", inv)]:
            # 에러 발생했던 cls=True 제거
            result = ocr.ocr(img_check)
            
            if result is None or len(result) == 0:
                continue
            
            # [기존 성공 코드의 핵심] 딕셔너리 구조 파싱
            ocr_data = result[0]
            
            # 데이터 형식이 딕셔너리인 경우 (사용자 환경)
            if isinstance(ocr_data, dict) and 'rec_texts' in ocr_data:
                texts = ocr_data['rec_texts']
                scores = ocr_data['rec_scores']
                boxes = ocr_data['dt_polys']
                
                for i in range(len(texts)):
                    text = texts[i]
                    conf = scores[i]
                    coords = boxes[i]
                    
                    if conf > 0.5:
                        y_coords = [p[1] for p in coords]
                        height = max(y_coords) - min(y_coords)
                        # 원본 이미지 좌표로 보정
                        abs_coords = [[p[0] + bx1, p[1] + by1] for p in coords]
                        
                        raw_results.append({
                            'text': text, 'conf': conf, 'coords': abs_coords,
                            'source': label, 'size': height, 'norm_key': clean_text(text)
                        })
            
            # 데이터 형식이 리스트인 경우 (일반적인 PaddleOCR 환경)
            elif isinstance(ocr_data, list):
                for line in ocr_data:
                    coords = line[0]
                    text, conf = line[1]
                    if conf > 0.5:
                        y_coords = [p[1] for p in coords]
                        height = max(y_coords) - min(y_coords)
                        abs_coords = [[p[0] + bx1, p[1] + by1] for p in coords]
                        raw_results.append({
                            'text': text, 'conf': conf, 'coords': abs_coords,
                            'source': label, 'size': height, 'norm_key': clean_text(text)
                        })

    # 3. 중복 제거 및 정렬
    raw_results.sort(key=lambda x: x['conf'], reverse=True)
    unique_dict = {}
    for item in raw_results:
        key = item['norm_key']
        if len(key) < 2: continue
        if key not in unique_dict:
            unique_dict[key] = item
    
    final_results = sorted(unique_dict.values(), key=lambda x: x['size'], reverse=True)

    # 4. 결과 출력
    print("\n" + "="*80)
    print(f" [라벨 분석 결과 (글자 크기순)]")
    print("-" * 80)
    if not final_results:
        print(" ❌ 인식된 텍스트가 없습니다.")
    else:
        print(f" {'Text':<25} | {'Size':<6} | {'Conf':<6} | {'Source'}")
        print("-" * 80)
        for item in final_results:
            print(f" {item['text']:<25} | {int(item['size']):<6} | {item['conf']:.2f}   | {item['source']}")
    print("="*80)

    # 시각화
    img_pil = Image.fromarray(cv2.cvtColor(original_cv, cv2.COLOR_BGR2RGB))
    draw = ImageDraw.Draw(img_pil)
    font = get_optimal_font(24)

    for item in final_results:
        poly = [tuple(p) for p in item['coords']]
        draw.polygon(poly, outline="red", width=3)
        draw.text((poly[0][0], poly[0][1]-30), item['text'], font=font, fill=(0, 255, 0))

    img_pil.show()

if __name__ == "__main__":
    target_img = "/Users/ljw/Desktop/whisky_assistant/cb996de6be6a6656843139bf6d1ecd9b.jpg.webp"
    run_whisky_ocr(target_img)
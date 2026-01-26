import cv2
import numpy as np
import os
import platform
import logging
from paddleocr import PaddleOCR
from PIL import Image, ImageDraw, ImageFont

# -----------------------------------------------------------
# [설정] 로그 레벨 조정
# -----------------------------------------------------------
logging.getLogger("ppocr").setLevel(logging.WARNING)

def get_optimal_font(size=20):
    """OS별 폰트 경로 자동 설정"""
    system_os = platform.system()
    font_path = ""
    
    if system_os == "Darwin": # Mac
        font_path = "/System/Library/Fonts/Supplemental/Arial.ttf"
        if not os.path.exists(font_path): font_path = "/Library/Fonts/Arial.ttf"
    elif system_os == "Windows": 
        font_path = "C:/Windows/Fonts/arial.ttf"
    else: 
        font_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

    try:
        return ImageFont.truetype(font_path, size) if os.path.exists(font_path) else ImageFont.load_default()
    except:
        return ImageFont.load_default()

def draw_bbox_size_sort(image_path):
    print(">>> PaddleOCR 모델 로딩 중 (Mobile 버전)...")
    try:
        # PP-OCRv4 Mobile 모델 사용
        ocr = PaddleOCR(use_textline_orientation=True, lang='en', ocr_version='PP-OCRv4')
    except Exception as e:
        print(f"❌ 모델 로딩 실패: {e}")
        return

    if not os.path.exists(image_path):
        print(f"오류: 파일 없음 -> {image_path}")
        return

    # 1. 이미지 로드
    original_cv = cv2.imread(image_path)
    if original_cv is None:
        print("이미지 로드 실패")
        return

    # 2. 검사할 이미지 리스트 준비 (원본 + 반전)
    img_original = original_cv.copy()
    gray = cv2.cvtColor(original_cv, cv2.COLOR_BGR2GRAY)
    inverted = cv2.bitwise_not(gray)
    img_inverted = cv2.cvtColor(inverted, cv2.COLOR_GRAY2BGR)

    check_list = [
        ("Original", img_original),
        ("Inverted", img_inverted)
    ]

    raw_results = []
    
    print(f">>> OCR 분석 시작: {image_path}")

    # 3. OCR 실행 및 데이터 수집
    for label, img_check in check_list:
        result = ocr.ocr(img_check)
        
        # 결과가 없으면 스킵
        if result is None or len(result) == 0:
            continue
            
        # 딕셔너리 구조 파싱
        ocr_data = result[0]
        
        if not ocr_data or not isinstance(ocr_data, dict) or 'rec_texts' not in ocr_data:
            continue
            
        texts = ocr_data['rec_texts']
        scores = ocr_data['rec_scores']
        boxes = ocr_data['dt_polys']
        
        num_items = len(texts)
        
        for i in range(num_items):
            text = texts[i]
            conf = scores[i]
            coords = boxes[i] # numpy array [[x,y], [x,y], ...]
            
            # [핵심] 폰트 크기(높이) 계산
            # Bounding Box의 Y좌표 차이를 구함
            y_coords = [p[1] for p in coords]
            height = max(y_coords) - min(y_coords)
            
            # 신뢰도 0.6 이상만 수집
            if conf > 0.6:
                raw_results.append({
                    'text': text,
                    'conf': conf,
                    'coords': coords,
                    'source': label,
                    'size': height  # 정렬 기준
                })

    if not raw_results:
        print("❌ 텍스트를 찾지 못했습니다.")
        return

    # 4. [정렬 및 중복 제거]
    # (1) 크기(size) 기준으로 내림차순 정렬 (가장 큰 글자가 맨 앞으로)
    raw_results.sort(key=lambda x: x['size'], reverse=True)

    unique_results = []
    seen_texts = set()

    # (2) 중복 제거 (이미 정렬되어 있으므로, 가장 큰 글자가 먼저 선택됨)
    for item in raw_results:
        text = item['text']
        # 텍스트가 이미 등록되었다면 스킵 (작은 크기의 중복 데이터 제거 효과)
        if text in seen_texts: continue
        
        seen_texts.add(text)
        unique_results.append(item)

    # 5. 결과 출력 (콘솔)
    print("\n" + "="*80)
    print(f" [최종 분석 결과 (폰트 크기순 정렬)]")
    print("-" * 80)
    print(f" {'Text':<25} | {'Size':<6} | {'Conf':<6} | {'Source'}")
    print("-" * 80)

    for item in unique_results:
        # Size를 정수로 깔끔하게 출력
        size_val = int(item['size'])
        print(f" {item['text']:<25} | {size_val:<6} | {item['conf']:.2f}   | {item['source']}")

    print("="*80 + "\n")

    # 6. 박스 그리기 및 이미지 띄우기
    image_rgb = cv2.cvtColor(original_cv, cv2.COLOR_BGR2RGB)
    image_pil = Image.fromarray(image_rgb)
    draw = ImageDraw.Draw(image_pil)
    font = get_optimal_font(size=24)

    # 상위 10개만 그릴지, 전체 다 그릴지 선택 (여기선 전체 다 그림)
    for item in unique_results:
        coords = item['coords']
        text = item['text']
        
        poly_coords = []
        try:
            for p in coords:
                poly_coords.append((int(p[0]), int(p[1])))
        except Exception:
            continue

        # 박스 그리기 (빨간색)
        draw.polygon(poly_coords, outline="red", width=3)

        # 텍스트 그리기
        x, y = poly_coords[0]
        try:
            left, top, right, bottom = draw.textbbox((x, y - 30), text, font=font)
            draw.rectangle((left-5, top-5, right+5, bottom+5), fill="black")
        except:
            pass 
        
        draw.text((x, y - 30), text, font=font, fill=(0, 255, 0))

    print(">>> 이미지 창이 열렸습니다. (아무 키나 누르면 종료)")
    image_pil.show()

if __name__ == "__main__":
    target_image = "/Users/ljw/Desktop/whisky_assistant/cb996de6be6a6656843139bf6d1ecd9b.jpg.webp"
    draw_bbox_size_sort(target_image)
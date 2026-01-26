import os
import glob
import json
import time
import pandas as pd
from tqdm import tqdm
import google.generativeai as genai
from google.generativeai.types import HarmCategory, HarmBlockThreshold

# ======================================================
# [0] API 키 및 모델 설정
# ======================================================
# 🚨 [여기!] 따옴표 안에 진짜 API 키를 붙여넣으세요 (AIza...)
MY_API_KEY = ""  

if MY_API_KEY == "YOUR_API_KEY_HERE" or MY_API_KEY == "여기에_진짜_키를_붙여넣으세요":
    print("❌ [오류] API 키가 설정되지 않았습니다! 코드 15번째 줄을 확인하세요.")
    exit() # 프로그램 종료

try:
    genai.configure(api_key=MY_API_KEY)
except Exception as e:
    print(f"❌ [설정 오류] API 키 형식이 잘못되었습니다: {e}")
    exit()

MODEL_NAME = 'gemini-2.5-flash-lite'
model = genai.GenerativeModel(MODEL_NAME)

# 안전 필터 해제
safety_settings = {
    HarmCategory.HARM_CATEGORY_HARASSMENT: HarmBlockThreshold.BLOCK_NONE,
    HarmCategory.HARM_CATEGORY_HATE_SPEECH: HarmBlockThreshold.BLOCK_NONE,
    HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT: HarmBlockThreshold.BLOCK_NONE,
    HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT: HarmBlockThreshold.BLOCK_NONE,
}

print(f"🔧 모델 설정 완료: {MODEL_NAME} (안전 필터 해제됨)")

# ======================================================
# [1] 파일 경로 설정 (Mac 로컬 경로)
# ======================================================
json_folder_path = '/Users/ljw/Desktop/whisky_assistant/crawlers/data/raw'
save_path = '/Users/ljw/Desktop/whisky_assistant/crawlers/data/whisky_training_data.csv'
BATCH_SIZE = 20
TEST_LIMIT = None 

print(f"📂 읽어올 폴더: {json_folder_path}")
print(f"💾 저장할 파일: {save_path}")

# ======================================================
# [2] 연결 테스트 (키 오류 시 즉시 중단 기능 추가)
# ======================================================
print("\n📡 [테스트] API 연결 시도 중...", end=" ", flush=True)
while True:
    try:
        model.generate_content("Hi", request_options={"timeout": 30})
        print("✅ 성공! (연결됨)")
        break 
    except Exception as e:
        error_msg = str(e)
        # 🚨 API 키가 틀렸으면 재시도하지 않고 바로 멈춤!
        if "400" in error_msg or "API key" in error_msg or "API_KEY_INVALID" in error_msg:
            print(f"\n\n❌ [치명적 오류] API 키가 올바르지 않습니다!")
            print(f"👉 원인: {error_msg}")
            print("👉 해결: 코드 상단의 'MY_API_KEY' 변수에 정확한 키를 입력했는지 확인하세요.")
            exit() # 프로그램 강제 종료
            
        print(f"\n⚠️ [연결 지연] {e}")
        print("⏳ 네트워크/API 불안정. 5초 후 재시도합니다...", end=" ", flush=True)
        time.sleep(5)
        print("재시도!")

# ======================================================
# [3] 배치 분석 함수
# ======================================================
def analyze_batch_reviews(review_list):
    formatted_reviews = ""
    for idx, r_text in enumerate(review_list):
        formatted_reviews += f"""
        [Review {idx+1}]
        "{r_text}"
        --------------------------------------------------
        """

    prompt = f"""
    너는 위스키 리뷰 데이터를 분석하는 '데이터 사이언티스트'야.
    아래 제공된 {len(review_list)}개의 위스키 리뷰(Review 1 ~ Review {len(review_list)})를 각각 분석해서 정량적인 수치로 변환해줘.

    [분석 규칙 - 매우 중요]
    1. **빈도(Frequency)와 강도(Intensity) 기반 채점**:
       - 리뷰에서 특정 맛이 얼마나 자주, 얼마나 강하게 언급되는지 분석해라.

    2. **동의어 및 하위 카테고리 매핑 (Smart Mapping)**:
       - 텍스트에 카테고리 단어가 직접적으로 없어도, **연관된 맛이나 하위 개념이 있으면 해당 카테고리 점수에 반영해라.**
       - 예시 1: "복숭아", "살구", "자두", "핵과류", "베리" -> **Fruity** 점수 증가
       - 예시 2: "토피", "흑설탕", "메이플 시럽", "크림 브륄레" -> **Sweet** 점수 증가
       - 예시 3: "병원 냄새", "요오드", "훈제 연어", "타이어 고무" -> **Peaty** 점수 증가
       - 예시 4: "후추", "생강", "정향", "알싸한" -> **Spicy** 점수 증가
       - 예시 5: "톱밥", "오래된 가구", "탄닌", "씁쓸한" -> **Woody** 점수 증가
       - 예시 6: "비스킷", "토스트", "시리얼", "구운 빵" -> **Malty** 점수 증가

    3. **점수 기준**:
       - 0점: 해당 카테고리와 관련된 단어나 뉘앙스가 전혀 없음.
       - 1~3점: 미미하거나 배경에 깔리는 정도.
       - 4~6점: 분명하게 느껴지지만 압도적이지 않음.
       - 7~9점: 해당 위스키의 지배적인(Dominant) 캐릭터임.
       - 10점: "이 위스키는 곧 이 맛이다"라고 할 정도로 강렬함 (예: 아드벡의 피트).

    [입력 데이터]
    {formatted_reviews}

    [출력 포맷 (JSON List only)]
    반드시 입력된 리뷰 순서대로 {len(review_list)}개의 객체를 가진 리스트를 반환해.
    [
        {{
            "fruity": 0~10,
            "sweet": 0~10,
            "peaty": 0~10,
            "spicy": 0~10,
            "woody": 0~10,
            "malty": 0~10,
            "review_sentiment": -1.0 ~ 1.0,
            "flavor_tags": ["추출된 핵심 맛1", "핵심 맛2", "핵심 맛3", "핵심 맛4", "핵심 맛5"]
        }},
        ... (반복)
    ]
    """

    while True:
        try:
            response = model.generate_content(prompt, safety_settings=safety_settings, request_options={"timeout": 50})
            text_res = response.text.replace("```json", "").replace("```", "").strip()
            
            results = json.loads(text_res)
            
            if isinstance(results, list) and len(results) == len(review_list):
                return results
            else:
                time.sleep(2)
                continue

        except Exception as e:
            error_msg = str(e)
            if "429" in error_msg or "503" in error_msg:
                print(f"\n⏳ [대기] 속도 제한. 40초 쉼...", end=" ", flush=True)
                time.sleep(40)
            else:
                print(f"\n⚠️ 배치 에러: {e} -> 5초 후 재시도")
                time.sleep(5)
            continue

# ======================================================
# [4] 메인 실행 로직
# ======================================================
if __name__ == "__main__":
    json_files = glob.glob(os.path.join(json_folder_path, "*.json"))
    print(f"📂 총 {len(json_files)}개의 파일을 찾았습니다.")

    if TEST_LIMIT:
        json_files = json_files[:TEST_LIMIT]

    training_data = []

    if os.path.exists(save_path):
        try:
            existing_df = pd.read_csv(save_path)
            training_data = existing_df.to_dict('records')
            print(f"🔄 기존 데이터 {len(training_data)}개를 로드했습니다.")
        except:
            pass

    for file_path in tqdm(json_files, desc="파일 처리 중..."):
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                raw_data = json.load(f)
            
            reviews_list = raw_data.get('reviews', [])
            if not reviews_list: continue

            valid_reviews = []
            target_reviews = reviews_list[:50]

            for review in target_reviews:
                combined_parts = []
                if isinstance(review, dict):
                    if review.get('nose'): combined_parts.append(f"Nose: {review.get('nose')}")
                    if review.get('taste'): combined_parts.append(f"Taste: {review.get('taste')}")
                    if review.get('finish'): combined_parts.append(f"Finish: {review.get('finish')}")
                    if review.get('content'): combined_parts.append(f"Comment: {review.get('content')}")
                    full_text = "\n".join(combined_parts)
                else:
                    full_text = str(review)

                if len(full_text) < 5: continue
                if any(d.get('review_text') == full_text for d in training_data):
                    continue
                valid_reviews.append(full_text)

            for i in range(0, len(valid_reviews), BATCH_SIZE):
                batch = valid_reviews[i : i + BATCH_SIZE]
                if not batch: continue
                
                batch_results = analyze_batch_reviews(batch)
                
                for review_text, result in zip(batch, batch_results):
                    combined_data = {
                        "file_name": os.path.basename(file_path),
                        "original_name": raw_data.get("name", "Unknown"),
                        "review_text": review_text,
                        **result
                    }
                    training_data.append(combined_data)

                pd.DataFrame(training_data).to_csv(save_path, index=False, encoding='utf-8-sig')
                time.sleep(5)

        except Exception as e:
            print(f"⚠️ 파일 에러 ({os.path.basename(file_path)}): {e}")
            continue

    print(f"\n🎉 완료! 총 {len(training_data)}개 데이터.")
    print(f"저장 위치: {save_path}")
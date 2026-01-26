import json
import requests

# 너 OCR 코드에서 run_whisky_ocr만 import 하거나,
# 같은 파일에 붙여넣어도 됨.
from your_ocr_file import run_whisky_ocr  # 파일명에 맞게 수정


def build_payload(results, top_k=12):
    # OCR 결과에서 text만 뽑기 (중요도(size) 순으로 이미 정렬되어 있음)
    candidates = [r["text"] for r in results if r.get("text")][:top_k]

    # text는 옵션: 후보들을 공백으로 합쳐서 검색 힌트로 제공
    # (Spring에서 candidates 우선 쓰게 설계했으면 text는 그냥 참고용)
    text = " ".join(candidates)

    return {
        "text": text,
        "candidates": candidates
    }


def call_spring_top3(spring_url, payload, timeout=30):
    resp = requests.post(spring_url, json=payload, timeout=timeout)
    resp.raise_for_status()
    return resp.json()


if __name__ == "__main__":
    target_img = ""  # 이미지 경로 넣기
    spring_url = "http://127.0.0.1:8080/api/whiskies/ocr/top3" 

    results = run_whisky_ocr(target_img)

    print(f"\n>>> OCR 결과 {len(results)}건")
    for r in results[:10]:
        print(f"[{r['text']}] size={int(r['size'])} conf={r['conf']:.2f}")

    payload = build_payload(results, top_k=12)

    print("\n>>> Spring 요청 payload")
    print(json.dumps(payload, ensure_ascii=False, indent=2))

    top3 = call_spring_top3(spring_url, payload)

    print("\n>>> Spring 응답 TOP3")
    print(json.dumps(top3, ensure_ascii=False, indent=2))

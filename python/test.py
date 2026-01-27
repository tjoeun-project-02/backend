## 1회 테스트용 스트립

import os

# ===========================================================
# ✅ Paddle/oneDNN 관련 충돌 방지 (가장 먼저!)
# ===========================================================
# - 값은 "false"보다 "0"이 더 확실하게 먹는 케이스가 많음
# - 환경에 따라 키를 다르게 읽는 경우가 있어 둘 다 꺼줌
os.environ["FLAGS_use_mkldnn"] = "0"      # oneDNN(mkldnn) 끄기
os.environ["FLAGS_use_onednn"] = "0"      # 어떤 버전은 이 키도 봄

# ✅ 모델 호스터 체크(허깅페이스 연결 테스트) 스킵
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"

# ===========================================================
# 일반 import
# ===========================================================
import json
import requests

from img_process.whisky_img import run_whisky_ocr


def build_payload(results, top_k=12):
    """
    OCR 결과에서 spring으로 보낼 텍스트/후보 리스트 만들기
    - 길이가 너무 짧은 텍스트는 후보에서 제외 (노이즈 방지)
    """
    candidates = [
        r["text"]
        for r in results
        if r.get("text") and len(r["text"]) >= 3
    ][:top_k]

    text = " ".join(candidates)

    return {
        "text": text,
        "candidates": candidates
    }


def call_spring_top3(spring_url, payload, timeout=30):
    """
    Spring 서버에 POST 요청 보내고 JSON 응답 받기
    """
    resp = requests.post(spring_url, json=payload, timeout=timeout)
    resp.raise_for_status()
    return resp.json()


if __name__ == "__main__":
    # ✅ 여기 경로가 진짜 있는지 꼭 확인 (python 폴더 기준)
    target_img = "./test_img.webp"
    spring_url = "http://127.0.0.1:8080/api/whiskies/ocr/top3"

    results = run_whisky_ocr(target_img)

    print(f"\n>>> OCR 결과 {len(results)}건")
    for r in results[:10]:
        print(f"[{r['text']}] size={int(r['size'])} conf={r['conf']:.2f}")

    payload = build_payload(results, top_k=12)

    print("\n>>> Spring 요청 payload")
    print(json.dumps(payload, ensure_ascii=False, indent=2))

    try:
        top3 = call_spring_top3(spring_url, payload)

        print("\n>>> Spring 응답 TOP3")
        print(json.dumps(top3, ensure_ascii=False, indent=2))

    except requests.exceptions.ConnectionError:
        print("\n❌ Spring 서버 연결 실패")
        print("   - Spring 실행 중인지 확인")
        print("   - URL이 맞는지 확인:", spring_url)

    except requests.exceptions.HTTPError as e:
        print("\n❌ Spring 서버가 에러 응답을 줬음(HTTPError)")
        print("   - status:", getattr(e.response, "status_code", None))
        print("   - body:", getattr(e.response, "text", None))

    except Exception as e:
        print("\n❌ 알 수 없는 에러:", repr(e))

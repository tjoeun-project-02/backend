import json
import glob
import requests

API_URL = "http://localhost:8080/api/whiskies"
HEADERS = {"Content-Type": "application/json"}

def convert_payload(raw_json: dict) -> dict:
    info = raw_json["info"]

    return {
        "wsName": info.get("ws_name"),
        "wsNameKo": info.get("ws_name_kr"),
        "wsDistillery": info.get("ws_distillery"),
        "wsCategory": info.get("ws_category"),
        "wsAge": info.get("ws_age"),
        "wsAbv": info.get("ws_abv"),
        "wsPrice": None,                     # DTO에는 있지만 데이터 없음
        "wsImage": info.get("ws_image"),
        "wsVol": None,                       # DTO에는 있지만 데이터 없음
        "wsRating": info.get("ws_rating"),
        "wsVoteCnt": info.get("ws_vote_cnt"),
        "tasteProfile": info.get("flavor_profile"),
        "tags": info.get("top_keywords", [])
    }

success, fail = 0, 0

for file in glob.glob("processed_data/*.json"):
    with open(file, encoding="utf-8") as f:
        raw_json = json.load(f)

    payload = convert_payload(raw_json)

    res = requests.post(API_URL, json=payload, headers=HEADERS)

    if res.status_code in (200, 201):
        success += 1
        print(f"✅ 성공: {payload['wsName']}")
    else:
        fail += 1
        print(f"❌ 실패: {payload['wsName']} / {res.status_code}")
        print(res.text)

print(f"\n총 성공: {success}, 실패: {fail}")

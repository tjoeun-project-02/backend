import os

os.environ["FLAGS_use_mkldnn"] = "0"
os.environ["FLAGS_use_onednn"] = "0"
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"

import uuid
import shutil
import json
import requests

from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from img_process.whisky_img import run_whisky_ocr

app = FastAPI(title="Whisky OCR Server")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "./uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

SPRING_URL = os.getenv(
    "SPRING_URL",
    "http://127.0.0.1:8080/api/whiskies/ocr/top3"
)


def build_payload(results, top_k=12):
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
    resp = requests.post(spring_url, json=payload, timeout=timeout)
    resp.raise_for_status()
    return resp.json()


@app.post("/ocr")
async def ocr_whisky(file: UploadFile = File(...)):
    if not file.filename:
        raise HTTPException(status_code=400, detail="파일 이름이 없습니다.")

    ext = file.filename.split(".")[-1].lower()
    filename = f"{uuid.uuid4()}.{ext}"
    img_path = os.path.join(UPLOAD_DIR, filename)

    try:
        with open(img_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        results = run_whisky_ocr(img_path)

        payload = build_payload(results, top_k=12)

        if not payload["candidates"]:
            return {
                "candidates": [],
                "raw": results,
                "payload": payload,
                "spring_top3": [],
                "message": "유효한 candidates가 없어 Spring 호출을 생략했습니다."
            }

        try:
            top3 = call_spring_top3(SPRING_URL, payload, timeout=30)
        except requests.exceptions.ConnectionError:
            return {
                "candidates": payload["candidates"],
                "raw": results,
                "payload": payload,
                "spring_top3": [],
                "message": f"Spring 서버 연결 실패: {SPRING_URL}"
            }
        except requests.exceptions.HTTPError as e:
            raise HTTPException(
                status_code=502,
                detail=f"Spring HTTPError: status={getattr(e.response, 'status_code', None)}, body={getattr(e.response, 'text', None)}"
            )
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Unknown error: {repr(e)}")

        return {
            "candidates": payload["candidates"],
            ## "raw": results,         ## OCR이 실제로 인식한 모든 텍스트
            "payload": payload,     ## 추천에 실제 사용한 텍스트들
            "spring_top3": top3     ## Spring에 실제로 보낸 요청 내용
        }

    finally:
        if os.path.exists(img_path):
            os.remove(img_path)

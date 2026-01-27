import os
os.environ["DISABLE_MODEL_SOURCE_CHECK"] = "True"
os.environ["FLAGS_use_onednn"] = "false"
os.environ["FLAGS_use_mkldnn"] = "false"

from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
import shutil
import uuid

from img_process.whisky_img import run_whisky_ocr

app = FastAPI(title="Whisky OCR Server")

# CORS (Spring / 앱 허용)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],   # 나중에 Spring 주소로 제한 가능
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "./uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


@app.post("/ocr")
async def ocr_whisky(file: UploadFile = File(...)):
    """
    위스키 이미지 OCR API
    """
    # 파일 저장
    ext = file.filename.split(".")[-1]
    filename = f"{uuid.uuid4()}.{ext}"
    img_path = os.path.join(UPLOAD_DIR, filename)

    with open(img_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # OCR 실행
    results = run_whisky_ocr(img_path)

    # Spring으로 넘기기 좋은 형태로 가공
    candidates = [
        r["text"]
        for r in results
        if r.get("text") and len(r["text"]) >= 3
    ][:12]

    return {
        "candidates": candidates,
        "raw": results
    }

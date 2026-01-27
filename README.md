# backend

# Python 3.10 가상환경 생성
py -V:3.10 -m venv .venv

# 가상환경 활성화
.\.venv\Scripts\activate

# pip 업그레이드
python -m pip install --upgrade pip

# requirements 설치
pip install -r requirements.txt

# 가상환경 끄기
deactivate
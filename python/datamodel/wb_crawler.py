from curl_cffi import requests
from bs4 import BeautifulSoup
import os
import sys
import json
import time
import random
import re

# -----------------------------------------------------------------------------
# 1. 설정 (Configuration)
# -----------------------------------------------------------------------------
try:
    from config import TARGET_URLS
except ImportError:
    print("❌ 'config.py' 파일이 없습니다.")
    sys.exit()

TARGET_REVIEW_COUNT = 50 
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_DIR = os.path.join(BASE_DIR, 'data', 'raw')
COOKIE_PATH = os.path.join(BASE_DIR, 'cookie.txt')

if not os.path.exists(DATA_DIR):
    os.makedirs(DATA_DIR)

# -----------------------------------------------------------------------------
# 2. 크롤러 클래스 (Crawler Class)
# -----------------------------------------------------------------------------
class WhiskyBaseCrawler:
    def __init__(self):
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'application/json, text/javascript, */*; q=0.01',
            'Accept-Language': 'en-US,en;q=0.9',
            'X-Requested-With': 'XMLHttpRequest',
        }
        self.cookie_data = self._load_and_forge_cookies()
        self.headers['Cookie'] = self.cookie_data

    def _load_and_forge_cookies(self):
        if not os.path.exists(COOKIE_PATH):
            print("❌ 'cookie.txt' 파일이 없습니다!")
            sys.exit()

        with open(COOKIE_PATH, 'r', encoding='utf-8') as f:
            data = f.read().strip()
            if data.startswith("Cookie:"):
                data = data.replace("Cookie:", "").strip()
            
            if "language=ko" in data:
                data = data.replace("language=ko", "language=en")
                print("   🔧 쿠키 설정 위조: 한국어 -> 영어")
            elif "language=en" not in data:
                data = "language=en; " + data
                print("   🔧 쿠키 설정 추가: 영어 모드 적용")
            return data

    def _get_clean_english_url(self, url):
        base = url.split("?")[0]
        return f"{base}?language=en"

    def _extract_id(self, url):
        match = re.search(r'/whisky/(\d+)/', url)
        return match.group(1) if match else f"unknown_{int(time.time())}"

    def _parse_metadata(self, soup, url, fixed_category=None):
        """
        [Final Logic]
        1. ws_category: config.py 설정값 우선 (강제 고정)
        2. ws_distillery: 직접 타겟팅 -> 헤더 링크
        3. ws_age: 직접 타겟팅
        4. ws_price, ws_vol: 삭제됨
        """
        info = {
            "ws_name": "Unknown",       
            "ws_category": fixed_category,  # 고정 카테고리 적용
            "ws_distillery": None,      
            "ws_age": None,             
            "ws_abv": None,             
            "ws_image": None,           
            "ws_rating": None,          
            "ws_vote_cnt": None,        
            "original_url": url
        }

        html_str = str(soup)

        # --- 1. JSON-LD 파싱 ---
        json_ld_script = soup.find('script', type='application/ld+json')
        ld_data = {}
        if json_ld_script:
            try:
                ld_data = json.loads(json_ld_script.string)
                info['ws_name'] = ld_data.get('name', 'Unknown')
                info['ws_image'] = ld_data.get('image', None)
                
                if 'brand' in ld_data:
                    if isinstance(ld_data['brand'], dict):
                        info['ws_distillery'] = ld_data['brand'].get('name')
                    elif isinstance(ld_data['brand'], str):
                        info['ws_distillery'] = ld_data['brand']
                
                if 'aggregateRating' in ld_data:
                    info['ws_rating'] = ld_data['aggregateRating'].get('ratingValue')
                    info['ws_vote_cnt'] = ld_data['aggregateRating'].get('reviewCount')
                    
                desc = ld_data.get('description', '')
                if not info['ws_abv']:
                    match = re.search(r'(\d+(?:\.\d+)?)\s*%', desc)
                    if match: info['ws_abv'] = float(match.group(1))
            except: pass

        # --- 2. [핵심] 주요 정보 정밀 타겟팅 ---
        
        # (1) 카테고리 (Category) - 고정값이 없을 때만 크롤링
        if not info['ws_category']:
            cat_dt = soup.find('dt', string=re.compile(r"^\s*Category\s*$", re.IGNORECASE))
            if cat_dt:
                cat_dd = cat_dt.find_next_sibling('dd')
                if cat_dd:
                    info['ws_category'] = cat_dd.get_text(strip=True)

        # (2) 증류소 (Distillery)
        if not info['ws_distillery']:
            dist_dt = soup.find('dt', string=re.compile("Distillery|Brand", re.IGNORECASE))
            if dist_dt:
                dist_dd = dist_dt.find_next_sibling('dd')
                if dist_dd:
                    link = dist_dd.find('a')
                    info['ws_distillery'] = link.get_text(strip=True) if link else dist_dd.get_text(strip=True)

        # (3) 숙성년수 (Stated Age)
        if not info['ws_age']:
            age_dt = soup.find('dt', string=re.compile("Stated Age", re.IGNORECASE))
            if age_dt:
                age_dd = age_dt.find_next_sibling('dd')
                if age_dd:
                    num = re.search(r'(\d+)', age_dd.get_text(strip=True))
                    if num: info['ws_age'] = int(num.group(1))
        
        # (4) 도수 (Strength)
        if not info['ws_abv']:
            abv_dt = soup.find('dt', string=re.compile("Strength", re.IGNORECASE))
            if abv_dt:
                abv_dd = abv_dt.find_next_sibling('dd')
                if abv_dd:
                    val_text = abv_dd.get_text(strip=True).replace('%', '').strip()
                    try: info['ws_abv'] = float(val_text)
                    except: pass

        # --- 3. 백업 로직 ---
        
        # Age 백업
        if not info['ws_age']:
             desc = ld_data.get('description', '')
             match = re.search(r'(\d+)\s*yr', desc)
             if match: info['ws_age'] = int(match.group(1))

        # ABV 백업
        if not info['ws_abv']:
            abv_matches = re.findall(r'(\d+(?:\.\d+)?)\s?%', html_str)
            for val in abv_matches:
                try:
                    fval = float(val)
                    if 30 <= fval <= 80:
                        info['ws_abv'] = fval
                        break
                except: pass

        # --- 4. 기타 보완 ---
        if info['ws_name'] == "Unknown":
            header_h1 = soup.select_one('header > h1')
            if header_h1: info['ws_name'] = header_h1.get_text(separator=" ", strip=True)
            elif soup.title: info['ws_name'] = soup.title.text.replace(' - Ratings and reviews - Whiskybase', '').strip()

        # [백업] 증류소 (헤더 링크)
        if not info['ws_distillery']:
            header_link = soup.select_one('header > h1 > a')
            if header_link: 
                info['ws_distillery'] = header_link.get_text(strip=True)
            
            if not info['ws_distillery']:
                crumbs = soup.select('.breadcrumb li')
                if len(crumbs) >= 3:
                    info['ws_distillery'] = crumbs[2].get_text(strip=True)

        # [백업] 카테고리 (텍스트 검색) - 고정값 없을 때만
        if not info['ws_category']:
            page_text = soup.get_text()
            for cat in ["Single Malt", "Blended", "Blend", "Bourbon", "Rye", "Grain", "Pot Still"]:
                if cat in page_text:
                    info['ws_category'] = cat
                    break
            if not info['ws_category']: info['ws_category'] = "Single Malt"

        if not info['ws_image']:
             fallback_img = soup.select_one('.visual-wrapper img')
             if fallback_img: info['ws_image'] = fallback_img.get('src')

        if info['ws_rating']:
            try: info['ws_rating'] = float(info['ws_rating'])
            except: pass

        return info

    def _fetch_reviews_from_api(self, wb_id, csrf_token, existing_keys):
        new_reviews = []
        offset = 0
        batch_size = 10
        api_url = f"https://www.whiskybase.com/whiskies/whisky/{wb_id}/nextnotes"

        print("   🔄 API로 리뷰 수집 시작...")

        while len(new_reviews) < TARGET_REVIEW_COUNT:
            payload = {
                '_token': csrf_token,
                '_r': int(time.time() * 1000),
                'from': offset,
                'mode': 'RECENT',
                'sorting': 'user-points',
                'type': ''
            }

            try:
                res = requests.post(api_url, data=payload, headers=self.headers, impersonate="chrome110", timeout=20)
                if res.status_code != 200:
                    print(f"   ✅ 수집 종료 (Status: {res.status_code})")
                    break

                try:
                    content = res.json().get('body', '')
                    if not content: break
                except:
                    content = res.text

                soup = BeautifulSoup(content, 'html.parser')
                notes = soup.select('li > article.wb--note')
                if not notes: break

                batch_cnt = 0
                for note in notes:
                    if 'blur' in note.get('class', []): continue
                    
                    user = note.select_one('.wb--note-title a')
                    user_name = user.text.strip() if user else "Anonymous"
                    date_txt = note.select_one('time.wb--note-date').get_text(strip=True)
                    
                    if f"{user_name}_{date_txt}" in existing_keys: continue

                    main = note.select_one('[data-translation-field="message"]')
                    main_text = main.text.strip() if main else ""
                    
                    notes_dict = {}
                    for field in ['nose', 'taste', 'finish']:
                        tag = note.select_one(f'[data-translation-field="{field}_text"]')
                        notes_dict[field] = tag.text.strip() if tag else ""

                    if not (main_text or any(notes_dict.values())): continue

                    rank = note.select_one('.user-role-icon')
                    rating_tag = note.select_one('.wb--note-title span')
                    rating = rating_tag.text.strip() if rating_tag else None

                    review_obj = {
                        "user_name": user_name,
                        "user_rank": rank.get('title') if rank else "Member",
                        "rating": rating,
                        "review_date": date_txt,
                        "content": main_text,
                        "nose": notes_dict['nose'],
                        "taste": notes_dict['taste'],
                        "finish": notes_dict['finish']
                    }
                    
                    new_reviews.append(review_obj)
                    existing_keys.add(f"{user_name}_{date_txt}")
                    batch_cnt += 1

                print(f"   📥 Offset {offset}: {batch_cnt}개 신규 (누적 {len(new_reviews)}개)", end='\r')
                offset += batch_size
                time.sleep(1)

            except Exception as e:
                print(f"\n   ⚠️ API 에러: {e}")
                break
        
        return new_reviews

    # [수정] fixed_category 인자 추가
    def run(self, raw_url, fixed_category=None):
        target_url = self._get_clean_english_url(raw_url)
        wb_id = self._extract_id(target_url)
        file_path = os.path.join(DATA_DIR, f"wb_{wb_id}.json")

        print(f"\n🥃 시작: {target_url}")
        if fixed_category:
            print(f"   🔒 카테고리 고정: {fixed_category}")

        try:
            print("   🌍 메인 페이지 접속 중...")
            res = requests.get(target_url, headers=self.headers, impersonate="chrome110", timeout=20)
            if res.status_code != 200:
                print(f"   ❌ 접속 실패: {res.status_code}")
                return

            soup = BeautifulSoup(res.text, 'html.parser')
            
            token_tag = soup.select_one('meta[name="csrf-token"]')
            csrf_token = token_tag['content'] if token_tag else ""
            if csrf_token: print("   🔑 보안 토큰 확보 완료")

            # [핵심] 카테고리 고정 정보 전달
            whisky_info = self._parse_metadata(soup, raw_url.split("?")[0], fixed_category)
            
            print(f"   📌 정보: {whisky_info['ws_name']} (카테고리: {whisky_info['ws_category']})")

        except Exception as e:
            print(f"   ❌ 초기화 에러: {e}")
            return

        existing_reviews = []
        id_set = set()
        if os.path.exists(file_path):
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    existing_reviews = data.get("reviews", [])
                    for rev in existing_reviews:
                        id_set.add(f"{rev['user_name']}_{rev['review_date']}")
                print(f"   📦 기존 리뷰 {len(existing_reviews)}개 로드됨")
            except: pass

        new_reviews = self._fetch_reviews_from_api(wb_id, csrf_token, id_set)

        if new_reviews or not os.path.exists(file_path):
            final_reviews = existing_reviews + new_reviews
            final_data = {
                "info": whisky_info,
                "collected_count": len(final_reviews),
                "reviews": final_reviews
            }
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(final_data, f, ensure_ascii=False, indent=4)
            print(f"\n   💾 저장 완료: {len(new_reviews)}개 추가 (총 {len(final_reviews)}개)")
        else:
            print("\n   ✅ 변경 사항 없음")

# -----------------------------------------------------------------------------
# 4. 메인 실행
# -----------------------------------------------------------------------------
if __name__ == "__main__":
    crawler = WhiskyBaseCrawler()
    
    if isinstance(TARGET_URLS, dict):
        total_links = sum(len(urls) for urls in TARGET_URLS.values())
        print(f"🚀 총 {total_links}개의 링크를 처리합니다. (카테고리 고정 모드)")
        
        for category, urls in TARGET_URLS.items():
            for i, url in enumerate(urls):
                crawler.run(url, category)
                if i < len(urls) - 1:
                    time.sleep(random.randint(2, 5))
            time.sleep(1)

    elif isinstance(TARGET_URLS, list):
        print(f"🚀 총 {len(TARGET_URLS)}개의 링크를 처리합니다. (자동 감지 모드)")
        for i, url in enumerate(TARGET_URLS):
            crawler.run(url)
            if i < len(TARGET_URLS) - 1:
                wait = random.randint(2, 5)
                print(f"   💤 {wait}초 대기...")
                time.sleep(wait)
    
    else:
        print("❌ config.py 형식이 올바르지 않습니다.")

    print("\n🎉 모든 작업 완료!")
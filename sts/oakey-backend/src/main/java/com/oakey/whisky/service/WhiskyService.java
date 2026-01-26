package com.oakey.whisky.service;

import com.oakey.metadata.service.MetadataService;
import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.dto.WhiskyCreateRequest;
import com.oakey.whisky.dto.WhiskyResponse;
import com.oakey.whisky.dto.WhiskyUpdateRequest;
import com.oakey.whisky.repository.WhiskyRepository;
import com.oakey.whisky.repository.WhiskySimilarityRow;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;
    private final MetadataService metadataService;

    public WhiskyService(WhiskyRepository whiskyRepository, MetadataService metadataService) {
        this.whiskyRepository = whiskyRepository;
        this.metadataService = metadataService;
    }

    /*위스키 생성*/
    public WhiskyResponse create(WhiskyCreateRequest request) {
        if (request.getWsName() == null || request.getWsName().isBlank()) {
            throw new IllegalArgumentException("wsName은 필수입니다.");
        }

        if (whiskyRepository.existsByWsName(request.getWsName())) {
            throw new IllegalArgumentException("이미 존재하는 wsName입니다.");
        }

        Whisky whisky = new Whisky(
                request.getWsName(),
                request.getWsNameKo(),
                request.getWsDistillery(),
                request.getWsCategory(),
                request.getWsAge(),
                request.getWsAbv(),
                request.getWsPrice(),
                request.getWsImage(),
                request.getWsVol(),
                request.getWsRating(),
                request.getWsVoteCnt(),
                request.getTasteProfile(),
                request.getTags()
        );

        Whisky saved = whiskyRepository.save(whisky);

        metadataService.incrementVersion("WHISKEY_LIST");

        return toResponse(saved);
    }

    /**
     * 위스키 단건 조회
     */
    @Transactional(readOnly = true)
    public WhiskyResponse findById(Integer wsId) {
        Whisky whisky = whiskyRepository.findById(wsId)
                .orElseThrow(() -> new EntityNotFoundException("Whisky not found"));

        // 트랜잭션 안에서 tags에 접근하여 데이터를 강제로 로딩(Lazy Loading 해결)
        List<String> tagList = new ArrayList<>(whisky.getTags());

        return new WhiskyResponse(
                whisky.getWsId(),
                whisky.getWsName(),
                whisky.getWsNameKo(),
                whisky.getWsDistillery(),
                whisky.getWsCategory(),
                whisky.getWsAge(),
                whisky.getWsAbv(),
                whisky.getWsPrice(),
                whisky.getWsImage(),
                whisky.getWsVol(),
                whisky.getWsRating(),
                whisky.getWsVoteCnt(),
                whisky.getTasteProfile(),
                tagList
        );
    }

    /**
     * 위스키 전체 조회
     */
    @Transactional(readOnly = true)
    public List<WhiskyResponse> findAll() {
        return whiskyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * NLP(FASTAPI) 결과 기반: 유사도 점수 TOP 3 위스키 조회
     *
     * 요구사항:
     * - ws_id가 있더라도 그것만 쓰지 않고, 알고리즘(Jaro-Winkler) 기준으로 상위 3개를 반환
     * - 반환 타입은 기존 WhiskyResponse 그대로 유지
     *
     * 입력값은 FASTAPI가 준 JSON(info)의 값들을 그대로 넘기면 된다.
     *  - wsName: info.ws_name
     *  - wsDistillery: info.ws_distillery
     *  - wsNameKo: info.ws_name_kr (없으면 null 가능)
     *  - wsAge: info.ws_age (없으면 null 가능)
     */
    @Transactional(readOnly = true)
    public List<WhiskyResponse> findTop3ByNlpResult(String wsName, String wsDistillery, String wsNameKo, Integer wsAge) {

        String qName = normalize(wsName);
        String qDist = normalize(wsDistillery);
        String qNameKo = normalize(wsNameKo);

        // 셋 다 비면 검색 불가
        if ((qName == null || qName.isBlank())
                && (qDist == null || qDist.isBlank())
                && (qNameKo == null || qNameKo.isBlank())) {
            return List.of();
        }

        // 성능 목적: 후보군을 줄이기 위한 prefix 생성 (이름 > 증류소 > 한글이름 우선)
        String prefix = makePrefix(qName, qDist, qNameKo);

        // Oracle UTL_MATCH 기반 TOP3 조회
        List<WhiskySimilarityRow> rows = whiskyRepository.findTop3ByJaroWinkler(qName, qDist, qNameKo, wsAge, prefix);

        if (rows == null || rows.isEmpty()) {
            return List.of();
        }

        // TOP3의 WS_ID 목록(정렬된 순서 유지)
        List<Integer> ids = rows.stream()
                .map(WhiskySimilarityRow::getWsId)
                .toList();

        // 엔티티 조회 (findAllById는 순서 보장 X 가능성 -> map으로 재정렬)
        List<Whisky> found = whiskyRepository.findAllById(ids);

        Map<Integer, Whisky> map = new HashMap<>();
        for (Whisky w : found) {
            map.put(w.getWsId(), w);
        }

        List<WhiskyResponse> result = new ArrayList<>();
        for (Integer id : ids) {
            Whisky w = map.get(id);
            if (w != null) {
                result.add(toResponse(w));
            }
        }

        return result;
    }

    /**
     * 위스키 수정
     */
    public WhiskyResponse update(Integer wsId, WhiskyUpdateRequest request) {
        Whisky whisky = whiskyRepository.findById(wsId)
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다. wsId=" + wsId));

        if (request.getWsName() == null || request.getWsName().isBlank()) {
            throw new IllegalArgumentException("wsName은 필수입니다.");
        }

        whisky.update(
                request.getWsName(),
                request.getWsNameKo(),
                request.getWsDistillery(),
                request.getWsCategory(),
                request.getWsAge(),
                request.getWsAbv(),
                request.getWsPrice(),
                request.getWsImage(),
                request.getWsVol(),
                request.getWsRating(),
                request.getWsVoteCnt(),
                request.getTasteProfile(),
                request.getTags()
        );

        metadataService.incrementVersion("WHISKEY_LIST");

        return toResponse(whisky);
    }

    /**
     * 위스키 삭제
     */
    public void delete(Integer wsId) {
        if (!whiskyRepository.existsById(wsId)) {
            throw new IllegalArgumentException("위스키를 찾을 수 없습니다. wsId=" + wsId);
        }

        metadataService.incrementVersion("WHISKEY_LIST");

        whiskyRepository.deleteById(wsId);
    }

    private WhiskyResponse toResponse(Whisky whisky) {
        return new WhiskyResponse(
                whisky.getWsId(),
                whisky.getWsName(),
                whisky.getWsNameKo(),
                whisky.getWsDistillery(),
                whisky.getWsCategory(),
                whisky.getWsAge(),
                whisky.getWsAbv(),
                whisky.getWsPrice(),
                whisky.getWsImage(),
                whisky.getWsVol(),
                whisky.getWsRating(),
                whisky.getWsVoteCnt(),
                whisky.getTasteProfile(),
                whisky.getTags()
        );
    }

    /**
     * NLP에서 넘어온 문자열을 비교용으로 정리한다.
     * - null이면 빈 문자열
     * - 소문자
     * - 공백 정리
     */
    private String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toLowerCase();
        x = x.replaceAll("\\s+", " ");
        return x;
    }

    /**
     * 후보군 필터링용 prefix 생성
     * - 우선순위: 영어 이름(wsName) > 증류소(wsDistillery) > 한글 이름(wsNameKo)
     * - 첫 토큰에서 영문/한글만 남기고 앞 3글자 사용(부족하면 1글자)
     */
    private String makePrefix(String qName, String qDist, String qNameKo) {
        String base = "";
        if (qName != null && !qName.isBlank()) base = qName;
        else if (qDist != null && !qDist.isBlank()) base = qDist;
        else base = (qNameKo == null ? "" : qNameKo);

        if (base.isBlank()) return "a";

        String token = base.split("\\s+")[0].replaceAll("[^a-z가-힣]", "");

        if (token.length() >= 3) return token.substring(0, 3);
        if (token.length() >= 1) return token.substring(0, 1);

        return "a";
    }
}

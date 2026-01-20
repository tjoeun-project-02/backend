package com.oakey.flavor.service;

import com.oakey.flavor.domain.Flavor;
import com.oakey.flavor.domain.FlavorKeyword;
import com.oakey.flavor.dto.*;
import com.oakey.flavor.repository.FlavorKeywordRepository;
import com.oakey.flavor.repository.FlavorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlavorService {

    private final FlavorKeywordRepository flavorKeywordRepository;
    private final FlavorRepository flavorRepository;

    public FlavorService(FlavorKeywordRepository flavorKeywordRepository, FlavorRepository flavorRepository) {
        this.flavorKeywordRepository = flavorKeywordRepository;
        this.flavorRepository = flavorRepository;
    }

    /*
     * 키워드 생성
     */
    public FlavorKeywordResponse createKeyword(FlavorKeywordCreateRequest request) {
        if (request.getKeyword() == null || request.getKeyword().isBlank()) {
            throw new IllegalArgumentException("keyword는 필수다.");
        }

        if (flavorKeywordRepository.existsByKeyword(request.getKeyword())) {
            throw new IllegalArgumentException("이미 존재하는 keyword다.");
        }

        FlavorKeyword saved = flavorKeywordRepository.save(new FlavorKeyword(request.getKeyword()));
        return new FlavorKeywordResponse(saved.getKeywordId(), saved.getKeyword());
    }

    /*
     * 키워드 전체 조회
     */
    @Transactional(readOnly = true)
    public List<FlavorKeywordResponse> getKeywords() {
        return flavorKeywordRepository.findAll()
                .stream()
                .map(k -> new FlavorKeywordResponse(k.getKeywordId(), k.getKeyword()))
                .toList();
    }

    /*
     * 위스키-키워드 연결 저장
     */
    public FlavorResponse createFlavor(FlavorCreateRequest request) {
        if (request.getWsId() == null) {
            throw new IllegalArgumentException("wsId는 필수다.");
        }
        if (request.getKeywordId() == null) {
            throw new IllegalArgumentException("keywordId는 필수다.");
        }
        if (request.getWeight() == null) {
            throw new IllegalArgumentException("weight는 필수다.");
        }

        Flavor saved = flavorRepository.save(
                new Flavor(request.getWsId(), request.getWsName(), request.getWsNameKo(), request.getKeywordId(), request.getWeight())
        );

        return new FlavorResponse(
                saved.getFlavorId(),
                saved.getWsId(),
                saved.getWsName(),
                saved.getWsNameKo(),
                saved.getKeywordId(),
                saved.getWeight()
        );
    }

    /*
     * 위스키별 flavor 조회
     */
    @Transactional(readOnly = true)
    public List<FlavorResponse> getFlavorsByWhisky(Integer wsId) {
        return flavorRepository.findByWsId(wsId)
                .stream()
                .map(f -> new FlavorResponse(f.getFlavorId(), f.getWsId(), f.getWsName(), f.getKeywordId(), f.getWeight()))
                .toList();
    }
}

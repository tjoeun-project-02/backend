package com.oakey.whisky.service;

import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.dto.WhiskyCreateRequest;
import com.oakey.whisky.dto.WhiskyResponse;
import com.oakey.whisky.dto.WhiskyUpdateRequest;
import com.oakey.whisky.repository.WhiskyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;

    public WhiskyService(WhiskyRepository whiskyRepository) {
        this.whiskyRepository = whiskyRepository;
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
                request.getWsVoteCnt()
        );

        Whisky saved = whiskyRepository.save(whisky);

        return toResponse(saved);
    }

    /**
     * 위스키 단건 조회
     */
    @Transactional(readOnly = true)
    public WhiskyResponse findById(Integer wsId) {
        Whisky whisky = whiskyRepository.findById(wsId)
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다. wsId=" + wsId));
        return toResponse(whisky);
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
                request.getWsVoteCnt()
        );

        return toResponse(whisky);
    }

    /**
     * 위스키 삭제
     */
    public void delete(Integer wsId) {
        if (!whiskyRepository.existsById(wsId)) {
            throw new IllegalArgumentException("위스키를 찾을 수 없습니다. wsId=" + wsId);
        }
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
                whisky.getWsVoteCnt()
        );
    }
}

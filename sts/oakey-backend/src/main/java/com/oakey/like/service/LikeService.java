package com.oakey.like.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oakey.like.domain.Like;
import com.oakey.like.dto.LikeRequest;
import com.oakey.like.dto.LikedWhiskyResponse;
import com.oakey.like.repository.LikeRepository;
import com.oakey.user.domain.User;
import com.oakey.user.repository.UserRepository;
import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.repository.WhiskyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final WhiskyRepository whiskyRepository;

    public String toggleLike(LikeRequest requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Whisky whisky = whiskyRepository.findById(requestDto.getWsId())
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다."));

        Optional<Like> like = likeRepository.findByUserAndWhisky(user, whisky);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return "좋아요 취소 완료";
        } else {
            likeRepository.save(Like.builder().user(user).whisky(whisky).build());
            return "좋아요 완료";
        }
    }
    
    public List<LikedWhiskyResponse> getLikedWhiskies(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 해당 유저의 좋아요 목록 조회
        List<Like> likes = likeRepository.findAllByUser(user);

        // 2. Like 엔티티에서 Whisky 정보를 추출하여 DTO로 변환
        return likes.stream()
                .map(like ->{
                	Whisky whisky = like.getWhisky();
                	return new LikedWhiskyResponse(
                			whisky.getWsId(),
                			whisky.getWsName(),
                            whisky.getWsNameKo(),
                			whisky.getWsImage());
                })
                .collect(Collectors.toList());
    }
}
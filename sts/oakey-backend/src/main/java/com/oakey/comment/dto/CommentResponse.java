package com.oakey.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//댓글 목록 응답
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
	private Integer commentId;
    private String nickname;     // 기존 필드
    private String content;      // commentBody 매핑용
    private String updateDate;
    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private String wsImage;      // 마이페이지용 추가 필드

    // 기존 getCommentsByWhisky에서 사용하던 생성자와 호환되도록 유지
    public CommentResponse(Integer commentId, String nickname, String content, String updateDate, String wsName, String wsNameKo) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.content = content;
        this.updateDate = updateDate;
        this.wsName = wsName;
        this.wsNameKo = wsNameKo;
    }
}
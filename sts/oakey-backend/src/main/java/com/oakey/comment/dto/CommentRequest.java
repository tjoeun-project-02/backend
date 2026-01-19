package com.oakey.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//댓글 작성 요청
@Getter
@NoArgsConstructor
public class CommentRequest {
 private Long userId;
 private Integer wsId;
 private String content;
}


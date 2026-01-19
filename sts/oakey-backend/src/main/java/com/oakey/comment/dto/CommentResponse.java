package com.oakey.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//댓글 목록 응답
@Getter
@AllArgsConstructor
public class CommentResponse {
	private Integer commentId;
	private String nickname;
	private String content;
	private String updateDate;
}
package com.oakey.comment.domain;

import java.time.LocalDate;

import com.oakey.user.domain.User;
import com.oakey.whisky.domain.Whisky;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 또는 Sequence 사용
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ws_id", nullable = false)
    private Whisky whisky;

    @Column(name = "comment_body", nullable = false, length = 300)
    private String commentBody;

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate; // ERD에 date 타입으로 명시됨

    @Builder
    public Comment(User user, Whisky whisky, String commentBody) {
        this.user = user;
        this.whisky = whisky;
        this.commentBody = commentBody;
        this.updateDate = LocalDate.now();
    }
}
package com.oakey.whisky.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * TB_WHISKY 테이블 매핑 엔티티
 * - Lombok @Getter로 getter 메서드를 자동 생성한다.
 * - Oracle SEQUENCE 기반 PK 생성 전략을 사용한다.
 * - 엔티티의 직접 set을 막기 위해 update 메서드로만 상태 변경을 허용한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_WHISKY")
public class Whisky {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "whisky_seq_gen")
    @SequenceGenerator(name = "whisky_seq_gen", sequenceName = "SEQ_TB_WHISKY", allocationSize = 1)
    @Column(name = "WS_ID", nullable = false)
    private Integer wsId;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name = "WS_NAME", nullable = false, length = 100)
    private String wsName;
    
    @Column(name = "WS_NAME_KO", length = 100)
    private String wsNameKo;

    @Column(name = "WS_DISTILLERY", length = 100)
    private String wsDistillery;

    @Column(name = "WS_CATEGORY", length = 100)
    private String wsCategory;

    @Column(name = "WS_AGE")
    private Integer wsAge;

    @Column(name = "WS_ABV", precision = 10, scale = 2)
    private BigDecimal wsAbv;

    @Column(name = "WS_PRICE", precision = 10, scale = 2)
    private BigDecimal wsPrice;

    @Column(name = "WS_IMAGE", length = 500)
    private String wsImage;

    @Column(name = "WS_VOL")
    private Integer wsVol;

    @Column(name = "WS_RATING", precision = 3, scale = 1)
    private BigDecimal wsRating;

    @Column(name = "WS_VOTE_CNT")
    private Integer wsVoteCnt;

    protected Whisky() {
        // JPA 기본 생성자
    }

    public Whisky(String wsName, String wsDistillery, String wsCategory, Integer wsAge,
                  BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                  BigDecimal wsRating, Integer wsVoteCnt, TasteProfile tasteProfile, List<String> tags) {
        this.wsName = wsName;
        this.wsNameKo = wsNameKo;
        this.wsDistillery = wsDistillery;
        this.wsCategory = wsCategory;
        this.wsAge = wsAge;
        this.wsAbv = wsAbv;
        this.wsPrice = wsPrice;
        this.wsImage = wsImage;
        this.wsVol = wsVol;
        this.wsRating = wsRating;
        this.wsVoteCnt = wsVoteCnt;
    }

    /*
     * 수정용 메서드
     * - 컨트롤러/서비스에서 엔티티 필드를 직접 set하지 않도록 update 메서드로만 변경한다.
     */
    public void update(String wsName, String wsNameKo, String wsDistillery, String wsCategory, Integer wsAge,
                       BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                       BigDecimal wsRating, Integer wsVoteCnt, TasteProfile tasteProfile, List<String> tags) {
        this.wsName = wsName;
        this.wsNameKo = wsNameKo;
        this.wsDistillery = wsDistillery;
        this.wsCategory = wsCategory;
        this.wsAge = wsAge;
        this.wsAbv = wsAbv;
        this.wsPrice = wsPrice;
        this.wsImage = wsImage;
        this.wsVol = wsVol;
        this.wsRating = wsRating;
        this.wsVoteCnt = wsVoteCnt;
        this.tasteProfile = tasteProfile;
        this.tags = tags;
    }
}

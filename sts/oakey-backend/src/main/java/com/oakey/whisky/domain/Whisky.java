package com.oakey.whisky.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    @Embedded
    @Column(name = "TASTE_PROFILE")
    private TasteProfile tasteProfile;

    public Whisky(String wsName, String wsNameKo, String wsDistillery, String wsCategory, Integer wsAge,
                  BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                  BigDecimal wsRating, Integer wsVoteCnt, TasteProfile tasteProfile) {
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
    }

    /**
     * 수정용 메서드: 컨트롤러에서 엔티티를 직접 set 하지 않도록 update 메서드를 제공
     */
    public void update(String wsName, String wsNameKo, String wsDistillery, String wsCategory, Integer wsAge,
                       BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                       BigDecimal wsRating, Integer wsVoteCnt, TasteProfile tasteProfile) {
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
    }
}

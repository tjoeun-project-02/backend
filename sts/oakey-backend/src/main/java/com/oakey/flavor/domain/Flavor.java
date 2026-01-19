package com.oakey.flavor.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/*
 * TB_FLAVOR : 위스키-키워드 연결 + 가중치(weight)
 */
@Entity
@Table(name = "TB_FLAVOR")
public class Flavor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flavor_seq_gen")
    @SequenceGenerator(
            name = "flavor_seq_gen",
            sequenceName = "SEQ_TB_FLAVOR",
            allocationSize = 1
    )
    @Column(name = "FLAVOR_ID", nullable = false)
    private Integer flavorId;

    @Column(name = "WS_ID", nullable = false)
    private Integer wsId;

    @Column(name = "WS_NAME", nullable = false, length = 100)
    private String wsName;

    @Column(name = "KEYWORD_ID", nullable = false)
    private Integer keywordId;

    @Column(name = "WEIGHT", nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    protected Flavor() {
        // JPA 기본 생성자
    }

    public Flavor(Integer wsId, String wsName, Integer keywordId, BigDecimal weight) {
        this.wsId = wsId;
        this.wsName = wsName;
        this.keywordId = keywordId;
        this.weight = weight;
    }

    public Integer getFlavorId() {
        return flavorId;
    }

    public Integer getWsId() {
        return wsId;
    }

    public String getWsName() {
        return wsName;
    }

    public Integer getKeywordId() {
        return keywordId;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}

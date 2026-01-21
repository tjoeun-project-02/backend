package com.oakey.metadata.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_METADATA")
public class Metadata {

    @Id
    @Column(name = "CATEGORY", length = 50)
    private String category;

    @Column(name = "CURRENT_VERSION", nullable = false)
    private Long currentVersion;

    @Column(name = "LAST_UPDATE", nullable = false)
    private LocalDateTime lastUpdate;
}
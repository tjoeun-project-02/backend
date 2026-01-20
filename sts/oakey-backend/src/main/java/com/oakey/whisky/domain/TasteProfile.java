package com.oakey.whisky.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TasteProfile {
    private Double fruity;
    private Double sweet;
    private Double peaty;
    private Double spicy;
    private Double woody;
    private Double malty;
}
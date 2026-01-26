package com.oakey.whisky.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * FastAPI에서 넘어오는 NLP 결과 JSON을 받기 위한 요청 DTO
 * 예)
 * {
 *   "info": {
 *     "ws_name": "...",
 *     "ws_distillery": "...",
 *     "ws_age": 10,
 *     "ws_name_kr": "..."
 *   }
 * }
 */
@Getter
@Setter
public class WhiskyNlpMatchRequest {

    private Info info;

    @Getter
    @Setter
    public static class Info {
        private String ws_name;
        private String ws_distillery;
        private Integer ws_age;
        private String ws_name_kr;
    }
}

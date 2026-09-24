package com.uzinfocom.citytour.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourStopResponse {
    private Long id;
    private AttractionResponse attraction;
    private Integer visitOrder;
    private Integer stayMinutes;
}

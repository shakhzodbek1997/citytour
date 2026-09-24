package com.uzinfocom.citytour.dto;

import com.uzinfocom.citytour.entity.enums.TourStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TourResponse {
    private Long id;
    private String title;
    private GuideResponse guide;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxSeats;
    private BigDecimal pricePerSeat;
    private TourStatus status;
    private List<TourStopResponse> stops;
}

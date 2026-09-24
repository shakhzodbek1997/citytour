package com.uzinfocom.citytour.dto;


import com.uzinfocom.citytour.entity.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private TourResponse tour;
    private String customerName;
    private String customerPhone;
    private Integer seats;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private Instant createdAt;
}

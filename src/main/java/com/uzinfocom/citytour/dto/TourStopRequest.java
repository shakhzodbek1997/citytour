package com.uzinfocom.citytour.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TourStopRequest {

    @NotNull(message = "Attraction ID ni kiritish majburiy")
    private Long attractionId;

    @NotNull(message = "Visit order korsatilishi shart!")
    @Min(value = 1, message = "Visit order kamida 1 bolishi kerak!")
    private Integer visitOrder;

    @NotNull(message = "Bu joyda qolish vaqti ko'rsatilishi kerak")
    @Min(value = 5, message = "QOolish vaqti kamida 5 min bolishi kerak")
    @Max(value = 240, message = "Qolsih vaqti 240minutdan oshmasin!")
    private Integer stayMinutes;
}

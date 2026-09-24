package com.uzinfocom.citytour.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TourRequest {
    @NotBlank(message = "Tur nomi bosh bolishi mumkin emas")
    private String title;

    @NotNull(message = "Gid ID-si korsatilishi kerak")
    private Long guideId;

    @NotNull(message = "Boshlanish vaqti korsatilishi shart")
    @Future(message = "Boshlanish vaqti kelajakda bolishi kerak /not passed time")
    private LocalDateTime startTime;

    @NotNull(message = "Tugash vaqti korsatilishi shart")
    @Future(message = "Tugash vaqti kelajakda bolishi kerak")
    private LocalDateTime endTime;

    @NotNull(message = "Maksimal orinlar soni korsatilishi kerak!")
    @Min(value = 1, message = "O'rinlar soni kamida 1 bo'lishi kerak")
    @Max(value = 50, message = "O'rinlar soni kopi bilan 50 ta bolishi mummkin")
    private Integer maxSeats;

    @NotNull(message = "Bir o'rin narxi ko'rsatilishi shart")
    @DecimalMin(value = "0.01", message = "Narx 0 dan katta bo'lishi kerak")
    private BigDecimal pricePerSeat;

    @Valid // DTO ichida boshqa DTO list kelganda uning ichidagi validatsiyalar ishalshi uchun qoyildi
    private List<TourStopRequest> stops;
}

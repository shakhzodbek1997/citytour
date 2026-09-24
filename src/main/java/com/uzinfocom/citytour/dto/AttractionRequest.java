package com.uzinfocom.citytour.dto;


import com.uzinfocom.citytour.entity.enums.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionRequest {

    @NotBlank(message = "Attraction nomi bo'sh bolishi mumkin emas!")
    private String name;

    @NotBlank(message = "Attraction manzili bo'sh bo'lishi mumkin emas!")
    private String address;

    @NotNull(message = "Latitude ko'rsatilishi shart")
    @Min(value = 37, message = "Latitude 37.0 dan kichik bolishi mumkin meas")
    @Max(value = 46, message = "Latitude 46 dan katta bolishi mumkin emas")
    private Double latitude;

    @NotNull(message = "Longitude ko'rsatilishi shart")
    @Min(value = 37, message = "Longitude 37.0 dan kichik bolishi mumkin meas")
    @Max(value = 46, message = "Longitude 46 dan katta bolishi mumkin emas")
    private Double longitude;

    @NotNull(message = "Kategoriya tanlanishi shart")
    private Category category;

    @NotNull(message = "Kirish narxi korsatilishi shart")
    @DecimalMin(value = "0.0", message = "Kirish narxo 0 dan kichik bolishi mumkin emas!")
    private BigDecimal entryFee;

}

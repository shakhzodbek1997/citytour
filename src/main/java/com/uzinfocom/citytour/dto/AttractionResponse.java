package com.uzinfocom.citytour.dto;

import com.uzinfocom.citytour.entity.enums.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AttractionResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Category category;
    private BigDecimal entryFee;
}

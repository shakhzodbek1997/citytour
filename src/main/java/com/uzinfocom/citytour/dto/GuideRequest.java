package com.uzinfocom.citytour.dto;


import com.uzinfocom.citytour.entity.enums.Language;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class GuideRequest {

    @NotBlank(message = "Ism-sharif bo'lishi mumkin emas")
    @Size(min = 3, max = 120, message = "Ism-sharif 3 dan 120 gachablegidan iborat bo'lishi kerak!")
    private String fullName;

    @NotBlank(message = "Tel raqami bo'sh bo'lishi mumkin emas!")
    @Pattern(regexp = "^\\\\+?[0-9]{9,15}$", message="Tel nomeri to'g'ri formatda bolishi kerak")
    private String phone;

    @NotNull(message = "Tilar ro'yxati bo'sh bo'lishi mumkin emas")
    @NotEmpty(message = "Gid kamida 1 ta tilni bilishi shart")
    private Set<Language> languages;

    @NotNull(message = "Ish tajribasi ko'rsatilishi kerak")
    @Min(value = 0, message = "ish tajribasi 0 dan katta bo'lishi kerak")
    private Integer experienceYears;

    private Boolean active;
}







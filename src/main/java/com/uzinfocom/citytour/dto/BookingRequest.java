package com.uzinfocom.citytour.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Tur ID-si ko'rsatilishi shart")
    private Long tourId;

    @NotBlank(message = "Mijoz ismi bo'sh bo'lishi mumkin emas")
    private String customerName;

    @NotBlank(message = "Mijoz telefon raqami bo'sh bo'lishi mumkin emas")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Telefon raqami to'g'ri formatda bo'lishi kerak")
    private String customerPhone;

    @NotNull(message = "O'rinlar soni ko'rsatilishi shart")
    @Min(value = 1, message = "Kamida 1 ta o'rin bron qilinishi kerak")
    @Max(value = 10, message = "Bir safarda ko'pi bilan 10 ta o'rin bron qilish mumkin")
    private Integer seats;
}

package com.uzinfocom.citytour.dto;

import com.uzinfocom.citytour.entity.enums.Language;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GuideResponse {
    private Long id;
    private String fullName;
    private String phone;
    private Set<Language> languages;
    private Integer experienceYears;
    private Boolean active;
}


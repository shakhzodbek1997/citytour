package com.uzinfocom.citytour.service;

import com.uzinfocom.citytour.dto.GuideRequest;
import com.uzinfocom.citytour.dto.GuideResponse;
import com.uzinfocom.citytour.entity.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GuideService {
    GuideResponse create(GuideRequest request);
    GuideResponse getById(Long id);
    Page<GuideResponse> getAll(
            Boolean active,
            Language language,
            Pageable pageable
            );
    GuideResponse update(Long id, GuideRequest request);
    void delete(Long id);
}

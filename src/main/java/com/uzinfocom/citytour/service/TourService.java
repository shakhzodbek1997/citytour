package com.uzinfocom.citytour.service;

import com.uzinfocom.citytour.dto.TourRequest;
import com.uzinfocom.citytour.dto.TourResponse;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TourService {

    TourResponse create(TourRequest request);

    TourResponse getById(Long id);

    Page<TourResponse> getAll(Long guideId, TourStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    TourResponse update(Long id, TourRequest request);

    void delete(Long id);

    TourResponse publishTour(Long id);

    TourResponse cancelTour(Long id);
}
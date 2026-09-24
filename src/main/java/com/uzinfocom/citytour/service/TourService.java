package com.uzinfocom.citytour.service;

import com.uzinfocom.citytour.dto.TourRequest;
import com.uzinfocom.citytour.dto.TourResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourService {

    TourResponse create(TourRequest request);
    TourResponse getById(Long id);
    Page<TourResponse> getAll(Pageable pageable);
    TourResponse update(Long id, TourRequest request);
    void delete(Long id);

}
package com.uzinfocom.citytour.service;

import com.uzinfocom.citytour.dto.AttractionRequest;
import com.uzinfocom.citytour.dto.AttractionResponse;

import java.util.List;

public interface AttractionService {
    AttractionResponse create(AttractionRequest request);
    List<AttractionResponse> getAll();
    AttractionResponse getById(Long id);
}
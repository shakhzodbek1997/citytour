package com.uzinfocom.citytour.service.impl;

import com.uzinfocom.citytour.dto.AttractionRequest;
import com.uzinfocom.citytour.dto.AttractionResponse;
import com.uzinfocom.citytour.entity.Attraction;
import com.uzinfocom.citytour.exception.ResourceNotFoundException;
import com.uzinfocom.citytour.repository.AttractionRepository;
import com.uzinfocom.citytour.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;

    @Override
    @Transactional
    public AttractionResponse create(AttractionRequest request) {
        Attraction attraction = Attraction.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .category(request.getCategory())
                .entryFee(request.getEntryFee())
                .build();

        Attraction savedAttraction = attractionRepository.save(attraction);
        return mapToResponse(savedAttraction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponse> getAll() {
        return attractionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttractionResponse getById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction topilmadi: id = " + id));
        return mapToResponse(attraction);
    }

    private AttractionResponse mapToResponse(Attraction attraction) {
        return AttractionResponse.builder()
                .id(attraction.getId())
                .name(attraction.getName())
                .address(attraction.getAddress())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .category(attraction.getCategory())
                .entryFee(attraction.getEntryFee())
                .build();
    }
}
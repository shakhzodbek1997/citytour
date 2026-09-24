package com.uzinfocom.citytour.service.impl;

import com.uzinfocom.citytour.dto.*;
import com.uzinfocom.citytour.entity.Attraction;
import com.uzinfocom.citytour.entity.Guide;
import com.uzinfocom.citytour.entity.Tour;
import com.uzinfocom.citytour.entity.TourStop;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import com.uzinfocom.citytour.exception.BusinessLogicException;
import com.uzinfocom.citytour.exception.ResourceNotFoundException;
import com.uzinfocom.citytour.repository.AttractionRepository;
import com.uzinfocom.citytour.repository.GuideRepository;
import com.uzinfocom.citytour.repository.TourRepository;
import com.uzinfocom.citytour.repository.TourStopRepository;
import com.uzinfocom.citytour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final GuideRepository guideRepository;
    private final AttractionRepository attractionRepository;
    private final TourStopRepository tourStopRepository;

    @Override
    @Transactional
    public TourResponse create(TourRequest request) {
        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Gid topilmadi: id = " + request.getGuideId()));

        if (!guide.getActive()) {
            throw new BusinessLogicException("Nofaol gidga yangi ekskursiya biriktirib bo'lmaydi!");
        }

        Tour tour = Tour.builder()
                .title(request.getTitle())
                .guide(guide)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxSeats(request.getMaxSeats())
                .pricePerSeat(request.getPricePerSeat())
                .status(TourStatus.DRAFT)
                .build();

        // TourStop (To'xtash joylari) mavjud bo'lsa, ularni tekshirib bog'laymiz
        if (request.getStops() != null && !request.getStops().isEmpty()) {
            List<TourStop> stops = request.getStops().stream().map(stopRequest -> {
                Attraction attraction = attractionRepository.findById(stopRequest.getAttractionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Attraction topilmadi: id = " + stopRequest.getAttractionId()));

                return TourStop.builder()
                        .tour(tour)
                        .attraction(attraction)
                        .visitOrder(stopRequest.getVisitOrder())
                        .stayMinutes(stopRequest.getStayMinutes())
                        .build();
            }).collect(Collectors.toList());

            tour.setStops(stops);
        }

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    @Override
    public TourResponse getById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));
        return mapToResponse(tour);
    }

    @Override
    public Page<TourResponse> getAll(Pageable pageable) {
        return tourRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TourResponse update(Long id, TourRequest request) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));

        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Gid topilmadi: id = " + request.getGuideId()));

        if (!guide.getActive()) {
            throw new BusinessLogicException("Nofaol gidga ekskursiya biriktirib bo'lmaydi!");
        }

        tour.setTitle(request.getTitle());
        tour.setGuide(guide);
        tour.setStartTime(request.getStartTime());
        tour.setEndTime(request.getEndTime());
        tour.setMaxSeats(request.getMaxSeats());
        tour.setPricePerSeat(request.getPricePerSeat());

        Tour updatedTour = tourRepository.save(tour);
        return mapToResponse(updatedTour);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id);
        }
        tourRepository.deleteById(id);
    }

    // Tour Entity -> TourResponse --> DTO mapping yordamchi metodi (kod takrorlanishining oldini olish uchun)
    private TourResponse mapToResponse(Tour tour) {
        GuideResponse guideResponse = GuideResponse.builder()
                .id(tour.getGuide().getId())
                .fullName(tour.getGuide().getFullName())
                .phone(tour.getGuide().getPhone())
                .languages(tour.getGuide().getLanguages())
                .experienceYears(tour.getGuide().getExperienceYears())
                .active(tour.getGuide().getActive())
                .build();
        List<TourStopResponse> stopResponses = null;
        if (tour.getStops() != null && !tour.getStops().isEmpty()) {
            stopResponses = tour.getStops().stream().map(stop -> {

                AttractionResponse attractionResponse = AttractionResponse.builder()
                        .id(stop.getAttraction().getId())
                        .name(stop.getAttraction().getName())
                        .address(stop.getAttraction().getAddress())
                        .latitude(stop.getAttraction().getLatitude())
                        .longitude(stop.getAttraction().getLongitude())
                        .category(stop.getAttraction().getCategory())
                        .entryFee(stop.getAttraction().getEntryFee())
                        .build();

                return TourStopResponse.builder()
                        .id(stop.getId())
                        .attraction(attractionResponse)
                        .visitOrder(stop.getVisitOrder())
                        .stayMinutes(stop.getStayMinutes())
                        .build();
            }).collect(Collectors.toList());
        }

        return TourResponse.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .guide(guideResponse)
                .startTime(tour.getStartTime())
                .endTime(tour.getEndTime())
                .maxSeats(tour.getMaxSeats())
                .pricePerSeat(tour.getPricePerSeat())
                .status(tour.getStatus())
                .stops(stopResponses)
                .build();
    }
}
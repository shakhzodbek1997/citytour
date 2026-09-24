package com.uzinfocom.citytour.service.impl;

import com.uzinfocom.citytour.dto.*;
import com.uzinfocom.citytour.entity.*;
import com.uzinfocom.citytour.entity.enums.BookingStatus;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import com.uzinfocom.citytour.exception.BusinessLogicException;
import com.uzinfocom.citytour.exception.ResourceNotFoundException;
import com.uzinfocom.citytour.repository.*;
import com.uzinfocom.citytour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final GuideRepository guideRepository;
    private final AttractionRepository attractionRepository;
    private final TourStopRepository tourStopRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public TourResponse create(TourRequest request) {
        Guide guide = guideRepository.findById(request.getGuideId())
                .orElseThrow(() -> new ResourceNotFoundException("Gid topilmadi: id = " + request.getGuideId()));

        if (!guide.getActive()) {
            throw new BusinessLogicException("Nofaol gidga yangi ekskursiya biriktirib bo'lmaydi!");
        }

        // BR-2: Gid bandligini tekshirish
        validateGuideAvailability(guide.getId(), null, request.getStartTime(), request.getEndTime());

        Tour tour = Tour.builder()
                .title(request.getTitle())
                .guide(guide)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxSeats(request.getMaxSeats())
                .pricePerSeat(request.getPricePerSeat())
                .status(TourStatus.DRAFT)
                .build();

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
    @Transactional(readOnly = true)
    public TourResponse getById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));
        return mapToResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TourResponse> getAll(Long guideId, TourStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        Specification<Tour> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (guideId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("guide").get("id"), guideId));
            }
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (dateFrom != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("startTime"), dateFrom));
            }
            if (dateTo != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("startTime"), dateTo));
            }
            return predicates;
        };

        return tourRepository.findAll(spec, pageable).map(this::mapToResponse);
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

        // BR-2: Gid bandligini tekshirish (o'zi uchun ushbu turni istisno qiladi)
        validateGuideAvailability(guide.getId(), id, request.getStartTime(), request.getEndTime());

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
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));

        if (tour.getStatus() != TourStatus.DRAFT) {
            throw new BusinessLogicException("Faqat DRAFT holatidagi turlarni o'chirish mumkin!");
        }

        List<Booking> bookings = bookingRepository.findByTourId(id);
        if (!bookings.isEmpty()) {
            throw new BusinessLogicException("Bronlari mavjud bo'lgan turni o'chirib bo'lmaydi!");
        }

        tourRepository.delete(tour);
    }

    @Override
    @Transactional
    public TourResponse publishTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));

        List<TourStop> stops = tour.getStops();

        // BR-1: Tur PUBLISHED bo'lishi uchun kamida 2 ta stop bo'lishi shart
        if (stops == null || stops.size() < 2) {
            throw new BusinessLogicException("Tur PUBLISHED bo'lishi uchun kamida 2 ta stop bo'lishi shart!");
        }

        // BR-1: visitOrder 1..N va takrorlanmasligi hamda bitta obyekt 2 marta uchramasligini tekshirish
        Set<Integer> orders = new HashSet<>();
        Set<Long> attractionIds = new HashSet<>();
        for (TourStop stop : stops) {
            if (!orders.add(stop.getVisitOrder())) {
                throw new BusinessLogicException("visitOrder qiymatlari takrorlanmas bo'lishi kerak!");
            }
            if (!attractionIds.add(stop.getAttraction().getId())) {
                throw new BusinessLogicException("Bitta obyekt (Attraction) bitta turda ikki marta uchramasin!");
            }
        }

        for (int i = 1; i <= stops.size(); i++) {
            if (!orders.contains(i)) {
                throw new BusinessLogicException("visitOrder qiymatlari 1 dan boshlab uzluksiz bo'lishi kerak!");
            }
        }

        // BR-8: Davomiylik nazorati (endTime - startTime >= sum(stayMinutes))
        long totalTourMinutes = Duration.between(tour.getStartTime(), tour.getEndTime()).toMinutes();
        int totalStayMinutes = stops.stream().mapToInt(TourStop::getStayMinutes).sum();

        if (totalTourMinutes < totalStayMinutes) {
            throw new BusinessLogicException("Turning umumiy vaqti barcha stayMinutes yig'indisidan kam bo'lmasligi kerak!");
        }

        tour.setStatus(TourStatus.PUBLISHED);
        return mapToResponse(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourResponse cancelTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ekskursiya topilmadi: id = " + id));

        tour.setStatus(TourStatus.CANCELLED);

        // BR-5: Tur CANCELLED bo'lsa, uning barcha CONFIRMED bronlari ham CANCELLED bo'ladi
        List<Booking> bookings = bookingRepository.findByTourId(id);
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }

        return mapToResponse(tourRepository.save(tour));
    }

    // BR-2 Yordamchi metod: Gid vaqti kesishishini tekshirish
    private void validateGuideAvailability(Long guideId, Long currentTourId, LocalDateTime start, LocalDateTime end) {
        List<Tour> guideTours = tourRepository.findAll().stream()
                .filter(t -> t.getGuide().getId().equals(guideId))
                .filter(t -> t.getStatus() != TourStatus.CANCELLED)
                .filter(t -> currentTourId == null || !t.getId().equals(currentTourId))
                .collect(Collectors.toList());

        for (Tour t : guideTours) {
            // Chegaralari tegib tursa (masalan 10:00-12:00 va 12:00-14:00) kesishgan hisoblanmaydi
            boolean isOverlap = start.isBefore(t.getEndTime()) && end.isAfter(t.getStartTime());
            if (isOverlap) {
                throw new BusinessLogicException("GUIDE_TIME_OVERLAP: Gid " + guideId + " ushbu vaqt oralig'ida band!");
            }
        }
    }

    private TourResponse mapToResponse(Tour tour) {
        int bookedSeats = bookingRepository.countBookedSeatsForTour(tour.getId(), BookingStatus.CONFIRMED);

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
                .bookedSeats(bookedSeats)
                .freeSeats(tour.getMaxSeats() - bookedSeats)
                .pricePerSeat(tour.getPricePerSeat())
                .status(tour.getStatus())
                .stops(stopResponses)
                .build();
    }
}
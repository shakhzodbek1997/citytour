package com.uzinfocom.citytour.service.impl;

import com.uzinfocom.citytour.dto.BookingRequest;
import com.uzinfocom.citytour.dto.BookingResponse;
import com.uzinfocom.citytour.entity.Booking;
import com.uzinfocom.citytour.entity.Tour;
import com.uzinfocom.citytour.entity.enums.BookingStatus;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import com.uzinfocom.citytour.exception.BusinessLogicException;
import com.uzinfocom.citytour.exception.ResourceNotFoundException;
import com.uzinfocom.citytour.repository.BookingRepository;
import com.uzinfocom.citytour.repository.TourRepository;
import com.uzinfocom.citytour.service.BookingService;
import com.uzinfocom.citytour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final TourService tourService; // TOurResponsega map qilish uchun

    @Override
    @Transactional
    public BookingResponse createBooking(Long tourId, BookingRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tur topilmadi: id = " + tourId));

        // BR-4: Bron faqat PUBLISHED turga va faqat tur boshlanishidan oldin qilinadi
        if (tour.getStatus() != TourStatus.PUBLISHED) {
            throw new BusinessLogicException("Faqat PUBLISHED holatidagi turlarga bron qilish mumkin!");
        }
        if (tour.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Boshlanib bolgan turga bron qilib bolmaydi!");
        }

        // BR-3: Joy sigimi nzorati
        int currentBookedSeats = bookingRepository.countBookedSeatsForTour(tourId, BookingStatus.CONFIRMED);
        int availableSeats = tour.getMaxSeats() - currentBookedSeats;

        if (request.getSeats() > availableSeats) {
            throw new BusinessLogicException("NOT_ENOUGH_SEATS: Yetarli bosh orin yoq. Qolgan bosh orinlar: " + availableSeats);
        }

        // BR-6: Narxni hisoblash: seats * (pricePerSeat + stops.entryFee sum)
        BigDecimal stopsEntryFeeSum = (tour.getStops() == null) ? BigDecimal.ZERO :
                tour.getStops().stream()
                        .map(stop -> stop.getAttraction().getEntryFee() != null ? stop.getAttraction().getEntryFee() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pricePerPerson = tour.getPricePerSeat().add(stopsEntryFeeSum);
        BigDecimal totalPrice = pricePerPerson.multiply(BigDecimal.valueOf(request.getSeats()));

        Booking booking = Booking.builder()
                .tour(tour)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .seats(request.getSeats())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByTour(Long tourId, BookingStatus status) {
        if (!tourRepository.existsById(tourId)) {
            throw new ResourceNotFoundException("Tur topilmadi: id = " + tourId);
        }

        List<Booking> bookings = (status != null) ?
                bookingRepository.findByTourId(tourId).stream()
                        .filter(b -> b.getStatus() == status)
                        .collect(Collectors.toList()) :
                bookingRepository.findByTourId(tourId);

        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bron topilmadi: id = " + bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .tour(tourService.getById(booking.getTour().getId()))
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .seats(booking.getSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
package com.uzinfocom.citytour.controller;

import com.uzinfocom.citytour.dto.BookingRequest;
import com.uzinfocom.citytour.dto.BookingResponse;
import com.uzinfocom.citytour.entity.enums.BookingStatus;
import com.uzinfocom.citytour.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/tours/{tourId}/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable Long tourId,
            @Valid
            @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(tourId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tours/{tourId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByTour(
            @PathVariable Long tourId,
            @RequestParam(required = false) BookingStatus status) {
        List<BookingResponse> bookings = bookingService.getBookingsByTour(tourId, status);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build(); // 204 No Content statusni qaytaradi
    }
}
package com.uzinfocom.citytour.service;

import com.uzinfocom.citytour.dto.BookingRequest;
import com.uzinfocom.citytour.dto.BookingResponse;
import com.uzinfocom.citytour.entity.enums.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(Long tourId, BookingRequest request);

    List<BookingResponse> getBookingsByTour(Long tourId, BookingStatus status);

    void cancelBooking(Long bookingId);
}
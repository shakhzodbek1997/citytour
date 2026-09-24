package com.uzinfocom.citytour.repository;

import com.uzinfocom.citytour.entity.Booking;
import com.uzinfocom.citytour.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTourId(Long tourId);

    List<Booking> findByCustomerPhone(String customerPhone);

    @Query("SELECT COALESCE(SUM(b.seats), 0) FROM Booking b " +
           "WHERE b.tour.id = :tourId AND b.status =:status")
    Integer countBookedSeatsForTour(
            @Param("tourId") Long tourId,
            @Param("status")BookingStatus status);
}

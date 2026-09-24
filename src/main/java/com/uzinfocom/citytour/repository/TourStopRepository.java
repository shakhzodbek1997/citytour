package com.uzinfocom.citytour.repository;

import com.uzinfocom.citytour.entity.TourStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourStopRepository extends JpaRepository<TourStop, Long> {
    List<TourStop> findByTourIdOrderByVisitOrderAsc(Long tourId); // to get all stops related to exact TOUR
    void deleteByTourId(Long tourId);

}

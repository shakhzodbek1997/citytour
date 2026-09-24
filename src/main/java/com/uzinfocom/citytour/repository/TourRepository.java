package com.uzinfocom.citytour.repository;

import com.uzinfocom.citytour.entity.Tour;
import com.uzinfocom.citytour.entity.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    List<Tour> findByStatus(TourStatus status);
    List<Tour> findByGuideId(Long guideId);

    @Query("SELECT t FROM Tour t WHERE t.guide.id = :guideId " +
            "AND t.status != 'CANCELLED' " +
            "AND ((t.startTime < :endTime AND t.endTime > :startTime))")
    List<Tour> findConflictingTours(@Param("guideId") Long guideId,
                                    @Param("startTime")LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}

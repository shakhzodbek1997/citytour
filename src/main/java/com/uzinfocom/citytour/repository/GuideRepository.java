package com.uzinfocom.citytour.repository;


import com.uzinfocom.citytour.entity.Guide;
import com.uzinfocom.citytour.entity.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    boolean existsByPhone(String phone);
    @Query("SELECT DISTINCT g FROM Guide g LEFT JOIN g.languages l " +
            "WHERE (:active IS NULL OR g.active = :active) " +
            "AND (:language IS NULL OR l = :language)")
    Page<Guide> findAllWithFilters(@Param("active") Boolean active,
                                   @Param("language") Language language,
                                   Pageable pageable);
    Optional<Guide> findByPhone(String phone);
}



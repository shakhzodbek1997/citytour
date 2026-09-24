package com.uzinfocom.citytour.repository;


import com.uzinfocom.citytour.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    boolean existsByPhone(String phone);
    Optional<Guide> findByPhone(String phone);
}



package com.uzinfocom.citytour.repository;


import com.uzinfocom.citytour.entity.Attraction;
import com.uzinfocom.citytour.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    boolean existsByName(String name);
    List<Attraction> findByCategory(Category category);
}

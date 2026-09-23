package com.uzinfocom.citytour.entity;

import com.uzinfocom.citytour.entity.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "attractions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String address;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private BigDecimal entryFee;
}

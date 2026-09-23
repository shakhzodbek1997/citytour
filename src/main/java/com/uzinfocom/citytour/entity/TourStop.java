package com.uzinfocom.citytour.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_stops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    @Column(nullable = false)
    private Integer visitOrder;

    @Column(nullable = false)
    private Integer stayMinutes;
}

package com.uzinfocom.citytour.entity;

import com.uzinfocom.citytour.entity.enums.TourStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer maxSeats;

    @Column(nullable = false)
    private BigDecimal pricePerSeat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourStop> stops = new ArrayList<>();
}

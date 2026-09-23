package com.uzinfocom.citytour.entity;


import com.uzinfocom.citytour.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(nullable = false, length = 120)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String customerPhone;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt= Instant.now();
    }
}

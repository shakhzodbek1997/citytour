package com.uzinfocom.citytour.entity;


import com.uzinfocom.citytour.entity.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "guide_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Set<Language> languages = new HashSet<>();

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private Boolean active = true;
}

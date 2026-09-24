package com.uzinfocom.citytour.controller;

import com.uzinfocom.citytour.dto.TourRequest;
import com.uzinfocom.citytour.dto.TourResponse;
import com.uzinfocom.citytour.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourResponse> create(@Valid @RequestBody TourRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TourResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(tourService.getAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourResponse> update(@PathVariable Long id, @Valid @RequestBody TourRequest request) {
        return ResponseEntity.ok(tourService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
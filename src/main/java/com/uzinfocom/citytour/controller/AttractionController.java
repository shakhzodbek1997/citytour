package com.uzinfocom.citytour.controller;

import com.uzinfocom.citytour.dto.AttractionRequest;
import com.uzinfocom.citytour.dto.AttractionResponse;
import com.uzinfocom.citytour.service.AttractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    @PostMapping
    public ResponseEntity<AttractionResponse> create(@Valid @RequestBody AttractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attractionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AttractionResponse>> getAll() {
        return ResponseEntity.ok(attractionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttractionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attractionService.getById(id));
    }
}
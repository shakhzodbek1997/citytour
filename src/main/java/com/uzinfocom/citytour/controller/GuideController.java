package com.uzinfocom.citytour.controller;

import com.uzinfocom.citytour.dto.GuideRequest;
import com.uzinfocom.citytour.dto.GuideResponse;
import com.uzinfocom.citytour.entity.enums.Language;
import com.uzinfocom.citytour.service.GuideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping
    public ResponseEntity<GuideResponse> create(@Valid @RequestBody GuideRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guideService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<GuideResponse>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Language language,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(guideService.getAll(active, language, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GuideRequest request) {
        return ResponseEntity.ok(guideService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
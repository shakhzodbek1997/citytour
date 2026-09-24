package com.uzinfocom.citytour.service.impl;


import com.uzinfocom.citytour.dto.GuideRequest;
import com.uzinfocom.citytour.dto.GuideResponse;
import com.uzinfocom.citytour.entity.Guide;
import com.uzinfocom.citytour.entity.enums.Language;
import com.uzinfocom.citytour.exception.BusinessLogicException;
import com.uzinfocom.citytour.exception.ResourceNotFoundException;
import com.uzinfocom.citytour.repository.GuideRepository;
import com.uzinfocom.citytour.repository.TourRepository;
import com.uzinfocom.citytour.service.GuideService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideServiceImpl  implements GuideService {
    private final GuideRepository guideRepository;
    private final TourRepository tourRepository;

    @Override
    @Transactional
    public GuideResponse create(GuideRequest request){
        // tel nomer unikalligni tekshirish
        if(guideRepository.existsByPhone(request.getPhone())){
            throw new BusinessLogicException("Ushbu telefon raqamli gid allqachon mavjud " + request.getPhone());
        }

        Guide guide = new Guide();
        guide.setFullName(request.getFullName());
        guide.setPhone(request.getPhone());
        guide.setLanguages(request.getLanguages());
        guide.setExperienceYears(request.getExperienceYears());
        guide.setActive(request.getActive() != null ? request.getActive() : true);

        Guide savedGuide = guideRepository.save(guide); // to store DB guide object

        return GuideResponse.builder()
                .id(savedGuide.getId())
                .fullName(savedGuide.getFullName())
                .phone(savedGuide.getPhone())
                .languages(savedGuide.getLanguages())
                .experienceYears(savedGuide.getExperienceYears())
                .active(savedGuide.getActive())
                .build();
    }

    public GuideResponse getById(Long id){
        Guide guide = guideRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Gid Topilmadi: id = " + id));

        return GuideResponse.builder()
                .id(guide.getId())
                .fullName(guide.getFullName())
                .phone(guide.getPhone())
                .languages(guide.getLanguages())
                .experienceYears(guide.getExperienceYears())
                .active(guide.getActive())
                .build();
    }

    @Override
    public Page<GuideResponse> getAll(
            Boolean active,
            Language language,
            Pageable pageable) {

        Page<Guide> guidePage = guideRepository.findAllWithFilters(active, language, pageable);

        return guidePage.map(guide -> GuideResponse.builder()
                .id(guide.getId())
                .fullName(guide.getFullName())
                .phone(guide.getPhone())
                .languages(guide.getLanguages())
                .experienceYears(guide.getExperienceYears())
                .active(guide.getActive())
                .build());
    }

    @Override
    @Transactional
    public GuideResponse update(Long id, GuideRequest request) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gid topilmadi: id = " + id));
        if (!guide.getPhone().equals(request.getPhone()) && guideRepository.existsByPhone(request.getPhone())) {
            throw new BusinessLogicException("Ushbu telefon raqamli gid allaqachon mavjud: " + request.getPhone());
        }
        guide.setFullName(request.getFullName());
        guide.setPhone(request.getPhone());
        guide.setLanguages(request.getLanguages());
        guide.setExperienceYears(request.getExperienceYears());
        guide.setActive(request.getActive());

        Guide updatedGuide = guideRepository.save(guide);

        return GuideResponse.builder()
                .id(updatedGuide.getId())
                .fullName(updatedGuide.getFullName())
                .phone(updatedGuide.getPhone())
                .languages(updatedGuide.getLanguages())
                .experienceYears(updatedGuide.getExperienceYears())
                .active(updatedGuide.getActive())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gid topilmadi: id = " + id));

        guideRepository.delete(guide);
    }

}

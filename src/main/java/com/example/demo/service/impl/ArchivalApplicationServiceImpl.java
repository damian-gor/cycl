package com.example.demo.service.impl;

import com.example.demo.dto.ArchivalApplicationDTO;
import com.example.demo.model.ArchivalApplication;
import com.example.demo.repository.ArchivalApplicationRepository;
import com.example.demo.service.ApplicationService;
import com.example.demo.service.ArchivalApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchivalApplicationServiceImpl implements ArchivalApplicationService {

    @Autowired
    private ArchivalApplicationRepository archivalApplicationRepository;
    @Autowired
    private ApplicationService applicationService;

    @Override
    public List<ArchivalApplicationDTO> getArchivalApplicationById(Long id) {
        applicationService.getApplication(id);
        return archivalApplicationRepository.findByApplicationId(id)
                .stream()
                .map(aa -> ArchivalApplicationDTO.builder()
                        .status(aa.getStatus())
                        .statusDate(aa.getStatusDate())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Utworzenie historii dla zmiany statusu aplikacji
     *
     * @param applicationId
     * @param status
     * @param statusDate
     */
    @Override
    public void createArchiveApplication(Long applicationId, String status, Date statusDate) {
        ArchivalApplication archivalApplication = ArchivalApplication.builder()
                .applicationId(applicationId)
                .status(status)
                .statusDate(statusDate)
                .build();
        archivalApplicationRepository.save(archivalApplication);
    }
}

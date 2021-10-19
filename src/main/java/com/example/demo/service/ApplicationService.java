package com.example.demo.service;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.model.Application;

import java.util.List;

public interface ApplicationService {
    List<ApplicationDTO> getApplications(Integer number, Integer page, String status, String name);

    ApplicationDTO getApplicationDTOById(Long id);

    ApplicationDTO addApplication(ApplicationDTO applicationDTO);

    ApplicationDTO updateApplicationBody(ApplicationDTO applicationDTO);

    void deleteApplication(ApplicationDTO applicationDTO);

    ApplicationDTO updateApplicationStatus(ApplicationDTO applicationDTO);

    Application getApplication(Long id);
}

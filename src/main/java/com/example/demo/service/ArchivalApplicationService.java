package com.example.demo.service;

import com.example.demo.dto.ArchivalApplicationDTO;

import java.util.Date;
import java.util.List;

public interface ArchivalApplicationService {

    List<ArchivalApplicationDTO> getArchivalApplicationById(Long id);

    void createArchiveApplication(Long applicationId, String status, Date statusDate);

}

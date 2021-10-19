package com.example.demo.service.impl;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.enums.Status;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.MissingRequestParameterException;
import com.example.demo.model.Application;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.service.ApplicationService;
import com.example.demo.service.ArchivalApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ArchivalApplicationService archivalApplicationService;
    @Value("${default-paginaton.page-size:10}")
    private Integer defaultPageSize;
    @Value("${default-paginaton.page-number:0}")
    private Integer defaultPageNumber;

    @Override
    public List<ApplicationDTO> getApplications(Integer pageNumber, Integer pageSize, String status, String name) {
        Pageable pageable = setPagination(pageNumber, pageSize);
        Page<Application> applicationPage;
        Status statusEnum = null;
        if (status != null) {
            statusEnum = Status.valueOf(status.toUpperCase());
        }
        applicationPage = applicationRepository.findByNameOrStatus(name, statusEnum, pageable);
        List<Application> applicationList = applicationPage.getContent();
        List<ApplicationDTO> applicationDTOList = applicationList.stream()
                .map(this::applicationToDTO)
                .collect(Collectors.toList());
        return applicationDTOList;
    }

    /**
     * Ustawienie parametrów paginacji - w przypadku nieokreślenia w zapytaniu, przymowane są defaultowe wartości
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    private Pageable setPagination(Integer pageNumber, Integer pageSize) {
        if (pageSize == null) {
            pageSize = defaultPageSize;
        }
        if (pageNumber == null) {
            pageNumber = defaultPageNumber;
        }
        return PageRequest.of(pageNumber, pageSize);
    }

    @Override
    public ApplicationDTO getApplicationDTOById(Long id) {
        Application application = getApplication(id);
        return applicationToDTO(application);
    }


    @Override
    public ApplicationDTO addApplication(ApplicationDTO applicationDTO) {
        if (applicationDTO.getName() == null || applicationDTO.getName()
                .length() == 0) {
            throw new MissingRequestParameterException("ApplicationDTO.name", "String");
        }
        if (applicationDTO.getBody() == null || applicationDTO.getBody()
                .length() == 0) {
            throw new MissingRequestParameterException("ApplicationDTO.body", "String");
        }
        Application application = Application.builder()
                .name(applicationDTO.getName())
                .body(applicationDTO.getBody())
                .status(Status.CREATED)
                .build();
        application = applicationRepository.save(application);
        return applicationToDTO(application);
    }

    @Override
    public ApplicationDTO updateApplicationBody(ApplicationDTO applicationDTO) {
        if (applicationDTO.getBody() == null || applicationDTO.getBody()
                .length() == 0) {
            throw new MissingRequestParameterException("ApplicationDTO.body", "String");
        }
        Application application = getApplication(applicationDTO.getId());
        if (!application.getStatus()
                .isEditableBody()) {
            throw new BadRequestException("Wniosek o statusie " + application.getStatus()
                    .name() + " nie podlega edycji.");
        }
        application.setBody(applicationDTO.getBody());
        application = applicationRepository.save(application);
        return applicationToDTO(application);
    }

    @Override
    public void deleteApplication(ApplicationDTO applicationDTO) {
        Application application = getApplication(applicationDTO.getId());
        if (!application.getStatus()
                .equals(Status.CREATED)) {
            throw new BadRequestException("Wniosek o statusie innym niż " + Status.CREATED + " nie może zostać usunięty." +
                    " Wybrany wniosek posiada status: " + application.getStatus()
                    .name()
            );
        }
        checkIfApplicationDTOHasReason(applicationDTO);
        archivalApplicationService.createArchiveApplication(application.getId(), application.getStatus()
                .name(), application.getModificationDate());
        application.setReason(applicationDTO.getReason());
        application.setStatus(Status.DELETED);
        applicationRepository.save(application);
    }

    @Override
    public ApplicationDTO updateApplicationStatus(ApplicationDTO applicationDTO) {
        Application application = getApplication(applicationDTO.getId());
        if (!application.getStatus()
                .isEditableStatus()) {
            throw new BadRequestException("Status " + application.getStatus()
                    .name() + " nie podlega zmianie.");
        }
        if (applicationDTO.getStatus() == null || applicationDTO.getStatus()
                .length() == 0) {
            throw new MissingRequestParameterException("ApplicationDTO.status", "String");
        }

        /**
         * CREATED -> VERIFIED
         */
        if (applicationDTO.getStatus()
                .equalsIgnoreCase(Status.VERIFIED.name())) {
            if (!application.getStatus()
                    .equals(Status.CREATED)) {
                throw new BadRequestException("Tylko wniosek o statusie "
                        + Status.CREATED.name()
                        + " może zmienić status na "
                        + Status.VERIFIED.name());
            }
            archivalApplicationService.createArchiveApplication(application.getId(), application.getStatus()
                    .name(), application.getModificationDate());
            application.setStatus(Status.VERIFIED);
        }
        /**
         * VERIFIED -> ACCEPTED
         */
        else if (applicationDTO.getStatus()
                .equalsIgnoreCase(Status.ACCEPTED.name())) {
            if (!application.getStatus()
                    .equals(Status.VERIFIED)) {
                throw new BadRequestException("Tylko wniosek o statusie "
                        + Status.VERIFIED.name()
                        + " może zmienić status na "
                        + Status.ACCEPTED.name());
            }
            archivalApplicationService.createArchiveApplication(application.getId(), application.getStatus()
                    .name(), application.getModificationDate());
            application.setStatus(Status.ACCEPTED);
        }
        /**
         * ACCEPTED -> PUSHED
         */
        else if (applicationDTO.getStatus()
                .equalsIgnoreCase(Status.PUBLISHED.name())) {
            if (!application.getStatus()
                    .equals(Status.ACCEPTED)) {
                throw new BadRequestException("Tylko wniosek o statusie "
                        + Status.ACCEPTED.name()
                        + " może zmienić status na "
                        + Status.PUBLISHED.name());
            }
            archivalApplicationService.createArchiveApplication(application.getId(), application.getStatus()
                    .name(), application.getModificationDate());
            application.setStatus(Status.PUBLISHED);
            application.setUniqueNumber(
                    applicationRepository.selectMaxUniqueNumber() == null ? 1L : applicationRepository.selectMaxUniqueNumber() + 1
            );
        }
        /**
         * VERIFIED / ACCEPTED -> REJECTED
         */
        else if (applicationDTO.getStatus()
                .equalsIgnoreCase(Status.REJECTED.name())) {
            if (!application.getStatus()
                    .equals(Status.ACCEPTED) || !application.getStatus()
                    .equals(Status.VERIFIED)) {
                throw new BadRequestException("Tylko wniosek o statusie "
                        + Status.ACCEPTED.name() + " lub " + Status.VERIFIED.name()
                        + " może zmienić status na "
                        + Status.REJECTED.name());
            }
            checkIfApplicationDTOHasReason(applicationDTO);
            archivalApplicationService.createArchiveApplication(application.getId(), application.getStatus()
                    .name(), application.getModificationDate());
            application.setStatus(Status.REJECTED);
            application.setReason(applicationDTO.getReason());
        }
        application = applicationRepository.save(application);
        return applicationToDTO(application);
    }

    /**
     * Wyszukanie aplikacji w bazie danych po ID
     *
     * @param id
     * @return
     */
    public Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono wniosku o id: " + id));
    }

    /**
     * Sprawdzenie czy w applicationDTO podano parametr 'reason'
     *
     * @param applicationDTO
     */
    private void checkIfApplicationDTOHasReason(ApplicationDTO applicationDTO) {
        if (applicationDTO.getReason() == null || applicationDTO.getReason()
                .length() == 0) {
            throw new MissingRequestParameterException("ApplicationDTO.reason", "String");
        }
    }


    /**
     * MAPPER
     */
    private ApplicationDTO applicationToDTO(Application a) {
        return ApplicationDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .body(a.getBody())
                .reason(a.getReason())
                .status(a.getStatus()
                        .name())
                .uniqueNumber(a.getUniqueNumber())
                .build();
    }

}

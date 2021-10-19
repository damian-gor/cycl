package com.example.demo.controller;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.dto.ArchivalApplicationDTO;
import com.example.demo.service.ApplicationService;
import com.example.demo.service.ArchivalApplicationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ArchivalApplicationService archivalApplicationService;


    @GetMapping
    @ApiOperation(value = "Zwraca listę wniosków",
            notes = "Możliwość określenia wielkości strony, numeru strony, filtra",
            response = ApplicationDTO.class)
    public ResponseEntity<List<ApplicationDTO>> getApplications(
            @RequestParam(name = "pageNumber", required = false) @ApiParam(value = "Numer strony, defaultowo 0") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false) @ApiParam(value = "Wielkość strony, defaultowo 10") Integer pageSize,
            @RequestParam(name = "status", required = false) @ApiParam(value = "Filtrowanie po statusie, defaultowo brak") String status,
            @RequestParam(name = "name", required = false) @ApiParam(value = "Filtrowanie po nazwie wniosku, defaultowo brak") String name
    ) {
        return ResponseEntity.ok(applicationService.getApplications(pageNumber, pageSize, status, name));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Zwraca wniosek o wskazanym ID",
            response = ApplicationDTO.class)
    public ResponseEntity<ApplicationDTO> getApplication(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(applicationService.getApplicationDTOById(id));
    }

    @GetMapping("/{id}/history")
    @ApiOperation(value = "Zwraca historię zmian statusu wniosku o wskazanym ID",
            response = ApplicationDTO.class)
    public ResponseEntity<List<ArchivalApplicationDTO>> getArchivalApplication(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(archivalApplicationService.getArchivalApplicationById(id));
    }

    @PostMapping
    @ApiOperation(value = "Tworzenie nowego wniosku",
            response = ApplicationDTO.class)
    @ResponseBody
    public ResponseEntity<ApplicationDTO> addApplication(@RequestBody ApplicationDTO applicationDTO) {
        return ResponseEntity.ok(applicationService.addApplication(applicationDTO));
    }

    @ApiOperation(value = "Edycja treści wniosku o wskazanym ID",
            response = ApplicationDTO.class)
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationDTO> updateApplicationBody(@RequestBody ApplicationDTO applicationDTO) {
        return ResponseEntity.ok(applicationService.updateApplicationBody(applicationDTO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Usunięcie wniosku o wskazanym ID",
            notes = "Konieczne jest podanie powodu usunięcia wniosku",
            response = ApplicationDTO.class)
    public ResponseEntity<HttpStatus> deleteApplication(@PathVariable(name = "id") Long id, @RequestBody ApplicationDTO applicationDTO) {
        applicationDTO.setId(id);
        applicationService.deleteApplication(applicationDTO);
        return ResponseEntity.ok()
                .build();
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Zmiana statusu wniosku. Dostępne wartości statusu wniosku: CREATED, DELETED, VERIFIED, ACCEPTED, REJECTED, PUSHED",
            notes = "W przypadku odrzucenia wniosku konieczne jest podanie powodu.",
            response = ApplicationDTO.class)
    public ResponseEntity<ApplicationDTO> updateApplicatinStatus(@RequestBody ApplicationDTO applicationDTO) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationDTO));
    }


}

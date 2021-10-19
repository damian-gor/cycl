package com.example.demo;

import com.example.demo.controller.ApplicationController;
import com.example.demo.dto.ApplicationDTO;
import com.example.demo.dto.ArchivalApplicationDTO;
import com.example.demo.exception.MissingRequestParameterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class ApplicationManagerApplicationTests {

    @Autowired
    ApplicationController applicationController;

    @Test
    public void test001addApplication() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .name("Nazwa wniosku 1 zzzz")
                .body("Tesć wniosku tralalala")
                .build();
        ApplicationDTO applicationDTORestponse = applicationController.addApplication(applicationDTO)
                .getBody();
        Assertions.assertNotNull(applicationDTORestponse);
    }

    /**
     * Dodawanie wielu wniosków, poprawne działanie paginacji
     */
    @Test
    public void test002addApplication_multiple() {
        for (int i = 2; i < 15; i++) {
            applicationController.addApplication(ApplicationDTO.builder()
                    .name("Wniosek nr " + i)
                    .body("Tesć wniosku tralalala " + i)
                    .build());
        }
        Assertions.assertEquals(Objects.requireNonNull(applicationController.getApplications(null, null, null, null)
                .getBody())
                .size(), 10);
        Assertions.assertEquals(Objects.requireNonNull(applicationController.getApplications(1, 4, null, null)
                .getBody())
                .size(), 4);
    }

    /**
     * Pobranie wniosku o wskazanym ID
     */
    @Test
    public void test003getApplication() {
        Assertions.assertNotNull(Objects.requireNonNull(applicationController.getApplication(3L)
                .getBody())
                .getName());
    }

    /**
     * Sprawdzenie możliwości edycji treści wniosku, ale nie nazwy
     */
    @Test
    public void test004editWniosek() {
        ApplicationDTO applicationDTO = applicationController.getApplication(4L)
                .getBody();
        Assertions.assertNotNull(applicationDTO);
        applicationDTO.setBody("Edytowana nazwa wniosku");
        applicationDTO.setName("Nazwy nie mozna edytowac");
        applicationDTO = applicationController.updateApplicationBody(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        Assertions.assertEquals("Edytowana nazwa wniosku", applicationDTO.getBody());
        Assertions.assertNotEquals("Nazwy nie mozna edytowac", applicationDTO.getName());
    }

    /**
     * Wyrzucenie bledu przy probie usuniecia wniosku bez podania przyczyny
     */
    @Test
    public void test005updateApplicatinStatus_bladPrzyUsunieciuBezPodaniaPowodu() {
        ApplicationDTO applicationDTODeleted = applicationController.getApplication(5L)
                .getBody();
        applicationDTODeleted.setStatus("DELETED");
        Assertions.assertThrows(MissingRequestParameterException.class,
                () -> applicationController.deleteApplication(5L, applicationDTODeleted));
    }

    /**
     * Poprawe usuniecie wniosku
     */
    @Test
    public void test006updateApplicatinStatus_usuwanie() {
        ApplicationDTO applicationDTODeleted = applicationController.getApplication(5L)
                .getBody();
        applicationDTODeleted.setStatus("DELETED");
        applicationDTODeleted.setReason("testowy powod");
        Assertions.assertDoesNotThrow(() -> applicationController.deleteApplication(5L, applicationDTODeleted));
    }


    /**
     * Poprawne działanie wyszukiwania po statusie lub nazwie
     */
    @Test
    public void test007getApplications_filtrowanie() {
        Assertions.assertEquals(Objects.requireNonNull(applicationController.getApplications(null, null, "DELETED", null)
                .getBody())
                .size(), 1);
        Assertions.assertEquals(Objects.requireNonNull(applicationController.getApplications(null, null, "CREATED", "Nazwa wniosku 1 zzzz")
                .getBody())
                .size(), 1);
    }

    /**
     * Przejście wniosku przez wszystkie pozytywne statusy: created -> verified -> accepted -> published,
     * oraz potwierdzenie nadania unikatowego numeru
     */
    @Test
    public void test008_updateApplicatinStatus_publishedApplication() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .name("Wniosek do publikacji")
                .body("Tesć wniosku 123")
                .build();
        applicationDTO = applicationController.addApplication(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        Assertions.assertEquals("CREATED", applicationDTO.getStatus());
        // created -> verified
        applicationDTO.setStatus("VERIFIED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        Assertions.assertEquals("VERIFIED", applicationDTO.getStatus());
        // verified -> accepted
        applicationDTO.setStatus("ACCEPTED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        Assertions.assertEquals("ACCEPTED", applicationDTO.getStatus());
        // accepted -> published
        applicationDTO.setStatus("PUBLISHED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        Assertions.assertEquals("PUBLISHED", applicationDTO.getStatus());
        Assertions.assertEquals(1L, applicationDTO.getUniqueNumber());
    }

    /**
     * Sprawdzenie czy podczas zmmiany statusów odkładają się rekordy archiwalnych statusów wniosku
     */
    @Test
    public void test009_getArchivalApplicationById() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .name("Wniosek do archiwizacji")
                .body("Tesć wniosku 12345")
                .build();
        applicationDTO = applicationController.addApplication(applicationDTO)
                .getBody();
        // created -> verified
        assert applicationDTO != null;
        applicationDTO.setStatus("VERIFIED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        // verified -> accepted
        assert applicationDTO != null;
        applicationDTO.setStatus("ACCEPTED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        // accepted -> published
        assert applicationDTO != null;
        applicationDTO.setStatus("PUBLISHED");
        applicationDTO = applicationController.updateApplicatinStatus(applicationDTO)
                .getBody();
        assert applicationDTO != null;
        List<ArchivalApplicationDTO> archivalApplicationDTOList =
                applicationController.getArchivalApplication(applicationDTO.getId()).getBody();
        assert archivalApplicationDTOList != null;
        Assertions.assertEquals(3, archivalApplicationDTOList.size());
    }
}

package com.example.demo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivalApplicationDTO {
    @ApiModelProperty(value = "Dostępne wartości statusu wniosku: CREATED, DELETED, VERIFIED, ACCEPTED, REJECTED, PUSHED")
    private String status;
    private Date statusDate;
}

package com.example.demo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDTO {
    private Long id;
    private String name;
    private String body;
    private String reason;
    @ApiModelProperty(value = "Dostępne wartości statusu wniosku: CREATED, DELETED, VERIFIED, ACCEPTED, REJECTED, PUSHED")
    private String status;
    private Long uniqueNumber;
}

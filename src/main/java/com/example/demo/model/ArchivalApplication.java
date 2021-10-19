package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ArchivalApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long archivalApplicationId;
    private Long applicationId;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;
}

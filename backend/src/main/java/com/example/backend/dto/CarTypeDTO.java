package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarTypeDTO {
    private String tNo;
    private String tName;
    private BigDecimal rentRate;
    private String imageUrl;
}

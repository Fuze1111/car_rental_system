package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 车辆维修记录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRepairRequestDTO {

    /**
     * 维修日期（接受字符串，支持 yyyy-MM-dd 或 yyyy-MM-dd'T'HH:mm:ss）
     */
    private String rDate;

    /**
     * 运行公里
     */
    private BigDecimal runKm;

    /**
     * 运行小时
     */
    private BigDecimal runHour;

    /**
     * 车辆号
     */
    private String carId;

    /**
     * 尝试将 rDate 解析为 LocalDateTime，支持多种常见格式
     */
    public LocalDateTime parseRDateToLocalDateTime() {
        if (this.rDate == null || this.rDate.isBlank()) return null;
        String s = this.rDate.trim();
        // Try ISO_LOCAL_DATE_TIME first
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ex) {
            // try date only
            try {
                LocalDate d = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
                return d.atStartOfDay();
            } catch (DateTimeParseException ex2) {
                // try common pattern without T: yyyy-MM-dd HH:mm:ss
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    return LocalDateTime.parse(s, f);
                } catch (DateTimeParseException ex3) {
                    // give up
                    return null;
                }
            }
        }
    }
}

package com.example.backend.controller;

import com.example.backend.entity.Car;
import com.example.backend.entity.CarType;
import com.example.backend.entity.Rental;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.CarTypeRepository;
import com.example.backend.repository.RentalRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报表导出控制器
 * 提供管理员控制台使用的 Excel 报表导出接口
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 导出财务营收报表（基于 rental 表的所有记录）
     */
    @GetMapping(value = "/financial", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportFinancialReport() throws IOException {
        List<Rental> rentals = rentalRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial");

        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        int col = 0;
        header.createCell(col++).setCellValue("发票号");
        header.createCell(col++).setCellValue("借车日期");
        header.createCell(col++).setCellValue("还车日期");
        header.createCell(col++).setCellValue("借车地点");
        header.createCell(col++).setCellValue("还车地点");
        header.createCell(col++).setCellValue("车牌号");
        header.createCell(col++).setCellValue("客户号");
        header.createCell(col++).setCellValue("司机号");
        header.createCell(col++).setCellValue("往返里程(km)");
        header.createCell(col++).setCellValue("行驶时间(小时)");
        header.createCell(col++).setCellValue("应付金额(元)");

        for (Rental r : rentals) {
            Row row = sheet.createRow(rowIdx++);
            int c = 0;
            row.createCell(c++).setCellValue(nullSafe(r.getInvoiceNo()));
            row.createCell(c++).setCellValue(formatDateTime(r.getLoanDate()));
            row.createCell(c++).setCellValue(formatDateTime(r.getReturnDate()));
            row.createCell(c++).setCellValue(nullSafe(r.getLoanPlace()));
            row.createCell(c++).setCellValue(nullSafe(r.getReturnPlace()));
            row.createCell(c++).setCellValue(nullSafe(r.getCarId()));
            row.createCell(c++).setCellValue(nullSafe(r.getCNo()));
            row.createCell(c++).setCellValue(nullSafe(r.getDNo()));
            row.createCell(c++).setCellValue(formatBigDecimal(r.getRoundTripDistance()));
            row.createCell(c++).setCellValue(formatBigDecimal(r.getDriveTime()));
            row.createCell(c).setCellValue(formatBigDecimal(r.getAmount()));
        }

        autoSizeColumns(sheet, 11);
        return buildExcelResponse(workbook, "financial-report.xlsx");
    }

    /**
     * 导出车辆库存清单（基于 car 表和 car_type 关联）
     */
    @GetMapping(value = "/cars", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportCarInventoryReport() throws IOException {
        List<Car> cars = carRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cars");

        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        int col = 0;
        header.createCell(col++).setCellValue("车牌号");
        header.createCell(col++).setCellValue("购入日期");
        header.createCell(col++).setCellValue("价格(元)");
        header.createCell(col++).setCellValue("车辆状态");
        header.createCell(col++).setCellValue("运行公里");
        header.createCell(col++).setCellValue("运行小时");
        header.createCell(col++).setCellValue("车型编号");
        header.createCell(col++).setCellValue("车型名称");
        header.createCell(col).setCellValue("小时租金(元)");

        for (Car c : cars) {
            Row row = sheet.createRow(rowIdx++);
            int i = 0;
            row.createCell(i++).setCellValue(nullSafe(c.getCarId()));
            row.createCell(i++).setCellValue(formatDate(c.getPurchaseDate()));
            row.createCell(i++).setCellValue(formatBigDecimal(c.getPrice()));
            row.createCell(i++).setCellValue(formatCarStatus(c.getCarStatus()));
            row.createCell(i++).setCellValue(formatBigDecimal(c.getRunKm()));
            row.createCell(i++).setCellValue(formatBigDecimal(c.getRunHour()));

            CarType ct = c.getCarType();
            if (ct != null) {
                row.createCell(i++).setCellValue(nullSafe(ct.getTNo()));
                row.createCell(i++).setCellValue(nullSafe(ct.getTName()));
                row.createCell(i).setCellValue(formatBigDecimal(ct.getRentRate()));
            } else {
                row.createCell(i++).setCellValue("");
                row.createCell(i++).setCellValue("");
                row.createCell(i).setCellValue("");
            }
        }

        autoSizeColumns(sheet, 9);
        return buildExcelResponse(workbook, "car-inventory.xlsx");
    }

    /**
     * 导出车型配置清单（基于 car_type 表，并统计每个车型的车辆数量）
     */
    @GetMapping(value = "/types", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportCarTypeReport() throws IOException {
        List<CarType> types = carTypeRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CarTypes");

        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        int col = 0;
        header.createCell(col++).setCellValue("车型编号");
        header.createCell(col++).setCellValue("车型名称");
        header.createCell(col++).setCellValue("小时租金(元)");
        header.createCell(col++).setCellValue("图片路径");
        header.createCell(col).setCellValue("车辆总数");

        for (CarType t : types) {
            Row row = sheet.createRow(rowIdx++);
            int i = 0;
            row.createCell(i++).setCellValue(nullSafe(t.getTNo()));
            row.createCell(i++).setCellValue(nullSafe(t.getTName()));
            row.createCell(i++).setCellValue(formatBigDecimal(t.getRentRate()));
            row.createCell(i++).setCellValue(nullSafe(t.getImagePath()));
            long count = carRepository.countByTNo(t.getTNo());
            row.createCell(i).setCellValue(count);
        }

        autoSizeColumns(sheet, 5);
        return buildExcelResponse(workbook, "car-types.xlsx");
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }

    private String formatBigDecimal(BigDecimal v) {
        return v == null ? "" : v.toPlainString();
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATE_TIME_FORMATTER);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    private String formatCarStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "0" -> "可租";
            case "1" -> "已租";
            case "2" -> "维修";
            default -> status;
        };
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private ResponseEntity<byte[]> buildExcelResponse(XSSFWorkbook workbook, String fileName) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build());
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }
    }
}

package com.example.backend.controller;

import com.example.backend.entity.CarType;
import com.example.backend.dto.CarTypeDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.backend.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车型管理控制器
 * 提供车型信息管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/car-types")
@CrossOrigin(origins = "*")
public class CarTypeController {

    @Autowired
    private CarTypeService carTypeService;

    /**
     * 获取所有车型信息
     * GET /api/car-types
     *
     * @return 车型列表
     */
    @GetMapping
    public ResponseEntity<List<CarTypeDTO>> getAllCarTypes() {
        List<CarType> carTypes = carTypeService.getAllCarTypes();

        List<CarTypeDTO> dtos = carTypes.stream().map(ct -> {
            // construct image filename to match files under resources/static/images
            String fileName = resolveImageFileName(ct.getTName(), ct.getTNo());
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(fileName)
                    .toUriString();

            return new CarTypeDTO(ct.getTNo(), ct.getTName(), ct.getRentRate(), imageUrl);
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * 根据类型编号获取车型信息
     * GET /api/car-types/{tNo}
     *
     * @param tNo   类型编号
     * @return 车型信息
     */
    @GetMapping("/{tNo}")
    public ResponseEntity<CarType> getCarTypeById(
            @PathVariable String tNo) {
        return carTypeService.getCarTypeById(tNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新车型
     * POST /api/car-types
     *
     * @param carType 车型信息
     * @return 创建的车型信息
     */
    @PostMapping
    public ResponseEntity<CarType> createCarType(@RequestBody CarType carType) {
        CarType createdCarType = carTypeService.createCarType(carType);
        return ResponseEntity.ok(createdCarType);
    }

    /**
     * 更新车型信息
     * PUT /api/car-types/{tNo}
     *
     * @param tNo     类型编号
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    @PutMapping("/{tNo}")
    public ResponseEntity<CarType> updateCarType(
            @PathVariable String tNo,
            @RequestBody CarType carType) {
        CarType updatedCarType = carTypeService.updateCarType(tNo, carType);
        return ResponseEntity.ok(updatedCarType);
    }

    /**
     * 删除车型
     * DELETE /api/car-types/{tNo}
     *
     * @param tNo   类型编号
     * @return 无内容
     */
    @DeleteMapping("/{tNo}")
    public ResponseEntity<Void> deleteCarType(
            @PathVariable String tNo) {
        carTypeService.deleteCarType(tNo);
        return ResponseEntity.noContent().build();
    }

    // Resolve an image filename based on type name or tNo.
    // This maps common Chinese/English model names to the english filenames stored in /static/images.
    private String resolveImageFileName(String tName, String tNo) {
        String name = (tName != null ? tName : "").toLowerCase();
        try {
            if (name.contains("丰田") || name.contains("凯美瑞") || name.contains("camry")) return "toyota_camry.jpg";
            if (name.contains("本田") || name.contains("雅阁") || name.contains("accord")) return "honda_accord.jpg";
            if (name.contains("奔驰") || name.contains("e级") || name.contains("benz") || name.contains("mercedes")) return "benz_e_class.jpg";
            if (name.contains("宝马") || name.contains("5系") || name.contains("bmw")) return "bmw_5_series.jpg";
            if (name.contains("奥迪") || name.contains("a6l") || name.contains("audi")) return "audi_a6l.jpg";
            if (name.contains("大众") || name.contains("帕萨特") || name.contains("passat")) return "vw_passat.jpg";
        } catch (Exception ex) {
            // ignore and fallback
        }

        // Fallback heuristics: if tNo maps to a known pattern (e.g., T001 -> toyota), you can extend here.
        if (tNo != null) {
            String tn = tNo.toLowerCase();
            // example mapping by tNo prefix
            if (tn.startsWith("t001")) return "toyota_camry.jpg";
            if (tn.startsWith("t002")) return "honda_accord.jpg";
            if (tn.startsWith("t003")) return "benz_e_class.jpg";
            if (tn.startsWith("t004")) return "bmw_5_series.jpg";
            if (tn.startsWith("t005")) return "audi_a6l.jpg";
            if (tn.startsWith("t006")) return "vw_passat.jpg";
        }

        // final fallback
        return "default.jpg";
    }
}
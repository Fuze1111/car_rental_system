package com.example.backend.controller;

import com.example.backend.entity.CarType;
import com.example.backend.dto.CarTypeDTO;
import com.example.backend.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 获取所有可租车型信息（包含图片路径从数据库读取）
     * 仅返回存在可用车辆的车型
     * GET /api/car-types
     *
     * @return 车型列表
     */
    @GetMapping
    public ResponseEntity<List<CarTypeDTO>> getAllCarTypes() {
        List<CarTypeDTO> carTypes = carTypeService.getAllCarTypes();
        return ResponseEntity.ok(carTypes);
    }

    /**
     * 管理端：获取数据库中全部车型（包含没有车辆的车型）
     * GET /api/car-types/all
     *
     * @return 车型列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<CarTypeDTO>> getAllCarTypesForAdmin() {
        List<CarTypeDTO> carTypes = carTypeService.getAllCarTypesIncludingEmpty();
        return ResponseEntity.ok(carTypes);
    }

    /**
     * 根据类型编号获取车型信息
     * GET /api/car-types/{tNo}
     *
     * @param tNo 类型编号
     * @return 车型信息
     */
    @GetMapping("/{tNo}")
    public ResponseEntity<CarType> getCarTypeById(@PathVariable String tNo) {
        return carTypeService.getCarTypeById(tNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据类型编号和型号名称获取车型信息（兼容旧接口）
     * GET /api/car-types/{tNo}/{tName}
     *
     * @param tNo   类型编号
     * @param tName 型号名称
     * @return 车型信息
     */
    @GetMapping("/{tNo}/{tName}")
    @Deprecated
    public ResponseEntity<CarType> getCarTypeByIdOld(
            @PathVariable String tNo,
            @PathVariable String tName) {
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
     * 上传车型图片文件并返回可访问的相对URL
     * POST /api/car-types/upload-image
     *
     * @param file 图片文件
     * @return 包含 imagePath 的JSON对象
     */
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadCarTypeImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null) {
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                    extension = originalFilename.substring(dotIndex);
                }
            }

            String newFileName = "car_type_" + System.currentTimeMillis() + extension;

            Path imagesDir = Paths.get("static/images/car-types").toAbsolutePath().normalize();
            Files.createDirectories(imagesDir);
            Path targetPath = imagesDir.resolve(newFileName).normalize();
            file.transferTo(targetPath.toFile());

            String relativeUrl = "/images/car-types/" + newFileName;
            Map<String, String> body = new HashMap<>();
            body.put("imagePath", relativeUrl);
            return ResponseEntity.ok(body);
        } catch (IOException e) {
            throw new RuntimeException("保存图片失败: " + e.getMessage(), e);
        }
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
     * 更新车型信息（兼容旧接口）
     * PUT /api/car-types/{tNo}/{tName}
     *
     * @param tNo     类型编号
     * @param tName   型号名称
     * @param carType 车型信息
     * @return 更新后的车型信息
     */
    @PutMapping("/{tNo}/{tName}")
    @Deprecated
    public ResponseEntity<CarType> updateCarTypeOld(
            @PathVariable String tNo,
            @PathVariable String tName,
            @RequestBody CarType carType) {
        CarType updatedCarType = carTypeService.updateCarType(tNo, carType);
        return ResponseEntity.ok(updatedCarType);
    }

    /**
     * 删除车型
     * DELETE /api/car-types/{tNo}
     *
     * @param tNo 类型编号
     * @return 无内容
     */
    @DeleteMapping("/{tNo}")
    public ResponseEntity<Void> deleteCarType(@PathVariable String tNo) {
        carTypeService.deleteCarType(tNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除车型（兼容旧接口）
     * DELETE /api/car-types/{tNo}/{tName}
     *
     * @param tNo   类型编号
     * @param tName 型号名称（已忽略）
     * @return 无内容
     */
    @DeleteMapping("/{tNo}/{tName}")
    @Deprecated
    public ResponseEntity<Void> deleteCarTypeOld(
            @PathVariable String tNo,
            @PathVariable String tName) {
        carTypeService.deleteCarType(tNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 管理员上传车型图片后，保存/更新图片 URL 到 car_type_image 表
     * 前端传入 TNo 和图片访问 URL（例如 /images/xxx.jpg 或完整 URL）
     */
    @PostMapping("/{tNo}/image")
    public ResponseEntity<Void> saveCarTypeImage(
            @PathVariable String tNo,
            @RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        carTypeService.saveOrUpdateCarTypeImage(tNo, imageUrl);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据 TNo 获取该车型的所有图片 URL（当前主要用于扩展或调试）
     */
    @GetMapping("/{tNo}/images")
    public ResponseEntity<List<String>> getCarTypeImages(@PathVariable String tNo) {
        List<String> paths = carTypeService.getCarTypeImages(tNo);
        return ResponseEntity.ok(paths);
    }
}
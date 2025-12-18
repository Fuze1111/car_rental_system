package com.example.backend.repository;

import com.example.backend.entity.CarTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 车型图片数据访问层
 */
@Repository
public interface CarTypeImageRepository extends JpaRepository<CarTypeImage, Integer> {

    /**
     * 仅返回指定车型编号的图片路径列表，按 ImageID 升序排列
     */
    @Query("select c.imagePath from CarTypeImage c where c.tNo = :tNo order by c.imageId asc")
    List<String> findImagePathsByTNo(@Param("tNo") String tNo);
}

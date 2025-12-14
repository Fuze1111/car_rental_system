package com.example.backend.repository;

import com.example.backend.entity.CarTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 车型图片数据访问层
 */
@Repository
public interface CarTypeImageRepository extends JpaRepository<CarTypeImage, Integer> {
    
    /**
     * 根据车型编号和名称查找图片
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 图片列表
     */
    @Query("SELECT c FROM CarTypeImage c WHERE c.tNo = :tNo AND c.tName = :tName")
    List<CarTypeImage> findByTNoAndTName(@Param("tNo") String tNo, @Param("tName") String tName);
    
    /**
     * 根据车型编号和名称查找第一张图片
     * @param tNo 车型编号
     * @param tName 车型名称
     * @return 图片
     */
    @Query("SELECT c FROM CarTypeImage c WHERE c.tNo = :tNo AND c.tName = :tName")
    Optional<CarTypeImage> findFirstByTNoAndTName(@Param("tNo") String tNo, @Param("tName") String tName);
}

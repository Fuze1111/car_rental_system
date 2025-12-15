package com.example.backend.repository;

import com.example.backend.entity.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 汽车类型Repository
 * 提供汽车类型信息的数据访问操作
 */
@Repository
public interface CarTypeRepository extends JpaRepository<CarType, String> {

    /**
     * 根据类型编号和型号名称查询车型
     * @param tNo 类型编号
     * @param tName 型号名称
     * @return 车型信息
     */
    @Query("SELECT ct FROM CarType ct WHERE ct.tNo = :tNo AND ct.tName = :tName")
    Optional<CarType> findByTNoAndTName(@Param("tNo") String tNo, @Param("tName") String tName);
}


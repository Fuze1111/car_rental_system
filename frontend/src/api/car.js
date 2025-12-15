import request from './request'

// 获取车辆列表（分页）
export function getCars(params) {
  return request({
    url: '/cars',
    method: 'get',
    params
  })
}

// 获取所有车辆（不分页）
export function getAllCars() {
  return request({
    url: '/cars',
    method: 'get'
  })
}

// 根据车牌号获取车辆详情
export function getCarById(carId) {
  return request({
    url: `/cars/${carId}`,
    method: 'get'
  })
}

// 创建车辆
export function createCar(data) {
  return request({
    url: '/cars',
    method: 'post',
    data
  })
}

// 更新车辆信息
export function updateCar(carId, data) {
  return request({
    url: `/cars/${carId}`,
    method: 'put',
    data
  })
}

// 删除车辆
export function deleteCar(carId) {
  return request({
    url: `/cars/${carId}`,
    method: 'delete'
  })
}

// 根据车辆状态查询车辆
export function getCarsByStatus(status) {
  return request({
    url: `/cars/status/${status}`,
    method: 'get'
  })
}

// 根据车型查询车辆
export function getCarsByType(params) {
  return request({
    url: '/cars/type',
    method: 'get',
    params
  })
}

// 多条件查询车辆
export function searchCars(params) {
  return request({
    url: '/cars/search',
    method: 'get',
    params
  })
}

// 更新车辆状态
export function updateCarStatus(carId, data) {
  return request({
    url: `/cars/${carId}/status`,
    method: 'patch',
    data
  })
}

// 获取车辆统计信息
export function getCarStatistics() {
  return request({
    url: '/cars/statistics',
    method: 'get'
  })
}

// 获取维修记录列表 (支持 params)
export function getRepairs(params) {
  return request({
    url: '/repairs',
    method: 'get',
    params
  })
}

// 根据维修编号获取维修记录
export function getRepairById(rNo) {
  return request({
    url: `/repairs/${rNo}`,
    method: 'get'
  })
}

// 根据车辆号获取维修记录
export function getRepairsByCarId(carId) {
  return request({
    url: `/repairs/car/${carId}`,
    method: 'get'
  })
}

// 创建维修记录
export function createRepair(data) {
  return request({
    url: '/repairs',
    method: 'post',
    data
  })
}

// 更新维修记录
export function updateRepair(rNo, data) {
  return request({
    url: `/repairs/${rNo}`,
    method: 'put',
    data
  })
}

// 删除维修记录
export function deleteRepair(rNo) {
  return request({
    url: `/repairs/${rNo}`,
    method: 'delete'
  })
}

// 获取车辆维修次数
export function getRepairCountByCarId(carId) {
  return request({
    url: `/repairs/car/${carId}/count`,
    method: 'get'
  })
}

// 获取所有车型
export function getCarTypes() {
  return request({
    url: '/car-types',
    method: 'get'
  })
}

// 创建车型
export function createCarType(data) {
  return request({
    url: '/car-types',
    method: 'post',
    data
  })
}

// 更新车型
export function updateCarType(tNo, data) {
  return request({
    url: `/car-types/${tNo}`,
    method: 'put',
    data
  })
}

// 删除车型
export function deleteCarType(tNo) {
  return request({
    url: `/car-types/${tNo}`,
    method: 'delete'
  })
}

// 搜索车辆（通过 carId，支持分页，返回结构可能是 Page 或 List）
export function searchCarsByCarId(params) {
  return request({
    url: '/cars/search/carId',
    method: 'get',
    params
  }).then(resp => {
    // 后端在分页时返回 Page 对象（含 content, totalElements, totalPages），否则返回 List
    if (resp && resp.content !== undefined) {
      return resp
    }
    // 如果是非分页列表，封装成统一结构
    return {
      content: resp || [],
      totalElements: (resp && resp.length) || 0,
      totalPages: 1
    }
  })
}

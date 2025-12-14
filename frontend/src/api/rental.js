import request from './request'

// 获取租赁记录（支持分页）
export function getRentals(params) {
  return request({
    url: '/rentals',
    method: 'get',
    params
  })
}

// 根据发票号获取租赁记录
export function getRentalById(invoiceNo) {
  return request({
    url: `/rentals/${invoiceNo}`,
    method: 'get'
  })
}

// 创建租赁记录
export function createRental(data) {
  return request({
    url: '/rentals',
    method: 'post',
    data
  })
}

// 更新租赁记录
export function updateRental(invoiceNo, data) {
  return request({
    url: `/rentals/${invoiceNo}`,
    method: 'put',
    data
  })
}

// 删除租赁记录
export function deleteRental(invoiceNo) {
  return request({
    url: `/rentals/${invoiceNo}`,
    method: 'delete'
  })
}

// 多条件查询（支持 cNo,dNo,carId,startDate,endDate）
export function searchRentals(params) {
  return request({
    url: '/rentals/search',
    method: 'get',
    params
  })
}

// 根据发票号模糊搜索
export function searchRentalsByInvoice(invoiceNo) {
  return request({
    url: '/rentals/search/invoice',
    method: 'get',
    params: { invoiceNo }
  })
}

// 根据借车地点模糊查询
export function searchRentalsByPlace(loanPlace) {
  return request({
    url: '/rentals/search/place',
    method: 'get',
    params: { loanPlace }
  })
}

// 根据日期范围查询
export function getRentalsByDateRange(start, end) {
  return request({
    url: '/rentals/date-range',
    method: 'get',
    params: { start, end }
  })
}

// 获取租赁统计信息
export function getRentalStatistics() {
  return request({
    url: '/rentals/statistics',
    method: 'get'
  })
}

// 计算指定日期范围内的收入
export function calculateRevenue(start, end) {
  return request({
    url: '/rentals/revenue',
    method: 'get',
    params: { start, end }
  })
}

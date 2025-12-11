import request from './request'

/**
 * 管理员用户管理API
 */

/**
 * 分页搜索客户
 * @param {Object} params - 搜索参数 { name, phone, company, page, size }
 * @returns {Promise}
 */
export const searchCustomers = (params) => {
  return request({
    url: '/customers/admin/search',
    method: 'post',
    data: {
      name: params.name || null,
      phone: params.phone || null,
      company: params.company || null,
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

/**
 * 分页搜索司机
 * @param {Object} params - 搜索参数 { name, status, licenseType, page, size }
 * @returns {Promise}
 */
export const searchDrivers = (params) => {
  return request({
    url: '/drivers/admin/search',
    method: 'post',
    data: {
      name: params.name || null,
      status: params.status || null,
      licenseType: params.licenseType || null,
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

/**
 * 更新司机信息
 * @param {String} dNo - 司机号
 * @param {Object} data - 司机信息
 * @returns {Promise}
 */
export const updateDriver = (dNo, data) => {
  return request({
    url: `/drivers/${dNo}`,
    method: 'put',
    data
  })
}

/**
 * 获取客户详情
 * @param {String} cNo - 客户号
 * @returns {Promise}
 */
export const getCustomerById = (cNo) => {
  return request({
    url: `/customers/${cNo}`,
    method: 'get'
  })
}

/**
 * 获取司机详情
 * @param {String} dNo - 司机号
 * @returns {Promise}
 */
export const getDriverById = (dNo) => {
  return request({
    url: `/drivers/${dNo}`,
    method: 'get'
  })
}

import request from './request'

/**
 * 获取司机个人信息
 * @param {string} dNo - 司机号
 * @returns {Promise}
 */
export const getDriverInfo = (dNo) => {
  return request({
    url: `/drivers/${dNo}`,
    method: 'get'
  })
}

/**
 * 更新司机个人信息
 * @param {string} dNo - 司机号
 * @param {Object} data - 司机信息
 * @returns {Promise}
 */
export const updateDriverInfo = (dNo, data) => {
  return request({
    url: `/drivers/${dNo}`,
    method: 'put',
    data
  })
}

/**
 * 司机修改密码
 * @param {Object} data - { userId, currentPassword, newPassword }
 * @returns {Promise}
 */
export const changeDriverPassword = (data) => {
  return request({
    url: '/auth/change-password/driver',
    method: 'post',
    data
  })
}

/**
 * 更新司机状态
 * @param {string} dNo - 司机号
 * @param {string} status - 状态（在岗/空闲）
 * @returns {Promise}
 */
export const updateDriverStatus = (dNo, status) => {
  return request({
    url: `/drivers/${dNo}/status`,
    method: 'patch',
    data: { dStatus: status }
  })
}

/**
 * 获取司机的任务/租赁记录
 * @param {string} dNo - 司机号
 * @returns {Promise}
 */
export const getDriverTasks = (dNo) => {
  return request({
    url: `/rentals/driver/${dNo}`,
    method: 'get'
  })
}

/**
 * 获取司机统计信息（管理端仪表盘使用）
 * @returns {Promise}
 */
export const getDriverStatistics = () => {
  return request({
    url: '/drivers/statistics',
    method: 'get'
  })
}

/**
 * 完成任务（更新rental记录的还车日期）
 * @param {string} invoiceNo - 发票号
 * @param {Object} data - 更新数据（包含returnDate等）
 * @returns {Promise}
 */
export const completeRental = (invoiceNo, data) => {
  return request({
    url: `/rentals/${invoiceNo}`,
    method: 'put',
    data
  })
}

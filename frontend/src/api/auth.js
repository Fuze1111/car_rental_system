import request from './request'

/**
 * 登录接口
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名/账号/手机号
 * @param {string} data.password - 密码
 * @param {string} data.role - 角色类型 (customer/driver/admin)
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 客户注册接口
 * @param {Object} data - 注册数据
 * @param {string} data.name - 姓名
 * @param {string} data.phone - 手机号
 * @param {string} data.password - 密码
 * @param {string} data.company - 公司（可选）
 * @param {string} data.address - 地址（可选）
 * @returns {Promise}
 */
export function registerCustomer(data) {
  return request({
    url: '/auth/register/customer',
    method: 'post',
    data
  })
}

/**
 * 司机注册接口
 * @param {Object} data - 注册数据
 * @param {string} data.name - 姓名
 * @param {string} data.password - 密码
 * @param {string} data.licenseType - 准驾车型
 * @returns {Promise}
 */
export function registerDriver(data) {
  return request({
    url: '/auth/register/driver',
    method: 'post',
    data
  })
}

/**
 * 测试API连接
 * @returns {Promise}
 */
export function testApi() {
  return request({
    url: '/auth/test',
    method: 'get'
  })
}
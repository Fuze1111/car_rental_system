import request from './request'

// 获取客户信息
export function getCustomerInfo(cNo) {
  return request({
    url: `/customers/${cNo}`,
    method: 'get'
  })
}

// 更新客户信息
export function updateCustomerInfo(cNo, data) {
  return request({
    url: `/customers/${cNo}`,
    method: 'put',
    data
  })
}

// 修改客户密码
export function changeCustomerPassword(data) {
  return request({
    url: '/auth/change-password/customer',
    method: 'post',
    data
  })
}

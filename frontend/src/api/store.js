import request from './request'

/**
 * 获取所有门店
 * @returns {Promise}
 */
export function getAllStores() {
  return request({
    url: '/stores',
    method: 'get'
  })
}

/**
 * 根据门店号获取门店信息
 * @param {String} storeNo - 门店号
 * @returns {Promise}
 */
export function getStoreById(storeNo) {
  return request({
    url: `/stores/${storeNo}`,
    method: 'get'
  })
}

/**
 * 根据城市查询门店
 * @param {String} city - 城市名称
 * @returns {Promise}
 */
export function getStoresByCity(city) {
  return request({
    url: `/stores/city/${city}`,
    method: 'get'
  })
}

/**
 * 根据门店名称模糊查询
 * @param {String} name - 门店名称关键字
 * @returns {Promise}
 */
export function searchStores(name) {
  return request({
    url: '/stores/search',
    method: 'get',
    params: { name }
  })
}

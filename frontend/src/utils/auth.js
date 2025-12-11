/**
 * 用户认证工具函数
 */

const USER_INFO_KEY = 'userInfo'
const USER_ROLE_KEY = 'userRole'
const TOKEN_KEY = 'token'

/**
 * 保存用户信息
 * @param {Object} userInfo - 用户信息对象
 */
export function setUserInfo(userInfo) {
  // 保存token（如果有的话）
  if (userInfo.token) {
    setToken(userInfo.token)
  }
  
  // 保存用户信息（去除token字段）
  const { token, ...userInfoWithoutToken } = userInfo
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfoWithoutToken))
}

/**
 * 获取用户信息
 * @returns {Object|null} 用户信息对象或null
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem(USER_INFO_KEY)
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 保存用户角色
 * @param {string} role - 用户角色
 */
export function setUserRole(role) {
  localStorage.setItem(USER_ROLE_KEY, role)
}

/**
 * 获取用户角色
 * @returns {string|null} 用户角色或null
 */
export function getUserRole() {
  return localStorage.getItem(USER_ROLE_KEY)
}

/**
 * 移除用户角色
 */
export function removeUserRole() {
  localStorage.removeItem(USER_ROLE_KEY)
}

/**
 * 保存Token
 * @param {string} token - 认证token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 获取Token
 * @returns {string|null} token或null
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 移除Token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeUserInfo()
  removeUserRole()
  removeToken()
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!getUserInfo()
}
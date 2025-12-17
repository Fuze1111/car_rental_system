<template>
  <div class="user-profile-page">
    <div class="content-wrapper">
      <el-card class="main-card" shadow="never">
        <div class="profile-header">
          <div class="user-info-large">
            <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar" />
            <div class="user-text">
              <h2>{{ userForm.name || 'User' }}</h2>
              <div class="badges">
                <el-tag type="warning" effect="dark" size="small" round>普通会员</el-tag>
                <el-tag type="info" effect="plain" size="small" round>信用良好</el-tag>
              </div>
            </div>
          </div>
          <div class="header-actions">
            <!-- 预留操作区 -->
          </div>
        </div>

        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="我的订单" name="orders">
            <!-- 订单子分类 -->
            <el-tabs v-model="activeOrderTab" type="card" class="order-sub-tabs">
              <!-- 现有订单 -->
              <el-tab-pane label="现有订单" name="current">
                <div class="orders-list">
                  <el-empty v-if="currentOrders.length === 0" description="暂无进行中的订单">
                    <el-button type="primary" round @click="$router.push('/user/cars')">去租车</el-button>
                  </el-empty>

                  <div v-else class="order-grid">
                    <div class="order-card active" v-for="order in currentOrders" :key="order.id">
                      <div class="card-header">
                        <span class="order-no">{{ order.invoiceNo }}</span>
                        <el-tag type="warning" size="small" effect="light">{{ order.status }}</el-tag>
                      </div>
                      <div class="card-body">
                        <div class="car-brief">
                          <div class="icon-box"><el-icon><Van /></el-icon></div>
                          <span class="car-name">{{ order.carName }}</span>
                          <span class="car-plate">{{ order.carPlate }}</span>
                        </div>
                        <div class="detail-list">
                          <div class="detail-item">
                            <span class="label">借车时间</span>
                            <span class="value">{{ order.startTime }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="label">取车地点</span>
                            <span class="value">{{ order.loanPlace }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="label">还车地点</span>
                            <span class="value">{{ order.returnPlace || '待定' }}</span>
                          </div>
                          <div class="detail-item" v-if="order.driverName">
                            <span class="label">司机</span>
                            <span class="value">{{ order.driverName }}</span>
                          </div>
                        </div>
                      </div>
                      <div class="card-footer">
                        <div class="amount-box">
                          <div class="fee-breakdown">
                            <div class="fee-item">
                              <span class="fee-label">基本费用：</span>
                              <span class="fee-value">¥{{ calculateBasicFee(order) }}</span>
                            </div>
                            <div class="fee-item">
                              <span class="fee-label">里程费用：</span>
                              <span class="fee-value pending">待计算</span>
                            </div>
                          </div>
                        </div>
                        <el-button link type="primary" @click="openDetail(order)">详情</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <!-- 历史订单 -->
              <el-tab-pane label="历史订单" name="history">
                <div class="orders-list">
                  <el-empty v-if="historyOrders.length === 0" description="暂无历史订单记录" />

                  <div v-else class="order-grid">
                    <div class="order-card" v-for="order in historyOrders" :key="order.id">
                      <div class="card-header">
                        <span class="order-no">{{ order.invoiceNo }}</span>
                        <el-tag type="success" size="small" effect="light">{{ order.status }}</el-tag>
                      </div>
                      <div class="card-body">
                        <div class="car-brief">
                          <div class="icon-box"><el-icon><Van /></el-icon></div>
                          <span class="car-name">{{ order.carName }}</span>
                          <span class="car-plate">{{ order.carPlate }}</span>
                        </div>
                        <div class="detail-list">
                          <div class="detail-item">
                            <span class="label">借车时间</span>
                            <span class="value">{{ order.startTime }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="label">还车时间</span>
                            <span class="value">{{ order.endTime }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="label">取车地点</span>
                            <span class="value">{{ order.loanPlace }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="label">还车地点</span>
                            <span class="value">{{ order.returnPlace }}</span>
                          </div>
                          <div class="detail-item" v-if="order.distance">
                            <span class="label">行驶里程</span>
                            <span class="value">{{ order.distance }} km</span>
                          </div>
                          <div class="detail-item" v-if="order.duration">
                            <span class="label">行驶时长</span>
                            <span class="value">{{ order.duration }} h</span>
                          </div>
                        </div>
                      </div>
                      <div class="card-footer">
                        <div class="amount-box">
                          <span class="label">实付金额</span>
                          <span class="amount">¥{{ order.amount }}</span>
                        </div>
                        <el-button link type="primary" @click="openDetail(order)">详情</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>

          <el-tab-pane label="个人信息" name="info">
            <div class="form-container">
              <el-form :model="userForm" label-position="top" class="custom-form">
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="姓名">
                      <el-input v-model="userForm.name" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="客户号">
                      <el-input v-model="userForm.id" disabled />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="手机号">
                      <el-input v-model="userForm.phone" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="邮编">
                      <el-input v-model="userForm.postCode" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="所属单位">
                  <el-input v-model="userForm.company" />
                </el-form-item>

                <el-form-item label="居住地址">
                  <el-input v-model="userForm.address" type="textarea" rows="3" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" round @click="saveProfile" :loading="saving" style="padding: 10px 30px">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>

              <!-- 修改密码区域 -->
              <div class="password-section">
                <el-divider content-position="left">
                  <span style="color: #64748b; font-size: 14px; font-weight: 600;">修改密码</span>
                </el-divider>
                
                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-position="top" class="custom-form">
                  <el-row :gutter="24">
                    <el-col :span="24">
                      <el-form-item label="当前密码" prop="currentPassword">
                        <el-input v-model="passwordForm.currentPassword" type="password" show-password placeholder="请输入当前密码" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="24">
                    <el-col :span="12">
                      <el-form-item label="新密码" prop="newPassword">
                        <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="确认新密码" prop="confirmPassword">
                        <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-form-item>
                    <el-button type="warning" round @click="changePassword" :loading="changingPassword" style="padding: 10px 30px">
                      <el-icon><Lock /></el-icon>
                      修改密码
                    </el-button>
                    <el-button round @click="resetPasswordForm" style="padding: 10px 20px; margin-left: 12px">
                      重置
                    </el-button>
                  </el-form-item>
                  
                  <el-alert 
                    title="密码安全提示" 
                    type="info" 
                    :closable="false"
                    show-icon>
                    <template #default>
                      <ul style="margin: 0; padding-left: 16px; font-size: 13px;">
                        <li>密码长度至少6位</li>
                        <li>建议包含字母、数字的组合</li>
                        <li>修改密码后下次登录需使用新密码</li>
                      </ul>
                    </template>
                  </el-alert>
                </el-form>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="800px"
      class="custom-dialog detail-dialog"
      destroy-on-close
      append-to-body
      @opened="initMap"
    >
      <div class="detail-container" v-if="currentOrder">
        <!-- 左侧信息 -->
        <div class="detail-info">
          <div class="info-block">
            <h4 class="block-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单号</span>
                <span class="value code">{{ currentOrder.invoiceNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">车辆</span>
                <span class="value plate">{{ currentOrder.CarID }}</span>
              </div>
              <div class="info-item">
                <span class="label">车型</span>
                <span class="value">{{ currentOrder.carName }}</span>
              </div>
              <div class="info-item">
                <span class="label">司机</span>
                <span class="value">{{ currentOrder.DNo ? '配备司机' : '自驾' }}</span>
              </div>
            </div>
          </div>

          <div class="info-block">
            <h4 class="block-title">费用与数据</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">金额状态</span>
                <span class="value amount" :class="{ 'pending': currentOrder.status === '进行中' }">
                  {{ currentOrder.status === '进行中' ? '待结算' : '¥' + currentOrder.amount }}
                </span>
              </div>
              <div class="info-item">
                <span class="label">订单状态</span>
                <el-tag size="small" :type="currentOrder.status === '已完成' ? 'success' : 'primary'">{{ currentOrder.status }}</el-tag>
              </div>
              <div class="info-item" v-if="currentOrder.status === '已完成'">
                <span class="label">行驶里程</span>
                <span class="value">{{ currentOrder.distance || '-' }} km</span>
              </div>
              <div class="info-item" v-if="currentOrder.status === '已完成'">
                <span class="label">行驶时长</span>
                <span class="value">{{ currentOrder.duration || '-' }} h</span>
              </div>
            </div>
          </div>

          <div class="info-block">
            <h4 class="block-title">行程路线</h4>
            <div class="route-timeline">
              <div class="route-point start">
                <div class="dot"></div>
                <div class="content">
                  <span class="time">{{ currentOrder.LoanDate }}</span>
                  <span class="place">{{ currentOrder.loanPlace }}</span>
                </div>
              </div>
              <div class="route-line"></div>
              <div class="route-point end">
                <div class="dot"></div>
                <div class="content">
                  <span class="time">{{ currentOrder.ReturnDate || '未还车' }}</span>
                  <span class="place">{{ currentOrder.returnPlace }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧地图 -->
        <div class="detail-map">
          <div id="order-map" class="map-container">
            <div class="map-placeholder" v-if="!hasMapKey">
              <el-icon class="map-icon"><LocationFilled /></el-icon>
              <p>地图预览区域</p>
              <span class="sub">请配置高德地图 Key 以查看真实轨迹</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Van, LocationFilled, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCustomerRentals } from '@/api/rental'
import { getCustomerInfo, updateCustomerInfo, changeCustomerPassword } from '@/api/customer'
import { getUserInfo } from '@/utils/auth'

const activeTab = ref('orders')
const activeOrderTab = ref('current') // 订单子标签页：current-现有订单, history-历史订单
const saving = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)
const hasMapKey = ref(false)
const loading = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref(null)

const userForm = reactive({
  id: '',
  name: '',
  phone: '',
  company: '',
  address: '',
  postCode: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 订单数据
const allOrders = ref([])
const currentOrders = ref([]) // 现有订单（进行中）
const historyOrders = ref([]) // 历史订单（已完成）

// 加载客户订单
const loadCustomerOrders = async () => {
  loading.value = true
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    // 获取客户的租赁订单
    const rentals = await getCustomerRentals(userInfo.userId)
    
    // 将后端数据转换为前端需要的格式
    const orders = rentals.map(rental => {
      // 判断订单状态：如果还车日期为空或未到，则为进行中；否则为已完成
      const now = new Date()
      const returnDate = rental.returnDate ? new Date(rental.returnDate) : null
      const isPending = !returnDate || returnDate > now
      
      return {
        id: rental.invoiceNo,
        invoiceNo: rental.invoiceNo,
        status: isPending ? '进行中' : '已完成',
        carName: rental.carType || rental.carBrand || '未知车辆',
        carPlate: rental.carId || '未知车牌',
        CarID: rental.carId,
        startTime: rental.loanDate ? new Date(rental.loanDate).toLocaleString('zh-CN') : '未知',
        endTime: rental.returnDate ? new Date(rental.returnDate).toLocaleString('zh-CN') : '待定',
        LoanDate: rental.loanDate,
        ReturnDate: rental.returnDate,
        loanPlace: rental.loanPlace || '未知地点',
        returnPlace: rental.returnPlace || (isPending ? '待定' : '未知地点'),
        amount: rental.amount || 0,
        distance: rental.roundTripDistance || null,
        duration: rental.driveTime || null,
        driverName: rental.driverName || null,
        DNo: rental.dNo,
        rentRate: rental.rentRate || 0
      }
    })
    
    // 将订单分为现有订单和历史订单
    allOrders.value = orders
    currentOrders.value = orders.filter(order => order.status === '进行中')
    historyOrders.value = orders.filter(order => order.status === '已完成')
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 计算现有订单显示用的“基本费用”（力求与租车大厅的预计总额保持一致，不含里程费）
const calculateBasicFee = (order) => {
  // 订单还没有预计/实际还车时间，无法给出基础费用
  if (!order.LoanDate || !order.ReturnDate) {
    return '待定'
  }

  const start = new Date(order.LoanDate)
  const end = new Date(order.ReturnDate)
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end <= start) {
    return '待定'
  }

  // 按天数计算租期：与 CarSelection.vue 中 totalDays 的算法保持一致
  const diffMs = Math.abs(end.getTime() - start.getTime())
  const days = Math.max(1, Math.ceil(diffMs / (1000 * 60 * 60 * 24)))

  // 如果后端提供了租金率（元/小时），则使用：日租金 = 每小时单价 × 24
  const hourlyRate = Number(order.rentRate) || 0
  if (!hourlyRate) {
    return '待定'
  }
  const dailyRate = hourlyRate * 24

  const baseRent = dailyRate * days
  const serviceFee = 50

  // 司机服务费：只要有 DNo 或 driverName，就视为带司机，按 200 元/天计算
  let driverFee = 0
  if (order.DNo || order.driverName) {
    driverFee = 200 * days
  }

  const total = baseRent + serviceFee + driverFee
  // 返回两位小数的数值，确保展示为正常金额
  return Number(total.toFixed(2))
}

const openDetail = (order) => {
  currentOrder.value = order
  detailVisible.value = true
  hasMapKey.value = false
}

// 常用地标/城市坐标映射（用于Mock环境或解析失败时的兜底）
const keywordCoordinates = {
  '虹桥': [121.3363, 31.1977],
  '浦东': [121.8055, 31.1444],
  '首都': [116.6033, 40.0799],
  '大兴': [116.4108, 39.5098],
  '白云': [113.2988, 23.3924],
  '天河': [113.3619, 23.1246],
  '北京': [116.4074, 39.9042],
  '上海': [121.4737, 31.2304],
  '广州': [113.2644, 23.1291],
  '深圳': [114.0579, 22.5431]
}

const initMap = () => {
  if (typeof AMap === 'undefined') {
    hasMapKey.value = false
    return
  }
  
  hasMapKey.value = true
  
  const order = currentOrder.value
  if (!order) return

  const startAddr = order.loanPlace
  const endAddr = order.returnPlace

  AMap.plugin(['AMap.Geocoder', 'AMap.Polyline'], function() {
    const geocoder = new AMap.Geocoder()
    const map = new AMap.Map('order-map', {
      zoom: 11,
      viewMode: '3D'
    })

    const getLngLat = (addr) => {
      return new Promise((resolve) => {
        // 1. 尝试使用高德API解析
        geocoder.getLocation(addr, (status, result) => {
          if (status === 'complete' && result.geocodes.length) {
            resolve(result.geocodes[0].location)
          } else {
            // 2. API解析失败（如无Key），尝试关键词模糊匹配
            console.warn(`API解析失败: ${addr}，尝试本地关键词匹配`)
            let matched = false
            for (const key in keywordCoordinates) {
              if (addr.includes(key)) {
                resolve(new AMap.LngLat(keywordCoordinates[key][0], keywordCoordinates[key][1]))
                matched = true
                break
              }
            }
            
            // 3. 兜底默认坐标 (北京天安门)
            if (!matched) {
              resolve(new AMap.LngLat(116.397428, 39.90923))
            }
          }
        })
      })
    }

    Promise.all([getLngLat(startAddr), getLngLat(endAddr)]).then(([startLngLat, endLngLat]) => {
      const startMarker = new AMap.Marker({
        position: startLngLat,
        title: startAddr,
        label: { content: '取车: ' + startAddr, direction: 'top' }
      })
      
      const endMarker = new AMap.Marker({
        position: endLngLat,
        title: endAddr,
        label: { content: '还车: ' + endAddr, direction: 'top' }
      })

      const polyline = new AMap.Polyline({
        path: [startLngLat, endLngLat],
        strokeColor: "#3366FF", 
        strokeWeight: 6,
        strokeStyle: "solid",
        lineJoin: 'round',
        lineCap: 'round'
      })

      map.add([startMarker, endMarker, polyline])
      map.setFitView([startMarker, endMarker], false, [50, 50, 50, 50])
    })
  })
}

// 加载客户信息
const loadCustomerInfo = async () => {
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      return
    }
    
    const customerInfo = await getCustomerInfo(userInfo.userId)
    userForm.id = customerInfo.cNo
    userForm.name = customerInfo.cName || ''
    userForm.phone = customerInfo.phone || ''
    userForm.company = customerInfo.company || ''
    userForm.address = customerInfo.address || ''
    userForm.postCode = customerInfo.postCode || ''
  } catch (error) {
    console.error('加载客户信息失败:', error)
  }
}

// 保存个人信息
const saveProfile = async () => {
  saving.value = true
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    const dataToSend = {
      cName: userForm.name,
      phone: userForm.phone,
      company: userForm.company,
      address: userForm.address,
      postCode: userForm.postCode
    }
    
    console.log('========== 前端保存个人信息 ==========')
    console.log('userForm数据:', userForm)
    console.log('准备发送的数据:', dataToSend)
    console.log('=======================================')
    
    await updateCustomerInfo(userInfo.userId, dataToSend)
    
    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error('保存失败：' + (error.message || '请稍后重试'))
  } finally {
    saving.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    changingPassword.value = true
    try {
      const userInfo = getUserInfo()
      if (!userInfo || !userInfo.userId) {
        ElMessage.error('未找到用户信息，请重新登录')
        return
      }
      
      await changeCustomerPassword({
        userId: userInfo.userId,
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword
      })
      
      ElMessage.success('密码修改成功，请重新登录')
      resetPasswordForm()
      
      // 可选：3秒后跳转到登录页
      setTimeout(() => {
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      }, 3000)
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.response?.data?.message || '修改密码失败，请稍后重试')
    } finally {
      changingPassword.value = false
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 组件挂载时加载订单数据和客户信息
onMounted(() => {
  loadCustomerOrders()
  loadCustomerInfo()
})
</script>

<style scoped lang="scss">
.user-profile-page {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.main-card {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
  min-height: 600px;
  
  :deep(.el-card__body) {
    padding: 40px;
  }
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;

  .user-info-large {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .avatar {
      border: 4px solid white;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    .user-text {
      h2 { margin: 0 0 8px 0; font-size: 24px; color: #1e293b; font-weight: 800; }
      .badges { display: flex; gap: 8px; }
    }
  }
}

.custom-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: #f1f5f9;
  }
  :deep(.el-tabs__item) {
    font-size: 16px;
    color: #64748b;
    &.is-active { color: #3b82f6; font-weight: 600; }
  }
}

/* 订单子标签页样式 - 现代分段控制器风格 */
.order-sub-tabs {
  margin-top: 24px;
  
  :deep(.el-tabs__header) {
    margin: 0 0 32px 0;
    display: flex;
    justify-content: center;
  }
  
  :deep(.el-tabs__nav-wrap) {
    &::after {
      display: none;
    }
  }
  
  :deep(.el-tabs__nav-scroll) {
    display: flex;
    justify-content: center;
  }
  
  :deep(.el-tabs__nav) {
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    padding: 6px;
    background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
    display: inline-flex;
    gap: 6px;
  }
  
  :deep(.el-tabs__item) {
    border: none !important;
    border-radius: 12px;
    padding: 12px 32px;
    margin: 0;
    color: #64748b;
    font-size: 15px;
    font-weight: 600;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    letter-spacing: 0.3px;
    min-width: 140px;
    text-align: center;
    
    &::before {
      content: '';
      position: absolute;
      left: 50%;
      bottom: 8px;
      width: 0;
      height: 2px;
      background: linear-gradient(90deg, #3b82f6, #2563eb);
      transform: translateX(-50%);
      transition: width 0.3s ease;
      border-radius: 2px;
    }
    
    &:hover {
      background: rgba(59, 130, 246, 0.08);
      color: #3b82f6;
      transform: translateY(-1px);
      
      &::before {
        width: 60%;
      }
    }
    
    &.is-active {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: white;
      box-shadow: 0 6px 20px rgba(59, 130,246, 0.35), 0 2px 8px rgba(59, 130, 246, 0.2);
      transform: translateY(-2px);
      
      &::before {
        width: 0;
      }
      
      &::after {
        content: '';
        position: absolute;
        inset: -2px;
        border-radius: 12px;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), transparent);
        pointer-events: none;
      }
    }
  }
  
  :deep(.el-tabs__active-bar) {
    display: none;
  }
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  padding-top: 20px;
}

.order-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.05);
    border-color: #e2e8f0;
  }
  
  &.active {
    border-color: #bfdbfe;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
  }

  .card-header {
    padding: 12px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .order-no { font-family: monospace; color: #64748b; font-size: 12px; }
  }

  .card-body {
    padding: 16px;
    
    .car-brief {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 16px;
      
      .icon-box {
        width: 32px;
        height: 32px;
        background: #eff6ff;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #3b82f6;
      }
      .car-name { font-weight: 700; color: #1e293b; font-size: 14px; }
      .car-plate { 
        background: #f1f5f9; 
        padding: 2px 8px; 
        border-radius: 4px; 
        font-size: 12px;
        color: #64748b;
        font-family: monospace;
        margin-left: auto;
      }
    }

    .detail-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .detail-item {
        display: flex;
        justify-content: space-between;
        font-size: 13px;
        
        .label { color: #94a3b8; }
        .value { color: #475569; font-weight: 500; }
      }
    }
  }

  .card-footer {
    padding: 12px 16px;
    border-top: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .amount-box {
      flex: 1;
      
      .label { font-size: 12px; color: #94a3b8; margin-right: 4px; }
      .amount { 
        font-size: 16px; 
        font-weight: 700; 
        color: #10b981;
        
        &.estimate { 
          color: #f59e0b;
          font-size: 14px;
        }
      }
      
      .fee-breakdown {
        display: flex;
        flex-direction: column;
        gap: 4px;
        
        .fee-item {
          display: flex;
          align-items: center;
          font-size: 13px;
          
          .fee-label {
            color: #64748b;
            font-weight: 500;
          }
          
          .fee-value {
            color: #10b981;
            font-weight: 700;
            margin-left: 4px;
            
            &.pending {
              color: #f59e0b;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

.form-container {
  max-width: 700px;
  padding-top: 20px;
  
  .custom-form {
    :deep(.el-input__wrapper) {
      border-radius: 12px;
      background: rgba(248, 250, 252, 0.6);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
      border: 1px solid rgba(226, 232, 240, 0.8);
      transition: all 0.3s ease;
      padding: 4px 16px;
      
      &:hover {
        background: white;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
        border-color: rgba(59, 130, 246, 0.3);
      }
      
      &.is-focus {
        background: white;
        box-shadow: 0 4px 16px rgba(59, 130, 246, 0.15);
        border-color: #3b82f6;
      }
    }
    
    :deep(.el-form-item__label) {
      font-weight: 600;
      color: #475569;
      font-size: 14px;
      margin-bottom: 8px;
    }
    
    :deep(.el-button) {
      border-radius: 50px;
      padding: 10px 32px;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
    
    :deep(.el-button--primary) {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
      
      &:hover {
        box-shadow: 0 6px 24px rgba(59, 130, 246, 0.5);
      }
    }
    
    :deep(.el-button--warning) {
      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      border: none;
      color: white;
      box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
      
      &:hover {
        box-shadow: 0 6px 24px rgba(245, 158, 11, 0.5);
      }
    }
  }
  
  .password-section {
    margin-top: 48px;
    padding: 32px;
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.8) 0%, rgba(241, 245, 249, 0.6) 100%);
    border-radius: 20px;
    border: 1px solid rgba(226, 232, 240, 0.8);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
    
    :deep(.el-divider) {
      margin: 0 0 28px 0;
      
      .el-divider__text {
        font-weight: 700;
        color: #334155;
        font-size: 15px;
        background: transparent;
      }
    }
    
    :deep(.el-alert) {
      margin-top: 20px;
      border-radius: 12px;
      background: rgba(239, 246, 255, 0.8);
      border: 1px solid rgba(147, 197, 253, 0.3);
    }
  }
}

.custom-form {
  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    background: #f8fafc;
    &:hover, &.is-focus {
      background: white;
      box-shadow: 0 0 0 1px #3b82f6 inset;
    }
  }
}

/* Detail Dialog Styles */
.detail-container {
  display: flex;
  gap: 24px;
  height: 400px;
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto;
  padding-right: 12px;
}

.detail-map {
  flex: 1;
  background: #f8fafc;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  background: #f1f5f9;
  
  .map-icon {
    font-size: 48px;
    margin-bottom: 16px;
    color: #cbd5e1;
  }
  
  p {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: #64748b;
  }
  
  .sub {
    font-size: 12px;
    color: #94a3b8;
  }
}

.info-block {
  .block-title {
    margin: 0 0 16px 0;
    font-size: 14px;
    color: #64748b;
    font-weight: 700;
    padding-left: 8px;
    border-left: 3px solid #3b82f6;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  
  .info-item {
    display: flex;
    flex-direction: column;
    
    .label {
      font-size: 12px;
      color: #94a3b8;
      margin-bottom: 4px;
    }
    
    .value {
      font-size: 14px;
      color: #1e293b;
      font-weight: 600;
      
      &.code { font-family: monospace; }
      &.plate { color: #3b82f6; }
      &.amount { 
        color: #10b981; 
        font-size: 16px; 
        
        &.pending { color: #f59e0b; }
      }
    }
  }
}

.route-timeline {
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
  padding-left: 8px;
  
  .route-point {
    display: flex;
    gap: 16px;
    position: relative;
    z-index: 2;
    
    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      margin-top: 6px;
      background: white;
      border: 2px solid #cbd5e1;
      flex-shrink: 0;
    }
    
    &.start .dot { border-color: #3b82f6; background: #3b82f6; }
    &.end .dot { border-color: #10b981; background: #10b981; }
    
    .content {
      display: flex;
      flex-direction: column;
      
      .time {
        font-size: 12px;
        color: #94a3b8;
      }
      .place {
        font-size: 14px;
        font-weight: 600;
        color: #334155;
      }
    }
  }
  
  .route-line {
    position: absolute;
    left: 12px;
    top: 14px;
    bottom: 24px;
    width: 2px;
    background: #e2e8f0;
    z-index: 1;
  }
}
</style>

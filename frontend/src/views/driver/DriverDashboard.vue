<template>
  <div class="driver-dashboard-page">
    <div class="content-wrapper">
      <el-card class="main-card" shadow="never">
        <!-- 头部概览 -->
        <div class="profile-header">
          <div class="user-info-large">
            <el-avatar :size="80" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" class="avatar" />
            <div class="user-text">
              <h2>{{ driverForm.name || '司机师傅' }}</h2>
              <div class="badges">
                <el-tag type="info" effect="plain" size="small" round>工号: {{ driverForm.id }}</el-tag>
                <el-tag type="primary" effect="light" size="small" round>在职</el-tag>
              </div>
            </div>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="custom-tabs">
          <!-- 1. 出车任务 -->
          <el-tab-pane label="出车任务" name="tasks">
            <!-- 任务子分类 -->
            <el-tabs v-model="activeTaskTab" type="card" class="task-sub-tabs">
              <!-- 现有任务 -->
              <el-tab-pane label="现有任务" name="current">
                <div class="tasks-list">
                  <el-empty v-if="currentTasks.length === 0" description="暂无进行中的任务" />
                  
                  <div v-else class="task-grid">
                    <div class="task-card active" v-for="task in currentTasks" :key="task.id">
                  <div class="card-header">
                    <span class="task-no">任务单号: {{ task.taskNo }}</span>
                    <el-tag :type="getStatusType(task.status)" size="small" effect="light">{{ getStatusText(task.status) }}</el-tag>
                  </div>
                  
                  <div class="card-body">
                    <!-- 客户信息 -->
                    <div class="customer-info">
                      <el-avatar :size="32" :icon="User" class="customer-avatar" />
                      <div class="customer-text">
                        <span class="name">{{ task.customerName }}</span>
                        <span class="phone">{{ task.customerPhone }}</span>
                      </div>
                      <el-button type="primary" link icon="Phone" @click="contactCustomer(task.customerPhone)">联系</el-button>
                    </div>

                    <div class="divider"></div>

                    <!-- 车辆信息 -->
                    <div class="car-brief">
                      <el-icon class="car-icon"><Van /></el-icon>
                      <span class="car-name">{{ task.carName }}</span>
                      <span class="car-plate">{{ task.carPlate }}</span>
                    </div>

                    <!-- 行程时间线 -->
                    <div class="route-timeline-mini">
                      <div class="route-point start">
                        <div class="dot"></div>
                        <div class="content">
                          <span class="time">{{ task.startTime }}</span>
                          <span class="place">{{ task.startPlace }}</span>
                        </div>
                      </div>
                      <div class="route-line"></div>
                      <div class="route-point end">
                        <div class="dot"></div>
                        <div class="content">
                          <span class="time">{{ task.endTime }}</span>
                          <span class="place">{{ task.endPlace }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="card-footer" v-if="task.status === 'pending'">
                    <el-button class="action-btn" type="primary" plain @click="completeTask(task)">确认完成任务</el-button>
                  </div>
                </div>
              </div>
                </div>
              </el-tab-pane>
              
              <!-- 历史任务 -->
              <el-tab-pane label="历史任务" name="history">
                <div class="tasks-list">
                  <el-empty v-if="historyTasks.length === 0" description="暂无历史任务记录" />
                  
                  <div v-else class="task-grid">
                    <div class="task-card" v-for="task in historyTasks" :key="task.id">
                  <div class="card-header">
                    <span class="task-no">任务单号: {{ task.taskNo }}</span>
                    <el-tag :type="getStatusType(task.status)" size="small" effect="light">{{ getStatusText(task.status) }}</el-tag>
                  </div>
                  
                  <div class="card-body">
                    <!-- 客户信息 -->
                    <div class="customer-info">
                      <el-avatar :size="32" :icon="User" class="customer-avatar" />
                      <div class="customer-text">
                        <span class="name">{{ task.customerName }}</span>
                        <span class="phone">{{ task.customerPhone }}</span>
                      </div>
                      <el-button type="primary" link icon="Phone" @click="contactCustomer(task.customerPhone)">联系</el-button>
                    </div>

                    <div class="divider"></div>

                    <!-- 车辆信息 -->
                    <div class="car-brief">
                      <el-icon class="car-icon"><Van /></el-icon>
                      <span class="car-name">{{ task.carName }}</span>
                      <span class="car-plate">{{ task.carPlate }}</span>
                    </div>

                    <!-- 行程时间线 -->
                    <div class="route-timeline-mini">
                      <div class="route-point start">
                        <div class="dot"></div>
                        <div class="content">
                          <span class="time">{{ task.startTime }}</span>
                          <span class="place">{{ task.startPlace }}</span>
                        </div>
                      </div>
                      <div class="route-line"></div>
                      <div class="route-point end">
                        <div class="dot"></div>
                        <div class="content">
                          <span class="time">{{ task.endTime }}</span>
                          <span class="place">{{ task.endPlace }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>

          <!-- 2. 个人信息 -->
          <el-tab-pane label="个人信息" name="profile">
            <div class="form-container">
              <el-form :model="driverForm" label-position="top" class="custom-form">
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="姓名">
                      <el-input v-model="driverForm.name" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="工号">
                      <el-input v-model="driverForm.id" disabled />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="基本工资">
                      <el-input v-model="driverForm.salary" disabled>
                        <template #prefix>¥</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="准驾车型">
                      <el-input v-model="driverForm.licenseType" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="入职日期">
                  <el-input v-model="driverForm.joinDate" disabled />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Phone, Van, Lock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDriverInfo, updateDriverInfo, changeDriverPassword, updateDriverStatus, getDriverTasks, completeRental } from '@/api/driver'
import { getUserInfo } from '@/utils/auth'

const activeTab = ref('tasks')
const activeTaskTab = ref('current') // 任务子标签页：current-现有任务, history-历史任务
const saving = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref(null)
const loading = ref(false)

// 司机信息
const driverForm = reactive({
  name: '',
  id: '',
  salary: '',
  licenseType: '',
  joinDate: ''
})

// 修改密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
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

// 任务数据
const tasks = ref([])
const currentTasks = ref([]) // 现有任务（进行中）
const historyTasks = ref([]) // 历史任务（已完成）

const getStatusType = (status) => {
  return status === 'pending' ? 'warning' : 'success'
}

const getStatusText = (status) => {
  return status === 'pending' ? '执行中' : '已完成'
}

const contactCustomer = (phone) => {
  ElMessageBox.alert(`客户电话：${phone}`, '联系客户', {
    confirmButtonText: '拨打',
    callback: () => {}
  })
}

const completeTask = async (task) => {
  try {
    await ElMessageBox.confirm(
      '确认该次出车任务已完成？完成后您的状态将变为"空闲"',
      '提示',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    // 司机端“完成任务”仅影响司机自身状态，不再修改租赁记录，
    // 避免与管理员的“还车结算”流程混淆。
    await updateDriverStatus(userInfo.userId, '空闲')

    // 重新加载司机信息与任务列表，以反映状态变化：
    // 当状态变为"空闲"时，不再展示任何现有任务。
    await loadDriverInfo()
    await loadDriverTasks()

    ElMessage.success('任务已完成，您的状态已更新为"空闲"')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成任务失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    // 调用API更新司机信息
    await updateDriverInfo(userInfo.userId, {
      dNo: driverForm.id,
      dName: driverForm.name,
      licenseType: driverForm.licenseType,
      salary: driverForm.salary,
      startWork: driverForm.joinDate,
      dStatus: '在岗' // 保持状态不变
    })
    
    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        const userInfo = getUserInfo()
        if (!userInfo || !userInfo.userId) {
          ElMessage.error('未找到用户信息，请重新登录')
          return
        }
        
        // 调用后端API修改密码
        await changeDriverPassword({
          userId: userInfo.userId,
          currentPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功，请妥善保管新密码')
        resetPasswordForm()
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.response?.data?.message || '修改密码失败，请检查当前密码是否正确')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 加载司机信息
const loadDriverInfo = async () => {
  loading.value = true
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    // 获取司机详细信息
    const driverInfo = await getDriverInfo(userInfo.userId)
    
    // 填充表单数据
    Object.assign(driverForm, {
      name: driverInfo.dName || '',
      id: driverInfo.dNo || '',
      salary: driverInfo.salary || '',
      licenseType: driverInfo.licenseType || '',
      joinDate: driverInfo.startWork || ''
    })
  } catch (error) {
    console.error('加载司机信息失败:', error)
    ElMessage.error('加载信息失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

// 加载司机任务
const loadDriverTasks = async () => {
  try {
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      return
    }
    
    // 获取司机的租赁任务
    const rentals = await getDriverTasks(userInfo.userId)
    
    // 将后端数据转换为前端需要的格式
    const allTasks = rentals.map(rental => {
      // 与订单管理保持一致：仅根据金额是否结算(amount 是否有值)判断订单是否已完成
      const hasAmount = rental.amount != null && rental.amount !== 0
      const status = hasAmount ? 'completed' : 'pending'
      
      return {
        id: rental.invoiceNo,
        taskNo: rental.invoiceNo,
        status,
        customerName: rental.customerName || '未知客户',
        customerPhone: rental.customerPhone || '无',
        carName: rental.carBrand && rental.carType ? `${rental.carBrand} ${rental.carType}` : (rental.carBrand || rental.carType || '未知车辆'),
        carPlate: rental.carId || '未知车牌',
        startTime: rental.loanDate ? new Date(rental.loanDate).toLocaleString('zh-CN') : '未知',
        startPlace: rental.loanPlace || '未知地点',
        endTime: rental.returnDate ? new Date(rental.returnDate).toLocaleString('zh-CN') : '待定',
        endPlace: rental.returnPlace || '未知地点'
      }
    })

    // 根据司机当前状态决定是否还展示现有任务：
    // 只有当司机 dStatus 为 "在岗" 时，才显示进行中的任务；
    // 若司机状态为 "空闲"，即使有未结算订单，也不再给该司机展示现有任务。
    const driverInfo = await getDriverInfo(userInfo.userId)
    const onDuty = driverInfo && driverInfo.dStatus === '在岗'

    tasks.value = allTasks
    if (onDuty) {
      currentTasks.value = allTasks.filter(task => task.status === 'pending')
      historyTasks.value = allTasks.filter(task => task.status === 'completed')
    } else {
      currentTasks.value = []
      historyTasks.value = allTasks
    }
  } catch (error) {
    console.error('加载任务失败:', error)
    // 不显示错误提示，避免干扰用户
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadDriverInfo()
  loadDriverTasks()
})
</script>

<style scoped lang="scss">
.driver-dashboard-page {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.main-card {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
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
  padding-bottom: 30px;
  border-bottom: 1px solid #f1f5f9;

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

  .header-stats {
    display: flex;
    align-items: center;
    gap: 32px;
    padding: 16px 32px;
    background: #f8fafc;
    border-radius: 16px;

    .stat-item {
      text-align: center;
      .stat-value { font-size: 24px; font-weight: 700; color: #1e293b; font-family: 'Inter', sans-serif; }
      .stat-label { font-size: 12px; color: #64748b; margin-top: 4px; }
    }
    
    .stat-divider { width: 1px; height: 32px; background: #cbd5e1; }
  }
}

.custom-tabs {
  :deep(.el-tabs__nav-wrap::after) { height: 1px; background-color: #f1f5f9; }
  :deep(.el-tabs__item) {
    font-size: 16px;
    color: #64748b;
    &.is-active { color: #3b82f6; font-weight: 600; }
  }
}

/* 任务子标签页样式 - 现代分段控制器风格 */
.task-sub-tabs {
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
      box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35), 0 2px 8px rgba(59, 130, 246, 0.2);
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

/* Tasks Grid */
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  padding-top: 20px;
}

.task-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  }
  
  &.active {
    border-color: #bfdbfe;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
  }

  .card-header {
    padding: 12px 20px;
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .task-no { font-family: monospace; color: #64748b; font-size: 12px; }
  }

  .card-body {
    padding: 20px;
    
    .customer-info {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
      
      .customer-avatar { background: #eff6ff; color: #3b82f6; }
      .customer-text {
        flex: 1;
        display: flex;
        flex-direction: column;
        .name { font-weight: 600; color: #1e293b; font-size: 14px; }
        .phone { font-size: 12px; color: #94a3b8; }
      }
    }

    .divider { height: 1px; background: #f1f5f9; margin: 12px 0; }

    .car-brief {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;
      color: #475569;
      font-size: 13px;
      .car-icon { color: #3b82f6; }
      .car-name { font-weight: 600; }
      .car-plate { background: #f1f5f9; padding: 2px 6px; border-radius: 4px; font-size: 12px; }
    }
  }

  .card-footer {
    padding: 12px 20px;
    border-top: 1px solid #f1f5f9;
    background: #f8fafc;
    .action-btn { width: 100%; border-radius: 8px; }
  }
}

/* Timeline Mini */
.route-timeline-mini {
  position: relative;
  padding-left: 12px;
  
  .route-line {
    position: absolute;
    left: 16px;
    top: 8px;
    bottom: 8px;
    width: 2px;
    background: #e2e8f0;
    z-index: 0;
  }
  
  .route-point {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    position: relative;
    z-index: 1;
    margin-bottom: 12px;
    
    &:last-child { margin-bottom: 0; }
    
    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      margin-top: 6px;
      border: 2px solid #cbd5e1;
      background: white;
    }
    
    &.start .dot { border-color: #3b82f6; background: #3b82f6; }
    &.end .dot { border-color: #10b981; background: #10b981; }
    
    .content {
      display: flex;
      flex-direction: column;
      .time { font-size: 12px; color: #94a3b8; margin-bottom: 2px; }
      .place { font-size: 13px; color: #1e293b; font-weight: 500; line-height: 1.4; }
    }
  }
}

/* Form Container */
.form-container {
  max-width: 700px;
  padding-top: 20px;
  
  .custom-form {
    /* 输入框样式 - 参考管理员端 */
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
    
    /* 表单项标签 */
    :deep(.el-form-item__label) {
      font-weight: 600;
      color: #475569;
      font-size: 14px;
      margin-bottom: 8px;
    }
    
    /* 按钮样式 - 参考管理员端 */
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
  
  /* 密码修改区域样式 */
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
      
      .el-alert__title {
        color: #1e40af;
        font-weight: 600;
        font-size: 14px;
      }
      
      .el-alert__content {
        color: #475569;
        font-size: 13px;
        line-height: 1.8;
      }
    }
  }
}
</style>

<style lang="scss">
/* 全局弹窗样式优化 - 需要非scoped样式 */
.el-message-box {
  border-radius: 20px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  border: none !important;
  
  .el-message-box__header {
    padding: 24px 24px 16px !important;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-bottom: 1px solid #e2e8f0;
    
    .el-message-box__title {
      font-size: 18px !important;
      font-weight: 700 !important;
      color: #1e293b !important;
    }
    
    .el-message-box__headerbtn {
      top: 20px !important;
      right: 20px !important;
      
      .el-message-box__close {
        color: #64748b !important;
        font-size: 18px !important;
        
        &:hover {
          color: #1e293b !important;
        }
      }
    }
  }
  
  .el-message-box__content {
    padding: 24px !important;
    
    .el-message-box__message {
      font-size: 15px !important;
      color: #475569 !important;
      line-height: 1.6 !important;
      
      p {
        margin: 0;
        line-height: 1.6;
      }
    }
    
    .el-message-box__status {
      font-size: 28px !important;
      
      &.el-message-box-icon--warning {
        color: #f59e0b !important;
      }
      
      &.el-message-box-icon--info {
        color: #3b82f6 !important;
      }
      
      &.el-message-box-icon--success {
        color: #10b981 !important;
      }
      
      &.el-message-box-icon--error {
        color: #ef4444 !important;
      }
    }
  }
  
  .el-message-box__btns {
    padding: 16px 24px 24px !important;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .el-button {
      border-radius: 12px !important;
      padding: 10px 24px !important;
      font-weight: 600 !important;
      font-size: 14px !important;
      transition: all 0.3s ease !important;
      border: none !important;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15) !important;
      }
    }
    
    .el-button--default {
      background: white !important;
      color: #64748b !important;
      border: 1px solid #e2e8f0 !important;
      
      &:hover {
        background: #f8fafc !important;
        color: #1e293b !important;
        border-color: #cbd5e1 !important;
      }
    }
    
    .el-button--primary {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
      color: white !important;
      box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4) !important;
      
      &:hover {
        box-shadow: 0 6px 24px rgba(59, 130, 246, 0.5) !important;
      }
    }
  }
}

/* Message 提示框样式优化 */
.el-message {
  border-radius: 12px !important;
  padding: 16px 20px !important;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1) !important;
  border: none !important;
  
  .el-message__icon {
    font-size: 20px !important;
  }
  
  .el-message__content {
    font-size: 14px !important;
    font-weight: 500 !important;
  }
  
  &.el-message--success {
    background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%) !important;
    
    .el-message__icon {
      color: #10b981 !important;
    }
    
    .el-message__content {
      color: #047857 !important;
    }
  }
  
  &.el-message--warning {
    background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%) !important;
    
    .el-message__icon {
      color: #f59e0b !important;
    }
    
    .el-message__content {
      color: #b45309 !important;
    }
  }
  
  &.el-message--error {
    background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%) !important;
    
    .el-message__icon {
      color: #ef4444 !important;
    }
    
    .el-message__content {
      color: #b91c1c !important;
    }
  }
  
  &.el-message--info {
    background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%) !important;
    
    .el-message__icon {
      color: #3b82f6 !important;
    }
    
    .el-message__content {
      color: #1e40af !important;
    }
  }
}
</style>

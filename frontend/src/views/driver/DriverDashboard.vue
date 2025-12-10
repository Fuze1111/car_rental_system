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
            <div class="tasks-list">
              <el-empty v-if="tasks.length === 0" description="暂无待执行任务，休息一下吧" />
              
              <div v-else class="task-grid">
                <div class="task-card" v-for="task in tasks" :key="task.id" :class="{ 'active': task.status === 'pending' }">
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
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Phone, Van } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('tasks')
const saving = ref(false)

// 司机信息
const driverForm = reactive({
  name: '王师傅',
  id: 'D001',
  salary: '5000.00',
  licenseType: 'C1',
  joinDate: '2020-05-01'
})

// Mock 任务数据
const tasks = ref([
  {
    id: 1,
    taskNo: 'T2023122001',
    status: 'pending', // pending, completed
    customerName: '李先生',
    customerPhone: '13900000000',
    carName: 'Tesla Model 3',
    carPlate: '京AD12345',
    startTime: '2023-12-20 09:30',
    startPlace: '上海虹桥国际机场',
    endTime: '2023-12-22 18:00',
    endPlace: '上海浦东国际机场'
  },
  {
    id: 2,
    taskNo: 'T2023121805',
    status: 'completed',
    customerName: '张女士',
    customerPhone: '13700000000',
    carName: 'Mercedes S-Class',
    carPlate: '京A66666',
    startTime: '2023-12-18 14:00',
    startPlace: '北京大兴国际机场',
    endTime: '2023-12-18 16:00',
    endPlace: '北京朝阳区国贸三期'
  }
])

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

const completeTask = (task) => {
  ElMessageBox.confirm(
    '确认该次出车任务已完成？',
    '提示',
    { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    task.status = 'completed'
    ElMessage.success('任务状态已更新')
  })
}

const saveProfile = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    ElMessage.success('个人信息保存成功')
  }, 800)
}
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
  max-width: 600px;
  padding-top: 20px;
  
  .custom-form :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    background: #f8fafc;
    border-radius: 8px;
    padding: 1px 11px;
    &:hover, &.is-focus {
      background: white;
      box-shadow: 0 0 0 1px #3b82f6 inset;
    }
  }
}
</style>

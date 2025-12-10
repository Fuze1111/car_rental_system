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
            <div class="orders-list">
              <el-empty v-if="myOrders.length === 0" description="暂无订单，快去租辆车吧！">
                <el-button type="primary" round @click="$router.push('/user/cars')">去租车</el-button>
              </el-empty>

              <div v-else class="order-grid">
                <div class="order-card" v-for="order in myOrders" :key="order.id">
                  <div class="card-header">
                    <span class="order-no">{{ order.invoiceNo }}</span>
                    <el-tag :type="order.status === '已完成' ? 'success' : 'primary'" size="small" effect="light">{{ order.status }}</el-tag>
                  </div>
                  <div class="card-body">
                    <div class="car-brief">
                      <div class="icon-box"><el-icon><Van /></el-icon></div>
                      <span class="car-name">{{ order.carName }}</span>
                    </div>
                    <div class="detail-list">
                      <div class="detail-item">
                        <span class="label">日期</span>
                        <span class="value">{{ order.dates }}</span>
                      </div>
                      <div class="detail-item">
                        <span class="label">取车</span>
                        <span class="value">{{ order.loanPlace }}</span>
                      </div>
                      <div class="detail-item">
                        <span class="label">还车</span>
                        <span class="value">{{ order.returnPlace }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="card-footer">
                    <div class="amount-box">
                      <span class="label">实付</span>
                      <span class="amount">¥{{ order.amount }}</span>
                    </div>
                    <el-button link type="primary">详情</el-button>
                  </div>
                </div>
              </div>
            </div>
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
                    <el-form-item label="手机号">
                      <el-input v-model="userForm.phone" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="电子邮箱">
                  <el-input v-model="userForm.email" />
                </el-form-item>

                <el-form-item label="驾照号码">
                  <el-input v-model="userForm.licenseNo" placeholder="请输入驾照号码" />
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
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Van } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('orders')
const saving = ref(false)

const userForm = reactive({
  name: '张三',
  phone: '13800138000',
  email: 'zhangsan@example.com',
  licenseNo: '110101199001011234',
  address: '北京市朝阳区某某街道123号'
})

const myOrders = ref([
  {
    id: 1,
    invoiceNo: 'INV2023120101',
    carName: 'Toyota Camry',
    dates: '12/01 - 12/03',
    loanPlace: '北京大兴机场',
    returnPlace: '北京首都机场',
    amount: 650,
    status: '已完成'
  },
  {
    id: 2,
    invoiceNo: 'INV2023121502',
    carName: 'Tesla Model 3',
    dates: '12/20 - 12/22',
    loanPlace: '上海虹桥机场',
    returnPlace: '上海浦东机场',
    amount: 800,
    status: '进行中'
  }
])

const saveProfile = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    ElMessage.success('个人信息保存成功')
  }, 800)
}
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
      .car-name { font-weight: 700; color: #1e293b; }
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
      .label { font-size: 12px; color: #94a3b8; margin-right: 4px; }
      .amount { font-size: 16px; font-weight: 700; color: #1e293b; }
    }
  }
}

.form-container {
  max-width: 600px;
  padding-top: 20px;
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
</style>

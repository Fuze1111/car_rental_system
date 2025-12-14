<template>
  <div class="settings-container">
    <div class="settings-content">
      <el-button type="primary" class="save-all-btn" :loading="saving" @click="handleSaveAll">
        <el-icon><Check /></el-icon> 保存所有更改
      </el-button>

      <el-tabs v-model="activeTab" class="glass-tabs">
        <!-- 个人账户 -->
        <el-tab-pane label="账户设置" name="account">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><User /></el-icon>
              <span>账户设置</span>
            </div>
          </template>

          <div class="tab-content">
            <div class="profile-header">
              <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="profile-avatar" />
              <div class="profile-info">
                <h3>{{ accountForm.username || '管理员' }}</h3>
                <p>Super Administrator</p>
              </div>
            </div>

            <el-divider />

            <el-form :model="accountForm" label-position="top" class="custom-form">
              <el-row :gutter="40">
                <el-col :span="12">
                  <el-form-item label="用户名">
                    <el-input v-model="accountForm.username" />
                  </el-form-item>
                </el-col>
              </el-row>

              <div class="section-title">安全设置</div>
              
              <el-row :gutter="40">
                <el-col :span="12">
                  <el-form-item label="当前密码">
                    <el-input v-model="accountForm.currentPassword" type="password" show-password />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="新密码">
                    <el-input v-model="accountForm.newPassword" type="password" show-password />
                  </el-form-item>
                </el-col>
              </el-row>

              <div style="margin-top: 40px; display: flex; justify-content: flex-end;">
                <el-button type="danger" plain size="large" @click="handleLogout">
                  退出登录
                </el-button>
              </div>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知偏好" name="notification">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><Bell /></el-icon>
              <span>通知偏好</span>
            </div>
          </template>

          <div class="tab-content">
            <div class="notification-group">
              <div class="group-title">邮件通知</div>
              
              <div class="switch-row">
                <div class="switch-info">
                  <span class="label">新订单提醒</span>
                  <span class="desc">当有新订单创建时发送邮件</span>
                </div>
                <el-switch v-model="notifyForm.newOrder" />
              </div>

              <div class="switch-row">
                <div class="switch-info">
                  <span class="label">车辆异常警告</span>
                  <span class="desc">当车辆状态异常或需要维修时通知</span>
                </div>
                <el-switch v-model="notifyForm.carAlert" />
              </div>
            </div>

            <el-divider />

            <div class="notification-group">
              <div class="group-title">系统通知</div>
              
              <div class="switch-row">
                <div class="switch-info">
                  <span class="label">每日报表推送</span>
                  <span class="desc">每天早上 8:00 推送昨日运营简报</span>
                </div>
                <el-switch v-model="notifyForm.dailyReport" />
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 关于系统 -->
        <el-tab-pane label="关于系统" name="about">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><InfoFilled /></el-icon>
              <span>关于系统</span>
            </div>
          </template>

          <div class="tab-content about-content">
            <div class="system-logo-large">
              <el-icon><Van /></el-icon>
            </div>
            <h2>Car Rental System</h2>
            <p class="version">Version 2.5.0 (Pro)</p>
            
            <div class="about-grid">
              <div class="about-item">
                <span class="label">Vue 版本</span>
                <span class="value">3.3.4</span>
              </div>
              <div class="about-item">
                <span class="label">Element Plus</span>
                <span class="value">2.3.8</span>
              </div>
              <div class="about-item">
                <span class="label">构建时间</span>
                <span class="value">2025-12-09</span>
              </div>
              <div class="about-item">
                <span class="label">许可证</span>
                <span class="value">MIT License</span>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Bell, InfoFilled, Check, Van } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { changeAdminPassword } from '@/api/admin'
import { getUserInfo, clearAuth } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const activeTab = ref('account')
const saving = ref(false)
const passwordChanging = ref(false)

const accountForm = reactive({
  username: '',
  adminId: '',
  currentPassword: '',
  newPassword: ''
})

const notifyForm = reactive({
  newOrder: true,
  carAlert: true,
  dailyReport: false
})

// 加载管理员信息
const loadAdminInfo = () => {
  const userInfo = getUserInfo()
  if (userInfo) {
    accountForm.username = userInfo.userName || 'admin'
    accountForm.adminId = userInfo.userId || ''
  }
}

const handleSaveAll = async () => {
  // 验证密码修改
  if (accountForm.currentPassword && accountForm.newPassword) {
    await handlePasswordChange()
  } else if (accountForm.currentPassword || accountForm.newPassword) {
    ElMessage.warning('请同时填写当前密码和新密码')
    return
  }
  
  // 保存通知设置到localStorage
  saving.value = true
  try {
    localStorage.setItem('notificationSettings', JSON.stringify(notifyForm))
    setTimeout(() => {
      saving.value = false
      ElMessage.success('系统设置已成功保存')
    }, 500)
  } catch (error) {
    saving.value = false
    ElMessage.error('保存设置失败')
  }
}

const handlePasswordChange = async () => {
  if (!accountForm.currentPassword) {
    ElMessage.warning('请输入当前密码')
    return
  }
  
  if (!accountForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  if (accountForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度至少6位')
    return
  }
  
  passwordChanging.value = true
  try {
    await changeAdminPassword({
      userId: accountForm.adminId,
      currentPassword: accountForm.currentPassword,
      newPassword: accountForm.newPassword
    })
    
    ElMessage.success('密码修改成功')
    
    // 清空密码输入框
    accountForm.currentPassword = ''
    accountForm.newPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.response?.data?.message || '修改密码失败')
  } finally {
    passwordChanging.value = false
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 清除认证信息
    clearAuth()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  loadAdminInfo()
  
  // 加载通知偏好设置
  try {
    const savedSettings = localStorage.getItem('notificationSettings')
    if (savedSettings) {
      const settings = JSON.parse(savedSettings)
      Object.assign(notifyForm, settings)
    }
  } catch (error) {
    console.error('加载通知设置失败:', error)
  }
})
</script>

<style scoped lang="scss">
.settings-container {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.settings-content {
  position: relative; /* For absolute button positioning */
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
  min-height: 600px;
}

.save-all-btn {
  position: absolute;
  top: 14px;
  right: 24px;
  z-index: 10;
  padding: 8px 20px;
  border-radius: 8px;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
  }
}

/* Tab 样式定制 */
.glass-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: rgba(255, 255, 255, 0.5);
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }

  :deep(.el-tabs__item) {
    height: 60px;
    font-size: 15px;
    color: #64748b;
    transition: all 0.3s;
    
    &.is-active {
      color: #409eff;
      font-weight: 600;
      
      .custom-tab-label {
        background: rgba(236, 245, 255, 0.8);
        color: #409eff;
      }
    }
    
    &:hover:not(.is-active) {
      color: #1e293b;
    }
  }
  
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: transparent;
  }

  :deep(.el-tabs__active-bar) {
    height: 3px;
    border-radius: 3px;
    background: linear-gradient(90deg, #409eff, #8b5cf6);
  }
}

.custom-tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.tab-content {
  padding: 40px;
  max-width: 800px;
}

/* 表单样式优化 */
.custom-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #475569;
    padding-bottom: 8px;
  }

  :deep(.el-input__wrapper), :deep(.el-textarea__wrapper) {
    background: rgba(255, 255, 255, 0.6);
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    padding: 8px 12px;
    border-radius: 10px;
    transition: all 0.3s;

    &:hover {
      background: white;
      box-shadow: 0 0 0 1px #cbd5e1 inset;
    }

    &.is-focus {
      background: white;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) inset, 0 0 0 1px #409eff inset;
    }
  }
}

.upload-container {
  display: flex;
  align-items: center;
  gap: 24px;
  
  .logo-preview {
    border: 2px solid #e2e8f0;
    padding: 4px;
    background: white;
    border-radius: 16px;
  }
}

.switch-item, .switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(248, 250, 252, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.8);
    transform: translateX(4px);
  }

  .switch-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .switch-label, .label {
      font-weight: 600;
      color: #1e293b;
      font-size: 15px;
    }

    .switch-desc, .desc {
      font-size: 13px;
      color: #94a3b8;
    }
  }
}

.switch-row {
  margin-bottom: 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

/* 账户部分 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;

  .profile-avatar {
    border: 4px solid white;
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  }

  .profile-info {
    h3 { margin: 0 0 8px 0; font-size: 24px; color: #1e293b; }
    p { margin: 0 0 12px 0; color: #64748b; }
  }
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 32px 0 24px;
  display: flex;
  align-items: center;
  
  &::before {
    content: '';
    width: 4px;
    height: 18px;
    background: #409eff;
    margin-right: 12px;
    border-radius: 2px;
  }
}

/* 通知部分 */
.notification-group {
  margin-bottom: 32px;
  
  .group-title {
    font-size: 16px;
    font-weight: 600;
    color: #64748b;
    margin-bottom: 16px;
    padding-left: 8px;
  }
}

/* 关于页面 */
.about-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding-top: 60px;

  .system-logo-large {
    font-size: 64px;
    color: #409eff;
    margin-bottom: 24px;
    background: linear-gradient(135deg, #eff6ff, #ffffff);
    width: 120px;
    height: 120px;
    border-radius: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 12px 32px rgba(64, 158, 255, 0.15);
  }

  h2 { margin: 0 0 8px; color: #1e293b; }
  .version { color: #94a3b8; font-family: monospace; font-size: 16px; margin-bottom: 40px; }

  .about-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
    margin-bottom: 40px;
    width: 100%;
    max-width: 600px;

    .about-item {
      display: flex;
      justify-content: space-between;
      padding: 16px 24px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.02);
      
      .label { color: #64748b; }
      .value { font-weight: 600; color: #1e293b; }
    }
  }

  .actions {
    display: flex;
    gap: 16px;
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

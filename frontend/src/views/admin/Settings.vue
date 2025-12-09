<template>
  <div class="settings-container">
    <div class="settings-content">
      <el-button type="primary" class="save-all-btn" :loading="saving" @click="handleSaveAll">
        <el-icon><Check /></el-icon> 保存所有更改
      </el-button>

      <el-tabs v-model="activeTab" class="glass-tabs">
        <!-- 基本设置 -->
        <el-tab-pane label="基本设置" name="general">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><Operation /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <div class="tab-content">
            <el-form :model="generalForm" label-position="top" class="custom-form">
              <el-row :gutter="40">
                <el-col :span="12">
                  <el-form-item label="系统名称">
                    <el-input v-model="generalForm.systemName" placeholder="Car Rental System" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="备案号">
                    <el-input v-model="generalForm.icpCode" placeholder="京ICP备..." />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="系统Logo">
                <div class="upload-container">
                  <el-avatar :size="80" shape="square" :src="generalForm.logoUrl" class="logo-preview" />
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :show-file-list="false"
                    :auto-upload="false"
                  >
                    <el-button type="primary" plain>更换Logo</el-button>
                    <template #tip>
                      <div class="el-upload__tip">只能上传 jpg/png 文件，且不超过 2MB</div>
                    </template>
                  </el-upload>
                </div>
              </el-form-item>

              <el-form-item label="系统维护模式">
                <div class="switch-item">
                  <div class="switch-info">
                    <span class="switch-label">开启维护模式</span>
                    <span class="switch-desc">开启后，除管理员外所有用户将无法访问系统</span>
                  </div>
                  <el-switch v-model="generalForm.maintenanceMode" />
                </div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

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
                <h3>Admin User</h3>
                <p>Super Administrator</p>
                <el-button link type="primary">更换头像</el-button>
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
                <el-col :span="12">
                  <el-form-item label="电子邮箱">
                    <el-input v-model="accountForm.email" />
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

              <div class="switch-row">
                <div class="switch-info">
                  <span class="label">用户注册提醒</span>
                  <span class="desc">新用户注册待审核时通知</span>
                </div>
                <el-switch v-model="notifyForm.newUser" />
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

            <div class="actions">
              <el-button type="primary" plain>检查更新</el-button>
              <el-button link>开发文档</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Operation, User, Bell, InfoFilled, Check, Van } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('general')
const saving = ref(false)

const generalForm = reactive({
  systemName: 'Car Rental Management',
  icpCode: '京ICP备20250001号',
  logoUrl: 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png',
  maintenanceMode: false
})

const accountForm = reactive({
  username: 'admin',
  email: 'admin@carrental.com',
  currentPassword: '',
  newPassword: ''
})

const notifyForm = reactive({
  newOrder: true,
  carAlert: true,
  dailyReport: false,
  newUser: true
})

const handleSaveAll = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    ElMessage.success('系统设置已成功保存')
  }, 1000)
}
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

<template>
  <div class="login-container">
    <div class="login-overlay"></div>
    <div class="login-content">
      <div class="brand-section">
        <div class="brand-decoration"></div>
        <div class="brand-content">
          <div class="logo-wrapper">
            <el-icon :size="50"><Van /></el-icon>
          </div>
          <h1>CAR RENTAL</h1>
          <div class="brand-divider"></div>
          <p class="subtitle">尊享出行 · 极致体验</p>
          <p class="description">Professional Car Rental System</p>
        </div>
      </div>
      
      <div class="login-box">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>请选择您的身份进行操作</p>
        </div>
        
        <div class="role-switch-container">
          <el-tabs v-model="activeRole" class="role-tabs custom-tabs" @tab-click="handleRoleChange" stretch>
            <el-tab-pane label="客户" name="customer">
              <template #label>
                <div class="tab-label">
                  <el-icon><User /></el-icon>
                  <span>客户</span>
                </div>
              </template>
            </el-tab-pane>
            <el-tab-pane label="司机" name="driver">
              <template #label>
                <div class="tab-label">
                  <el-icon><Van /></el-icon>
                  <span>司机</span>
                </div>
              </template>
            </el-tab-pane>
            <el-tab-pane label="管理员" name="admin">
              <template #label>
                <div class="tab-label">
                  <el-icon><Management /></el-icon>
                  <span>管理员</span>
                </div>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <transition name="fade-slide" mode="out-in">
          <el-form :key="activeRole" :model="loginForm" :rules="rules" ref="loginFormRef" size="large" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                :placeholder="usernamePlaceholder"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码" 
                show-password
                prefix-icon="Lock"
              />
            </el-form-item>
            
            <div class="form-actions">
              <el-button type="primary" @click="handleLogin" :loading="loading" class="submit-btn" round>
                登 录
              </el-button>
              <div class="extra-links" v-if="activeRole !== 'admin'">
                <span>还没有账号？</span>
                <el-link type="primary" @click="handleRegister">立即注册</el-link>
              </div>
            </div>
          </el-form>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { User, Van, Management, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { setUserInfo, setUserRole } from '@/utils/auth'

const router = useRouter()
const activeRole = ref('customer')
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const usernamePlaceholder = computed(() => {
  switch (activeRole.value) {
    case 'customer': return '请输入客户账号/手机号'
    case 'driver': return '请输入司机工号/手机号'
    case 'admin': return '请输入管理员账号'
    default: return '请输入账号'
  }
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleRoleChange = () => {
  loginForm.username = ''
  loginForm.password = ''
  // 移除校验结果
  if(loginFormRef.value) loginFormRef.value.clearValidate()
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用登录API
        const response = await login({
          username: loginForm.username,
          password: loginForm.password,
          role: activeRole.value
        })
        
        // 保存用户信息到localStorage
        setUserInfo(response)
        setUserRole(activeRole.value)
        
        ElMessage.success(response.message || '登录成功')
        
        // 根据角色跳转
        if (activeRole.value === 'admin') {
          router.push('/admin/dashboard')
        } else if (activeRole.value === 'customer') {
          router.push('/user/cars')
        } else if (activeRole.value === 'driver') {
          router.push('/driver/dashboard')
        }
      } catch (error) {
        console.error('登录失败:', error)
        // 错误处理已在axios拦截器中完成
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = () => {
  router.push('/register')
}
</script>

<style scoped lang="scss">
/* 动画定义 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes pulse {
  0% { transform: scale(1); box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08); }
  50% { transform: scale(1.08); box-shadow: 0 12px 28px rgba(64, 158, 255, 0.3); }
  100% { transform: scale(1); box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08); }
}

.login-container {
  width: 100%;
  height: 100vh;
  position: relative;
  /* 使用本地背景图 */
  background-image: url('@/assets/images/bg.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1); /* 亮色遮罩，稍微提亮 */
  backdrop-filter: blur(4px);
  z-index: 1;
}

.login-content {
  position: relative;
  z-index: 2;
  display: flex;
  width: 900px;
  height: 550px;
  background: rgba(255, 255, 255, 0.6); /* 增加透明度：从0.75降到0.6 */
  backdrop-filter: blur(25px); /* 稍微增加一点模糊度来平衡透明度带来的杂乱感 */
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15); /* 柔和阴影 */
  border: 1px solid rgba(255, 255, 255, 0.6);
  overflow: hidden;
  /* 修复：直接使用动画，不设置初始不透明度，靠动画的第一帧 */
  animation: fadeInUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  
  @media (max-width: 960px) {
    width: 90%;
    flex-direction: column;
    height: auto;
  }
}

.brand-section {
  flex: 1;
  /* 米白色底色，增加半透明 */
  background: rgba(253, 251, 247, 0.85);
  background-image: linear-gradient(135deg, rgba(253, 251, 247, 0.85) 0%, rgba(244, 246, 249, 0.85) 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #303133; /* 深色文字 */
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}

.brand-decoration {
  position: absolute;
  top: -50px;
  left: -50px;
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #409eff 0%, #a0cfff 100%); /* 装饰球保留蓝色点缀 */
  opacity: 0.1;
  border-radius: 50%;
  z-index: 1;
  /* 修复：确保浮动动画生效 */
  animation: float 6s ease-in-out infinite;
}

.brand-decoration::after {
  content: '';
  position: absolute;
  top: 250px;
  left: 200px;
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #409eff 0%, #a0cfff 100%);
  opacity: 0.6;
  border-radius: 50%;
  animation: float 8s ease-in-out infinite reverse;
}

.brand-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  color: #409eff; /* 图标颜色 */
  /* 修复：拆分动画，进场动画结束后保留 pulse */
  animation: fadeInUp 0.8s 0.2s cubic-bezier(0.2, 0.8, 0.2, 1) both, 
             pulse 3s ease-in-out infinite 1s; 
}

.brand-section h1 {
  font-size: 2.8rem;
  margin: 0;
  font-weight: 800;
  letter-spacing: 4px;
  color: #2c3e50;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  text-shadow: none;
  /* 修复：添加 both 确保初始状态为隐藏 */
  animation: fadeInUp 0.8s 0.3s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.brand-divider {
  width: 60px;
  height: 4px;
  background: #409eff;
  margin: 20px 0;
  border-radius: 2px;
  animation: fadeInUp 0.8s 0.4s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.subtitle {
  font-size: 1.4rem;
  font-weight: 500;
  margin: 0 0 8px;
  letter-spacing: 1px;
  color: #606266;
  animation: fadeInUp 0.8s 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.description {
  font-size: 0.9rem;
  opacity: 0.8;
  margin: 0;
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: #909399;
  animation: fadeInUp 0.8s 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.login-box {
  flex: 1;
  padding: 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.3); /* 稍微更透明一点 */
  color: #303133;
  
  @media (max-width: 960px) {
    padding: 30px;
  }
}

.login-header {
  margin-bottom: 30px;
  text-align: center; /* 改为居中对齐，与胶囊条呼应 */
  
  h2 {
    font-size: 28px; /* 加大字号 */
    color: #2c3e50; /* 深蓝灰色，比纯黑更有质感 */
    margin: 0 0 12px;
    font-weight: 700;
    letter-spacing: 2px; /* 增加字间距 */
    font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  }
  
  p {
    color: #909399; /* 浅灰色 */
    font-size: 15px;
    margin: 0;
    letter-spacing: 1px; /* 增加字间距 */
    font-weight: 400;
  }
}

.role-switch-container {
  margin-bottom: 25px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 4px;
}

.custom-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    border: none;
  }
  
  :deep(.el-tabs__nav-wrap) {
    &::after {
      display: none; /* 移除默认的底部灰线 */
    }
  }

  :deep(.el-tabs__active-bar) {
    display: none; /* 移除默认的蓝色指示条，我们用背景色代替 */
  }

  :deep(.el-tabs__item) {
    padding: 0; /* 移除左右 padding，因为已经 stretch 了 */
    height: 40px;
    display: flex; /* 使用 flex 布局 */
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中 */
    border-radius: 6px;
    color: #606266;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &.is-active {
      background: white;
      color: #409eff;
      font-weight: 600;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    &:hover:not(.is-active) {
      color: #409eff;
      background: rgba(255, 255, 255, 0.5);
    }
  }
}

.tab-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px; /* 稍微增加一点图标和文字的间距 */
  width: 100%;
}

/* 表单切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.login-form {
  /* ... existing styles ... */
  min-height: 200px; /* 防止切换时高度塌陷导致抖动 */
  :deep(.el-input__wrapper) {
    background-color: white;
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc inset;
    }
    &.is-focus {
       box-shadow: 0 0 0 1px #409eff inset;
    }
  }
  
  :deep(.el-input__inner) {
    color: #303133;
    &::placeholder {
      color: #a8abb2;
    }
  }
  
  :deep(.el-input__prefix-inner) {
    color: #909399;
  }
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 16px;
  font-weight: 600;
  height: 44px;
  background: linear-gradient(90deg, #409eff 0%, #0052d9 100%);
  border: none;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }
}

.extra-links {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: #606266;
  
  .el-link {
    font-size: 14px;
    vertical-align: baseline;
    margin-left: 5px;
    color: #409eff;
    &:hover {
      color: #66b1ff;
    }
  }
}
</style>

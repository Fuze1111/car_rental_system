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
          <h2>注册账号</h2>
          <p>欢迎加入我们的租车服务平台</p>
        </div>
        
        <div class="role-switch-container">
          <el-tabs v-model="activeRole" class="role-tabs custom-tabs" @tab-click="handleRoleChange" stretch>
            <el-tab-pane label="客户注册" name="customer">
              <template #label>
                <div class="tab-label">
                  <el-icon><User /></el-icon>
                  <span>客户注册</span>
                </div>
              </template>
            </el-tab-pane>
            <el-tab-pane label="司机注册" name="driver">
              <template #label>
                <div class="tab-label">
                  <el-icon><Van /></el-icon>
                  <span>司机注册</span>
                </div>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="scrollable-form">
          <transition name="fade-slide" mode="out-in">
            <!-- 客户注册表单 -->
            <el-form 
              v-if="activeRole === 'customer'"
              :key="'customer'" 
              :model="customerForm" 
              :rules="customerRules" 
              ref="customerFormRef" 
              size="large" 
              class="login-form"
            >
              <el-form-item prop="name">
                <el-input v-model="customerForm.name" placeholder="请输入姓名" prefix-icon="User" />
              </el-form-item>
              <el-form-item prop="phone">
                <el-input v-model="customerForm.phone" placeholder="请输入手机号" prefix-icon="Phone" />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="customerForm.password" type="password" placeholder="设置密码" show-password prefix-icon="Lock" />
              </el-form-item>
              <el-form-item prop="confirmPassword">
                <el-input v-model="customerForm.confirmPassword" type="password" placeholder="确认密码" show-password prefix-icon="Lock" />
              </el-form-item>
              <el-form-item prop="company">
                <el-input v-model="customerForm.company" placeholder="工作单位 (选填)" prefix-icon="OfficeBuilding" />
              </el-form-item>
              <el-form-item prop="address">
                <el-input v-model="customerForm.address" placeholder="联系地址 (选填)" prefix-icon="Location" />
              </el-form-item>
              
              <div class="form-actions">
                <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn" round>
                  立即注册
                </el-button>
              </div>
            </el-form>

            <!-- 司机注册表单 -->
            <el-form 
              v-else
              :key="'driver'" 
              :model="driverForm" 
              :rules="driverRules" 
              ref="driverFormRef" 
              size="large" 
              class="login-form"
            >
              <el-form-item prop="name">
                <el-input v-model="driverForm.name" placeholder="请输入姓名" prefix-icon="User" />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="driverForm.password" type="password" placeholder="设置密码" show-password prefix-icon="Lock" />
              </el-form-item>
              <el-form-item prop="confirmPassword">
                <el-input v-model="driverForm.confirmPassword" type="password" placeholder="确认密码" show-password prefix-icon="Lock" />
              </el-form-item>
              <el-form-item prop="licenseType">
                <el-select v-model="driverForm.licenseType" placeholder="请选择准驾车型" style="width: 100%">
                  <template #prefix>
                    <el-icon class="el-input__icon"><CreditCard /></el-icon>
                  </template>
                  <el-option label="C1 (小型汽车)" value="C1" />
                  <el-option label="C2 (小型自动挡汽车)" value="C2" />
                  <el-option label="A1 (大型客车)" value="A1" />
                  <el-option label="A2 (牵引车)" value="A2" />
                  <el-option label="B1 (中型客车)" value="B1" />
                </el-select>
              </el-form-item>
              
              <div class="form-actions">
                <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn" round>
                  提交申请
                </el-button>
              </div>
            </el-form>
          </transition>
        </div>

        <div class="extra-links">
          <span>已有账号？</span>
          <el-link type="primary" @click="handleLogin">立即登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Van, Lock, Phone, OfficeBuilding, Location, CreditCard } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeRole = ref('customer')
const loading = ref(false)
const customerFormRef = ref(null)
const driverFormRef = ref(null)

const customerForm = reactive({
  name: '',
  phone: '',
  password: '',
  confirmPassword: '',
  company: '',
  address: ''
})

const driverForm = reactive({
  name: '',
  password: '',
  confirmPassword: '',
  licenseType: ''
})

const validatePass2 = (rule, value, callback) => {
  const form = activeRole.value === 'customer' ? customerForm : driverForm
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const customerRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

const driverRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  licenseType: [{ required: true, message: '请选择准驾车型', trigger: 'change' }]
}

const handleRoleChange = () => {
  // Clear forms if needed
}

const handleRegister = async () => {
  const formRef = activeRole.value === 'customer' ? customerFormRef.value : driverFormRef.value
  if (!formRef) return
  
  await formRef.validate((valid) => {
    if (valid) {
      loading.value = true
      setTimeout(() => {
        loading.value = false
        ElMessage.success('注册成功！请登录')
        router.push('/login')
      }, 1000)
    }
  })
}

const handleLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
/* 复用 Login.vue 的大部分样式，增加 scrollable-form */

/* 由于没有公共样式文件，这里复制 Login.vue 的核心样式并微调 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
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
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(4px);
  z-index: 1;
}

.login-content {
  position: relative;
  z-index: 2;
  display: flex;
  width: 900px;
  height: 600px; /* 稍微加高以容纳更多表单项 */
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(25px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.6);
  overflow: hidden;
  animation: fadeInUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.brand-section {
  flex: 1;
  background: rgba(253, 251, 247, 0.85);
  background-image: linear-gradient(135deg, rgba(253, 251, 247, 0.85) 0%, rgba(244, 246, 249, 0.85) 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #303133;
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}

.brand-decoration {
  position: absolute;
  top: -50px;
  left: -50px;
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #409eff 0%, #a0cfff 100%);
  opacity: 0.1;
  border-radius: 50%;
  z-index: 1;
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
  color: #409eff;
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
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  background-color: rgba(255, 255, 255, 0.3);
  color: #303133;
}

.login-header {
  margin-bottom: 20px;
  text-align: center;
  
  h2 {
    font-size: 28px;
    color: #2c3e50;
    margin: 0 0 12px;
    font-weight: 700;
    letter-spacing: 2px;
  }
  
  p {
    color: #909399;
    font-size: 15px;
    margin: 0;
    letter-spacing: 1px;
  }
}

.role-switch-container {
  margin-bottom: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 4px;
}

.custom-tabs {
  :deep(.el-tabs__header) { margin: 0; border: none; }
  :deep(.el-tabs__nav-wrap::after) { display: none; }
  :deep(.el-tabs__active-bar) { display: none; }
  :deep(.el-tabs__item) {
    padding: 0;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    color: #606266;
    transition: all 0.3s;
    
    &.is-active {
      background: white;
      color: #409eff;
      font-weight: 600;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

.tab-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* Scrollable Form Area for Register Page */
.scrollable-form {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
  margin-right: -4px; /* 抵消 padding 避免对齐问题 */
  
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: #dcdfe6; border-radius: 4px; }
  &::-webkit-scrollbar-track { background: transparent; }
}

.login-form {
  :deep(.el-input__wrapper) {
    background-color: white;
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    &:hover { box-shadow: 0 0 0 1px #c0c4cc inset; }
    &.is-focus { box-shadow: 0 0 0 1px #409eff inset; }
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
  .el-link { font-size: 14px; margin-left: 5px; color: #409eff; }
}

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateX(20px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-20px); }
</style>

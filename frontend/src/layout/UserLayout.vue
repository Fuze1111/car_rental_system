<template>
  <div class="user-layout">
    <!-- 动态背景层 (与管理端保持一致) -->
    <div class="layout-bg">
      <div class="bg-image" :style="bgStyle"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="user-header" :class="{ 'is-scrolled': isScrolled }">
      <div class="header-content">
        <div class="brand">
          <div class="brand-logo">
            <el-icon><Van /></el-icon>
          </div>
          <div class="brand-text">
            <span class="cn">汽车租赁系统</span>
            <span class="en">CAR RENTAL</span>
          </div>
        </div>

        <nav class="main-nav">
          <router-link to="/user/cars" class="nav-item" active-class="active">
            <el-icon><Van /></el-icon> 租车大厅
          </router-link>
          <router-link to="/user/profile" class="nav-item" active-class="active">
            <el-icon><User /></el-icon> 个人中心
          </router-link>
        </nav>

        <div class="user-actions">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar" />
              <span class="username">用户</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided class="danger-item">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="user-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <!-- 底部 -->
    <footer class="user-footer">
      <p>&copy; 2025 汽车租赁系统 | Car Rental System</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Van, CaretBottom, User } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const isScrolled = ref(false)
const scrollY = ref(0)

const handleScroll = () => {
  scrollY.value = window.scrollY
  isScrolled.value = window.scrollY > 20
}

// 背景视差效果
const bgStyle = computed(() => {
  return {
    transform: `translate3d(0, -${scrollY.value * 0.3}px, 0)`
  }
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped lang="scss">
.user-layout {
  min-height: 100vh;
  position: relative;
  background: transparent;
  font-family: 'Inter', sans-serif;
  overflow-x: hidden;
}

.layout-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
  background: #fdfbfb;

  .bg-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 160vh; /* 支持视差滚动 */
    background-image: url('@/assets/images/bg-large.jpg');
    background-size: cover;
    background-position: center top;
    will-change: transform;
    background-color: #f1f5f9;
    filter: blur(8px);
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.6);
    }
  }
}

.user-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(12px);
  z-index: 100;
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);

  &.is-scrolled {
    background: rgba(255, 255, 255, 0.85);
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  }

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
  }
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .brand-logo {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  }

  .brand-text {
    display: flex;
    flex-direction: column;
    
    .cn { font-size: 16px; font-weight: 800; color: #1e293b; line-height: 1.2; }
    .en { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 1px; }
  }
}

.main-nav {
  display: flex;
  gap: 8px;
  background: rgba(255,255,255,0.5);
  padding: 4px;
  border-radius: 50px;
  border: 1px solid rgba(255,255,255,0.5);

  .nav-item {
    text-decoration: none;
    color: #64748b;
    font-weight: 600;
    font-size: 14px;
    padding: 8px 20px;
    border-radius: 20px;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    gap: 6px;

    &:hover {
      color: #3b82f6;
      background: rgba(255,255,255,0.5);
    }

    &.active {
      color: white;
      background: #3b82f6;
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
    }
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  background: rgba(255,255,255,0.5);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 30px;
  transition: all 0.3s;

  &:hover {
    background: white;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  }

  .avatar { border: 2px solid white; }
  .username { font-size: 14px; font-weight: 600; color: #1e293b; }
}

.user-main {
  position: relative;
  z-index: 1;
  padding-top: 100px;
  min-height: calc(100vh - 60px);
  width: 100%;
}

.user-footer {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 24px;
  color: #94a3b8;
  font-size: 13px;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.danger-item { color: #ef4444; }
</style>

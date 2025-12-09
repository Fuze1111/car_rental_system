<template>
  <el-container class="admin-layout" @mousemove="handleMouseMove" direction="vertical">
    <!-- 动态鼠标跟随光斑 -->
    <div class="mouse-light" :style="lightStyle"></div>
    
    <el-header class="top-nav-header" :class="{ 'is-scrolled': isScrolled }">
      <div class="nav-content">
        <!-- Logo -->
        <div class="logo-container">
          <span class="app-title">CAR RENTAL</span>
        </div>

        <!-- 顶部导航菜单 -->
        <el-menu
          :default-active="activeMenu"
          class="top-menu"
          mode="horizontal"
          :ellipsis="false"
          router
        >
          <el-menu-item index="/admin/dashboard">DASHBOARD</el-menu-item>
          <el-menu-item index="/admin/cars">CARS</el-menu-item>
          <el-menu-item index="/admin/users">USERS</el-menu-item>
          <el-menu-item index="/admin/orders">ORDERS</el-menu-item>
          <el-menu-item index="/admin/settings">SETTINGS</el-menu-item>
        </el-menu>

        <!-- 右侧用户信息 -->
        <div class="header-right">
          <div class="action-item">
            <el-icon><Bell /></el-icon>
            <span class="badge"></span>
          </div>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="user-avatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout" class="danger-item">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <el-main class="admin-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isScrolled = ref(false)
const lightStyle = ref({
  left: '50%',
  top: '50%'
})

const activeMenu = computed(() => route.path)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleMouseMove = (e) => {
  lightStyle.value = {
    left: `${e.clientX}px`,
    top: `${e.clientY}px`
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'modern-message-box'
    }).then(() => {
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Anton&family=Inter:wght@400;600;800&display=swap');

.admin-layout {
  min-height: 100vh;
  position: relative;
  /* 淡米白色背景 */
  background: linear-gradient(135deg, #fdfbfb 0%, #f7f5f2 100%);
  overflow-x: hidden;
  font-family: 'Inter', sans-serif;
}

.mouse-light {
  position: fixed;
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.08) 0%, rgba(139, 92, 246, 0.05) 30%, transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 1;
  transition: opacity 0.5s ease;
  mix-blend-mode: multiply;
}

.top-nav-header {
  height: 100px;
  width: 100%;
  padding: 0 60px;
  position: fixed; /* 固定在顶部 */
  top: 0;
  left: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

  &.is-scrolled {
    height: 80px; /* 滚动后稍微变窄 */
    background: rgba(255, 255, 255, 0.85); /* 滚动后显示玻璃背景 */
    backdrop-filter: blur(20px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
    padding: 0 40px; /* 滚动后左右间距稍微收缩 */
  }

  .nav-content {
    width: 100%;
    max-width: 1800px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    /* 去除所有背景和边框，实现完全通透 */
    background: transparent;
    box-shadow: none;
    border: none;
  }

  .logo-container {
    .app-title {
      font-family: 'Anton', sans-serif; /* 使用类似涂鸦/海报的粗体字 */
      font-size: 32px;
      letter-spacing: 2px;
      background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      transform: skewX(-5deg); /* 稍微倾斜，增加动感 */
      cursor: pointer;
      transition: transform 0.3s;
      
      &:hover {
        transform: skewX(-5deg) scale(1.05);
      }
    }
  }

  .top-menu {
    border-bottom: none;
    background: transparent;
    height: 100%;
    display: flex;
    align-items: center;
    gap: 20px;
    
    :deep(.el-menu-item) {
      height: 40px;
      line-height: 40px;
      padding: 0 12px;
      margin: 0;
      border: none !important;
      background: transparent !important;
      
      /* 文字样式 */
      font-family: 'Inter', sans-serif;
      font-size: 14px;
      font-weight: 800; /* 特粗体 */
      color: #94a3b8 !important;
      letter-spacing: 1px;
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      
      &:hover {
        color: #1e293b !important;
        transform: translateY(-2px);
      }
      
      &.is-active {
        color: #1e293b !important;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 4px;
          height: 4px;
          background: #1e293b;
          border-radius: 50%;
        }
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 24px;

    .action-item {
      cursor: pointer;
      color: #94a3b8;
      transition: color 0.3s;
      position: relative;
      
      &:hover {
        color: #1e293b;
      }
      
      .el-icon { font-size: 20px; }

      .badge {
        position: absolute;
        top: -2px;
        right: -2px;
        width: 6px;
        height: 6px;
        background: #ef4444;
        border-radius: 50%;
        border: 1px solid #fdfbfb;
      }
    }
    
    .user-info {
      cursor: pointer;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.1);
      }

      .user-avatar {
        border: 2px solid white;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      }
    }
  }
}

.admin-main {
  /* 顶部留出空间，防止内容被固定头部遮挡 */
  padding: 120px 60px 40px 60px;
  overflow-y: visible;
}

/* 动画效果 */
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
</style>

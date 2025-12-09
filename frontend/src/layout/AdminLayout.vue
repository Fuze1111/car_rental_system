<template>
  <el-container class="admin-layout" @mousemove="handleMouseMove" direction="vertical">
    <!-- 动态鼠标跟随光斑 -->
    <div class="mouse-light" :style="lightStyle"></div>
    
    <el-header class="top-nav-header" :class="{ 'is-scrolled': isScrolled }">
      <div class="nav-content">
        <!-- Logo -->
        <div class="logo-container">
          <span class="app-title-cn">汽车租赁系统</span>
          <span class="app-title-en">CAR RENTAL</span>
        </div>

        <!-- 顶部导航菜单 -->
        <el-menu
          :default-active="activeMenu"
          class="top-menu"
          mode="horizontal"
          :ellipsis="false"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <div class="menu-text">
              <span class="cn">控制台</span>
              <span class="en">DASHBOARD</span>
            </div>
          </el-menu-item>
          
          <el-sub-menu index="/admin/cars-group" popper-class="modern-submenu">
            <template #title>
              <div class="menu-text">
                <span class="cn">车辆管理</span>
                <span class="en">VEHICLES</span>
              </div>
            </template>
            <el-menu-item index="" @click="handleNav('/admin/cars', 'section-cars')">车辆列表</el-menu-item>
            <el-menu-item index="" @click="handleNav('/admin/cars', 'section-types')">车型租金</el-menu-item>
            <el-menu-item index="/admin/maintenance">维修管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="/admin/users-group" popper-class="modern-submenu">
            <template #title>
              <div class="menu-text">
                <span class="cn">用户管理</span>
                <span class="en">USERS</span>
              </div>
            </template>
            <el-menu-item index="" @click="handleNav('/admin/users', 'section-customers')">客户列表</el-menu-item>
            <el-menu-item index="" @click="handleNav('/admin/users', 'section-drivers')">司机列表</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/orders">
            <div class="menu-text">
              <span class="cn">订单管理</span>
              <span class="en">ORDERS</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/admin/settings">
            <div class="menu-text">
              <span class="cn">系统设置</span>
              <span class="en">SETTINGS</span>
            </div>
          </el-menu-item>
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

const handleNav = (path, hash) => {
  if (route.path !== path) {
    router.push(path).then(() => {
      setTimeout(() => {
        const el = document.getElementById(hash)
        if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 500)
    })
  } else {
    const el = document.getElementById(hash)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Anton&family=Inter:wght@400;600;800&display=swap');

:deep(.el-sub-menu__icon-arrow) {
  display: none;
}

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
    display: flex;
    align-items: baseline;
    gap: 12px;
    cursor: pointer;
    transition: transform 0.3s;

    &:hover {
      transform: scale(1.02);
    }

    .app-title-cn {
      font-size: 24px;
      font-weight: 800;
      color: #1e293b;
      letter-spacing: 1px;
    }

    .app-title-en {
      font-family: 'Anton', sans-serif;
      font-size: 20px;
      color: #94a3b8;
      letter-spacing: 1px;
      transform: translateY(-1px);
    }
  }

  .top-menu {
    border-bottom: none;
    background: transparent;
    height: 100%;
    display: flex;
    align-items: center;
    gap: 10px;
    
    :deep(.el-menu-item), :deep(.el-sub-menu .el-sub-menu__title) {
      height: 54px;
      line-height: 1.2;
      padding: 0 16px;
      margin: 0;
      border: none !important;
      background: transparent !important;
      
      .menu-text {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        transition: all 0.3s;
        
        .cn {
          font-size: 15px;
          font-weight: 700;
          color: #64748b;
          transition: color 0.3s;
        }
        
        .en {
          font-family: 'Inter', sans-serif;
          font-size: 9px;
          font-weight: 800;
          color: #cbd5e1;
          letter-spacing: 1px;
          margin-top: 2px;
          transition: color 0.3s;
        }
      }

      &:hover {
        background: transparent !important;
        
        .menu-text {
          transform: translateY(-2px);
          
          .cn { color: #1e293b; }
          .en { color: #94a3b8; }
        }
      }
    }
    
    :deep(.el-menu-item.is-active), :deep(.el-sub-menu.is-active .el-sub-menu__title) {
        .menu-text {
          .cn { color: #1e293b; }
          .en { color: #1e293b; }
        }
        
        &::after {
          display: none; 
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

<style lang="scss">
/* Global styles for the submenu popper */
.el-popper.modern-submenu {
  /* 1. 移除外层默认的白色背景和阴影 */
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;

  /* 2. 移除白色小三角箭头 */
  .el-popper__arrow {
    display: none !important;
  }

  /* 3. 自定义玻璃拟态菜单 */
  .el-menu--popup {
    background: rgba(255, 255, 255, 0.45) !important; /* 更通透 */
    backdrop-filter: blur(25px) saturate(180%) !important;
    -webkit-backdrop-filter: blur(25px) saturate(180%) !important;
    border: 1px solid rgba(255, 255, 255, 0.4) !important;
    box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1) !important;
    border-radius: 16px !important;
    padding: 8px !important;
    min-width: 160px;
    margin-top: 8px;

    .el-menu-item {
      height: 42px;
      line-height: 42px;
      border-radius: 10px;
      margin: 4px 0;
      color: #334155;
      font-weight: 600;
      background: transparent !important;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.4) !important;
        color: #2563eb;
        transform: translateX(4px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.03);
      }

      &.is-active {
        color: #2563eb;
        background: rgba(255, 255, 255, 0.6) !important;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
      }
    }
  }
}
</style>

<template>
  <el-container class="admin-layout" @mousemove="handleMouseMove" direction="vertical">
    <!-- 动态背景层 -->
    <div class="layout-bg">
      <div class="bg-image" :style="bgStyle"></div>
    </div>

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

          <el-sub-menu index="/admin/orders-group" popper-class="modern-submenu">
            <template #title>
              <div class="menu-text">
                <span class="cn">订单管理</span>
                <span class="en">ORDERS</span>
              </div>
            </template>
            <el-menu-item index="" @click="handleNav('/admin/orders', 'section-active')">进行中订单</el-menu-item>
            <el-menu-item index="" @click="handleNav('/admin/orders', 'section-history')">历史订单</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/settings">
            <div class="menu-text">
              <span class="cn">系统设置</span>
              <span class="en">SETTINGS</span>
            </div>
          </el-menu-item>
        </el-menu>

        <!-- 右侧用户信息 -->
        <div class="header-right">
          <!-- 通知中心 Popover -->
          <el-popover
            placement="bottom"
            :width="320"
            trigger="click"
            popper-class="notification-popover"
          >
            <template #reference>
              <div class="action-item">
                <el-icon><Bell /></el-icon>
                <span class="badge" v-if="unreadCount > 0"></span>
              </div>
            </template>
            
            <div class="notification-container">
              <div class="notification-header">
                <span>通知 ({{ unreadCount }})</span>
                <el-button link type="primary" size="small" @click="handleClearNotifications" :disabled="unreadCount === 0">全部已读</el-button>
              </div>
              <div class="notification-list" v-if="notifications.length > 0">
                <div class="notification-item" v-for="(item, index) in notifications" :key="index">
                  <div class="dot" :class="item.type"></div>
                  <div class="content">
                    <p class="title">{{ item.title }}</p>
                    <p class="time">{{ item.time }}</p>
                  </div>
                </div>
              </div>
              <div class="empty-notification" v-else>
                <p>暂无新通知</p>
              </div>
            </div>
          </el-popover>

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
const scrollY = ref(0)
const lightStyle = ref({
  left: '50%',
  top: '50%'
})

const bgStyle = computed(() => {
  // 背景图移动速度比滚动速度慢，产生视差
  // 向上移动，显示图片下半部分
  return {
    transform: `translate3d(0, -${scrollY.value * 0.3}px, 0)`
  }
})

// 通知数据
const notifications = ref([
  { title: '新订单: 京A88888 租赁申请', time: '10分钟前', type: 'info' },
  { title: '系统将于今晚 02:00 进行维护', time: '2小时前', type: 'warning' },
  { title: '新用户注册: 张三', time: '5小时前', type: 'success' }
])

const unreadCount = computed(() => notifications.value.length)

const handleClearNotifications = () => {
  notifications.value = []
  ElMessage.success('所有通知已标记为已读')
}

const activeMenu = computed(() => route.path)

const handleScroll = () => {
  scrollY.value = window.scrollY
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
    ElMessageBox.confirm('确定要退出登录吗?', '退出确认', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'modern-message-box'
    }).then(() => {
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/admin/settings?tab=account')
  }
}

const handleNav = (path, hash) => {
  if (route.path !== path) {
    router.push(path).then(() => {
      scrollToHash(hash)
    })
  } else {
    scrollToHash(hash)
  }
}

const scrollToHash = (hash) => {
  if (!hash) return
  
  // 简单的轮询检测元素是否存在，解决页面切换DOM未渲染的问题
  let attempts = 0
  const maxAttempts = 50 // 增加到5秒超时，以防加载慢
  
  const checkAndScroll = () => {
    const el = document.getElementById(hash)
    if (el) {
      // 稍微偏移一点，避免被吸顶Header遮挡
      // Header滚动后高度为80px，留5px余量
      const headerOffset = 85
      const elementPosition = el.getBoundingClientRect().top
      const offsetPosition = elementPosition + window.pageYOffset - headerOffset
  
      window.scrollTo({
        top: offsetPosition,
        behavior: "smooth"
      })
    } else {
      attempts++
      if (attempts < maxAttempts) {
        setTimeout(checkAndScroll, 100)
      }
    }
  }
  
  checkAndScroll()
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
  /* 移除原来的背景色，让图片显示出来 */
  background: transparent;
  overflow-x: hidden;
  font-family: 'Inter', sans-serif;
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
  background: #fdfbfb; /* 图片加载前的底色 */

  .bg-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    /* 高度设为130%以支持视差滚动而不露底 */
    height: 160vh;
    background-image: url('@/assets/images/bg-large.jpg');
    background-size: cover;
    background-position: center top;
    will-change: transform;
    /* 默认渐变背景，防止无图时太丑 */
    background-color: #f1f5f9;
    
    /* 毛玻璃模糊效果 */
    filter: blur(8px);
    
    /* 遮罩层，增加一点白色半透明，让文字更清晰 */
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
  position: relative;
  z-index: 2;
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

/* 通知中心 Popover 样式 */
.el-popper.notification-popover {
  padding: 0 !important;
  border-radius: 12px !important;
  border: 1px solid #e2e8f0 !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
  overflow: hidden;

  .notification-container {
    .notification-header {
      padding: 12px 16px;
      border-bottom: 1px solid #f1f5f9;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      color: #1e293b;
      background: #f8fafc;
    }

    .notification-list {
      max-height: 300px;
      overflow-y: auto;

      .notification-item {
        padding: 12px 16px;
        display: flex;
        gap: 12px;
        cursor: pointer;
        transition: background 0.2s;
        border-bottom: 1px solid #f8fafc;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background: #f1f5f9;
        }

        .dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          margin-top: 6px;
          flex-shrink: 0;

          &.info { background: #3b82f6; }
          &.warning { background: #f59e0b; }
          &.success { background: #10b981; }
        }

        .content {
          flex: 1;
          
          .title {
            font-size: 13px;
            color: #334155;
            margin: 0 0 4px 0;
            line-height: 1.4;
          }
          
          .time {
            font-size: 11px;
            color: #94a3b8;
            margin: 0;
          }
        }
      }
    }

    .empty-notification {
      padding: 32px 0;
      text-align: center;
      color: #94a3b8;
      font-size: 13px;
    }
  }
}
</style>

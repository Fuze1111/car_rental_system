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
          <div class="brand-text">
            <span class="cn">汽车租赁系统</span>
            <span class="en">CAR RENTAL</span>
          </div>
        </div>

        <nav class="main-nav">
          <router-link to="/user/cars" class="nav-item" active-class="active">
            <div class="menu-text">
              <span class="cn">租车大厅</span>
              <span class="en">RENTAL</span>
            </div>
          </router-link>
          <router-link to="/user/profile" class="nav-item" active-class="active">
            <div class="menu-text">
              <span class="cn">个人中心</span>
              <span class="en">PROFILE</span>
            </div>
          </router-link>
        </nav>

        <div class="user-actions">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar" />
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
@import url('https://fonts.googleapis.com/css2?family=Anton&family=Inter:wght@400;600;800&display=swap');

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
  height: 100px; /* Admin Style Height */
  padding: 0 60px;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

  &.is-scrolled {
    height: 80px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
    padding: 0 40px;
  }

  .header-content {
    width: 100%;
    max-width: 1800px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.brand {
  display: flex;
  align-items: baseline;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s;

  &:hover {
    transform: scale(1.02);
  }

  .brand-text {
    display: flex;
    align-items: baseline;
    gap: 12px;
    
    .cn {
      font-size: 24px;
      font-weight: 800;
      color: #1e293b;
      letter-spacing: 1px;
    }
    .en {
      font-family: 'Anton', sans-serif;
      font-size: 20px;
      color: #94a3b8;
      letter-spacing: 1px;
    }
  }
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 32px;
  height: 100%;

  .nav-item {
    text-decoration: none;
    height: 100%;
    display: flex;
    align-items: center;
    padding: 0 8px;
    position: relative;

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
      .menu-text {
        transform: translateY(-2px);
        .cn { color: #1e293b; }
        .en { color: #94a3b8; }
      }
    }

    &.active {
      .menu-text {
        .cn { color: #1e293b; }
        .en { color: #1e293b; }
      }
    }
  }
}

.user-profile {
  cursor: pointer;
  transition: transform 0.3s;
  
  &:hover {
    transform: scale(1.1);
  }

  .avatar {
    border: 2px solid white;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }
}

.user-main {
  position: relative;
  z-index: 1;
  padding-top: 120px; /* Match Admin padding */
  min-height: calc(100vh - 60px);
  width: 100%;
  padding-left: 60px;
  padding-right: 60px;
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

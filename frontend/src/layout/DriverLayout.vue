<template>
  <div class="driver-layout">
    <!-- 动态背景层 -->
    <div class="layout-bg">
      <div class="bg-image"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="driver-header">
      <div class="header-content">
        <div class="brand">
          <div class="brand-text">
            <span class="cn">司机工作台</span>
            <span class="en">DRIVER CENTER</span>
          </div>
        </div>

        <div class="user-actions">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" class="avatar" />
              <span class="username">王师傅</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item command="logout" divided class="danger-item">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="driver-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <!-- 底部 -->
    <footer class="driver-footer">
      <p>&copy; 2025 汽车租赁系统 - 司机端</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()

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
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Anton&family=Inter:wght@400;600;800&display=swap');

.driver-layout {
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
    height: 100%;
    background-image: url('@/assets/images/bg-large.jpg');
    background-size: cover;
    background-position: center;
    filter: blur(8px);
    opacity: 0.5;
  }
}

.driver-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
  padding: 0 40px;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);

  .header-content {
    width: 100%;
    max-width: 1200px;
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

  .brand-text {
    display: flex;
    align-items: baseline;
    gap: 12px;
    
    .cn {
      font-size: 22px;
      font-weight: 800;
      color: #1e293b;
      letter-spacing: 1px;
    }
    .en {
      font-family: 'Anton', sans-serif;
      font-size: 18px;
      color: #94a3b8;
      letter-spacing: 1px;
    }
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s;
  
  &:hover {
    background: rgba(0,0,0,0.05);
  }

  .username {
    font-size: 14px;
    font-weight: 600;
    color: #475569;
  }

  .avatar {
    border: 2px solid white;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }
}

.driver-main {
  position: relative;
  z-index: 1;
  padding-top: 100px;
  min-height: calc(100vh - 60px);
  width: 100%;
  display: flex;
  justify-content: center;
}

.driver-footer {
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

<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="admin-aside">
      <div class="aside-header" :class="{ 'collapsed': isCollapse }">
        <el-icon class="logo-icon"><Van /></el-icon>
        <span class="app-title" v-show="!isCollapse">Car Rental</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409eff"
        router
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>控制台</template>
        </el-menu-item>
        
        <el-menu-item-group v-show="!isCollapse" title="业务管理">
        </el-menu-item-group>
        <!-- 折叠时 menu-item-group 的 title 会显示得很奇怪，简单处理：直接放 menu-item 或者用 div 包裹 -->
        
        <el-menu-item index="/admin/cars">
          <el-icon><Van /></el-icon>
          <template #title>车辆管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/settings" style="margin-top: 20px">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="admin-header">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon v-if="isCollapse"><Expand /></el-icon>
            <el-icon v-else><Fold /></el-icon>
          </div>
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">管理员</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Odometer, Van, User, List, CaretBottom, Setting, Fold, Expand } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title || '当前页面')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

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
.admin-layout {
  height: 100vh;
}

.admin-aside {
  background-color: #ffffff; /* 亮色背景 */
  color: #303133;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 1px 0 0 0 #e6e6e6; /* 边框线代替阴影，更清爽 */
  z-index: 10;

  .aside-header {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    font-size: 20px;
    font-weight: 700;
    color: #409eff; /* 品牌色标题 */
    background-color: #fff;
    border-bottom: 1px solid #f0f0f0;
    white-space: nowrap;
    overflow: hidden;
    
    .logo-icon {
      font-size: 28px;
    }
    
    .app-title {
      font-family: 'Avenir', Helvetica, Arial, sans-serif;
      transition: opacity 0.3s;
    }

    &.collapsed {
      padding: 0;
      .app-title {
        display: none;
      }
    }
  }

  .admin-menu {
    border-right: none;
    flex: 1;
    padding-top: 10px;

    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
      margin: 4px 0; /* 移除左右 margin 以适应折叠 */
      border-right: 3px solid transparent; /* 右侧指示条 */
      
      &:hover {
        background-color: #f5f7fa !important;
        color: #409eff !important;
      }
      
      &.is-active {
        background: #ecf5ff !important; /* 淡蓝色背景 */
        color: #409eff !important;
        border-right-color: #409eff;
        font-weight: 500;
      }
      
      .el-icon {
        font-size: 18px;
        margin-right: 12px; /* 调整图标间距 */
        transition: margin 0.3s;
      }
    }
    
    /* 折叠时的样式修正 */
    &.el-menu--collapse {
      :deep(.el-menu-item) {
        .el-icon {
          margin: 0;
        }
      }
    }
  }
}

.main-container {
  background-color: #f5f7fa; /* 整体背景稍微变浅一点的灰 */
  display: flex;
  flex-direction: column;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6; /* 统一边框颜色 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  /* 移除阴影，改用边框，更扁平化 */
  position: relative;
  z-index: 9;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #606266;
      display: flex;
      align-items: center;
      transition: color 0.3s;
      
      &:hover {
        color: #409eff;
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    gap: 10px;
    color: #606266;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background 0.3s;
    
    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
    
    .username {
      font-weight: 500;
    }
  }
}

.admin-main {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s cubic-bezier(0.55, 0, 0.1, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>

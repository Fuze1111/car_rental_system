<template>
  <div class="dashboard-container">
    <!-- 欢迎 Banner -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="left-panel">
          <div class="user-greeting">
            <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar-shadow" />
            <div class="greeting-text">
              <h2>{{ timeGreeting }}，<span class="user-name">Admin</span></h2>
              <el-tag type="success" size="small" effect="light" round class="status-tag">系统运行正常</el-tag>
            </div>
          </div>
          <p class="quote">“ {{ greetingText }} ”</p>
          <div class="weather-brief">
            <span class="brief-item"><el-icon><Monitor /></el-icon> 监控中</span>
            <span class="divider">|</span>
            <span class="brief-item"><el-icon><Bell /></el-icon> 无新告警</span>
          </div>
        </div>
        
        <div class="center-panel">
          <div class="glass-stat-pill">
            <div class="icon-box blue"><el-icon><DataLine /></el-icon></div>
            <div class="pill-info">
              <span class="label">今日浏览</span>
              <span class="value">2,845</span>
            </div>
          </div>
          <div class="glass-stat-pill">
            <div class="icon-box purple"><el-icon><Timer /></el-icon></div>
            <div class="pill-info">
              <span class="label">平均租期</span>
              <span class="value">3.5天</span>
            </div>
          </div>
          <div class="glass-stat-pill">
            <div class="icon-box green"><el-icon><CircleCheck /></el-icon></div>
            <div class="pill-info">
              <span class="label">准点还车</span>
              <span class="value">98%</span>
            </div>
          </div>
        </div>

        <div class="right-panel">
          <div class="aurora-blob"></div>
          <div class="time-widget">
            <div class="time-display">{{ currentTimeStr }}</div>
            <div class="date-display">{{ currentDateStr }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="stats-grid">
      <div class="stat-card blue">
        <div class="card-content">
          <div class="stat-header">
            <span class="label">总车辆数</span>
            <span class="trend positive">+1.5% <el-icon><Top /></el-icon></span>
          </div>
          <div class="stat-value">128</div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot"></span>
              2 辆新增
            </div>
            <el-icon class="bg-icon"><Van /></el-icon>
          </div>
        </div>
      </div>
<!-- ... (rest of the template remains largely the same, but styles will update) ... -->
      <div class="stat-card green">
        <div class="card-content">
          <div class="stat-header">
            <span class="label">注册用户</span>
            <span class="trend positive">+5.2% <el-icon><Top /></el-icon></span>
          </div>
          <div class="stat-value">3,502</div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot"></span>
              12 位今日注册
            </div>
            <el-icon class="bg-icon"><User /></el-icon>
          </div>
        </div>
      </div>

      <div class="stat-card purple">
        <div class="card-content">
          <div class="stat-header">
            <span class="label">待处理订单</span>
            <span class="trend negative">-5.0% <el-icon><Bottom /></el-icon></span>
          </div>
          <div class="stat-value">12</div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot warning"></span>
              需尽快处理
            </div>
            <el-icon class="bg-icon"><List /></el-icon>
          </div>
        </div>
      </div>

      <div class="stat-card orange">
        <div class="card-content">
          <div class="stat-header">
            <span class="label">今日营收</span>
            <span class="trend positive">+8.4% <el-icon><Top /></el-icon></span>
          </div>
          <div class="stat-value">
            <span class="currency">¥</span>12,580
          </div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot"></span>
              实时更新
            </div>
            <el-icon class="bg-icon"><Money /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="dashboard-main">
      <!-- 图表区域 -->
      <div class="main-card chart-section">
        <div class="section-header">
          <div class="title-group">
            <h3>租赁趋势分析</h3>
            <p>近7天车辆租赁与营收数据</p>
          </div>
          <div class="actions">
            <div class="time-switch">
              <span class="switch-item active">周</span>
              <span class="switch-item">月</span>
              <span class="switch-item">年</span>
            </div>
          </div>
        </div>
        
        <div class="chart-container">
          <!-- 模拟图表 -->
          <div class="mock-chart">
            <div class="chart-grid">
              <div class="grid-line" v-for="i in 5" :key="i"></div>
            </div>
            <div class="chart-bars">
              <div class="bar-group" v-for="(height, index) in [40, 60, 35, 80, 55, 90, 70]" :key="index">
                <div class="bar" :style="{ height: height + '%' }">
                  <div class="bar-glow"></div>
                </div>
                <span class="bar-label">{{ ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'][index] }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 侧边栏区域 -->
      <div class="side-column">
        <!-- 快捷操作 -->
        <div class="main-card quick-actions-card">
          <h3>快捷操作</h3>
          <div class="quick-actions-grid">
            <div class="action-btn-modern">
              <div class="icon-circle blue"><el-icon><Plus /></el-icon></div>
              <span>添加车辆</span>
            </div>
            <div class="action-btn-modern">
              <div class="icon-circle green"><el-icon><UserFilled /></el-icon></div>
              <span>注册审核</span>
            </div>
            <div class="action-btn-modern">
              <div class="icon-circle purple"><el-icon><Document /></el-icon></div>
              <span>导出报表</span>
            </div>
            <div class="action-btn-modern">
              <div class="icon-circle orange"><el-icon><Setting /></el-icon></div>
              <span>系统设置</span>
            </div>
          </div>
        </div>

        <!-- 资源监控 -->
        <div class="main-card resource-card">
          <h3>资源监控</h3>
          <div class="resource-list">
            <div class="resource-item">
              <div class="res-header">
                <span class="res-name">空闲车辆</span>
                <span class="res-val blue">85/128</span>
              </div>
              <div class="progress-bg">
                <div class="progress-bar blue" style="width: 66%"></div>
              </div>
            </div>
            
            <div class="resource-item">
              <div class="res-header">
                <span class="res-name">空闲司机</span>
                <span class="res-val orange">12/45</span>
              </div>
              <div class="progress-bg">
                <div class="progress-bar orange" style="width: 26%"></div>
              </div>
            </div>
            
            <div class="info-alert">
              <el-icon><Warning /></el-icon>
              <span>5辆车即将到期归还，请注意查收</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { Van, User, List, Money, Top, Bottom, Plus, UserFilled, Document, Warning, Setting, Monitor, Bell, DataLine, Timer, CircleCheck } from '@element-plus/icons-vue'

const currentTime = ref(new Date())
let timer = null

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date()
  }, 1000) // 每秒更新一次，显示秒
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const currentTimeStr = computed(() => {
  return currentTime.value.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
})

const currentDateStr = computed(() => {
  return currentTime.value.toLocaleDateString('zh-CN', { 
    year: 'numeric',
    month: 'long', 
    day: 'numeric', 
    weekday: 'long' 
  })
})

const timeGreeting = computed(() => {
  const hour = currentTime.value.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

const greetingText = computed(() => {
  const hour = currentTime.value.getHours()
  if (hour < 9) return '新的一天，元气满满'
  if (hour < 12) return '保持专注，效率倍增'
  if (hour < 18) return '忙碌之余，记得休息'
  return '愿您有个美好的夜晚'
})
</script>

<style scoped lang="scss">
.dashboard-container {
  max-width: 1600px;
  margin: 0 auto;
}

/* 欢迎 Banner */
.welcome-section {
  position: relative;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  margin-bottom: 32px;
  padding: 32px 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 2;
  }

  .left-panel {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .user-greeting {
      display: flex;
      align-items: center;
      gap: 16px;

      .avatar-shadow {
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
        border: 2px solid white;
      }

      .greeting-text {
        h2 {
          font-size: 28px;
          font-weight: 700;
          color: #1e293b;
          margin: 0 0 4px 0;
          display: flex;
          align-items: center;
          gap: 8px;

          .user-name {
            background: linear-gradient(45deg, #3b82f6, #8b5cf6);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
          }
        }
        
        .status-tag {
          font-weight: 500;
        }
      }
    }

    .quote {
      font-size: 15px;
      color: #64748b;
      font-style: italic;
      margin: 0;
      padding-left: 64px; /* Align with text start */
      opacity: 0.8;
    }
    
    .weather-brief {
      padding-left: 64px;
      display: flex;
      gap: 12px;
      align-items: center;
      font-size: 13px;
      color: #94a3b8;
      
      .brief-item {
        display: flex;
        align-items: center;
        gap: 6px;
      }
      
      .divider { color: #cbd5e1; font-size: 12px; }
    }
  }

  .center-panel {
    display: flex;
    gap: 24px;
    align-items: center;
    
    .glass-stat-pill {
      background: rgba(255, 255, 255, 0.6);
      backdrop-filter: blur(10px);
      padding: 10px 20px 10px 12px;
      border-radius: 50px;
      display: flex;
      align-items: center;
      gap: 12px;
      border: 1px solid rgba(255, 255, 255, 0.5);
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      cursor: pointer;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);

      &:hover {
        transform: translateY(-3px);
        background: rgba(255, 255, 255, 0.9);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
      }

      .icon-box {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        
        &.blue { background: #eff6ff; color: #3b82f6; }
        &.purple { background: #f5f3ff; color: #8b5cf6; }
        &.green { background: #ecfdf5; color: #10b981; }
      }

      .pill-info {
        display: flex;
        flex-direction: column;
        line-height: 1.2;

        .label { font-size: 12px; color: #64748b; font-weight: 500; }
        .value { font-size: 15px; color: #1e293b; font-weight: 700; font-family: 'Inter', sans-serif; }
      }
    }
  }

  .right-panel {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    width: 400px;
    height: 120px;

    .aurora-blob {
      position: absolute;
      top: 50%;
      right: -20px;
      transform: translateY(-50%);
      width: 200px;
      height: 200px;
      background: radial-gradient(circle, rgba(59, 130, 246, 0.4) 0%, rgba(139, 92, 246, 0.2) 40%, transparent 70%);
      filter: blur(40px);
      z-index: 0;
      animation: pulse-glow 4s ease-in-out infinite alternate;
    }

    .time-widget {
      position: relative;
      z-index: 1;
      text-align: right;

      .time-display {
        font-size: 64px;
        font-weight: 200; /* Thin modern look */
        color: #1e293b;
        line-height: 1;
        font-family: 'Segoe UI', 'SF Pro Display', sans-serif;
        letter-spacing: -2px;
      }

      .date-display {
        font-size: 16px;
        color: #64748b;
        font-weight: 500;
        margin-top: 4px;
        letter-spacing: 1px;
        text-transform: uppercase;
      }
    }
  }
}

@keyframes pulse-glow {
  0% { transform: translateY(-50%) scale(1); opacity: 0.8; }
  100% { transform: translateY(-50%) scale(1.2); opacity: 1; }
}

/* 核心指标卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  /* 玻璃拟态卡片 */
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 24px;
  position: relative;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px rgba(0,0,0,0.05);

  &:hover {
    transform: translateY(-8px);
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 20px 40px rgba(0,0,0,0.08);
  }

  .card-content {
    position: relative;
    z-index: 2;
  }

  /* ... header, value, footer styles remain similar but cleaned up ... */
  .stat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .label {
      color: #64748b;
      font-size: 14px;
      font-weight: 600;
    }

    .trend {
      font-size: 13px;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 4px 8px;
      border-radius: 8px;

      &.positive { color: #10b981; background: rgba(16, 185, 129, 0.1); }
      &.negative { color: #ef4444; background: rgba(239, 68, 68, 0.1); }
    }
  }

  .stat-value {
    font-size: 36px;
    font-weight: 800;
    color: #1e293b;
    margin-bottom: 20px;
    letter-spacing: -1px;
    
    .currency { font-size: 24px; vertical-align: top; margin-top: 4px; display: inline-block; }
  }

  .stat-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .footer-item {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #64748b;
      font-size: 13px;
      font-weight: 500;

      .dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #10b981;
        &.warning { background: #f59e0b; }
      }
    }

    .bg-icon {
      font-size: 48px;
      position: absolute;
      right: 20px;
      bottom: 20px;
      opacity: 0.1;
      transform: rotate(-15deg);
      transition: all 0.3s;
    }
  }

  &:hover .bg-icon {
    transform: rotate(0deg) scale(1.1);
    opacity: 0.2;
  }

  /* 颜色主题 */
  &.blue { border-bottom: 4px solid #3b82f6; .bg-icon { color: #3b82f6; } }
  &.green { border-bottom: 4px solid #10b981; .bg-icon { color: #10b981; } }
  &.purple { border-bottom: 4px solid #8b5cf6; .bg-icon { color: #8b5cf6; } }
  &.orange { border-bottom: 4px solid #f59e0b; .bg-icon { color: #f59e0b; } }
}

/* 主内容布局 */
.dashboard-main {
  display: grid;
  grid-template-columns: 2.5fr 1fr;
  gap: 30px;
}

.main-card {
  /* 同样应用玻璃拟态 */
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.05);
  border: 1px solid rgba(255, 255, 255, 0.6);
  
  h3 {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 24px 0;
  }
}

/* ... existing chart and layout styles ... */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;

  .title-group {
    h3 { margin-bottom: 4px; }
    p { margin: 0; color: #94a3b8; font-size: 14px; }
  }

  .time-switch {
    background: rgba(241, 245, 249, 0.8); /* 半透明背景 */
    padding: 4px;
    border-radius: 12px;
    display: flex;

    .switch-item {
      padding: 6px 16px;
      font-size: 13px;
      color: #64748b;
      cursor: pointer;
      border-radius: 8px;
      font-weight: 600;
      transition: all 0.2s;

      &.active {
        background: white;
        color: #1e293b;
        box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      }
    }
  }
}

.mock-chart {
  height: 300px;
  position: relative;
  .chart-grid {
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    z-index: 1;
    .grid-line { height: 1px; background: rgba(241, 245, 249, 0.6); width: 100%; }
  }
  /* ... bars styles ... */
  .chart-bars {
    position: absolute;
    top: 20px; left: 20px; right: 20px; bottom: 0;
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    z-index: 2;

    .bar-group {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      height: 100%;
      justify-content: flex-end;

      .bar {
        width: 32px;
        background: linear-gradient(180deg, #3b82f6 0%, #60a5fa 100%);
        border-radius: 8px 8px 0 0;
        position: relative;
        transition: all 0.3s;
        cursor: pointer;
        opacity: 0.9; 

        &:hover {
          transform: scaleY(1.05);
          opacity: 1;
          box-shadow: 0 10px 20px rgba(59, 130, 246, 0.3);
        }
        .bar-glow {
          position: absolute;
          top: 0; left: 0; right: 0;
          height: 20px;
          background: rgba(255,255,255,0.2);
          border-radius: 8px 8px 0 0;
        }
      }
      .bar-label { font-size: 12px; color: #64748b; font-weight: 500; }
    }
  }
}

.side-column {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .action-btn-modern {
    background: rgba(248, 250, 252, 0.6); /* 半透明 */
    border-radius: 16px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;

    &:hover {
      background: rgba(255, 255, 255, 0.9);
      border-color: #e2e8f0;
      box-shadow: 0 10px 20px rgba(0,0,0,0.03);
      transform: translateY(-2px);
    }
    
    /* ... icon styles ... */
    .icon-circle {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      
      &.blue { background: #eff6ff; color: #3b82f6; }
      &.green { background: #ecfdf5; color: #10b981; }
      &.purple { background: #f5f3ff; color: #8b5cf6; }
      &.orange { background: #fffbeb; color: #f59e0b; }
    }
    span { font-size: 13px; font-weight: 600; color: #475569; }
  }
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .resource-item {
    .res-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      font-size: 14px;
      font-weight: 600;
      color: #1e293b;
      .res-val {
        &.blue { color: #3b82f6; }
        &.orange { color: #f59e0b; }
      }
    }
    .progress-bg {
      height: 8px;
      background: rgba(241, 245, 249, 0.6);
      border-radius: 4px;
      overflow: hidden;
      .progress-bar {
        height: 100%;
        border-radius: 4px;
        &.blue { background: #3b82f6; }
        &.orange { background: #f59e0b; }
      }
    }
  }

  .info-alert {
    background: rgba(255, 251, 235, 0.6);
    border: 1px solid #fef3c7;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    color: #b45309;
    font-size: 13px;
    font-weight: 500;
  }
}

@media (max-width: 1400px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .dashboard-main { grid-template-columns: 1fr; }
}
</style>

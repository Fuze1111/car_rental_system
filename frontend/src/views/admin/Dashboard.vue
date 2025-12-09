<template>
  <div class="dashboard-container">
    <!-- 欢迎 Banner -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>{{ timeGreeting }}，管理员 <span class="wave">👋</span></h2>
          <p>今天是 {{ currentDate }}。{{ greetingText }}</p>
        </div>
        <div class="welcome-img">
          <!-- 装饰性矢量图 -->
          <img src="https://cdn-icons-png.flaticon.com/512/7466/7466142.png" alt="Dashboard" />
        </div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="stats-grid">
      <!-- ... stats cards remain unchanged ... -->
      <div class="stat-card blue">
        <div class="card-header">
          <span class="card-title">总车辆数</span>
          <span class="card-tag">+2 新增</span>
        </div>
        <div class="card-body">
          <div class="stat-value">128</div>
          <div class="stat-trend positive">
            <el-icon><Top /></el-icon>
            <span>较上周 1.5%</span>
          </div>
        </div>
        <div class="card-icon">
          <el-icon><Van /></el-icon>
        </div>
      </div>

      <div class="stat-card green">
        <div class="card-header">
          <span class="card-title">注册用户</span>
          <span class="card-tag">活跃</span>
        </div>
        <div class="card-body">
          <div class="stat-value">3,502</div>
          <div class="stat-trend positive">
            <el-icon><Top /></el-icon>
            <span>较上周 5.2%</span>
          </div>
        </div>
        <div class="card-icon">
          <el-icon><User /></el-icon>
        </div>
      </div>

      <div class="stat-card purple">
        <div class="card-header">
          <span class="card-title">待处理订单</span>
          <span class="card-tag">High Priority</span>
        </div>
        <div class="card-body">
          <div class="stat-value">12</div>
          <div class="stat-trend negative">
            <el-icon><Bottom /></el-icon>
            <span>较昨日 -5.0%</span>
          </div>
        </div>
        <div class="card-icon">
          <el-icon><List /></el-icon>
        </div>
      </div>

      <div class="stat-card orange">
        <div class="card-header">
          <span class="card-title">今日营收</span>
          <el-tag size="small" type="warning" effect="dark">实时</el-tag>
        </div>
        <div class="card-body">
          <div class="stat-value">¥ 12,580</div>
          <div class="stat-trend positive">
            <el-icon><Top /></el-icon>
            <span>较昨日 8.4%</span>
          </div>
        </div>
        <div class="card-icon">
          <el-icon><Money /></el-icon>
        </div>
      </div>
    </div>

    <!-- 图表与快捷功能区 -->
    <div class="dashboard-main">
      <div class="chart-section">
        <div class="section-header">
          <h3>租赁趋势分析</h3>
          <div class="actions">
            <el-radio-group v-model="trendType" size="small">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart-container">
          <!-- 模拟图表 -->
          <div class="mock-chart">
            <div class="chart-bars">
              <div class="bar" style="height: 40%"></div>
              <div class="bar" style="height: 60%"></div>
              <div class="bar" style="height: 35%"></div>
              <div class="bar" style="height: 80%"></div>
              <div class="bar" style="height: 55%"></div>
              <div class="bar active" style="height: 90%">
                <div class="tooltip">¥12,580</div>
              </div>
              <div class="bar" style="height: 70%"></div>
            </div>
            <div class="chart-labels">
              <span>周一</span><span>周二</span><span>周三</span><span>周四</span>
              <span>周五</span><span>周六</span><span>周日</span>
            </div>
          </div>
        </div>
      </div>

      <div class="side-section">
        <div class="section-header">
          <h3>快捷操作</h3>
        </div>
        <div class="quick-actions">
          <button class="action-btn">
            <div class="icon-box blue-bg"><el-icon><Plus /></el-icon></div>
            <span>添加车辆</span>
          </button>
          <button class="action-btn">
            <div class="icon-box green-bg"><el-icon><UserFilled /></el-icon></div>
            <span>注册审核</span>
          </button>
          <button class="action-btn">
            <div class="icon-box purple-bg"><el-icon><Document /></el-icon></div>
            <span>导出报表</span>
          </button>
        </div>

        <div class="section-header" style="margin-top: 30px">
          <h3>实时资源监控</h3>
        </div>
        <div class="resource-status">
          <div class="status-item">
            <div class="status-label">
              <span>空闲车辆</span>
              <span class="value">85 / 128</span>
            </div>
            <el-progress :percentage="66" color="#67c23a" :stroke-width="10" />
            <div class="status-desc">库存充足，可满足当前需求</div>
          </div>
          <div class="status-item">
            <div class="status-label">
              <span>空闲司机</span>
              <span class="value">12 / 45</span>
            </div>
            <el-progress :percentage="26" color="#e6a23c" :stroke-width="10" />
            <div class="status-desc">运力紧张，建议调度</div>
          </div>
          <div class="status-item info-box">
            <div class="info-row">
              <span class="label">即将到期归还</span>
              <span class="val highlight">5 辆</span>
            </div>
            <div class="info-row">
              <span class="label">待维修车辆</span>
              <span class="val">3 辆</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { Van, User, List, Money, Top, Bottom, Plus, UserFilled, Document } from '@element-plus/icons-vue'

const trendType = ref('week')
const currentTime = ref(new Date())
let timer = null

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date()
  }, 60000) // 每分钟更新一次
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const currentDate = computed(() => {
  return currentTime.value.toLocaleDateString('zh-CN', { 
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
  if (hour < 9) return '新的一天开始了，元气满满！'
  if (hour < 12) return '工作顺利，保持好心情。'
  if (hour < 18) return '忙碌了一天，记得喝杯茶休息一下。'
  return '今天的任务完成了吗？早点休息哦。'
})
</script>

<style scoped lang="scss">
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎 Banner */
.welcome-section {
  background: linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%);
  border-radius: 16px;
  padding: 30px 40px;
  color: #fff;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(142, 197, 252, 0.2);

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 2;
  }

  .welcome-text {
    h2 {
      font-size: 28px;
      margin: 0 0 10px;
      font-weight: 700;
      color: #2c3e50;
      
      .wave {
        display: inline-block;
        animation: wave 2s infinite;
        transform-origin: 70% 70%;
      }
    }
    p {
      margin: 0;
      font-size: 16px;
      color: #475669;
      opacity: 0.9;
    }
  }

  .welcome-img img {
    height: 120px;
    filter: drop-shadow(0 10px 10px rgba(0,0,0,0.1));
    transform: rotate(-10deg) translateY(10px);
    transition: transform 0.3s;
    
    &:hover {
      transform: rotate(0) translateY(0);
    }
  }
}

@keyframes wave {
  0% { transform: rotate(0deg); }
  10% { transform: rotate(14deg); }
  20% { transform: rotate(-8deg); }
  30% { transform: rotate(14deg); }
  40% { transform: rotate(-4deg); }
  50% { transform: rotate(10deg); }
  60% { transform: rotate(0deg); }
  100% { transform: rotate(0deg); }
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.05);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .card-title {
      font-size: 14px;
      color: #909399;
      font-weight: 500;
    }
    
    .card-tag {
      font-size: 12px;
      padding: 2px 8px;
      border-radius: 10px;
      background: #f5f7fa;
      color: #606266;
    }
  }

  .card-body {
    .stat-value {
      font-size: 32px;
      font-weight: 800;
      color: #303133;
      margin-bottom: 8px;
      font-family: 'DIN Alternate', 'Helvetica Neue', Arial, sans-serif;
    }

    .stat-trend {
      font-size: 13px;
      display: flex;
      align-items: center;
      gap: 4px;
      
      &.positive { color: #67c23a; }
      &.negative { color: #f56c6c; }
    }
  }

  /* 背景大图标装饰 */
  .card-icon {
    position: absolute;
    right: -20px;
    bottom: -20px;
    font-size: 100px;
    opacity: 0.1;
    transform: rotate(-15deg);
    transition: all 0.3s;
  }
  
  &:hover .card-icon {
    transform: rotate(0deg) scale(1.1);
    opacity: 0.15;
  }

  /* 颜色主题 */
  &.blue { .card-icon { color: #409eff; } }
  &.green { .card-icon { color: #67c23a; } }
  &.purple { .card-icon { color: #a0cfff; } } /* 使用柔和的紫色 */
  &.orange { .card-icon { color: #e6a23c; } }
}

/* 主内容区 */
.dashboard-main {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-section, .side-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #f0f0f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

/* 模拟图表 */
.mock-chart {
  height: 250px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  
  .chart-bars {
    flex: 1;
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    
    .bar {
      width: 40px;
      background: #ebf5ff;
      border-radius: 4px 4px 0 0;
      transition: all 0.3s;
      position: relative;
      cursor: pointer;
      
      &:hover, &.active {
        background: #409eff;
      }
      
      .tooltip {
        position: absolute;
        top: -30px;
        left: 50%;
        transform: translateX(-50%);
        background: #303133;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        opacity: 0;
        transition: opacity 0.3s;
        white-space: nowrap;
      }
      
      &:hover .tooltip, &.active .tooltip {
        opacity: 1;
      }
    }
  }
  
  .chart-labels {
    display: flex;
    justify-content: space-around;
    margin-top: 10px;
    color: #909399;
    font-size: 12px;
  }
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  
  .action-btn {
    background: #f8f9fb;
    border: none;
    border-radius: 12px;
    padding: 15px 5px;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    
    &:hover {
      background: #ecf5ff;
      transform: translateY(-2px);
    }
    
    .icon-box {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 20px;
      
      &.blue-bg { background: #d9ecff; color: #409eff; }
      &.green-bg { background: #e1f3d8; color: #67c23a; }
      &.purple-bg { background: #e9e9eb; color: #909399; }
    }
    
    span {
      font-size: 12px;
      color: #606266;
    }
  }
}

.resource-status {
  .status-item {
    margin-bottom: 20px;
    
    .status-label {
      font-size: 14px;
      color: #303133;
      margin-bottom: 8px;
      display: flex;
      justify-content: space-between;
      font-weight: 500;
      
      .value {
        color: #409eff;
      }
    }
    
    .status-desc {
      font-size: 12px;
      color: #909399;
      margin-top: 6px;
    }
  }
  
  .info-box {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 15px;
    margin-top: 10px;
    
    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      font-size: 13px;
      color: #606266;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .val {
        font-weight: 600;
        color: #303133;
        
        &.highlight {
          color: #f56c6c;
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .dashboard-main {
    grid-template-columns: 1fr;
  }
}
</style>

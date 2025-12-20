<template>
  <div class="dashboard-container">
    <!-- 欢迎 Banner -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="left-panel">
          <div class="user-greeting">
            <el-avatar :size="48" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="avatar-shadow" />
            <div class="greeting-text">
              <h2>{{ timeGreeting }}，<span class="user-name">{{ adminName }}</span></h2>
              <el-tag type="success" size="small" effect="light" round class="status-tag">系统运行正常</el-tag>
            </div>
          </div>
          <p class="quote">“ {{ greetingText }} ”</p>
        </div>
        
        <div class="center-panel">
          <div class="glass-stat-pill">
            <div class="icon-box blue"><el-icon><List /></el-icon></div>
            <div class="pill-info">
              <span class="label">今日订单</span>
              <span class="value">{{ metrics.todayOrders }}</span>
            </div>
          </div>
          <div class="glass-stat-pill">
            <div class="icon-box purple"><el-icon><Timer /></el-icon></div>
            <div class="pill-info">
              <span class="label">平均租期</span>
              <span class="value">{{ metrics.averageDurationDays }}天</span>
            </div>
          </div>
          <div class="glass-stat-pill">
            <div class="icon-box orange"><el-icon><Tools /></el-icon></div>
            <div class="pill-info">
              <span class="label">今日维修</span>
              <span class="value">{{ metrics.todayRepairs }}辆</span>
            </div>
          </div>
          <div class="glass-stat-pill">
            <div class="icon-box green"><el-icon><Trophy /></el-icon></div>
            <div class="pill-info">
              <span class="label">最多租赁</span>
              <span class="value">{{ metrics.topType }}</span>
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
          <div class="stat-value">{{ metrics.totalCars }}</div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot"></span>
              2 辆本月新增
            </div>
            <el-icon class="bg-icon"><Van /></el-icon>
          </div>
        </div>
      </div>
      <div class="stat-card green">
        <div class="card-content">
          <div class="stat-header">
            <span class="label">注册用户</span>
            <span class="trend positive">+5.2% <el-icon><Top /></el-icon></span>
          </div>
          <div class="stat-value">{{ metrics.registeredUsers }}</div>
          <div class="stat-footer">
            <div class="footer-item">
              <span class="dot"></span>
              12 位今日下单
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
          <div class="stat-value">{{ metrics.pendingOrders }}</div>
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
            <span class="currency">¥</span>{{ metrics.todayRevenue }}
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
            <p>近7天订单量与营收对比</p>
          </div>
          <div class="actions">
            <div class="time-switch">
              <span class="switch-item" :class="{ active: activeTimeRange === 'week' }" @click="handleTimeSwitch('week')">周</span>
              <span class="switch-item" :class="{ active: activeTimeRange === 'month' }" @click="handleTimeSwitch('month')">月</span>
              <span class="switch-item" :class="{ active: activeTimeRange === 'year' }" @click="handleTimeSwitch('year')">年</span>
            </div>
          </div>
        </div>
        
        <!-- 图例 -->
        <div class="chart-legend">
          <div class="legend-item">
            <span class="legend-dot blue"></span>
            <span>订单数量</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot green"></span>
            <span>营收金额 (¥千)</span>
          </div>
        </div>
        
        <div class="chart-container">
          <!-- 双Y轴柱状图 -->
          <div class="mock-chart">
            <!-- 左侧Y轴 (订单数量) -->
            <div class="y-axis left">
              <div class="axis-label">订单数</div>
              <div class="y-labels">
                <div class="y-label" v-for="val in yAxisLeftTicks" :key="'left-' + val">
                  <span class="value">{{ val }}</span>
                </div>
              </div>
            </div>
            
            <!-- 图表主体 -->
            <div class="chart-main">
              <div class="chart-grid">
                <div class="grid-line" v-for="i in 6" :key="i"></div>
              </div>
              <div class="chart-bars">
                <div class="bar-group" v-for="(day, index) in chartData" :key="index">
                  <div class="dual-bars">
                    <!-- 订单数量柱 -->
                    <div class="bar orders" :style="{ height: (day.orders / currentScale.maxOrders * 100) + '%' }">
                      <div class="bar-glow"></div>
                      <span class="bar-value">{{ day.orders }}</span>
                    </div>
                    <!-- 营收金额柱 -->
                    <div class="bar revenue" :style="{ height: (day.revenue / currentScale.maxRevenue * 100) + '%' }">
                      <div class="bar-glow"></div>
                      <span class="bar-value">{{ day.revenue }}k</span>
                    </div>
                  </div>
                  <span class="bar-label">{{ day.label }}</span>
                </div>
              </div>
            </div>
            
            <!-- 右侧Y轴 (营收金额) -->
            <div class="y-axis right">
              <div class="axis-label">营收(¥千)</div>
              <div class="y-labels">
                <div class="y-label" v-for="val in yAxisRightTicks" :key="'right-' + val">
                  <span class="value">{{ val }}</span>
                </div>
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
            <div class="action-btn-modern" @click="handleQuickAction('add-car')">
              <div class="icon-circle blue"><el-icon><Plus /></el-icon></div>
              <span>添加车辆</span>
            </div>
            <div class="action-btn-modern" @click="handleQuickAction('return-car')">
              <div class="icon-circle green"><el-icon><CircleCheck /></el-icon></div>
              <span>还车处理</span>
            </div>
            <div class="action-btn-modern" @click="handleQuickAction('export-report')">
              <div class="icon-circle purple"><el-icon><Document /></el-icon></div>
              <span>导出报表</span>
            </div>
            <div class="action-btn-modern" @click="handleQuickAction('system-settings')">
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
                <span class="res-val blue">{{ (metrics.totalCars !== null && metrics.totalCars !== undefined) ? (metrics.availableCars + '/' + metrics.totalCars) : '-' }}</span>
              </div>
              <div class="progress-bg">
                <div class="progress-bar blue" :style="{ width: availableCarsPercent }"></div>
              </div>
            </div>
            
            <div class="resource-item">
              <div class="res-header">
                <span class="res-name">空闲司机</span>
                <span class="res-val orange">{{ (metrics.totalDrivers !== null && metrics.totalDrivers !== undefined) ? (metrics.freeDrivers + '/' + metrics.totalDrivers) : '-' }}</span>
              </div>
              <div class="progress-bg">
                <div class="progress-bar orange" :style="{ width: freeDriversPercent }"></div>
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

    <!-- 导出报表弹窗 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="导出数据报表"
      width="480px"
      destroy-on-close
      class="custom-dialog"
    >
      <div class="report-selection">
        <p class="instruction">请选择要导出的报表类型：</p>
        
        <div class="report-options">
          <div 
            class="report-option" 
            :class="{ active: reportType === 'financial' }"
            @click="reportType = 'financial'"
          >
            <div class="icon-box blue"><el-icon><Money /></el-icon></div>
            <div class="option-info">
              <span class="name">财务营收报表</span>
              <span class="desc">包含营收明细与统计</span>
            </div>
            <div class="check-mark" v-if="reportType === 'financial'"><el-icon><Select /></el-icon></div>
          </div>

          <div 
            class="report-option" 
            :class="{ active: reportType === 'cars' }"
            @click="reportType = 'cars'"
          >
            <div class="icon-box green"><el-icon><Van /></el-icon></div>
            <div class="option-info">
              <span class="name">车辆库存清单</span>
              <span class="desc">当前所有车辆的状态、里程与保养信息</span>
            </div>
            <div class="check-mark" v-if="reportType === 'cars'"><el-icon><Select /></el-icon></div>
          </div>

          <div 
            class="report-option" 
            :class="{ active: reportType === 'types' }"
            @click="reportType = 'types'"
          >
            <div class="icon-box purple"><el-icon><List /></el-icon></div>
            <div class="option-info">
              <span class="name">车型配置清单</span>
              <span class="desc">所有车型规格、定价与总持有量</span>
            </div>
            <div class="check-mark" v-if="reportType === 'types'"><el-icon><Select /></el-icon></div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDialogVisible = false" class="footer-btn">取消</el-button>
          <el-button type="primary" @click="handleExportConfirm" :loading="exportLoading" class="footer-btn submit-btn">
            <el-icon class="el-icon--left"><Download /></el-icon>确认导出
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Van, User, List, Money, Top, Bottom, Plus, Document, Warning, Setting, Monitor, Bell, Timer, CircleCheck, Download, Select, Tools, Trophy } from '@element-plus/icons-vue'

// new imports for API
import { getCarStatistics as apiGetCarStatistics, getRepairs as apiGetRepairs } from '@/api/car'
import { getRentalStatistics, calculateRevenue, getRentalsByDateRange, getRentals, getRentalTrend, exportFinancialReport, exportCarInventoryReport, exportCarTypeReport } from '@/api/rental'
import { getCustomerStatistics } from '@/api/admin'
import { getDriverStatistics as apiGetDriverStatistics } from '@/api/driver'
import { getUserInfo } from '@/utils/auth'

const router = useRouter()
const currentTime = ref(new Date())
let timer = null

// 从本地登录信息获取管理员用户名
const adminName = computed(() => {
  const info = getUserInfo()
  // 后端 LoginResponse 中 admin 使用 username 字段，前端存为 userName
  return info?.userName || info?.username || 'Admin'
})

// UI dialogs and controls (were referenced in template)
const reportDialogVisible = ref(false)
const reportType = ref('financial')
const exportLoading = ref(false)

const handleQuickAction = (action) => {
  switch (action) {
    case 'add-car':
      router.push('/admin/cars')
      break
    case 'return-car':
      router.push('/admin/orders')
      break
    case 'export-report':
      reportType.value = 'financial'
      reportDialogVisible.value = true
      break
    case 'system-settings':
      router.push('/admin/settings')
      break
  }
}

const handleExportConfirm = async () => {
  exportLoading.value = true
  const map = { financial: '财务营收报表', cars: '车辆库存清单', types: '车型配置清单' }
  const filenameMap = { financial: 'financial-report.xlsx', cars: 'car-inventory.xlsx', types: 'car-types.xlsx' }
  try {
    let blob
    if (reportType.value === 'financial') {
      blob = await exportFinancialReport()
    } else if (reportType.value === 'cars') {
      blob = await exportCarInventoryReport()
    } else {
      blob = await exportCarTypeReport()
    }

    // axios response interceptor returns data directly; when responseType='blob', blob is the data
    const data = blob
    const fileName = filenameMap[reportType.value] || 'report.xlsx'
    const url = window.URL.createObjectURL(data)
    const a = document.createElement('a')
    a.href = url
    a.download = fileName
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)

    reportDialogVisible.value = false
    ElMessage.success(`《${map[reportType.value]}》已导出成功`)
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败，请稍后重试')
  } finally {
    exportLoading.value = false
  }
}

// KPI reactive state
const loading = ref(false)
const metrics = ref({
  todayOrders: 0,
  averageDurationDays: 0,
  todayRepairs: 0,
  topType: '-',
  totalCars: null,
  registeredUsers: null,
  pendingOrders: null,
  todayRevenue: null,
  availableCars: null,
  repairCars: null,
  totalDrivers: null,
  freeDrivers: null
})

// chart data state
const chartData = ref([])
const activeTimeRange = ref('week')
const currentScale = computed(() => {
  if (activeTimeRange.value === 'week') return { maxOrders: 10, maxRevenue: 5 }
  if (activeTimeRange.value === 'month') return { maxOrders: 60, maxRevenue: 20 }
  return { maxOrders: 200, maxRevenue: 100 }
})

// Y-axis ticks used in template
const yAxisLeftTicks = computed(() => {
  const max = currentScale.value.maxOrders
  const step = max / 5
  return [max, max - step, max - step * 2, max - step * 3, max - step * 4, 0]
})
const yAxisRightTicks = computed(() => {
  const max = currentScale.value.maxRevenue
  const step = max / 5
  return [max, max - step, max - step * 2, max - step * 3, max - step * 4, 0]
})

const handleTimeSwitch = async (range) => {
  activeTimeRange.value = range
  await fetchChartData()
}

// helpers to get today's date strings
const toDateString = (d) => {
  const y = d.getFullYear(); const m = String(d.getMonth()+1).padStart(2,'0'); const day = String(d.getDate()).padStart(2,'0');
  return `${y}-${m}-${day}`
}

// fetch all metrics from backend
const fetchMetrics = async () => {
  loading.value = true
  try {
    // parallel calls where possible
    const [carStats, rentalStats, customerStats, driverStats] = await Promise.all([
      apiGetCarStatistics().catch(e => { console.error('carStats err', e); return {} }),
      getRentalStatistics().catch(e => { console.error('rentalStats err', e); return {} }),
      getCustomerStatistics().catch(e => { console.error('customerStats err', e); return {} }),
      apiGetDriverStatistics().catch(e => { console.error('driverStats err', e); return {} })
    ])

    // map car stats
    if (carStats) {
      const total = carStats.total ?? carStats.totalCount
      const avail = carStats.available ?? carStats.availableCount
      const repair = carStats.repair ?? carStats.repairCount
      const rented = carStats.rented

      metrics.value.totalCars = total !== undefined && total !== null ? Number(total) : 0
      metrics.value.availableCars = avail !== undefined && avail !== null ? Number(avail) : null
      metrics.value.repairCars = repair !== undefined && repair !== null ? Number(repair) : 0

      // if available missing but rented provided, compute available = total - rented
      // try compute available using rented and repair when available not provided
      if ((metrics.value.availableCars === null || isNaN(metrics.value.availableCars))) {
        const t = Number(metrics.value.totalCars) || 0
        const r = rented !== undefined && rented !== null ? Number(rented) : 0
        const rep = repair !== undefined && repair !== null ? Number(repair) : 0
        const computed = Math.max(0, t - r - rep)
        // only assign if we have at least total and (rented or repair)
        if (t > 0 && (rented !== undefined || repair !== undefined)) {
          metrics.value.availableCars = computed
        } else {
          // fallback to 0 to avoid null display
          metrics.value.availableCars = 0
        }
      }
    }

    // customer stats -> registered users
    if (customerStats) {
      metrics.value.registeredUsers = customerStats.total !== undefined && customerStats.total !== null ? Number(customerStats.total) : null
    }

    // rentals overall stats
    if (rentalStats && rentalStats.pending !== undefined && rentalStats.pending !== null) {
      metrics.value.pendingOrders = Number(rentalStats.pending)
    } else {
      // no server-side pending value: compute from rentals list (filter by missing returnDate)
      try {
        const allRentalsResp = await getRentals({ page: -1, size: -1 }).catch(() => null)
        const allRentals = Array.isArray(allRentalsResp) ? allRentalsResp : (allRentalsResp && allRentalsResp.content ? allRentalsResp.content : [])
        metrics.value.pendingOrders = Array.isArray(allRentals) ? allRentals.filter(r => !r.returnDate).length : 0
      } catch (e) {
        metrics.value.pendingOrders = 0
      }
    }

    // driver stats used for free drivers metric
    if (driverStats) {
      metrics.value.totalDrivers = driverStats.total !== undefined && driverStats.total !== null ? Number(driverStats.total) : 0
      const freeVal = driverStats.free ?? driverStats.freeCount
      metrics.value.freeDrivers = freeVal !== undefined && freeVal !== null ? Number(freeVal) : null
      // if free missing but onDuty present, compute free = total - onDuty
      if ((metrics.value.freeDrivers === null || isNaN(metrics.value.freeDrivers)) && driverStats.onDuty !== undefined && driverStats.onDuty !== null) {
        const onDuty = Number(driverStats.onDuty) || 0
        const totalD = Number(metrics.value.totalDrivers) || 0
        metrics.value.freeDrivers = Math.max(0, totalD - onDuty)
      }
      // if still null, fallback to 0
      if (metrics.value.freeDrivers === null || isNaN(metrics.value.freeDrivers)) metrics.value.freeDrivers = 0
    }

    // fetch today's orders and revenue by querying rentals between today 00:00 and today 23:59
    const today = new Date()
    const start = toDateString(today)
    const end = toDateString(today)

    // get rentals for today (silent fallback on error)
    const rentalsToday = await getRentalsByDateRange(start, end).catch(() => [])
    metrics.value.todayOrders = Array.isArray(rentalsToday) ? rentalsToday.length : (rentalsToday.content ? rentalsToday.content.length : 0)

    // calculate revenue for today
    const revenueResp = await calculateRevenue(start, end).catch(() => ({ revenue: 0 }))
    metrics.value.todayRevenue = revenueResp && revenueResp.revenue ? Number(revenueResp.revenue) : 0

    // get today's repairs count using API helper (CarRepairController expects startDate/endDate)
    try {
      const repairsResp = await apiGetRepairs({ startDate: start, endDate: end }).catch(() => [])
      metrics.value.todayRepairs = Array.isArray(repairsResp) ? repairsResp.length : (repairsResp && repairsResp.content ? repairsResp.content.length : 0)
    } catch (e) {
       metrics.value.todayRepairs = 0
     }

    // where earlier fetch('/api/rentals') was used, replace with getRentals (no params) or getRentals({ page: -1, size: -1 })
// for computing avg durations and pending if needed:
try {
  const allRentalsResp = await getRentals({ page: -1, size: -1 }).catch(() => null)
  const allRentals = Array.isArray(allRentalsResp) ? allRentalsResp : (allRentalsResp && allRentalsResp.content ? allRentalsResp.content : [])
  if (Array.isArray(allRentals) && allRentals.length) {
    const durations = allRentals.map(r => {
      if (!r.loanDate || !r.returnDate) return 0
      return (new Date(r.returnDate) - new Date(r.loanDate)) / (1000*60*60*24)
    }).filter(Boolean)
    if (durations.length) {
      const avg = durations.reduce((a,b)=>a+b,0)/durations.length
      metrics.value.averageDurationDays = Math.round(avg*10)/10
    }
  }
} catch (err) { console.error(err) }

// for topType calculation replace fetch('/api/rentals') similarly
try {
  if (!carStats || !carStats.mostRentedType) {
    const rentalsResp = await getRentals({ page: -1, size: -1 }).catch(() => [])
    const rentals = Array.isArray(rentalsResp) ? rentalsResp : (rentalsResp && rentalsResp.content ? rentalsResp.content : [])
    const typeCount = {}
    if (Array.isArray(rentals)) {
      rentals.forEach(r => { if (r.carType) typeCount[r.carType] = (typeCount[r.carType] || 0) + 1 })
      const sorted = Object.entries(typeCount).sort((a,b)=>b[1]-a[1])
      metrics.value.topType = sorted.length ? sorted[0][0] : '-'
    }
  } else {
    metrics.value.topType = carStats.mostRentedType
  }
} catch (e) { console.error(e) }

  } catch (err) {
    console.error('fetchMetrics error', err)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// fetch chart data for the selected time range (week/month/year)
const fetchChartData = async () => {
  try {
    const now = new Date()
    let startDate, endDate, periodParam

    if (activeTimeRange.value === 'week') {
      periodParam = 'day'
      endDate = new Date(now)
      startDate = new Date(now)
      startDate.setDate(now.getDate() - 6) // last 7 days
    } else if (activeTimeRange.value === 'month') {
      periodParam = 'month'
      startDate = new Date(now.getFullYear(), now.getMonth(), 1)
      endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0)
    } else { // year
      periodParam = 'year'
      startDate = new Date(now.getFullYear(), 0, 1)
      endDate = new Date(now.getFullYear(), 11, 31)
    }

    const start = toDateString(startDate)
    const end = toDateString(endDate)

    const trend = await getRentalTrend(start, end, periodParam).catch(() => [])
    // trend is array of { label, orders, revenue }
    const arr = []
    if (Array.isArray(trend) && trend.length) {
      for (const item of trend) {
        const orders = Number(item.orders) || 0
        const revenue = Number(item.revenue) || 0
        let label = String(item.label || '')
        if (periodParam === 'day') {
          // label is ISO date string; show localized weekday
          try { label = new Date(label).toLocaleDateString('zh-CN', { weekday: 'short' }) } catch(e) { /* keep original */ }
        }
        arr.push({ label, orders, revenue: Math.round(revenue/1000*10)/10 })
      }
    } else {
      // empty or error -> build fallback buckets with zeros
      if (periodParam === 'day') {
        const d = new Date(startDate)
        while (d <= endDate) {
          const lab = d.toLocaleDateString('zh-CN', { weekday: 'short' })
          arr.push({ label: lab, orders: 0, revenue: 0 })
          d.setDate(d.getDate() + 1)
        }
      } else if (periodParam === 'month') {
        arr.push({ label: 'W1', orders: 0, revenue: 0 })
        arr.push({ label: 'W2', orders: 0, revenue: 0 })
        arr.push({ label: 'W3', orders: 0, revenue: 0 })
        arr.push({ label: 'W4', orders: 0, revenue: 0 })
      } else {
        arr.push({ label: 'Q1', orders: 0, revenue: 0 })
        arr.push({ label: 'Q2', orders: 0, revenue: 0 })
        arr.push({ label: 'Q3', orders: 0, revenue: 0 })
        arr.push({ label: 'Q4', orders: 0, revenue: 0 })
      }
    }

    chartData.value = arr
  } catch (e) {
    // on unexpected error, fallback to zeros
    console.error('fetchChartData error', e)
    if (activeTimeRange.value === 'month') {
      chartData.value = [ { label: 'W1', orders: 0, revenue: 0 }, { label: 'W2', orders: 0, revenue: 0 }, { label: 'W3', orders: 0, revenue: 0 }, { label: 'W4', orders: 0, revenue: 0 } ]
    } else if (activeTimeRange.value === 'year') {
      chartData.value = [ { label: 'Q1', orders: 0, revenue: 0 }, { label: 'Q2', orders: 0, revenue: 0 }, { label: 'Q3', orders: 0, revenue: 0 }, { label: 'Q4', orders: 0, revenue: 0 } ]
    } else {
      chartData.value = Array.from({ length: 7 }).map((_, i) => ({ label: '-', orders: 0, revenue: 0 }))
    }
  }
}

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)

  // initial data load
  fetchMetrics()
  fetchChartData()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

// remaining computed helpers (currentTimeStr, currentDateStr, timeGreeting, greetingText) kept from existing file
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

// percent helpers for progress bars
const formatPct = (num) => {
  // clamp and format to one decimal (drop .0 for integer)
  const n = Math.min(Math.max(num, 0), 100)
  const fixed = Math.round(n * 10) / 10
  return (Number.isInteger(fixed) ? fixed.toString() : fixed.toFixed(1)) + '%'
}

const availableCarsPercent = computed(() => {
  const total = metrics.value.totalCars
  const avail = metrics.value.availableCars
  if (!total || total === 0) return '0%'
  const a = (avail === null || avail === undefined || isNaN(avail)) ? 0 : Number(avail)
  const pct = (a / Number(total)) * 100
  return formatPct(pct)
})

const freeDriversPercent = computed(() => {
  const total = metrics.value.totalDrivers
  const free = metrics.value.freeDrivers
  if (!total || total === 0) return '0%'
  const f = (free === null || free === undefined || isNaN(free)) ? 0 : Number(free)
  const pct = (f / Number(total)) * 100
  return formatPct(pct)
})

// expose things to template via refs already defined earlier (note: template uses original variable names like chartData, activeTimeRange etc.)
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
            background-clip: text; /* standard property for compatibility */
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
        &.orange { background: #fff7ed; color: #f97316; }
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
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.05);
  border: 1px solid rgba(255, 255, 255, 0.6);
  
  h3 {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 24px 0;
  }
}

.chart-section {
  padding: 28px !important;
}

/* ... existing chart and layout styles ... */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;

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

.chart-legend {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-left: 8px;
  
  .legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #64748b;
    font-weight: 500;
    
    .legend-dot {
      width: 12px;
      height: 12px;
      border-radius: 3px;
      
      &.blue { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
      &.green { background: linear-gradient(135deg, #10b981, #34d399); }
    }
  }
}

.chart-container {
  width: 100%;
  margin-top: 0;
}

.mock-chart {
  height: 400px;
  display: flex;
  gap: 12px;
  position: relative;
  padding-bottom: 30px; /* 预留标签空间 */
  
  .y-axis {
    width: 60px;
    display: flex;
    flex-direction: column;
    
    &.left {
      align-items: flex-end;
    }
    
    &.right {
      align-items: flex-start;
    }
    
    .axis-label {
      font-size: 12px;
      color: #64748b;
      font-weight: 700;
      margin-bottom: 8px;
      padding: 0 4px;
      white-space: nowrap;
    }
    
    .y-labels {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 10px 0 0 0; /* 底部不再留白，直接对齐 Grid 底部 */
      
      .y-label {
        display: flex;
        align-items: center;
        height: 0;
        
        .value {
          font-size: 12px;
          color: #94a3b8;
          font-weight: 600;
          font-family: 'Inter', monospace;
          padding: 0 4px;
        }
      }
    }
  }
  
  .chart-main {
    flex: 1;
    position: relative;
    
    .chart-grid {
      position: absolute;
      top: 20px;
      left: 0;
      right: 0;
      bottom: 0; /* 底部归零 */
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      z-index: 1;
      
      .grid-line {
        height: 1px;
        background: rgba(203, 213, 225, 0.4);
        width: 100%;
      }
    }
    
    .chart-bars {
      position: absolute;
      top: 20px;
      left: 20px;
      right: 20px;
      bottom: 0; /* 底部归零 */
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
        flex: 1;
        position: relative; /* 为标签定位 */

        .dual-bars {
          display: flex;
          gap: 6px;
          align-items: flex-end;
          height: 100%;
        }

        .bar {
          width: 32px;
          border-radius: 8px 8px 0 0;
          position: relative;
          transition: all 0.3s;
          cursor: pointer;
          opacity: 0.9;
          
          &.orders {
            background: linear-gradient(180deg, #3b82f6 0%, #60a5fa 100%);
            
            &:hover {
              transform: translateY(-4px);
              opacity: 1;
              box-shadow: 0 10px 20px rgba(59, 130, 246, 0.4);
            }
          }
          
          &.revenue {
            background: linear-gradient(180deg, #10b981 0%, #34d399 100%);
            
            &:hover {
              transform: translateY(-4px);
              opacity: 1;
              box-shadow: 0 10px 20px rgba(16, 185, 129, 0.4);
            }
          }

          .bar-glow {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 20px;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 8px 8px 0 0;
          }
          
          .bar-value {
            position: absolute;
            top: -26px;
            left: 50%;
            transform: translateX(-50%);
            font-size: 12px;
            font-weight: 700;
            color: #1e293b;
            white-space: nowrap;
            opacity: 1;
            background: rgba(255, 255, 255, 0.95);
            padding: 2px 6px;
            border-radius: 4px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          }
        }
        
        .bar-label {
          position: absolute;
          bottom: -30px;
          font-size: 13px;
          color: #64748b;
          font-weight: 600;
          white-space: nowrap;
        }
      }
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
      position: relative; /* ensure absolute child positions correctly */
      width: 100%;
      height: 8px;
      background: rgba(241, 245, 249, 0.6);
      border-radius: 4px;
      overflow: hidden;
      .progress-bar {
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 0; /* numeric width will be set inline as e.g. '60%' */
        border-radius: 4px;
        transition: width 420ms cubic-bezier(.2,.8,.2,1);
        &.blue { background: #3b82f6; }
        &.orange { background: #f59e0b; }
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
}

/* 报表导出弹窗样式 */
.report-selection {
  padding: 8px 0;
  
  .instruction {
    margin: 0 0 16px 0;
    font-size: 14px;
    color: #64748b;
  }

  .report-options {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .report-option {
      display: flex;
      align-items: center;
      padding: 16px;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s;
      position: relative;
      background: white;

      &:hover {
        border-color: #cbd5e1;
        background: #f8fafc;
      }

      &.active {
        border-color: #3b82f6;
        background: #eff6ff;
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
      }

      .icon-box {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        margin-right: 16px;
        
        &.blue { background: #dbeafe; color: #3b82f6; }
        &.green { background: #d1fae5; color: #10b981; }
        &.purple { background: #ede9fe; color: #8b5cf6; }
      }

      .option-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        
        .name {
          font-size: 14px;
          font-weight: 600;
          color: #1e293b;
          margin-bottom: 2px;
        }
        
        .desc {
          font-size: 12px;
          color: #64748b;
        }
      }

      .check-mark {
        color: #3b82f6;
        font-size: 18px;
        font-weight: bold;
      }
    }
  }
}

.footer-btn {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 500;
  
  &.submit-btn {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    border: none;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
    }
  }
}

@media (max-width: 1400px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .dashboard-main { grid-template-columns: 1fr; }
}
</style>

<template>
  <div class="order-management">
    
    <!-- 进行中订单 (Active) -->
    <div id="section-active">
      <el-card class="main-card" shadow="never">
        <div class="section-header">
          <div class="header-left">
            <h3>进行中订单</h3>
            <el-tag type="success" effect="light" round class="count-tag">{{ activePagination.total }} 进行中</el-tag>
          </div>
        </div>

        <div class="search-bar">
          <el-form :inline="true" :model="activeSearchForm">
            <el-form-item>
              <el-input 
                v-model="activeSearchForm.keyword" 
                placeholder="订单号 / 车牌 / 客户" 
                prefix-icon="Search" 
                clearable 
                class="search-input"
                @keyup.enter="handleActiveSearch"
                @clear="handleActiveSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="activeSearchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="借车开始"
                end-placeholder="借车结束"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 380px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleActiveSearch" round>查询</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="activeOrders" class="modern-table" v-loading="loading" style="width: 100%">
          <el-table-column prop="InvoiceNo" label="订单号" width="140" fixed />
          
          <el-table-column label="车辆信息" min-width="180">
            <template #default="scope">
              <div class="info-group">
                <span class="plate">{{ scope.row.CarID }}</span>
                <span class="sub-text">{{ displayCar(scope.row) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="客户" min-width="120">
            <template #default="scope">
              <div class="info-group">
                <span class="main-text">{{ displayCustomer(scope.row) }}</span>
                <span class="sub-text">{{ scope.row.CNo }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="司机" min-width="120">
            <template #default="scope">
              <template v-if="(scope.row.DriverName && scope.row.DriverName !== '') || (scope.row.DNo && scope.row.DNo !== '')">
                <el-tag size="small" type="info">{{ displayDriver(scope.row) }}</el-tag>
              </template>
              <span v-else class="sub-text">自驾</span>
            </template>
          </el-table-column>

          <el-table-column label="借还时间" width="320">
            <template #default="scope">
              <div class="date-range">
                <span class="date">{{ scope.row.LoanDate }}</span>
                <el-icon class="arrow"><Right /></el-icon>
                <span class="date">{{ scope.row.ReturnDate || '未还' }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="借还地点" min-width="200" show-overflow-tooltip>
             <template #default="scope">
               <div class="place-info">
                 <div class="place-row"><el-icon><Location /></el-icon> 借: {{ scope.row.LoanPlace }}</div>
                 <div class="place-row"><el-icon><LocationFilled /></el-icon> 还: {{ scope.row.ReturnPlace || '未定' }}</div>
               </div>
             </template>
          </el-table-column>

          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" round @click="openCompleteDialog(scope.row)">还车结算</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="activePagination.currentPage"
            v-model:page-size="activePagination.pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="activePagination.total"
            @size-change="handleActiveSizeChange"
            @current-change="handleActivePageChange"
          />
        </div>
      </el-card>
    </div>

    <div class="section-divider"></div>

    <!-- 历史订单 (History) -->
    <div id="section-history">
      <el-card class="main-card" shadow="never">
        <div class="section-header">
          <div class="header-left">
            <h3>历史订单</h3>
            <el-tag type="info" effect="plain" round class="count-tag">{{ pagination.total }} 已归档</el-tag>
          </div>
        </div>

        <div class="search-bar">
          <el-form :inline="true" :model="historySearchForm">
            <el-form-item>
              <el-input v-model="historySearchForm.keyword" placeholder="订单号/客户/司机" prefix-icon="Search" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item label="金额范围">
               <el-input-number v-model="historySearchForm.minAmount" :min="0" :controls="false" placeholder="Min" style="width: 80px" />
               <span style="margin: 0 4px">-</span>
               <el-input-number v-model="historySearchForm.maxAmount" :min="0" :controls="false" placeholder="Max" style="width: 80px" />
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="historySearchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 380px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleHistorySearch" round>查询</el-button>
              <el-button @click="handleHistoryReset" round>重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="historyOrders" class="modern-table" v-loading="loading" style="width: 100%">
          <el-table-column prop="InvoiceNo" label="订单号" width="140" fixed />
          
          <el-table-column label="车辆" min-width="120">
            <template #default="scope">
              <span class="plate-mini">{{ scope.row.CarID }}</span>
            </template>
          </el-table-column>

          <el-table-column label="客户" min-width="120">
            <template #default="scope">{{ displayCustomer(scope.row) }}</template>
          </el-table-column>
          
          <el-table-column label="司机" min-width="120">
            <template #default="scope">
              <!-- Use displayDriver helper which prefers DriverName then mapped DNo; always render a sensible label -->
              <template v-if="(scope.row.DriverName && scope.row.DriverName !== '') || (scope.row.DNo && scope.row.DNo !== '')">
                <el-tag size="small" type="info">{{ displayDriver(scope.row) }}</el-tag>
              </template>
              <span v-else class="sub-text">自驾</span>
            </template>
          </el-table-column>

          <el-table-column prop="RoundTripDistance" label="里程(km)" width="120" align="right" />
          <el-table-column prop="DriveTime" label="时长(h)" width="120" align="right" />

          <el-table-column prop="Amount" label="实付金额" width="120" align="right">
            <template #default="scope">
              <span class="amount">¥{{ scope.row.Amount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="借还时间" width="320" sortable>
            <template #default="scope">
              <div class="date-range">
                <span class="date">{{ scope.row.LoanDate }}</span>
                <el-icon class="arrow"><Right /></el-icon>
                <span class="date">{{ scope.row.ReturnDate }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="借还地点" min-width="180" show-overflow-tooltip>
             <template #default="scope">
               <div class="place-info">
                 <div class="place-row"><el-icon><Location /></el-icon> 借: {{ scope.row.LoanPlace }}</div>
                 <div class="place-row"><el-icon><LocationFilled /></el-icon> 还: {{ scope.row.ReturnPlace }}</div>
               </div>
             </template>
          </el-table-column>

          <el-table-column label="操作" width="80" fixed="right" align="center">
            <template #default="scope">
              <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="还车结算确认"
      width="460px"
      class="custom-dialog settlement-dialog"
      destroy-on-close
      append-to-body
    >
      <div class="dialog-content">
        <p class="dialog-tip">请录入车辆返回时的实际行驶数据</p>
        <el-form :model="completeForm" label-width="100px" size="large" class="settlement-form">
          <el-form-item label="往返里程">
            <el-input-number
              v-model="completeForm.RoundTripDistance"
              :min="0"
              :step="10"
              controls-position="right"
              class="number-input"
            >
              <template #append>km</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="行驶时间">
            <!-- 改为只读显示，根据借还时间差自动计算 -->
            <div class="readonly-time">
              <span class="time-value">{{ formattedDriveTime }}</span>
              <span class="time-unit">h</span>
            </div>
          </el-form-item>

          <!-- 费用明细展示（含公式） -->
          <div class="fee-summary-card modern">
            <div class="fee-summary-header">
              <div class="title-group">
                <span class="title">费用明细</span>
                <span class="subtitle">结算依据（管理员录入）</span>
              </div>
              <div class="total">
                <span class="label">合计</span>
                <span class="amount dynamic">¥{{ computedEstimatedAmount }}</span>
              </div>
            </div>

            <div class="fee-details">
              <!-- 里程费 -->
              <div class="fee-row distance">
                <div class="fee-main">
                  <span class="fee-label">里程费</span>
                  <span class="fee-amount dynamic">¥{{ distanceFee }}</span>
                </div>
                <div class="fee-formula">
                  <span class="formula-label">计算：</span>
                  <span class="formula-text">
                    <span class="highlight-number">{{ completeForm.RoundTripDistance || 0 }}</span>
                    <span class="unit"> km × </span>
                    <span class="highlight-number">{{ pricePerKmDisplay }}</span>
                    <span class="unit"> 元/km</span>
                  </span>
                </div>
              </div>

              <!-- 时间费 -->
              <div class="fee-row time">
                <div class="fee-main">
                  <span class="fee-label">时间费</span>
                  <span class="fee-amount dynamic">¥{{ timeFee }}</span>
                </div>
                <div class="fee-formula">
                  <span class="formula-label">计算：</span>
                  <span class="formula-text">
                    <span class="highlight-number">{{ formattedDriveTime }}</span>
                    <span class="unit"> h × </span>
                    <span class="highlight-number">{{ hourlyRateDisplay }}</span>
                    <span class="unit"> 元/h</span>
                  </span>
                </div>
              </div>

              <!-- 司机服务费：与合计金额使用同一 hasDriver 条件 -->
              <div class="fee-row driver" v-if="currentOrder && (currentOrder.DNo || currentOrder.DriverName)">
                <div class="fee-main">
                  <span class="fee-label">司机服务费</span>
                  <span class="fee-amount dynamic">¥{{ driverFee }}</span>
                </div>
                <div class="fee-formula">
                  <span class="formula-label">计算：</span>
                  <span class="formula-text">
                    <span class="highlight-number">{{ driverDaysDisplay }}</span>
                    <span class="unit"> 天 × </span>
                    <span class="highlight-number">200</span>
                    <span class="unit"> 元/天</span>
                  </span>
                </div>
              </div>

              <!-- 固定服务费 -->
              <div class="fee-row service">
                <div class="fee-main">
                  <span class="fee-label">固定服务费</span>
                  <span class="fee-amount">¥{{ serviceFee }}</span>
                </div>
                <div class="fee-formula">
                  <span class="formula-label">说明：</span>
                  <span class="formula-text">订单处理与平台服务固定费用 50 元</span>
                </div>
              </div>
            </div>
          </div>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCompleteSubmit" :loading="submitting">确认还车</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      class="custom-dialog detail-dialog"
      destroy-on-close
      append-to-body
      @opened="initMap"
    >
      <div class="detail-container" v-if="currentDetailOrder">
        <!-- 左侧信息 -->
        <div class="detail-info">
          <div class="info-block">
            <h4 class="block-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单号</span>
                <span class="value code">{{ currentDetailOrder.InvoiceNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">车辆</span>
                <span class="value plate">{{ currentDetailOrder.CarID }}</span>
              </div>
              <div class="info-item">
                <span class="label">客户</span>
                <span class="value">{{ displayCustomer(currentDetailOrder) }}</span>
              </div>
              <div class="info-item">
                <span class="label">司机</span>
                <span class="value">{{ displayDriver(currentDetailOrder) }}</span>
              </div>
            </div>
          </div>

          <div class="info-block">
            <h4 class="block-title">费用与数据</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">实付金额</span>
                <span class="value amount">¥{{ currentDetailOrder.Amount }}</span>
              </div>
              <div class="info-item">
                <span class="label">行驶里程</span>
                <span class="value">{{ currentDetailOrder.RoundTripDistance || '-' }} km</span>
              </div>
              <div class="info-item">
                <span class="label">行驶时长</span>
                <span class="value">{{ currentDetailOrder.DriveTime || '-' }} h</span>
              </div>
            </div>
          </div>

          <div class="info-block">
            <h4 class="block-title">行程路线</h4>
            <div class="route-timeline">
              <div class="route-point start">
                <div class="dot"></div>
                <div class="content">
                  <span class="time">{{ currentDetailOrder.LoanDate }}</span>
                  <span class="place">{{ currentDetailOrder.LoanPlace }}</span>
                </div>
              </div>
              <div class="route-line"></div>
              <div class="route-point end">
                <div class="dot"></div>
                <div class="content">
                  <span class="time">{{ currentDetailOrder.ReturnDate }}</span>
                  <span class="place">{{ currentDetailOrder.ReturnPlace }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧地图 -->
        <div class="detail-map">
          <div id="order-map" class="map-container">
            <!-- 占位提示，如果没有配置 Key -->
            <div class="map-placeholder" v-if="!hasMapKey">
              <el-icon class="map-icon"><LocationFilled /></el-icon>
              <p>地图预览区域</p>
              <span class="sub">请配置高德地图 Key 以查看真实轨迹</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Right, Location, LocationFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getRentals, deleteRental, updateRental } from '@/api/rental'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const currentDetailOrder = ref(null)
const hasMapKey = ref(false) // 是否检测到地图API

// 高德地图安全密钥配置 (请在此处填入您的 Key)
// window._AMapSecurityConfig = {
//   securityJsCode: '您的安全密钥',
// }

const activeSearchForm = reactive({ keyword: '' })
const historySearchForm = reactive({ 
  keyword: '', 
  dateRange: [],
  minAmount: undefined,
  maxAmount: undefined
})
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const activePagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const completeForm = reactive({
  RoundTripDistance: 0,
  DriveTime: 0
})

const activeOrders = ref([])
const historyOrders = ref([])

// Mock Aux Data
const mockCars = { '京A88888': 'Toyota Camry', '京AD12345': 'Tesla Model 3', '京A66666': 'Benz S-Class', '京A99999': 'BMW X5' };
const mockCustomers = { 'C0000001': '张客户1', 'C0000002': '李客户2' }
const mockDrivers = { 'D0000001': '王司机1' }

const getCarName = (id) => mockCars[id] || id
const getCustomerName = (id) => mockCustomers[id] || id
const getDriverName = (id) => mockDrivers[id] || id

// 基于借还时间差自动计算行驶时间（小时）
const calculatedDriveTime = computed(() => {
  if (!currentOrder.value || !currentOrder.value.LoanDate || !currentOrder.value.ReturnDate) return 0
  const start = new Date(currentOrder.value.LoanDate)
  const end = new Date(currentOrder.value.ReturnDate)
  const diffMs = end.getTime() - start.getTime()
  if (!isFinite(diffMs) || diffMs <= 0) return 0
  const hours = diffMs / (1000 * 60 * 60)
  // 业务上按小时向上取整，保证不少计
  return Math.ceil(hours)
})

const formattedDriveTime = computed(() => calculatedDriveTime.value.toString())

// 订单天数（用于司机服务费）：按借还时间差向上取整到天
const driverDays = computed(() => {
  if (!currentOrder.value || !currentOrder.value.LoanDate || !currentOrder.value.ReturnDate) return 0
  const start = new Date(currentOrder.value.LoanDate)
  const end = new Date(currentOrder.value.ReturnDate)
  const diffMs = end.getTime() - start.getTime()
  if (!isFinite(diffMs) || diffMs <= 0) return 0
  const days = diffMs / (1000 * 60 * 60 * 24)
  return Math.ceil(days)
})

const driverDaysDisplay = computed(() => driverDays.value.toString())
const driverFee = computed(() => driverDays.value * 200)

// 判断当前订单是否包含司机服务（DNo 非空且非自驾标识）
const hasDriverService = computed(() => {
  if (!currentOrder.value) return false
  const dno = (currentOrder.value.DNo || '').toString().trim()
  // 为空或特殊标记 SELF_DRIVE 视为自驾
  if (!dno || dno === 'SELF_DRIVE') return false
  return true
})

// 根据当前订单的租金率计算每小时、每公里单价
const hourlyRate = computed(() => {
  if (!currentOrder.value) return 0
  const raw = currentOrder.value.RentRate
  const num = Number(raw)
  return Number.isFinite(num) && num > 0 ? num : 0
})

const pricePerKm = computed(() => {
  // 与后端 calculateRentalAmount 逻辑保持一致：每公里费用 = 每小时租金 / 50
  if (!hourlyRate.value) return 0
  return +(hourlyRate.value / 50).toFixed(2)
})

// 供模板直接展示的字符串
const pricePerKmDisplay = computed(() => pricePerKm.value ? pricePerKm.value.toString() : '0')
const hourlyRateDisplay = computed(() => hourlyRate.value ? hourlyRate.value.toString() : '0')

// 统一的固定服务费常量
const serviceFee = 50

// 结算金额统一使用自动计算的行驶时间
const computedEstimatedAmount = computed(() => {
  const distance = Number(completeForm.RoundTripDistance) || 0
  const hours = calculatedDriveTime.value
  const distanceFeeLocal = distance * pricePerKm.value
  const timeFeeLocal = hours * hourlyRate.value
  const base = serviceFee
  // 兼容两种情况：1）DNo 有值；2）DriverName 有值（即使 DNo 为 null）
  const hasDriver = !!(currentOrder.value && (currentOrder.value.DNo || currentOrder.value.DriverName))
  const driver = hasDriver ? driverFee.value : 0
  return Math.round((base + distanceFeeLocal + timeFeeLocal + driver) * 100) / 100
})

// 为模板中的明细行提供拆分出来的费用
const distanceFee = computed(() => {
  const distance = Number(completeForm.RoundTripDistance) || 0
  return Math.round((distance * pricePerKm.value) * 100) / 100
})

const timeFee = computed(() => {
  const hours = calculatedDriveTime.value
  return Math.round((hours * hourlyRate.value) * 100) / 100
})

// Map Initialization
const initMap = () => {
  // 检查是否引入了高德地图 SDK
  if (typeof AMap === 'undefined') {
    hasMapKey.value = false
    return
  }
  
  hasMapKey.value = true
  
  const order = currentDetailOrder.value
  const startAddr = order.LoanPlace
  const endAddr = order.ReturnPlace

  // 加载地理编码插件
  AMap.plugin(['AMap.Geocoder', 'AMap.Polyline'], function() {
    const geocoder = new AMap.Geocoder()
    
    // 创建地图实例
    const map = new AMap.Map('order-map', {
      zoom: 11,
      viewMode: '3D'
    })

    // 解析借车地址
    const p1 = new Promise((resolve) => {
      geocoder.getLocation(startAddr, (status, result) => {
        if (status === 'complete' && result.geocodes.length) {
          resolve(result.geocodes[0].location)
        } else {
          console.warn('借车地址解析失败，使用默认坐标')
          resolve(new AMap.LngLat(116.397428, 39.90923)) // 默认北京
        }
      })
    })

    // 解析还车地址
    const p2 = new Promise((resolve) => {
      geocoder.getLocation(endAddr, (status, result) => {
        if (status === 'complete' && result.geocodes.length) {
          resolve(result.geocodes[0].location)
        } else {
          console.warn('还车地址解析失败，使用默认坐标')
          resolve(new AMap.LngLat(116.46, 39.92)) // 默认坐标
        }
      })
    })

    // 当两个地址都解析完成后绘制
    Promise.all([p1, p2]).then(([startLngLat, endLngLat]) => {
      // 起点 Marker
      const startMarker = new AMap.Marker({
        position: startLngLat,
        title: startAddr,
        label: { content: '取车: ' + startAddr, direction: 'top' }
      })
      
      // 终点 Marker
      const endMarker = new AMap.Marker({
        position: endLngLat,
        title: endAddr,
        label: { content: '还车: ' + endAddr, direction: 'top' }
      })

      // 路线规划 (直线)
      const polyline = new AMap.Polyline({
        path: [startLngLat, endLngLat],
        strokeColor: "#3366FF", 
        strokeWeight: 6,
        strokeStyle: "solid",
        lineJoin: 'round',
        lineCap: 'round'
      })

      map.add([startMarker, endMarker, polyline])
      map.setFitView([startMarker, endMarker], false, [50, 50, 50, 50]) // 自动缩放视野以包含两点
    })
  })
}

// Mock Data Generation
const generateData = () => {
  // keep mock generator as fallback for dev
  // Active Orders (Unfinished: No Amount, Distance, Time)
  const active = []
  for (let i = 1; i <= 3; i++) {
    active.push({
      InvoiceNo: `INV2023129${i}`,
      LoanDate: `2023-12-08 09:${10 + i}:00`,
      ReturnDate: `2023-12-10 14:${30 + i}:00`,
      LoanPlace: '北京大兴国际机场',
      ReturnPlace: '北京首都国际机场',
      CarID: Object.keys(mockCars)[i % 4],
      CNo: 'C0000001',
      DNo: null
    })
  }
  activeOrders.value = active
  activePagination.total = 3

  // History Orders (Finished)
  const history = []
  
  // 测试用地名池 (覆盖不同城市和地标)
  const locations = [
    { loan: '上海虹桥国际机场', return: '上海浦东国际机场' },
    { loan: '广州白云国际机场', return: '广州塔' },
    { loan: '深圳宝安国际机场', return: '深圳北站' },
    { loan: '成都双流国际机场', return: '成都春熙路' },
    { loan: '杭州萧山国际机场', return: '杭州西湖风景名胜区' },
    { loan: '武汉天河国际机场', return: '武汉黄鹤楼' }
  ]

  for (let i = 1; i <= 10; i++) {
    const loc = locations[(i - 1) % locations.length]
    history.push({
      InvoiceNo: `INV2023110${i}`,
      LoanDate: `2023-11-01 10:${15 + i}:00`,
      ReturnDate: `2023-11-03 16:${45 + i}:00`,
      LoanPlace: loc.loan,
      ReturnPlace: loc.return,
      CarID: Object.keys(mockCars)[i % 4],
      CNo: 'C0000002',
      DNo: 'D0000001',
      RoundTripDistance: 120 + i * 10,
      DriveTime: 5 + i,
      Amount: 800 + i * 100
    })
  }
  historyOrders.value = history
  pagination.total = 10
}

// 日期时间格式化（兼容后端 LocalDateTime 字符串）
const formatDateTime = (value) => {
  if (!value) return ''
  // If already formatted string in template-like format, return as-is
  try {
    const d = new Date(value)
    if (isNaN(d.getTime())) return String(value)
    return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0') + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0') + ':' + String(d.getSeconds()).padStart(2, '0')
  } catch (e) {
    return String(value)
  }
}

// Helper to normalize a rental DTO from backend into the template's expected shape
const normalizeRental = (r) => {
  return {
    InvoiceNo: r.invoiceNo || r.InvoiceNo || '',
    LoanDate: formatDateTime(r.loanDate || r.LoanDate || r.loanDateStr || ''),
    ReturnDate: formatDateTime(r.returnDate || r.ReturnDate || r.returnDateStr || ''),
    LoanPlace: r.loanPlace || r.LoanPlace || '',
    ReturnPlace: r.returnPlace || r.ReturnPlace || '',
    CarID: r.carId || r.CarID || '',
    CNo: r.cNo || r.CNo || '',
    DNo: r.dNo || r.DNo || null,
    RoundTripDistance: (r.roundTripDistance !== undefined ? r.roundTripDistance : (r.RoundTripDistance !== undefined ? r.RoundTripDistance : null)),
    DriveTime: (r.driveTime !== undefined ? r.driveTime : (r.DriveTime !== undefined ? r.DriveTime : null)),
    Amount: (r.amount !== undefined ? r.amount : (r.Amount !== undefined ? r.Amount : null)),
    // 把后端的租金率映射到统一的 RentRate 字段，便于后续计算
    RentRate: r.rentRate ?? r.RentRate ?? null,
    CustomerName: r.customerName || r.CustomerName || getCustomerName(r.cNo || r.CNo || ''),
    DriverName: r.driverName || r.DriverName || getDriverName(r.dNo || r.DNo || ''),
    CarType: r.carType || r.CarType || getCarName(r.carId || r.CarID || '')
  }
}

// Helper: check if a rental is active (not yet settled)
const isActiveRental = (r) => {
  // Per requirement: use only the `Amount` field being null/undefined as the single source of truth
  // for whether an order is "进行中". Do NOT rely on ReturnDate.
  return (r.Amount === null || r.Amount === undefined)
}

// Helper: apply keyword/date/amount filters to a list
const applyHistoryFilters = (list) => {
  return list.filter(item => {
    // keyword
    if (historySearchForm.keyword) {
      const kw = historySearchForm.keyword.toString().trim().toLowerCase()
      if (kw) {
        const found = (item.InvoiceNo || '').toString().toLowerCase().includes(kw) ||
                      (item.CarID || '').toString().toLowerCase().includes(kw) ||
                      (item.CNo || '').toString().toLowerCase().includes(kw) ||
                      (item.CustomerName || '').toString().toLowerCase().includes(kw) ||
                      (item.DriverName || '').toString().toLowerCase().includes(kw) ||
                      (item.CarType || '').toString().toLowerCase().includes(kw)
        if (!found) return false
      }
    }
    // date range
    if (historySearchForm.dateRange && historySearchForm.dateRange.length === 2) {
      const start = new Date(historySearchForm.dateRange[0]).getTime()
      const end = new Date(historySearchForm.dateRange[1]).getTime()
      const loan = item.LoanDate ? new Date(item.LoanDate).getTime() : null
      if (loan && (loan < start || loan > end)) return false
    }
    // amount range
    if (historySearchForm.minAmount !== undefined && historySearchForm.minAmount !== null) {
      if (item.Amount === null || item.Amount === undefined || Number(item.Amount) < Number(historySearchForm.minAmount)) return false
    }
    if (historySearchForm.maxAmount !== undefined && historySearchForm.maxAmount !== null) {
      if (item.Amount === null || item.Amount === undefined || Number(item.Amount) > Number(historySearchForm.maxAmount)) return false
    }

    return true
  })
}

const applyActiveFilters = (list) => {
  return list.filter(item => {
    if (activeSearchForm.keyword) {
      const kw = activeSearchForm.keyword.toString().trim().toLowerCase()
      if (kw) {
        const found = (item.InvoiceNo || '').toString().toLowerCase().includes(kw) ||
                      (item.CarID || '').toString().toLowerCase().includes(kw) ||
                      (item.CNo || '').toString().toLowerCase().includes(kw) ||
                      (item.CustomerName || '').toString().toLowerCase().includes(kw) ||
                      (item.DriverName || '').toString().toLowerCase().includes(kw) ||
                      (item.CarType || '').toString().toLowerCase().includes(kw)
        if (!found) return false
      }
    }
    if (activeSearchForm.dateRange && activeSearchForm.dateRange.length === 2) {
      const start = new Date(activeSearchForm.dateRange[0]).getTime()
      const end = new Date(activeSearchForm.dateRange[1]).getTime()
      const loan = item.LoanDate ? new Date(item.LoanDate).getTime() : null
      if (loan && (loan < start || loan > end)) return false
    }
    return true
  })
}

// Client-side paginate helper
const paginate = (list, page, size) => {
  const start = (page - 1) * size
  return list.slice(start, start + size)
}

// Fetch active (ongoing) orders from backend — now fetch full list and split
const fetchActive = async () => {
  loading.value = true
  try {
    // request non-paged list
    const resp = await getRentals({ page: -1, size: 0 })
    const data = resp && (resp.data || resp) || []
    const list = Array.isArray(data) ? data : (data.content || [])

    // Normalize first
    const normalized = list.map(normalizeRental)

    // Filter active rentals (no amount or no return date)
    let activeList = normalized.filter(isActiveRental)

    // Apply active search filters
    activeList = applyActiveFilters(activeList)

    // set total and slice for current page
    activePagination.total = activeList.length
    activeOrders.value = paginate(activeList, activePagination.currentPage, activePagination.pageSize)
  } catch (err) {
    console.error('获取进行中订单失败, 使用mock数据', err)
    generateData()
  } finally {
    loading.value = false
  }
}

// Fetch history orders (finished) from backend — fetch full list and split
const fetchHistory = async () => {
  loading.value = true
  try {
    const resp = await getRentals({ page: -1, size: 0 })
    const data = resp && (resp.data || resp) || []
    const list = Array.isArray(data) ? data : (data.content || [])

    const normalized = list.map(normalizeRental)

    // History = those with Amount (settled) and/or returnDate
    let historyList = normalized.filter(item => !isActiveRental(item))

    // Apply history filters (keyword/date/amount)
    historyList = applyHistoryFilters(historyList)

    // set total and slice
    pagination.total = historyList.length
    historyOrders.value = paginate(historyList, pagination.currentPage, pagination.pageSize)
  } catch (err) {
    console.error('获取历史订单失败, 使用mock数据', err)
    generateData()
  } finally {
    loading.value = false
  }
}

// Update search handlers to reset page and call the fetch that will paginate client-side
const handleActiveSearch = () => {
  activePagination.currentPage = 1
  fetchActive()
}

const handleHistorySearch = () => {
  pagination.currentPage = 1
  fetchHistory()
}

// Reset history search form and reload history list
const handleHistoryReset = () => {
  historySearchForm.keyword = ''
  // use null for dateRange to ensure date-picker clears correctly
  historySearchForm.dateRange = null
  historySearchForm.minAmount = null
  historySearchForm.maxAmount = null
  pagination.currentPage = 1
  pagination.pageSize = 10
  fetchHistory()
}

// When page changes we need to re-run fetch to paginate client-side
const handleActivePageChange = (val) => {
  activePagination.currentPage = val
  fetchActive()
}

const handlePageChange = (val) => {
  pagination.currentPage = val
  fetchHistory()
}

// Replace earlier @current-change handlers in template to use these names (no template edit needed because we used fetchActive/fetchHistory earlier),
// but ensure page-change handlers are in sync with pagination events.

// Dialog Logic
const openCompleteDialog = (row) => {
  currentOrder.value = row
  console.log('[OrderManagement] openCompleteDialog currentOrder:', JSON.parse(JSON.stringify(row)))
  completeForm.RoundTripDistance = 0
  completeForm.DriveTime = calculatedDriveTime.value
  dialogVisible.value = true
}

const handleCompleteSubmit = async () => {
  if (!currentOrder.value) return
  submitting.value = true
  try {
    // 构造更新 payload：只提交还车结算相关字段
    const payload = {
      invoiceNo: currentOrder.value.InvoiceNo,
      roundTripDistance: Number(completeForm.RoundTripDistance) || 0,
      driveTime: calculatedDriveTime.value,
      amount: computedEstimatedAmount.value
    }

    await updateRental(currentOrder.value.InvoiceNo, payload)

    ElMessage.success('还车结算完成')
    dialogVisible.value = false

    // 重新加载进行中 / 历史订单列表
    await fetchActive()
    await fetchHistory()
  } catch (error) {
    console.error('还车结算失败', error)
    ElMessage.error('还车结算失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 删除历史/订单
const handleDelete = async (row) => {
  try {
    await deleteRental(row.InvoiceNo || row.invoiceNo)
    ElMessage.success('删除成功')
    fetchHistory()
    fetchActive()
  } catch (error) {
    console.error('删除失败', error)
    ElMessage.error('删除失败')
  }
}

const handleActiveSizeChange = (val) => {
  activePagination.pageSize = val
  fetchActive()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchHistory()
}

onMounted(async () => {
  // Try to load real data; fallback to mock if backend not available
  await fetchActive()
  await fetchHistory()
})

// Display helpers
const displayCustomer = (row) => {
  return row.CustomerName || getCustomerName(row.CNo) || row.CNo || ''
}
const displayDriver = (row) => {
  return row.DriverName || (row.DNo ? getDriverName(row.DNo) : '自驾')
}
const displayCar = (row) => {
  return row.CarType || getCarName(row.CarID) || row.CarID || ''
}

// 打开详情视图
const handleView = (row) => {
  // row expected to be normalized already
  currentDetailOrder.value = row
  detailDialogVisible.value = true
  // initMap will run on dialog opened hook
}
</script>

<style scoped lang="scss">
/* Order Detail Dialog Styles */
.detail-container {
  display: flex;
  gap: 24px;
  height: 400px; /* Fixed height for layout */
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto;
  padding-right: 12px;
}

.detail-map {
  flex: 1;
  background: #f8fafc;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  background: #f1f5f9;
  
  .map-icon {
    font-size: 48px;
    margin-bottom: 16px;
    color: #cbd5e1;
  }
  
  p {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: #64748b;
  }
  
  .sub {
    font-size: 12px;
    color: #94a3b8;
  }
}

.info-block {
  .block-title {
    margin: 0 0 16px 0;
    font-size: 14px;
    color: #64748b;
    font-weight: 700;
    padding-left: 8px;
    border-left: 3px solid #3b82f6;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  
  .info-item {
    display: flex;
    flex-direction: column;
    
    .label {
      font-size: 12px;
      color: #94a3b8;
      margin-bottom: 4px;
    }
    
    .value {
      font-size: 14px;
      color: #1e293b;
      font-weight: 600;
      
      &.code { font-family: monospace; }
      &.plate { color: #3b82f6; }
      &.amount { color: #10b981; font-size: 16px; }
    }
  }
}

.route-timeline {
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
  padding-left: 8px;
  
  .route-point {
    display: flex;
    gap: 16px;
    position: relative;
    z-index: 2;
    
    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      margin-top: 6px;
      background: white;
      border: 2px solid #cbd5e1;
      flex-shrink: 0;
    }
    
    &.start .dot { border-color: #3b82f6; background: #3b82f6; }
    &.end .dot { border-color: #10b981; background: #10b981; }
    
    .content {
      display: flex;
      flex-direction: column;
      
      .time {
        font-size: 12px;
        color: #94a3b8;
      }
      .place {
        font-size: 14px;
        font-weight: 600;
        color: #334155;
      }
    }
  }
  
  .route-line {
    position: absolute;
    left: 12px;
    top: 14px;
    bottom: 24px;
    width: 2px;
    background: #e2e8f0;
    z-index: 1;
  }
}

.order-management {
  max-width: 1600px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;

  .section-divider {
    height: 40px;
    margin: 20px 0;
    border-top: 1px dashed rgba(0,0,0,0.1);
    position: relative;
    &::after {
      content: '•••';
      position: absolute;
      top: -12px;
      left: 50%;
      transform: translateX(-50%);
      background: #fdfbfb;
      padding: 0 10px;
      color: #ccc;
      letter-spacing: 4px;
    }
  }

  .main-card {
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    
    :deep(.el-card__body) {
      padding: 32px;
    }
  }

  .section-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 700;
        color: #334155;
        padding-left: 12px;
        border-left: 4px solid #409eff;
      }
    }
  }

  .search-bar {
    margin-bottom: 24px;
    
    .search-input {
      width: 260px;
    }
    
    :deep(.el-input__wrapper) {
      border-radius: 50px;
      background: rgba(248, 250, 252, 0.8);
      box-shadow: none;
      border: 1px solid transparent;
      padding-left: 16px;
      transition: all 0.3s;
      
      &:hover, &.is-focus {
        background: white;
        box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      }
    }
  }

  /* Modern Table Overrides */
  .modern-table {
    background: transparent !important;
    --el-table-border-color: transparent;
    --el-table-header-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    
    &::before { display: none; }

    :deep(th.el-table__cell) {
      background: transparent;
      font-weight: 600;
      color: #64748b;
      border-bottom: 1px solid rgba(0,0,0,0.05);
    }
    :deep(td.el-table__cell) {
      border-bottom: 1px solid rgba(0,0,0,0.03);
      padding: 16px 0;
    }
    :deep(.el-table__row:hover) {
      background-color: rgba(255,255,255,0.6) !important;
      td.el-table__cell { background-color: rgba(255,255,255,0.6) !important; }
    }
  }

  /* Info Styles */
  .plate {
    font-weight: 700;
    color: #3b82f6;
    background: #eff6ff;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Monaco', monospace;
  }
  .plate-mini {
    font-size: 12px;
    font-weight: 600;
    color: #475569;
    font-family: 'Monaco', monospace;
  }
  .sub-text { font-size: 12px; color: #94a3b8; margin-left: 6px; }
  .main-text { font-weight: 600; color: #1e293b; }
  
  .date-range {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #64748b;
    .arrow { color: #cbd5e1; font-size: 12px; }
    .date { font-family: 'Inter', monospace; }
  }

  .place-info {
    font-size: 13px;
    color: #475569;
    .place-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 2px;
      .el-icon { color: #94a3b8; }
    }
  }

  .amount {
    color: #10b981;
    font-weight: 700;
    font-family: 'Inter', sans-serif;
  }

  /* Dialog */
  .dialog-tip {
    margin: 0 0 20px;
    color: #64748b;
    font-size: 14px;
    background: #f8fafc;
    padding: 12px;
    border-radius: 8px;
    border: 1px dashed #e2e8f0;
  }
  
  .calculated-amount {
    margin-top: 24px;
    text-align: right;
    padding-top: 16px;
    border-top: 1px solid #f1f5f9;
    
    .label { color: #64748b; margin-right: 8px; }
    .value { font-size: 24px; font-weight: 700; color: #ef4444; }
  }

  /* 结算弹窗专用样式（放在最外层，配合 :deep 提升优先级） */
  :deep(.settlement-dialog) {
    .el-dialog__header {
      border-bottom: 1px solid #e5e7eb;
      margin-right: 0;
      padding-bottom: 12px;
    }

    .el-dialog__body {
      padding-top: 12px;
    }

    .settlement-form {
      .number-input {
        width: 220px;

        .el-input-number__decrease,
        .el-input-number__increase {
          border-left: none;
        }
      }
    }

    .fee-summary-card {
      margin-top: 18px;
      padding: 20px;
      border-radius: 12px;
      background: #ffffff;
      border: 1px solid #e6eef6;
      box-shadow: 0 6px 18px rgba(15,23,42,0.06);
      font-family: inherit;

      .fee-summary-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .title {
          font-size: 16px;
          font-weight: 700;
          color: #334155;
        }
        .subtitle {
          display: block;
          margin-top: 4px;
          font-size: 12px;
          color: #94a3b8;
          font-weight: 500;
        }
        .total {
          text-align: right;

          .label {
            display: block;
            font-size: 12px;
            color: #64748b;
          }
          .amount.dynamic {
            display: block;
            font-size: 22px;
            font-weight: 800;
            color: #059669; /* green */
            margin-top: 4px;
          }
        }
      }

      .fee-row {
        display: block;
        padding: 10px 0;
        border-top: 1px dashed #f1f5f9;

        .fee-main {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .fee-label {
            font-size: 14px;
            color: #475569;
            font-weight: 700;
          }
          .fee-amount {
            font-size: 16px;
            font-weight: 700;
            color: #475569;
          }
        }

        /* Per-item color accents */
        &.distance .fee-amount { color: #f97316; } /* orange */
        &.time .fee-amount     { color: #ef4444; } /* red */
        &.driver .fee-amount   { color: #2563eb; } /* blue */
        &.service .fee-amount  { color: #6b7280; } /* gray */

        .fee-formula {
          display: block;
          margin-top: 6px;
          font-size: 13px;
          color: #6b7280;

          .highlight-number {
            color: #111827;
            font-weight: 700;
            font-size: 14px;
          }
          .unit.small {
            font-size: 12px;
            color: #94a3b8;
            margin-left: 6px;
          }
        }
      }
    }
  }

  .pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }

  .readonly-time {
    display: inline-flex;
    align-items: baseline;
    padding: 4px 12px;
    border-radius: 6px;
    background: #f9fafb;
    border: 1px dashed #cbd5e1;

    .time-value {
      font-size: 18px;
      font-weight: 600;
      color: #0f766e;
      margin-right: 4px;
    }

    .time-unit {
      font-size: 13px;
      color: #64748b;
    }
  }
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

<!-- Global styles for elements appended to <body> (el-dialog with append-to-body) -->
<style lang="scss">
/* More specific selectors to ensure they override Element Plus defaults */
.el-dialog.settlement-dialog .fee-summary-card,
.el-dialog__wrapper.settlement-dialog .fee-summary-card,
.settlement-dialog .fee-summary-card {
  margin-top: 18px;
  padding: 20px !important;
  border-radius: 12px !important;
  background: #ffffff !important;
  border: 1px solid #e6eef6 !important;
  box-shadow: 0 6px 18px rgba(15,23,42,0.06) !important;
  font-family: inherit !important;
}

.el-dialog.settlement-dialog .fee-summary-header,
.el-dialog__wrapper.settlement-dialog .fee-summary-header,
.settlement-dialog .fee-summary-header {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  margin-bottom: 12px !important;
}

.el-dialog.settlement-dialog .fee-summary-header .title,
.settlement-dialog .fee-summary-header .title {
  font-size: 16px !important;
  font-weight: 700 !important;
  color: #334155 !important;
}

.el-dialog.settlement-dialog .fee-summary-header .subtitle,
.settlement-dialog .fee-summary-header .subtitle {
  display: block !important;
  margin-top: 4px !important;
  font-size: 12px !important;
  color: #94a3b8 !important;
  font-weight: 500 !important;
}

.el-dialog.settlement-dialog .fee-summary-header .total .amount.dynamic,
.settlement-dialog .fee-summary-header .total .amount.dynamic {
  display: block !important;
  font-size: 22px !important;
  font-weight: 800 !important;
  color: #059669 !important; /* green */
  margin-top: 4px !important;
}

/* keep other rules (per-item accents) with higher specificity */
.el-dialog.settlement-dialog .fee-row.distance .fee-amount,
.settlement-dialog .fee-row.distance .fee-amount { color: #f97316 !important; }
.el-dialog.settlement-dialog .fee-row.time .fee-amount,
.settlement-dialog .fee-row.time .fee-amount     { color: #ef4444 !important; }
.el-dialog.settlement-dialog .fee-row.driver .fee-amount,
.settlement-dialog .fee-row.driver .fee-amount   { color: #2563eb !important; }
.el-dialog.settlement-dialog .fee-row.service .fee-amount,
.settlement-dialog .fee-row.service .fee-amount  { color: #6b7280 !important; }

/* Footer layout specificity */
.el-dialog.settlement-dialog .el-dialog__footer,
.settlement-dialog .el-dialog__footer {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 12px !important;
}

@media (max-width: 480px) {
  .el-dialog.settlement-dialog .fee-summary-header .total .amount.dynamic,
  .settlement-dialog .fee-summary-header .total .amount.dynamic { font-size: 18px !important; }
  .el-dialog.settlement-dialog .fee-row .fee-amount,
  .settlement-dialog .fee-row .fee-amount { font-size: 15px !important; }
}
</style>

<template>
  <div class="order-management">
    
    <!-- 进行中订单 (Active) -->
    <div id="section-active">
      <el-card class="main-card" shadow="never">
        <div class="section-header">
          <div class="header-left">
            <h3>进行中订单</h3>
            <el-tag type="success" effect="light" round class="count-tag">{{ activeOrders.length }} 进行中</el-tag>
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
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="activeSearchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="借车开始"
                end-placeholder="借车结束"
                value-format="YYYY-MM-DD"
                style="width: 240px"
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
                <span class="sub-text">{{ getCarName(scope.row.CarID) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="客户" min-width="120">
            <template #default="scope">
              <div class="info-group">
                <span class="main-text">{{ getCustomerName(scope.row.CNo) }}</span>
                <span class="sub-text">{{ scope.row.CNo }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="借还日期" width="220">
            <template #default="scope">
              <div class="date-range">
                <span class="date">{{ scope.row.LoanDate }}</span>
                <el-icon class="arrow"><Right /></el-icon>
                <span class="date">{{ scope.row.ReturnDate }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="借还地点" min-width="200" show-overflow-tooltip>
             <template #default="scope">
               <div class="place-info">
                 <div class="place-row"><el-icon><Location /></el-icon> 借: {{ scope.row.LoanPlace }}</div>
                 <div class="place-row"><el-icon><LocationFilled /></el-icon> 还: {{ scope.row.ReturnPlace }}</div>
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
            @current-change="fetchActive"
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
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="historySearchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD"
                style="width: 220px"
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
            <template #default="scope">{{ getCustomerName(scope.row.CNo) }}</template>
          </el-table-column>
          
          <el-table-column label="司机" min-width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.DNo" size="small" type="info">{{ getDriverName(scope.row.DNo) }}</el-tag>
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
          
          <el-table-column label="借还日期" width="220" sortable>
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
            @current-change="fetchHistory"
          />
        </div>
      </el-card>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="还车结算确认"
      width="420px"
      class="custom-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <p class="dialog-tip">请录入车辆返回时的实际行驶数据</p>
        <el-form :model="completeForm" label-width="100px" size="large">
          <el-form-item label="往返里程">
            <el-input-number v-model="completeForm.RoundTripDistance" :min="0" :step="10" controls-position="right">
              <template #append>km</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="行驶时间">
            <el-input-number v-model="completeForm.DriveTime" :min="0" :step="1" controls-position="right">
              <template #append>h</template>
            </el-input-number>
          </el-form-item>
          
          <div class="calculated-amount">
            <span class="label">预计金额:</span>
            <span class="value">¥{{ calculateAmount }}</span>
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
                <span class="value">{{ getCustomerName(currentDetailOrder.CNo) }}</span>
              </div>
              <div class="info-item">
                <span class="label">司机</span>
                <span class="value">{{ currentDetailOrder.DNo ? getDriverName(currentDetailOrder.DNo) : '自驾' }}</span>
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
import { Search, Right, Location, LocationFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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
const mockCars = { '京A88888': 'Toyota Camry', '京AD12345': 'Tesla Model 3', '京A66666': 'Benz S-Class', '京A99999': 'BMW X5' }
const mockCustomers = { 'C0000001': '张客户1', 'C0000002': '李客户2' }
const mockDrivers = { 'D0000001': '王司机1' }

const getCarName = (id) => mockCars[id] || id
const getCustomerName = (id) => mockCustomers[id] || id
const getDriverName = (id) => mockDrivers[id] || id

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
  // Active Orders (Unfinished: No Amount, Distance, Time)
  const active = []
  for (let i = 1; i <= 3; i++) {
    active.push({
      InvoiceNo: `INV2023129${i}`,
      LoanDate: '2023-12-08',
      ReturnDate: '2023-12-10',
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
  for (let i = 1; i <= 10; i++) {
    history.push({
      InvoiceNo: `INV2023110${i}`,
      LoanDate: '2023-11-01',
      ReturnDate: '2023-11-03',
      LoanPlace: '上海虹桥国际机场',
      ReturnPlace: '上海浦东国际机场',
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

const handleActiveSearch = () => {
  loading.value = true
  // 模拟向后端发送查询请求
  const queryParams = { ...activeSearchForm }
  console.log('【后端查询】正在请求进行中订单，参数:', queryParams)
  
  // TODO: 对接后端时，使用 axios.post('/api/orders/active/search', queryParams)
  setTimeout(() => { loading.value = false; ElMessage.success('查询完成 (Mock)') }, 300)
}

const handleHistorySearch = () => {
  loading.value = true
  // 模拟向后端发送查询请求
  const queryParams = { 
    ...historySearchForm,
    // 如果日期范围存在，拆分为 startDate 和 endDate 发给后端 SQL 使用
    startDate: historySearchForm.dateRange?.[0],
    endDate: historySearchForm.dateRange?.[1]
  }
  console.log('【后端查询】正在请求历史订单，参数:', queryParams)

  // TODO: 对接后端时，使用 axios.post('/api/orders/history/search', queryParams)
  // 后端 SQL 示例: WHERE LoanDate BETWEEN #{startDate} AND #{endDate} AND Amount >= #{minAmount}...
  setTimeout(() => { loading.value = false; ElMessage.success('查询完成 (Mock)') }, 300)
}

const handleHistoryReset = () => {
  historySearchForm.keyword = ''
  historySearchForm.dateRange = []
  historySearchForm.minAmount = undefined
  historySearchForm.maxAmount = undefined
  handleHistorySearch()
}

// Dialog Logic
const openCompleteDialog = (row) => {
  currentOrder.value = row
  completeForm.RoundTripDistance = 0
  completeForm.DriveTime = 0
  dialogVisible.value = true
}

const calculateAmount = computed(() => {
  // Simple Mock Calculation: Base 200 + 2 * km + 50 * hour
  return 200 + (completeForm.RoundTripDistance * 2) + (completeForm.DriveTime * 50)
})

const handleCompleteSubmit = () => {
  submitting.value = true
  setTimeout(() => {
    // Mock Move to History
    const finishedOrder = {
      ...currentOrder.value,
      RoundTripDistance: completeForm.RoundTripDistance,
      DriveTime: completeForm.DriveTime,
      Amount: calculateAmount.value
    }
    
    // Remove from active
    activeOrders.value = activeOrders.value.filter(o => o.InvoiceNo !== finishedOrder.InvoiceNo)
    // Add to history (top)
    historyOrders.value.unshift(finishedOrder)
    
    submitting.value = false
    dialogVisible.value = false
    ElMessage.success('订单已结算归档')
  }, 600)
}

const handleView = (row) => {
  currentDetailOrder.value = row
  detailDialogVisible.value = true
  hasMapKey.value = false // Reset, let initMap check again
}

const fetchHistory = () => {
  // Mock page change
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchHistory()
}

const fetchActive = () => {
  // Mock page change
}

const handleActiveSizeChange = (val) => {
  activePagination.pageSize = val
  fetchActive()
}

onMounted(() => {
  generateData()
})
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

  .pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

<template>
  <div class="order-management">
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="订单号">
            <el-input v-model="searchForm.invoiceNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="客户">
            <el-input v-model="searchForm.customer" placeholder="请输入客户姓名" clearable />
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="InvoiceNo" label="订单号" width="140" fixed />
        
        <el-table-column label="车辆信息" min-width="160">
          <template #default="scope">
            <div class="car-info">
              <span class="plate">{{ scope.row.CarID }}</span>
              <span class="model">{{ getCarName(scope.row.CarID) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="客户信息" min-width="120">
          <template #default="scope">
            <div>{{ getCustomerName(scope.row.CNo) }}</div>
            <div class="sub-text">{{ scope.row.CNo }}</div>
          </template>
        </el-table-column>

        <el-table-column label="司机信息" min-width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.DNo" size="small" type="success">{{ getDriverName(scope.row.DNo) }}</el-tag>
            <span v-else class="sub-text">自驾</span>
          </template>
        </el-table-column>

        <el-table-column prop="Place" label="地点" min-width="150" show-overflow-tooltip />
        <el-table-column prop="Date" label="日期" width="120" sortable />
        
        <el-table-column prop="Amount" label="金额" width="120" align="right">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.Amount }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next, jumper"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const searchForm = reactive({
  invoiceNo: '',
  customer: '',
  dateRange: []
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

// Mock 辅助数据
const mockCars = {
  '京A88888': 'Toyota Camry',
  '京AD12345': 'Tesla Model 3',
  '京A66666': 'Benz S-Class',
  '京A99999': 'BMW X5'
}
const mockCustomers = {
  'C0000001': '张客户1',
  'C0000002': '李客户2'
}
const mockDrivers = {
  'D0000001': '王司机1'
}

// 辅助函数
const getCarName = (carId) => mockCars[carId] || '未知车型'
const getCustomerName = (cNo) => mockCustomers[cNo] || cNo
const getDriverName = (dNo) => mockDrivers[dNo] || dNo

// 生成 Mock 订单数据
const generateMockData = () => {
  const data = []
  for (let i = 1; i <= 15; i++) {
    data.push({
      InvoiceNo: `INV2023120${i.toString().padStart(2, '0')}`,
      Date: `2023-12-${(i % 30 + 1).toString().padStart(2, '0')}`,
      Place: i % 2 === 0 ? '北京大兴国际机场' : '上海虹桥火车站',
      CarID: Object.keys(mockCars)[i % 4],
      CNo: i % 2 === 0 ? 'C0000001' : 'C0000002',
      DNo: i % 3 === 0 ? 'D0000001' : null,
      RoundTripDistance: 50 + i * 10,
      DriveTime: 2 + i * 0.5,
      Amount: 500 + i * 150
    })
  }
  return data
}

const fetchData = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = generateMockData()
    pagination.total = 15
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  ElMessage.success('搜索已执行')
  fetchData()
}

const handleReset = () => {
  searchForm.invoiceNo = ''
  searchForm.customer = ''
  searchForm.dateRange = []
  fetchData()
}

const handleView = (row) => {
  ElMessageBox.alert(
    `
    <p><strong>订单号:</strong> ${row.InvoiceNo}</p>
    <p><strong>行程:</strong> ${row.Place}</p>
    <p><strong>里程:</strong> ${row.RoundTripDistance} km</p>
    <p><strong>时长:</strong> ${row.DriveTime} 小时</p>
    <p><strong>金额:</strong> ¥${row.Amount}</p>
    `,
    '订单详情',
    { dangerouslyUseHTMLString: true }
  )
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.order-management {
  max-width: 1600px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;

  /* 搜索卡片玻璃拟态 */
  .search-card {
    margin-bottom: 24px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
    
    :deep(.el-card__body) {
      padding: 24px;
    }
  }
  
  /* 表格卡片玻璃拟态 */
  .table-card {
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    
    :deep(.el-card__body) {
      padding: 32px;
    }
  }

  /* 表格样式透传 */
  :deep(.el-table) {
    background-color: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-header-bg-color: rgba(248, 250, 252, 0.5);
    --el-table-row-hover-bg-color: rgba(64, 158, 255, 0.08);
    
    th.el-table__cell {
      font-weight: 600;
      color: #475569;
      height: 50px;
    }
    
    td.el-table__cell {
      color: #1e293b;
      padding: 12px 0;
    }
  }

  .car-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .plate {
      font-weight: 700;
      color: #1e293b;
      font-family: 'Monaco', monospace;
      background: #eff6ff;
      padding: 2px 6px;
      border-radius: 4px;
      display: inline-block;
      width: fit-content;
      font-size: 13px;
      color: #3b82f6;
    }
    .model {
      font-size: 13px;
      color: #64748b;
    }
  }

  .sub-text {
    font-size: 12px;
    color: #94a3b8;
    margin-top: 2px;
  }

  .amount {
    color: #ef4444;
    font-weight: 700;
    font-family: 'Inter', sans-serif;
    font-size: 15px;
  }

  .pagination-container {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

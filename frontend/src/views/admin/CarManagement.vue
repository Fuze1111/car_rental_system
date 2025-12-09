<template>
  <div class="car-management">
    <!-- 顶部搜索与操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">车辆管理</h2>
        <span class="page-subtitle">管理车队信息与状态</span>
      </div>
      <div class="header-right">
        <el-button type="primary" size="large" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增车辆
        </el-button>
      </div>
    </div>

    <el-card class="main-card" shadow="hover">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item>
            <el-input v-model="searchForm.CarID" placeholder="搜索车牌号..." prefix-icon="Search" clearable class="search-input" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="searchForm.TName" placeholder="搜索车型..." prefix-icon="Van" clearable class="search-input" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.CarStatus" placeholder="车辆状态" clearable style="width: 140px">
              <el-option label="全部状态" value="" />
              <el-option label="可租" value="0" />
              <el-option label="已租" value="1" />
              <el-option label="维修中" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" plain @click="handleSearch" class="search-btn">
              查询
            </el-button>
            <el-button link @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 车辆列表表格 -->
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading" 
        :header-cell-style="{ background: '#f8f9fa', color: '#606266', height: '50px' }"
        :row-style="{ height: '70px' }"
      >
        <!-- 车牌号 (CarID) -->
        <el-table-column prop="CarID" label="车牌号" min-width="140" align="left">
          <template #default="scope">
            <div class="license-plate" :class="{ 'green-plate': scope.row.CarID.length > 7 }">
              {{ scope.row.CarID }}
            </div>
          </template>
        </el-table-column>

        <!-- 车型名称 (TName) -->
        <el-table-column prop="TName" label="车型信息" min-width="200" align="left">
          <template #default="scope">
            <div class="car-info">
              <div class="car-model">{{ scope.row.TName }}</div>
              <div class="car-code">编号: {{ scope.row.TNo }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 租金率 (RentRate) -->
        <el-table-column prop="RentRate" label="日租金" width="150" align="left">
          <template #default="scope">
            <div class="price-tag">
              <span class="currency">¥</span>
              <span class="amount">{{ scope.row.RentRate }}</span>
              <span class="unit">/天</span>
            </div>
          </template>
        </el-table-column>

        <!-- 购入价格 (Price) -->
        <el-table-column prop="Price" label="购入价格" width="150" align="right">
          <template #default="scope">
            <span class="text-secondary">¥{{ scope.row.Price.toLocaleString() }}</span>
          </template>
        </el-table-column>

        <!-- 购入日期 -->
        <el-table-column prop="PurchaseDate" label="购入日期" width="140" align="center">
          <template #default="scope">
            <span class="text-secondary">{{ scope.row.PurchaseDate }}</span>
          </template>
        </el-table-column>

        <!-- 状态 (CarStatus) -->
        <el-table-column label="状态" width="120" align="center">
          <template #default="scope">
            <div class="status-badge" :class="getStatusClass(scope.row.CarStatus)">
              <span class="dot"></span>
              {{ getStatusLabel(scope.row.CarStatus) }}
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button circle size="small" type="primary" plain @click="handleEdit(scope.row)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button circle size="small" type="danger" plain @click="handleDelete(scope.row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <CarEditDialog 
      v-model:visible="dialogVisible"
      :edit-data="currentEditData"
      @submit="handleDialogSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, defineAsyncComponent } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, Van } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 异步加载组件
const CarEditDialog = defineAsyncComponent(() => 
  import('./components/CarDialog.vue')
)

// 弹窗控制
const dialogVisible = ref(false)
const currentEditData = ref(null)

// 搜索表单
const searchForm = reactive({
  CarID: '',
  TName: '',
  CarStatus: ''
})

const loading = ref(false)
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 4
})

// Mock 数据
const tableData = ref([
  {
    CarID: '京A88888',
    TNo: 'T001',
    TName: 'Toyota Camry 2023款',
    RentRate: 350.00,
    CarStatus: '0',
    PurchaseDate: '2022-05-15',
    Price: 200000.00
  },
  {
    CarID: '京AD12345',
    TNo: 'T002',
    TName: 'Tesla Model 3 Performance',
    RentRate: 500.00,
    CarStatus: '1',
    PurchaseDate: '2023-01-10',
    Price: 280000.00
  },
  {
    CarID: '京A66666',
    TNo: 'T003',
    TName: 'Mercedes-Benz S-Class',
    RentRate: 1200.00,
    CarStatus: '2',
    PurchaseDate: '2021-11-20',
    Price: 900000.00
  },
  {
    CarID: '京A99999',
    TNo: 'T004',
    TName: 'BMW X5 xDrive40i',
    RentRate: 1100.00,
    CarStatus: '0',
    PurchaseDate: '2022-08-08',
    Price: 750000.00
  },
])

const getStatusClass = (status) => {
  const map = {
    '0': 'status-success', // 可租
    '1': 'status-danger',  // 已租
    '2': 'status-warning'  // 维修
  }
  return map[status] || ''
}

const getStatusLabel = (status) => {
  const map = {
    '0': '可租',
    '1': '已租',
    '2': '维修中'
  }
  return map[status] || '未知'
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索结果已更新')
  }, 500)
}

const resetSearch = () => {
  searchForm.CarID = ''
  searchForm.TName = ''
  searchForm.CarStatus = ''
  handleSearch()
}

const handleAdd = () => {
  currentEditData.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentEditData.value = { ...row }
  dialogVisible.value = true
}

const handleDialogSubmit = (formData) => {
  if (currentEditData.value) {
    const index = tableData.value.findIndex(item => item.CarID === currentEditData.value.CarID)
    if (index !== -1) {
      tableData.value[index] = { ...tableData.value[index], ...formData }
    }
  } else {
    tableData.value.unshift({ ...formData })
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除 ${row.TName} (${row.CarID}) 吗？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(() => {
    tableData.value = tableData.value.filter(item => item.CarID !== row.CarID)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  console.log(`每页 ${val} 条`)
}
const handleCurrentChange = (val) => {
  console.log(`当前页: ${val}`)
}
</script>

<style scoped lang="scss">
.car-management {
  padding: 0;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
      .page-title {
        font-size: 24px;
        font-weight: 600;
        color: #1a1a1a;
        margin: 0 0 8px 0;
      }
      
      .page-subtitle {
        color: #909399;
        font-size: 14px;
      }
    }

    .add-btn {
      border-radius: 8px;
      padding: 12px 24px;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-2px);
      }
    }
  }

  .main-card {
    border: none;
    border-radius: 16px;
    background: #fff;
    overflow: hidden;
    
    :deep(.el-card__body) {
      padding: 24px;
    }
  }

  .search-bar {
    margin-bottom: 24px;
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      
      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
      }

      .search-input {
        width: 220px;
        
        :deep(.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 1px #dcdfe6 inset;
          
          &:hover {
            box-shadow: 0 0 0 1px #c0c4cc inset;
          }
          
          &.is-focus {
            box-shadow: 0 0 0 1px #409eff inset;
          }
        }
      }

      .search-btn {
        border-radius: 8px;
      }
    }
  }

  /* 车牌样式 */
  .license-plate {
    display: inline-block;
    padding: 2px 8px;
    background: #409eff;
    color: white;
    border-radius: 4px;
    border: 2px solid white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    font-family: 'Monaco', 'Consolas', monospace;
    font-weight: bold;
    font-size: 14px;
    
    &.green-plate {
      background: #00b578; /* 新能源绿 */
    }
  }

  .car-info {
    .car-model {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    .car-code {
      font-size: 12px;
      color: #909399;
    }
  }

  .price-tag {
    .currency {
      font-size: 12px;
      color: #f56c6c;
      margin-right: 2px;
    }
    .amount {
      font-size: 18px;
      font-weight: bold;
      color: #f56c6c;
      font-family: 'DIN Alternate', sans-serif;
    }
    .unit {
      font-size: 12px;
      color: #909399;
    }
  }

  .text-secondary {
    color: #606266;
  }

  /* 状态徽章 */
  .status-badge {
    display: inline-flex;
    align-items: center;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 500;
    
    .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      margin-right: 6px;
    }

    &.status-success {
      background: #f0f9eb;
      color: #67c23a;
      .dot { background: #67c23a; }
    }
    
    &.status-danger {
      background: #fef0f0;
      color: #f56c6c;
      .dot { background: #f56c6c; }
    }
    
    &.status-warning {
      background: #fdf6ec;
      color: #e6a23c;
      .dot { background: #e6a23c; }
    }
  }

  .pagination-container {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

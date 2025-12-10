<template>
  <div class="maintenance-management">
    <el-card class="main-card" shadow="hover">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="search-left">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item>
              <el-input v-model="searchForm.keyword" placeholder="搜索车牌号..." prefix-icon="Search" clearable class="search-input" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch" class="search-btn">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="resetSearch">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 维修列表表格 -->
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading" 
        class="modern-table"
      >
        <el-table-column prop="RNo" label="维修单号" width="120" align="center">
          <template #default="scope">
            <span class="mono-text">#{{ scope.row.RNo }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="CarID" label="车牌号" width="140" align="center">
          <template #default="scope">
            <el-tag effect="light">{{ scope.row.CarID }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="RDate" label="送修日期" width="160" align="center" sortable />

        <el-table-column label="送修时状态" align="center">
          <el-table-column prop="RunKM" label="里程 (km)" align="right">
            <template #default="scope">{{ scope.row.RunKM.toLocaleString() }}</template>
          </el-table-column>
          <el-table-column prop="RunHour" label="运行时间 (h)" align="right" />
        </el-table-column>

        <el-table-column prop="Status" label="当前状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.Status === 'Pending' ? 'warning' : 'success'" round>
              {{ scope.row.Status === 'Pending' ? '维修中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.Status === 'Pending'"
              size="small" 
              type="success" 
              plain 
              @click="handleFinish(scope.row)"
            >
              <el-icon style="margin-right: 4px"><Check /></el-icon>完成维修
            </el-button>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Check } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const searchForm = reactive({ keyword: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const tableData = ref([])

// Mock Data
const mockData = [
  { RNo: '1001', CarID: '京A66666', RDate: '2023-12-01', RunKM: 50000, RunHour: 1200, Status: 'Pending' },
  { RNo: '1000', CarID: '京A88888', RDate: '2023-11-15', RunKM: 30000, RunHour: 800, Status: 'Completed' },
]

const fetchData = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = mockData.filter(item => 
      !searchForm.keyword || item.CarID.includes(searchForm.keyword)
    )
    pagination.total = tableData.value.length
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  fetchData()
  ElMessage.success('搜索已完成')
}

const resetSearch = () => {
  searchForm.keyword = ''
  fetchData()
}

const handleFinish = (row) => {
  ElMessageBox.confirm(`确认车辆 ${row.CarID} 维修已完成？\n车辆状态将恢复为“可租”。`, '完成维修', {
    type: 'success',
    confirmButtonText: '确认完成',
    cancelButtonText: '取消'
  }).then(() => {
    row.Status = 'Completed'
    ElMessage.success('维修记录已更新，车辆已恢复可租状态')
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.maintenance-management {
  .main-card {
    border: 1px solid rgba(255, 255, 255, 0.8);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    overflow: hidden;
    
    :deep(.el-card__body) { padding: 32px; }
  }

  .search-bar {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .search-input {
      width: 260px;
      :deep(.el-input__wrapper) {
        border-radius: 50px;
        background: rgba(248, 250, 252, 0.8);
        box-shadow: none;
        padding-left: 16px;
        transition: all 0.3s;
        &:hover { background: white; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        &.is-focus { background: white; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15); }
      }
    }

    :deep(.el-button) {
      border-radius: 50px;
      padding: 8px 24px;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.3s;
      &:hover { transform: translateY(-2px); }
    }

    .search-btn {
      background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
      border: none;
      color: white;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      &:hover { box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4); }
    }
  }

  .modern-table {
    background-color: transparent !important;
    --el-table-border-color: transparent;
    --el-table-header-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    &::before { display: none; }

    :deep(th.el-table__cell) {
      background: transparent;
      font-weight: 600;
      color: #64748b;
      height: 56px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    }

    :deep(td.el-table__cell) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.03);
      padding: 16px 0;
      transition: all 0.3s;
    }

    :deep(.el-table__row) {
      transition: all 0.3s;
      &:hover {
        background-color: rgba(255, 255, 255, 0.6) !important;
        transform: scale(1.002);
        td.el-table__cell { background-color: rgba(255, 255, 255, 0.6) !important; }
      }
    }
  }

  .mono-text {
    font-family: 'Monaco', monospace;
    color: #64748b;
  }
  
  .text-gray { color: #ccc; }

  .pagination-container {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

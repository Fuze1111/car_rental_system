<template>
  <div class="user-management">
    <el-card shadow="never" class="main-card">
      <!-- 顶部标签页切换客户/司机 -->
      <el-tabs v-model="activeTab" class="user-tabs" @tab-click="handleTabChange">
        <el-tab-pane label="客户列表" name="customer">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><User /></el-icon>
              <span>客户列表</span>
            </div>
          </template>
        </el-tab-pane>
        <el-tab-pane label="司机列表" name="driver">
          <template #label>
            <div class="custom-tab-label">
              <el-icon><Van /></el-icon>
              <span>司机列表</span>
            </div>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="searchForm.phone" placeholder="请输入电话" clearable />
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

      <!-- 用户表格 -->
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <!-- 客户特有字段 -->
        <template v-if="activeTab === 'customer'">
          <el-table-column prop="CNo" label="客户号" width="120" />
          <el-table-column prop="CName" label="姓名" width="120">
            <template #default="scope">
              <div class="user-name">{{ scope.row.CName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="Company" label="单位" min-width="180" show-overflow-tooltip />
          <el-table-column prop="Phone" label="电话" width="140" />
          <el-table-column prop="Address" label="地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="PostCode" label="邮编" width="100" />
        </template>

        <!-- 司机特有字段 -->
        <template v-if="activeTab === 'driver'">
          <el-table-column prop="DNo" label="司机号" width="120" />
          <el-table-column prop="DName" label="姓名" width="120">
            <template #default="scope">
              <div class="user-name">{{ scope.row.DName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="LicenseType" label="准驾车型" width="100" align="center">
            <template #default="scope">
              <el-tag size="small" type="warning">{{ scope.row.LicenseType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="StartWork" label="入职时间" width="140" />
          <el-table-column prop="Salary" label="基本工资" width="120" align="right">
            <template #default="scope">¥{{ scope.row.Salary }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getDriverStatusType(scope.row.DStatus)" size="small" round>
                {{ scope.row.DStatus }}
              </el-tag>
            </template>
          </el-table-column>
        </template>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
import { User, Van, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('customer')
const loading = ref(false)

const searchForm = reactive({
  name: '',
  phone: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

// Mock 数据生成 (基于 schema.sql)
const generateMockData = (type) => {
  const data = []
  const count = type === 'customer' ? 8 : 5
  
  if (type === 'customer') {
    for (let i = 1; i <= count; i++) {
      data.push({
        CNo: `C000000${i}`,
        CName: `张客户${i}`,
        Company: `科技无限公司${i}分部`,
        Phone: `1380013800${i}`,
        Address: `北京市海淀区科技园路${i}号`,
        PostCode: '100000'
      })
    }
  } else {
    for (let i = 1; i <= count; i++) {
      data.push({
        DNo: `D000000${i}`,
        DName: `李司机${i}`,
        StartWork: '2020-01-01',
        Salary: 5000 + i * 100,
        DStatus: i % 2 === 0 ? '在岗' : '空闲',
        LicenseType: 'A1'
      })
    }
  }
  return data
}

const getDriverStatusType = (status) => {
  return status === '空闲' ? 'success' : 'warning'
}

const fetchData = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = generateMockData(activeTab.value)
    pagination.total = activeTab.value === 'customer' ? 85 : 24
    loading.value = false
  }, 300)
}

const handleTabChange = () => {
  pagination.currentPage = 1
  fetchData()
}

const handleSearch = () => {
  ElMessage.success('搜索已执行')
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.phone = ''
  fetchData()
}

const handleView = (row) => {
  const name = activeTab.value === 'customer' ? row.CName : row.DName
  ElMessage.info(`查看用户: ${name}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.user-management {
  max-width: 1600px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;

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

  /* 标签页样式优化 */
  .user-tabs {
    :deep(.el-tabs__nav-wrap::after) {
      height: 1px;
      background-color: rgba(0,0,0,0.05);
    }
    
    :deep(.el-tabs__item) {
      font-size: 16px;
      height: 50px;
      color: #64748b;
      
      &.is-active {
        color: #409eff;
        font-weight: 600;
        
        .custom-tab-label {
          background: #ecf5ff;
          color: #409eff;
        }
      }
    }

    :deep(.el-tabs__active-bar) {
      height: 3px;
      border-radius: 3px;
      background: linear-gradient(90deg, #409eff, #60a5fa);
    }
  }

  .custom-tab-label {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 16px;
    border-radius: 8px;
    transition: all 0.3s;
    
    .el-icon { font-size: 18px; }
  }

  .search-bar {
    margin: 24px 0;
    padding: 24px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.6);
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
    }
    
    td.el-table__cell {
      color: #1e293b;
    }
  }

  .user-name {
    font-weight: 600;
    color: #1e293b;
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

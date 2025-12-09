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
          <el-form-item label="用户名/姓名">
            <el-input v-model="searchForm.keyword" placeholder="请输入姓名或昵称" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="正常" value="active" />
              <el-option label="禁用" value="banned" />
            </el-select>
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
        <el-table-column label="用户" min-width="180">
          <template #default="scope">
            <div class="user-info-cell">
              <el-avatar :size="40" :src="scope.row.avatar" />
              <div class="info-text">
                <div class="name">{{ scope.row.nickname || scope.row.realName }}</div>
                <div class="username">@{{ scope.row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        
        <!-- 司机特有字段 -->
        <el-table-column v-if="activeTab === 'driver'" prop="licenseNumber" label="驾驶证号" width="180" />
        <el-table-column v-if="activeTab === 'driver'" prop="drivingYears" label="驾龄" width="100" align="center">
          <template #default="scope">
            {{ scope.row.drivingYears }} 年
          </template>
        </el-table-column>

        <el-table-column prop="registerTime" label="注册时间" width="180" />
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'" size="small" round>
              {{ scope.row.status === 'active' ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
            <el-button 
              link 
              :type="scope.row.status === 'active' ? 'danger' : 'success'" 
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('customer')
const loading = ref(false)

const searchForm = reactive({
  keyword: '',
  phone: '',
  status: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

// Mock 数据生成
const generateMockData = (type) => {
  const data = []
  const count = type === 'customer' ? 8 : 5
  for (let i = 1; i <= count; i++) {
    data.push({
      id: i,
      username: type === 'customer' ? `user_00${i}` : `driver_00${i}`,
      realName: type === 'customer' ? `张客户${i}` : `李司机${i}`,
      nickname: type === 'customer' ? `快乐小狗${i}号` : `老司机${i}号`,
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      phone: `1380013800${i}`,
      status: i % 4 === 0 ? 'banned' : 'active',
      registerTime: '2023-12-01 10:00:00',
      // 司机特有
      licenseNumber: type === 'driver' ? `11010119900101000${i}` : undefined,
      drivingYears: type === 'driver' ? 5 + i : undefined
    })
  }
  return data
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
  searchForm.keyword = ''
  searchForm.phone = ''
  searchForm.status = ''
  fetchData()
}

const handleView = (row) => {
  ElMessage.info(`查看用户: ${row.realName}`)
}

const handleToggleStatus = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    row.status = row.status === 'active' ? 'banned' : 'active'
    ElMessage.success(`已${action}`)
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.user-management {
  .main-card {
    border-radius: 8px;
  }

  .custom-tab-label {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .search-bar {
    margin-top: 10px;
    margin-bottom: 10px;
    padding-bottom: 10px;
    border-bottom: 1px dashed #eee;
  }

  .user-info-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .info-text {
      .name {
        font-weight: 600;
        font-size: 14px;
        color: #303133;
      }
      .username {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

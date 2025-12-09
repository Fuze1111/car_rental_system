<template>
  <div class="car-management">
    <!-- 顶部搜索与操作栏 -->
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="品牌/型号">
            <el-input v-model="searchForm.keyword" placeholder="请输入车辆品牌或型号" clearable />
          </el-form-item>
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.plateNumber" placeholder="请输入车牌号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="空闲" value="available" />
              <el-option label="已租出" value="rented" />
              <el-option label="维修中" value="maintenance" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="action-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增车辆
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 车辆列表表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe>
        <!-- 图片列 -->
        <el-table-column label="车辆图片" width="120" align="center">
          <template #default="scope">
            <el-image 
              class="car-thumb"
              :src="scope.row.image" 
              :preview-src-list="[scope.row.image]"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <!-- 车辆信息 -->
        <el-table-column label="车辆信息" min-width="180">
          <template #default="scope">
            <div class="car-info">
              <div class="car-name">{{ scope.row.brand }} {{ scope.row.model }}</div>
              <div class="car-plate">
                <el-tag size="small" type="info" effect="plain">{{ scope.row.plateNumber }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="100" align="center" />
        
        <el-table-column prop="dailyRate" label="日租金" width="120" align="right">
          <template #default="scope">
            <span class="price">¥{{ scope.row.dailyRate }}</span>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="dark" round>
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="updateTime" label="更新时间" width="160" align="center" />

        <!-- 操作列 -->
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <CarDialog 
      v-model:visible="dialogVisible"
      :edit-data="currentEditData"
      @submit="handleDialogSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh, Plus, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CarDialog from './components/CarDialog.vue'

// 弹窗控制
const dialogVisible = ref(false)
const currentEditData = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  plateNumber: '',
  status: ''
})

const loading = ref(false)
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 45
})

// Mock 数据
const tableData = ref([
  {
    id: 1,
    brand: 'Toyota',
    model: 'Camry',
    plateNumber: '京A 88888',
    type: '轿车',
    dailyRate: 350,
    status: 'available',
    image: 'https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&w=300&q=80',
    updateTime: '2023-12-09 10:30'
  },
  {
    id: 2,
    brand: 'Tesla',
    model: 'Model 3',
    plateNumber: '京A D12345',
    type: '电动车',
    dailyRate: 500,
    status: 'rented',
    image: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&w=300&q=80',
    updateTime: '2023-12-08 14:20'
  },
  {
    id: 3,
    brand: 'Mercedes',
    model: 'Benz S-Class',
    plateNumber: '京A 66666',
    type: '豪华轿车',
    dailyRate: 1200,
    status: 'maintenance',
    image: 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=300&q=80',
    updateTime: '2023-12-07 09:15'
  },
  {
    id: 4,
    brand: 'BMW',
    model: 'X5',
    plateNumber: '京A 99999',
    type: 'SUV',
    dailyRate: 1100,
    status: 'available',
    image: 'https://images.unsplash.com/photo-1556189250-72ba954522bb?auto=format&fit=crop&w=300&q=80',
    updateTime: '2023-12-09 11:00'
  },
])

// 状态处理
const getStatusType = (status) => {
  const map = {
    available: 'success',
    rented: 'danger',
    maintenance: 'warning'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = {
    available: '空闲',
    rented: '已租出',
    maintenance: '维修中'
  }
  return map[status] || '未知'
}

// 按钮操作
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索已执行')
  }, 500)
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.plateNumber = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  currentEditData.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentEditData.value = { ...row } // 浅拷贝，避免直接修改表格数据
  dialogVisible.value = true
}

const handleDialogSubmit = (formData) => {
  if (currentEditData.value) {
    // 编辑逻辑：找到对应 ID 更新
    const index = tableData.value.findIndex(item => item.id === currentEditData.value.id)
    if (index !== -1) {
      tableData.value[index] = { 
        ...tableData.value[index], 
        ...formData,
        updateTime: new Date().toLocaleString()
      }
    }
  } else {
    // 新增逻辑：添加到头部
    tableData.value.unshift({
      id: Date.now(), // 模拟 ID
      ...formData,
      updateTime: new Date().toLocaleString()
    })
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除 ${row.brand} ${row.model} 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 模拟删除
    tableData.value = tableData.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 分页操作
const handleSizeChange = (val) => {
  console.log(`每页 ${val} 条`)
}
const handleCurrentChange = (val) => {
  console.log(`当前页: ${val}`)
}
</script>

<style scoped lang="scss">
.car-management {
  .search-card {
    margin-bottom: 20px;
    border-radius: 8px;
    
    .search-bar {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      flex-wrap: wrap;
    }
    
    .search-form {
      .el-form-item {
        margin-bottom: 0;
        margin-right: 20px;
        
        @media (max-width: 768px) {
          margin-bottom: 10px;
        }
      }
    }
  }

  .table-card {
    border-radius: 8px;
    
    .car-thumb {
      width: 80px;
      height: 50px;
      border-radius: 4px;
      cursor: pointer;
      display: block;
      
      .image-slot {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        background: #f5f7fa;
        color: #909399;
      }
    }

    .car-info {
      .car-name {
        font-weight: bold;
        font-size: 15px;
        color: #303133;
        margin-bottom: 4px;
      }
      .car-plate {
        font-size: 12px;
      }
    }

    .price {
      color: #f56c6c;
      font-weight: bold;
      font-family: 'DIN Alternate', sans-serif;
      font-size: 16px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

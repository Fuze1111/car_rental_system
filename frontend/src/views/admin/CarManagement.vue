<template>
  <div class="car-management">
    <div id="section-cars">
      <el-card class="main-card" shadow="hover">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="search-left">
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
              <el-button type="primary" @click="handleSearch" class="search-btn">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="resetSearch">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <div class="search-right">
          <el-button type="primary" class="add-btn" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增车辆
          </el-button>
        </div>
      </div>

      <!-- 车辆列表表格 -->
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading" 
        class="modern-table"
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
            <el-tooltip content="送修" placement="top" v-if="scope.row.CarStatus !== '2'">
              <el-button circle size="small" type="warning" plain @click="handleMaintenance(scope.row)">
                <el-icon><Tools /></el-icon>
              </el-button>
            </el-tooltip>
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
    </div>

    <div class="section-divider"></div>

    <div id="section-types">
      <el-card class="main-card" shadow="hover">
        <div class="section-header">
          <h3>车型租金管理</h3>
        </div>
        <!-- 搜索栏 (Types) -->
        <div class="search-bar">
          <div class="search-left">
            <el-form :inline="true" :model="typeSearchForm" class="search-form">
              <el-form-item>
                <el-input v-model="typeSearchForm.keyword" placeholder="搜索车型..." prefix-icon="Search" clearable class="search-input" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleTypeSearch" class="search-btn">
                  <el-icon><Search /></el-icon>查询
                </el-button>
                <el-button @click="resetTypeSearch">
                  <el-icon><Refresh /></el-icon>重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
          <div class="search-right">
             <el-button type="primary" class="add-btn" @click="handleTypeAdd">
               <el-icon><Plus /></el-icon>新增车型
             </el-button>
          </div>
        </div>

        <!-- Table Types -->
        <el-table :data="typeTableData" style="width: 100%" v-loading="typeLoading" class="modern-table">
            <el-table-column prop="TNo" label="类型编号" min-width="120" align="center">
              <template #default="scope">
                <el-tag type="info" effect="plain" round>{{ scope.row.TNo }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="TName" label="车型名称" min-width="200" align="center">
              <template #default="scope">
                <span style="font-weight: 600; color: #1e293b;">{{ scope.row.TName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="RentRate" label="基准日租金" min-width="150" align="right">
              <template #default="scope">
                <div class="price-tag">
                  <span class="currency">¥</span>
                  <span class="amount">{{ scope.row.RentRate }}</span>
                  <span class="unit">/天</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="scope">
                <el-tooltip content="编辑" placement="top">
                  <el-button circle size="small" type="primary" plain @click="handleTypeEdit(scope.row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button circle size="small" type="danger" plain @click="handleTypeDelete(scope.row)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </template>
            </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- Type Dialog -->
    <el-dialog
      :model-value="typeDialogVisible"
      :title="isTypeEdit ? '编辑车型' : '新增车型'"
      width="500px"
      @close="handleTypeClose"
      destroy-on-close
    >
      <el-form ref="typeFormRef" :model="typeFormData" :rules="typeRules" label-width="100px">
        <el-form-item label="类型编号" prop="TNo">
          <el-input v-model="typeFormData.TNo" placeholder="例如: T001" :disabled="isTypeEdit" />
        </el-form-item>
        <el-form-item label="车型名称" prop="TName">
          <el-input v-model="typeFormData.TName" placeholder="例如: Toyota Camry" />
        </el-form-item>
        <el-form-item label="日租金" prop="RentRate">
          <el-input-number v-model="typeFormData.RentRate" :min="0" :step="50" style="width: 100%" controls-position="right">
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleTypeClose">取消</el-button>
          <el-button type="primary" @click="handleTypeSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <CarEditDialog 
      v-model:visible="dialogVisible"
      :edit-data="currentEditData"
      @submit="handleDialogSubmit"
    />

    <!-- 送修弹窗 -->
    <CarMaintenanceDialog 
      v-model:visible="maintenanceDialogVisible"
      :car-data="currentMaintenanceCar"
      @submit="handleMaintenanceSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, defineAsyncComponent } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, Van, Tools } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 异步加载组件
const CarEditDialog = defineAsyncComponent(() => 
  import('./components/CarDialog.vue')
)
const CarMaintenanceDialog = defineAsyncComponent(() => 
  import('./components/CarMaintenanceDialog.vue')
)

// 弹窗控制
const dialogVisible = ref(false)
const maintenanceDialogVisible = ref(false)
const typeDialogVisible = ref(false) // For Type Dialog
const currentEditData = ref(null)
const currentMaintenanceCar = ref(null)
const isTypeEdit = ref(false)

// 搜索表单 (Cars)
const searchForm = reactive({
  CarID: '',
  TName: '',
  CarStatus: ''
})

// 搜索表单 (Types)
const typeSearchForm = reactive({ keyword: '' })

const loading = ref(false)
const typeLoading = ref(false) // Loading for types
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 4
})

// Form Data for Types
const typeFormData = reactive({
  TNo: '',
  TName: '',
  RentRate: 0
})

const typeRules = {
  TNo: [{ required: true, message: '请输入类型编号', trigger: 'blur' }],
  TName: [{ required: true, message: '请输入车型名称', trigger: 'blur' }],
  RentRate: [{ required: true, message: '请输入日租金', trigger: 'blur' }]
}

// Mock 数据 (Cars)
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

// Mock Data (Types)
const mockTypeData = [
  { TNo: 'T001', TName: 'Toyota Camry', RentRate: 350.00 },
  { TNo: 'T002', TName: 'Tesla Model 3', RentRate: 500.00 },
  { TNo: 'T003', TName: 'Benz S-Class', RentRate: 1200.00 },
  { TNo: 'T004', TName: 'BMW X5', RentRate: 1100.00 }
]
const typeTableData = ref([...mockTypeData])

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

const handleMaintenance = (row) => {
  currentMaintenanceCar.value = { ...row }
  maintenanceDialogVisible.value = true
}

const handleMaintenanceSubmit = (formData) => {
  const index = tableData.value.findIndex(item => item.CarID === formData.CarID)
  if (index !== -1) {
    // Update status to '2' (Maintenance)
    tableData.value[index].CarStatus = '2'
    ElMessage.success('车辆已送修')
  }
}

// --- Type Management Methods ---
const typeFormRef = ref(null)

const handleTypeSearch = () => {
  typeLoading.value = true
  setTimeout(() => {
    typeTableData.value = mockTypeData.filter(item => 
      !typeSearchForm.keyword || 
      item.TName.toLowerCase().includes(typeSearchForm.keyword.toLowerCase()) || 
      item.TNo.toLowerCase().includes(typeSearchForm.keyword.toLowerCase())
    )
    typeLoading.value = false
    ElMessage.success('车型搜索完成')
  }, 300)
}

const resetTypeSearch = () => {
  typeSearchForm.keyword = ''
  handleTypeSearch()
}

const handleTypeAdd = () => {
  isTypeEdit.value = false
  typeFormData.TNo = ''
  typeFormData.TName = ''
  typeFormData.RentRate = 0
  typeDialogVisible.value = true
}

const handleTypeEdit = (row) => {
  isTypeEdit.value = true
  Object.assign(typeFormData, row)
  typeDialogVisible.value = true
}

const handleTypeDelete = (row) => {
  ElMessageBox.confirm(`确定要删除车型 ${row.TName} 吗？`, '警告', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(() => {
    typeTableData.value = typeTableData.value.filter(item => item.TNo !== row.TNo)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleTypeClose = () => {
  typeDialogVisible.value = false
}

const handleTypeSubmit = async () => {
  if (!typeFormRef.value) return
  await typeFormRef.value.validate((valid) => {
    if (valid) {
      setTimeout(() => {
        if (isTypeEdit.value) {
          const index = typeTableData.value.findIndex(item => item.TNo === typeFormData.TNo)
          if (index !== -1) typeTableData.value[index] = { ...typeFormData }
        } else {
          typeTableData.value.push({ ...typeFormData })
        }
        typeDialogVisible.value = false
        ElMessage.success(isTypeEdit.value ? '修改成功' : '新增成功')
      }, 500)
    }
  })
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
      background: #fdfbfb; /* Match page bg */
      padding: 0 10px;
      color: #ccc;
      letter-spacing: 4px;
    }
  }

  .section-header {
    margin-bottom: 24px;
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 700;
      color: #334155;
      padding-left: 12px;
      border-left: 4px solid #409eff;
    }
  }

  .main-card {
    border: 1px solid rgba(255, 255, 255, 0.8);
    border-radius: 24px; /* Increased radius to match others */
    background: rgba(255, 255, 255, 0.85); /* Glass background */
    backdrop-filter: blur(20px); /* Blur effect */
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    overflow: hidden;
    
    :deep(.el-card__body) {
      padding: 32px; /* Increased padding to match others */
    }
  }

  .search-bar {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center; /* Center align items */
    flex-wrap: wrap;
    gap: 16px;
    padding: 8px 4px; /* Add some breathing room */
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px; /* Increased gap */
      align-items: center;
      
      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
      }

      .search-input {
        width: 220px;
        
        :deep(.el-input__wrapper) {
          border-radius: 50px; /* Pill shape */
          background: rgba(248, 250, 252, 0.8);
          box-shadow: none;
          border: 1px solid transparent;
          transition: all 0.3s;
          padding-left: 16px;
          
          &:hover {
            background: white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
          }
          
          &.is-focus {
            background: white;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
            border-color: rgba(64, 158, 255, 0.3);
          }
        }
      }

      /* Select styling */
      :deep(.el-select .el-input__wrapper) {
        border-radius: 50px;
        background: rgba(248, 250, 252, 0.8);
        box-shadow: none;
        border: 1px solid transparent;
        
        &:hover {
          background: white;
          box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        
        &.is-focus {
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
        }
      }

      /* General Button Styling (Reset & Search) */
      :deep(.el-button) {
        border-radius: 50px;
        padding: 8px 24px;
        font-weight: 500;
        transition: all 0.3s;
        display: flex;
        align-items: center;
        gap: 6px;
        
        &:hover {
          transform: translateY(-2px);
        }
      }

      .search-btn {
        background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
        border: none;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        letter-spacing: 1px;
        color: white;
        display: flex;
        align-items: center;
        gap: 6px;

        &:hover {
          box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        }
      }
    }

    .add-btn {
      border-radius: 50px;
      padding: 10px 24px;
      background: linear-gradient(135deg, #10b981 0%, #059669 100%); /* Green for Add action */
      border: none;
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
      transition: all 0.3s;
      display: flex;
      align-items: center;
      gap: 6px;
      font-weight: 600;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
        background: linear-gradient(135deg, #34d399 0%, #059669 100%);
      }
    }
  }

  /* 现代表格样式优化 */
  .modern-table {
    background-color: transparent !important;
    --el-table-border-color: transparent;
    --el-table-header-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    
    &::before { display: none; } /* 去除底部边框线 */

    :deep(th.el-table__cell) {
      background: transparent;
      font-weight: 600;
      color: #64748b;
      height: 56px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      font-size: 13px;
      letter-spacing: 0.5px;
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
        
        td.el-table__cell {
          background-color: rgba(255, 255, 255, 0.6) !important;
        }
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

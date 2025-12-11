<template>
  <div class="repair-management">
    <el-card class="main-card" shadow="hover">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="search-left">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item>
              <el-input 
                v-model="searchForm.carId" 
                placeholder="搜索车牌号..." 
                prefix-icon="Search" 
                clearable 
                class="search-input" 
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="searchForm.startDate"
                type="date"
                placeholder="维修开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="search-date"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="searchForm.endDate"
                type="date"
                placeholder="维修结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="search-date"
              />
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
            <el-icon><Plus /></el-icon>新增维修记录
          </el-button>
        </div>
      </div>

      <!-- 维修记录表格 -->
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading" 
        class="modern-table"
      >
        
        
        <!-- 维修编号 -->
        <el-table-column prop="rNo" label="维修编号" min-width="100" align="center">
          <template #default="scope">
            <el-tag type="primary" round>#{{ scope.row.rNo }}</el-tag>
          </template>
        </el-table-column>

        <!-- 车牌号 -->
        <el-table-column prop="carId" label="车牌号" min-width="120" align="center">
          <template #default="scope">
            <div class="license-plate">{{ scope.row.carId }}</div>
          </template>
        </el-table-column>

        <!-- 车辆型号 -->
        <el-table-column prop="carModel" label="车辆型号" min-width="150" align="center" />

        <!-- 维修日期 -->
        <el-table-column prop="rDate" label="维修日期" min-width="180" align="center">
          <template #default="scope">
            <span>{{ formatDate(scope.row.rDate) }}</span>
          </template>
        </el-table-column>

        <!-- 运行公里 -->
        <el-table-column prop="runKm" label="运行公里" min-width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.runKm }} km</span>
          </template>
        </el-table-column>

        <!-- 运行小时 -->
        <el-table-column prop="runHour" label="运行小时" min-width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.runHour }} h</span>
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

    <!-- 新增/编辑维修记录弹窗 -->
    <el-dialog
      :model-value="dialogVisible"
      :title="isEdit ? '编辑维修记录' : '新增维修记录'"
      width="500px"
      @close="handleDialogClose"
      destroy-on-close
      class="custom-dialog"
    >
      <el-form 
        ref="repairFormRef" 
        :model="repairFormData" 
        :rules="repairRules" 
        label-width="100px"
        class="repair-form"
      >
        <el-form-item label="车牌号" prop="carId">
          <el-input 
            v-model="repairFormData.carId" 
            placeholder="请输入车牌号" 
            :disabled="isEdit"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item label="维修日期" prop="rDate">
          <el-date-picker
            v-model="repairFormData.rDate"
            type="datetime"
            placeholder="请选择维修日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            class="custom-date-picker"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="运行公里" prop="runKm">
              <el-input-number 
                v-model="repairFormData.runKm" 
                :min="0" 
                :step="100" 
                style="width: 100%"
                controls-position="right"
                class="custom-input-number"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运行小时" prop="runHour">
              <el-input-number 
                v-model="repairFormData.runHour" 
                :min="0" 
                :step="1" 
                style="width: 100%"
                controls-position="right"
                class="custom-input-number"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose" class="footer-btn">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="submitLoading"
            class="footer-btn submit-btn"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRepairs, 
  getRepairById, 
  createRepair, 
  updateRepair, 
  deleteRepair,
  getRepairsByCarId
} from '@/api/car'

// 数据相关
const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditId = ref(null)

// 表单引用
const repairFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  carId: '',
  startDate: '',
  endDate: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 维修记录表单数据
const repairFormData = reactive({
  carId: '',
  rDate: '',
  runKm: 0,
  runHour: 0
})

// 表单验证规则
const repairRules = {
  carId: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  rDate: [{ required: true, message: '请选择维修日期', trigger: 'change' }],
  runKm: [{ required: true, message: '请输入运行公里数', trigger: 'blur' }],
  runHour: [{ required: true, message: '请输入运行小时数', trigger: 'blur' }]
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 如果是 LocalDateTime 格式，格式化显示
  const date = new Date(dateStr)
  return date.getFullYear() + '-' +
    String(date.getMonth() + 1).padStart(2, '0') + '-' +
    String(date.getDate()).padStart(2, '0') + ' ' +
    String(date.getHours()).padStart(2, '0') + ':' +
    String(date.getMinutes()).padStart(2, '0') + ':' +
    String(date.getSeconds()).padStart(2, '0')
}

// 页面加载时获取数据
onMounted(() => {
  fetchRepairs()
})

// 获取维修记录列表
const fetchRepairs = async () => {
  loading.value = true
  try {
    // 构造查询参数
    const params = {
      page: pagination.currentPage - 1,
      size: pagination.pageSize
    }
    
    // 添加搜索条件
    if (searchForm.carId) {
      params.carId = searchForm.carId
    }
    
    if (searchForm.startDate) {
      params.startDate = searchForm.startDate
    }
    
    if (searchForm.endDate) {
      params.endDate = searchForm.endDate
    }
    
    const response = await getRepairs()
    // 检查响应结构
    const data = response.data || response
    
    // 如果是数组直接使用，否则使用content字段
    let repairs = []
    let total = 0
    
    if (Array.isArray(data)) {
      repairs = data
      total = data.length
    } else if (data.content) {
      repairs = data.content
      total = data.totalElements || data.content.length
    } else {
      repairs = []
      total = 0
    }
    
    // 转换数据格式以适配前端显示
    tableData.value = repairs.map(repair => ({
      rNo: repair.rno,
      carId: repair.carId,
      carModel: repair.carModel,
      rDate: repair.rdate,
      runKm: repair.runKm,
      runHour: repair.runHour
    }))
    
    pagination.total = total
  } catch (error) {
    console.error('获取维修记录失败:', error)
    ElMessage.error('获取维修记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchRepairs()
}

// 重置搜索
const resetSearch = () => {
  searchForm.carId = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  handleSearch()
}

// 处理分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchRepairs()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchRepairs()
}

// 新增维修记录
const handleAdd = () => {
  isEdit.value = false
  currentEditId.value = null
  resetRepairForm()
  dialogVisible.value = true
}

// 编辑维修记录
const handleEdit = async (row) => {
  isEdit.value = true
  currentEditId.value = row.rNo
  // 填充表单数据
  repairFormData.carId = row.carId
  repairFormData.rDate = row.rDate
  repairFormData.runKm = row.runKm
  repairFormData.runHour = row.runHour
  dialogVisible.value = true
}

// 删除维修记录
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除维修记录 #${row.rNo} 吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteRepair(row.rNo)
      ElMessage.success('删除成功')
      fetchRepairs()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!repairFormRef.value) return
  
  await repairFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 准备提交数据
        const repairData = {
          carId: repairFormData.carId,
          rDate: repairFormData.rDate,
          runKm: repairFormData.runKm,
          runHour: repairFormData.runHour
        }
        
        if (isEdit.value) {
          // 更新维修记录
          await updateRepair(currentEditId.value, repairData)
          ElMessage.success('维修记录更新成功')
        } else {
          // 创建维修记录
          await createRepair(repairData)
          ElMessage.success('维修记录创建成功')
        }
        
        // 关闭对话框并刷新数据
        handleDialogClose()
        fetchRepairs()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false
  resetRepairForm()
}

// 重置表单
const resetRepairForm = () => {
  repairFormData.carId = ''
  repairFormData.rDate = ''
  repairFormData.runKm = 0
  repairFormData.runHour = 0
}
</script>

<style lang="scss" scoped>
.repair-management {
  padding: 0;
  
  .main-card {
    border: 1px solid rgba(255, 255, 255, 0.8);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    overflow: hidden;
    
    :deep(.el-card__body) {
      padding: 32px;
    }
  }

  .search-bar {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
    padding: 8px 4px;
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: center;
      
      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
      }

      .search-input {
        width: 220px;
        
        :deep(.el-input__wrapper) {
          border-radius: 50px;
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
      
      .search-date {
        :deep(.el-input__wrapper) {
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
      }

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

        &:hover {
          box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        }
      }
    }

    .add-btn {
      border-radius: 50px;
      padding: 10px 24px;
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
    
    &::before { display: none; }

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
  }

  .pagination-container {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}

/* 对话框样式 */
.custom-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;

  :deep(.el-dialog__header) {
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    background: #f8fafc;
    
    .el-dialog__title {
      font-weight: 700;
      color: #1e293b;
      font-size: 18px;
    }
  }

  :deep(.el-dialog__body) {
    padding: 32px 24px !important;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 24px;
    border-top: 1px solid #f1f5f9;
    background: #f8fafc;
  }
}

.repair-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #475569;
  }
}

.custom-input,
.custom-date-picker,
.custom-input-number {
  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    padding: 4px 12px;
    transition: all 0.2s;
    
    &:hover {
      box-shadow: 0 0 0 1px #cbd5e1 inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
      border-color: #3b82f6;
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
</style>
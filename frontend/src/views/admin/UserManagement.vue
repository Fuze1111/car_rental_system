<template>
  <div class="user-management">
    
    <!-- 客户管理部分 -->
    <div id="section-customers">
      <el-card shadow="never" class="main-card">
        <div class="section-header">
          <h3>客户列表</h3>
        </div>

        <!-- 客户搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true" :model="customerSearchForm">
            <el-form-item label="姓名">
              <el-input v-model="customerSearchForm.name" placeholder="请输入姓名" clearable />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="customerSearchForm.phone" placeholder="请输入电话" clearable />
            </el-form-item>
            <el-form-item label="单位">
              <el-input v-model="customerSearchForm.company" placeholder="请输入单位" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCustomerSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="handleCustomerReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 客户表格 -->
        <el-table :data="customerData" v-loading="customerLoading" class="modern-table" style="width: 100%">
          <el-table-column prop="cNo" label="客户号" width="120" />
          <el-table-column prop="cName" label="姓名" width="120">
            <template #default="scope">
              <div class="user-name">{{ scope.row.cName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="company" label="单位" min-width="180" show-overflow-tooltip />
          <el-table-column prop="phone" label="电话" width="140" />
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="postCode" label="邮编" width="100" />
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="handleView(scope.row, 'customer')">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="customerPagination.currentPage"
            v-model:page-size="customerPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="customerPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleCustomerSizeChange"
            @current-change="fetchCustomers"
          />
        </div>
      </el-card>
    </div>

    <div class="section-divider"></div>

    <!-- 司机管理部分 -->
    <div id="section-drivers">
      <el-card shadow="never" class="main-card">
        <div class="section-header">
          <h3>司机列表</h3>
        </div>

        <!-- 司机搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true" :model="driverSearchForm">
            <el-form-item label="姓名">
              <el-input v-model="driverSearchForm.name" placeholder="请输入姓名" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="driverSearchForm.status" placeholder="全部" clearable style="width: 100px">
                <el-option label="在岗" value="在岗" />
                <el-option label="空闲" value="空闲" />
              </el-select>
            </el-form-item>
            <el-form-item label="车型">
              <el-select v-model="driverSearchForm.licenseType" placeholder="全部" clearable style="width: 100px">
                <el-option label="A1" value="A1" />
                <el-option label="A2" value="A2" />
                <el-option label="C1" value="C1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleDriverSearch">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="handleDriverReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 司机表格 -->
        <el-table :data="driverData" v-loading="driverLoading" class="modern-table" style="width: 100%">
          <el-table-column prop="dNo" label="司机号" width="120" />
          <el-table-column prop="dName" label="姓名" min-width="120">
            <template #default="scope">
              <div class="user-name">{{ scope.row.dName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="licenseType" label="准驾车型" width="100" align="center">
            <template #default="scope">
              <el-tag size="small" type="warning">{{ scope.row.licenseType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startWork" label="入职时间" width="140" />
          <el-table-column prop="salary" label="基本工资" width="120" align="right">
            <template #default="scope">¥{{ scope.row.salary }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getDriverStatusType(scope.row.dStatus)" size="small" round>
                {{ scope.row.dStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="driverPagination.currentPage"
            v-model:page-size="driverPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="driverPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleDriverSizeChange"
            @current-change="fetchDrivers"
          />
        </div>
      </el-card>
    </div>

    <!-- 客户详情弹窗 -->
    <el-dialog
      v-model="customerDetailVisible"
      title="客户详情"
      width="600px"
      class="custom-dialog detail-dialog"
      destroy-on-close
      append-to-body
    >
      <div class="detail-container" v-if="currentDetailCustomer">
        <div class="detail-info">
          <div class="info-block">
            <h4 class="block-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">客户号</span>
                <span class="value code">{{ currentDetailCustomer.cNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">姓名</span>
                <span class="value main-text">{{ currentDetailCustomer.cName }}</span>
              </div>
              <div class="info-item">
                <span class="label">电话</span>
                <span class="value">{{ currentDetailCustomer.phone }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮编</span>
                <span class="value">{{ currentDetailCustomer.postCode }}</span>
              </div>
            </div>
          </div>

          <div class="info-block">
            <h4 class="block-title">详细信息</h4>
            <div class="info-grid single-col">
              <div class="info-item">
                <span class="label">单位</span>
                <span class="value">{{ currentDetailCustomer.company || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">地址</span>
                <span class="value">{{ currentDetailCustomer.address || '-' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑司机弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑司机信息"
      width="500px"
      class="custom-dialog"
      destroy-on-close
      append-to-body
    >
      <el-form :model="editDriverForm" label-width="100px" class="edit-form">
        <el-form-item label="工号">
          <el-input v-model="editDriverForm.dNo" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editDriverForm.dName" disabled />
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input-number 
            v-model="editDriverForm.salary" 
            :min="0" 
            :step="100" 
            style="width: 100%" 
            controls-position="right"
          >
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="准驾车型">
          <el-select v-model="editDriverForm.licenseType" placeholder="请选择车型" style="width: 100%">
            <el-option label="A1" value="A1" />
            <el-option label="A2" value="A2" />
            <el-option label="B1" value="B1" />
            <el-option label="C1" value="C1" />
            <el-option label="C2" value="C2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editDriverForm.dStatus">
            <el-radio-button label="在岗" />
            <el-radio-button label="空闲" />
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false" class="footer-btn">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit" :loading="submitting" class="footer-btn submit-btn">保存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Van, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { searchCustomers, searchDrivers, updateDriver } from '@/api/admin'

// --- Customer Logic ---
const customerLoading = ref(false)
const customerData = ref([])
const customerSearchForm = reactive({ name: '', phone: '', company: '' })
const customerPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

// --- Driver Logic ---
const driverLoading = ref(false)
const driverData = ref([])
const driverSearchForm = reactive({ name: '', status: '', licenseType: '' })
const driverPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

// Edit Logic
const editDialogVisible = ref(false)
const submitting = ref(false)
const editDriverForm = reactive({
  dNo: '',
  dName: '',
  salary: 0,
  licenseType: '',
  dStatus: ''
})

const getDriverStatusType = (status) => {
  return status === '空闲' ? 'success' : 'warning'
}

// Fetch Functions
const fetchCustomers = async () => {
  customerLoading.value = true
  
  try {
    const queryParams = {
      name: customerSearchForm.name || null,
      phone: customerSearchForm.phone || null,
      company: customerSearchForm.company || null,
      page: customerPagination.currentPage - 1, // 后端从0开始
      size: customerPagination.pageSize
    }
    
    const response = await searchCustomers(queryParams)
    
    // 调试：查看返回的数据格式
    console.log('客户数据返回:', response)
    if (response.content && response.content.length > 0) {
      console.log('第一条客户数据:', response.content[0])
    }
    
    // Spring Data Page对象结构: { content, totalElements, totalPages, ... }
    customerData.value = response.content
    customerPagination.total = response.totalElements
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
    customerData.value = []
  } finally {
    customerLoading.value = false
  }
}

const fetchDrivers = async () => {
  driverLoading.value = true
  
  try {
    const queryParams = {
      name: driverSearchForm.name || null,
      status: driverSearchForm.status || null,
      licenseType: driverSearchForm.licenseType || null,
      page: driverPagination.currentPage - 1, // 后端从0开始
      size: driverPagination.pageSize
    }
    
    const response = await searchDrivers(queryParams)
    
    // 调试：查看返回的数据格式
    console.log('司机数据返回:', response)
    if (response.content && response.content.length > 0) {
      console.log('第一条司机数据:', response.content[0])
    }
    
    // Spring Data Page对象结构: { content, totalElements, totalPages, ... }
    driverData.value = response.content
    driverPagination.total = response.totalElements
  } catch (error) {
    console.error('获取司机列表失败:', error)
    ElMessage.error('获取司机列表失败')
    driverData.value = []
  } finally {
    driverLoading.value = false
  }
}

// Handlers
const handleCustomerSearch = () => { fetchCustomers(); ElMessage.success('查询完成') }
const handleCustomerReset = () => { 
  customerSearchForm.name = ''
  customerSearchForm.phone = ''
  customerSearchForm.company = ''
  fetchCustomers() 
}

const handleDriverSearch = () => { fetchDrivers(); ElMessage.success('查询完成') }
const handleDriverReset = () => { 
  driverSearchForm.name = ''
  driverSearchForm.status = ''
  driverSearchForm.licenseType = ''
  fetchDrivers() 
}

const handleCustomerSizeChange = (val) => {
  customerPagination.pageSize = val
  fetchCustomers()
}

const handleDriverSizeChange = (val) => {
  driverPagination.pageSize = val
  fetchDrivers()
}

// Detail Logic
const customerDetailVisible = ref(false)
const currentDetailCustomer = ref(null)

const handleView = (row, type) => {
  if (type === 'customer') {
    currentDetailCustomer.value = row
    customerDetailVisible.value = true
  } else {
    // 司机详情逻辑暂未实现或不需要
    const name = row.dName
    ElMessage.info(`查看司机详情: ${name}`)
  }
}

const handleEdit = (row) => {
  Object.assign(editDriverForm, row)
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  submitting.value = true
  try {
    await updateDriver(editDriverForm.dNo, {
      dNo: editDriverForm.dNo,
      dName: editDriverForm.dName,
      salary: editDriverForm.salary,
      licenseType: editDriverForm.licenseType,
      dStatus: editDriverForm.dStatus
    })
    
    editDialogVisible.value = false
    ElMessage.success('司机信息修改成功')
    await fetchDrivers() // Refresh list
  } catch (error) {
    console.error('修改司机信息失败:', error)
    ElMessage.error('修改司机信息失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCustomers()
  fetchDrivers()
})
</script>

<style lang="scss">
/* Global Dialog Styles for UserManagement */
.custom-dialog {
  border-radius: 20px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  
  .el-dialog__header {
    margin: 0;
    padding: 24px;
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;
    margin-right: 0 !important;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
    }
  }
  
  .el-dialog__body {
    padding: 32px !important;
  }
  
  .el-dialog__footer {
    padding: 24px;
    background: #f8fafc;
    border-top: 1px solid #f1f5f9;
  }

  /* 弹窗内容样式 - 需要放在全局样式中以生效 */
  .detail-container {
    display: flex;
    gap: 24px;
  }

  .detail-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 24px;
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
    
    &.single-col {
      grid-template-columns: 1fr;
    }
    
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
      }
    }
  }
  
  .code { font-family: monospace; color: #475569; }
  .main-text { font-weight: 600; color: #1e293b; font-size: 16px; }

  /* 统一弹窗按钮样式 */
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
}
</style>

<style scoped lang="scss">
.user-management {
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
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
    
    :deep(.el-card__body) {
      padding: 32px;
    }
  }

  .search-bar {
    margin: 24px 0;
    padding: 8px 4px;
    
    :deep(.el-form) {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: center;
    }

    :deep(.el-form-item) {
      margin-bottom: 0;
      margin-right: 0;
    }

    :deep(.el-input__wrapper) {
      border-radius: 50px;
      background: rgba(248, 250, 252, 0.8);
      box-shadow: none;
      border: 1px solid transparent;
      transition: all 0.3s;
      padding-left: 16px;
      width: 200px;
      
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

    /* Buttons */
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

    :deep(.el-button--primary) {
      background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
      border: none;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      color: white;
      
      &:hover {
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
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

  /* Info Styles */
  // .code { font-family: monospace; color: #475569; }
  // .main-text { font-weight: 600; color: #1e293b; font-size: 16px; }

  // .detail-container {
  //   display: flex;
  //   gap: 24px;
  // }

  // .detail-info {
  //   flex: 1;
  //   display: flex;
  //   flex-direction: column;
  //   gap: 24px;
  // }

  // .info-block {
  //   .block-title {
  //     margin: 0 0 16px 0;
  //     font-size: 14px;
  //     color: #64748b;
  //     font-weight: 700;
  //     padding-left: 8px;
  //     border-left: 3px solid #3b82f6;
  //   }
  // }

  // .info-grid {
  //   display: grid;
  //   grid-template-columns: repeat(2, 1fr);
  //   gap: 16px;
    
  //   &.single-col {
  //     grid-template-columns: 1fr;
  //   }
    
  //   .info-item {
  //     display: flex;
  //     flex-direction: column;
      
  //     .label {
  //       font-size: 12px;
  //       color: #94a3b8;
  //       margin-bottom: 4px;
  //     }
      
  //     .value {
  //       font-size: 14px;
  //       color: #1e293b;
  //       font-weight: 600;
  //     }
  //   }
  // }

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

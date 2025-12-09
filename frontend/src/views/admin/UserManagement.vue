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
          <el-table-column prop="DNo" label="司机号" width="120" />
          <el-table-column prop="DName" label="姓名" min-width="120">
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
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="handleView(scope.row, 'driver')">详情</el-button>
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

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Van, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

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

// Mock Generator
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

// Fetch Functions
const fetchCustomers = () => {
  customerLoading.value = true
  setTimeout(() => {
    customerData.value = generateMockData('customer').filter(item => 
      !customerSearchForm.name || item.CName.includes(customerSearchForm.name)
    )
    customerPagination.total = 85
    customerLoading.value = false
  }, 300)
}

const fetchDrivers = () => {
  driverLoading.value = true
  setTimeout(() => {
    driverData.value = generateMockData('driver').filter(item => 
      !driverSearchForm.name || item.DName.includes(driverSearchForm.name)
    )
    driverPagination.total = 24
    driverLoading.value = false
  }, 300)
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

const handleView = (row, type) => {
  const name = type === 'customer' ? row.CName : row.DName
  ElMessage.info(`查看详情: ${name}`)
}

onMounted(() => {
  fetchCustomers()
  fetchDrivers()
})
</script>

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

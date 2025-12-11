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
              <el-select v-model="searchForm.TName" placeholder="选择车型" clearable filterable style="width: 180px">
                <el-option 
                  v-for="type in carTypeOptions" 
                  :key="type.tNo + type.tName" 
                  :label="type.tName" 
                  :value="type.tName">
                   <span style="float: left">{{ type.tName }}</span>
                   <span style="float: right; color: #8492a6; font-size: 13px">{{ type.tNo }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="价格范围">
               <el-input-number v-model="searchForm.minPrice" :min="0" :controls="false" placeholder="Min" style="width: 100px" />
               <span style="margin: 0 4px">-</span>
               <el-input-number v-model="searchForm.maxPrice" :min="0" :controls="false" placeholder="Max" style="width: 100px" />
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
        <el-table-column prop="RentRate" label="小时租金" width="150" align="left">
          <template #default="scope">
            <div class="price-tag">
              <span class="currency">¥</span>
              <span class="amount">{{ scope.row.RentRate }}</span>
              <span class="unit">/小时</span>
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

        <!-- 运行公里 (RUNKM) -->
        <el-table-column prop="RUNKM" label="运行公里" width="120" align="right">
          <template #default="scope">
            <span class="text-secondary">{{ scope.row.RUNKM }} km</span>
          </template>
        </el-table-column>

        <!-- 运行小时 (RunHour) -->
        <el-table-column prop="RunHour" label="运行小时" width="120" align="right">
          <template #default="scope">
            <span class="text-secondary">{{ scope.row.RunHour }} h</span>
          </template>
        </el-table-column>

        <!-- 最后维修日期 (LastRepairDate) -->
        <el-table-column prop="LastRepairDate" label="最后维修日期" width="160" align="center">
          <template #default="scope">
            <span class="text-secondary">{{ scope.row.LastRepairDate ? formatDateTime(scope.row.LastRepairDate) : '-' }}</span>
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
            
            <el-table-column label="车型图片" width="120" align="center">
              <template #default="scope">
                <el-image 
                  :src="scope.row.Image" 
                  :preview-src-list="[scope.row.Image]"
                  fit="cover" 
                  class="type-thumb"
                  preview-teleported
                />
              </template>
            </el-table-column>

            <el-table-column prop="TName" label="车型名称" min-width="200" align="center">
              <template #default="scope">
                <span style="font-weight: 600; color: #1e293b;">{{ scope.row.TName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="rentRate" label="基准小时租金" min-width="150" align="right">
              <template #default="scope">
                <div class="price-tag">
                  <span class="currency">¥</span>
                  <span class="amount">{{ scope.row.rentRate }}</span>
                  <span class="unit">/小时</span>
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

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="typePagination.currentPage"
            v-model:page-size="typePagination.pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="typePagination.total"
            @size-change="handleTypeSizeChange"
            @current-change="handleTypeCurrentChange"
            background
          />
        </div>
      </el-card>
    </div>

    <!-- Type Dialog -->
    <el-dialog
      :model-value="typeDialogVisible"
      :title="isTypeEdit ? '编辑车型' : '新增车型'"
      width="500px"
      @close="handleTypeClose"
      destroy-on-close
      class="custom-dialog"
      append-to-body
    >
      <el-form ref="typeFormRef" :model="typeFormData" :rules="typeRules" label-width="100px" class="type-form">
        <el-form-item label="类型编号" prop="TNo">
          <el-input v-model="typeFormData.TNo" placeholder="例如: C1" class="custom-input" :disabled="isTypeEdit" />
        </el-form-item>
        <el-form-item label="车型名称" prop="TName">
          <el-input v-model="typeFormData.TName" placeholder="例如: Toyota Camry" class="custom-input" :disabled="isTypeEdit" />
        </el-form-item>
        <el-form-item label="小时租金" prop="RentRate">
          <el-input-number 
            v-model="typeFormData.RentRate" 
            :min="0" 
            :step="50" 
            style="width: 100%" 
            controls-position="right"
            class="custom-input-number"
          >
            <template #prefix>¥</template>
            <template #append>/小时</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="车型图片" prop="Image">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <img v-if="typeFormData.Image" :src="typeFormData.Image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">支持 jpg/png，建议尺寸 800x600</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleTypeClose" class="footer-btn">取消</el-button>
          <el-button type="primary" @click="handleTypeSubmit" class="footer-btn submit-btn">确定</el-button>
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
import { ref, reactive, defineAsyncComponent, onMounted } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, Van, Tools, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCars, 
  createCar, 
  updateCar, 
  deleteCar, 
  getCarById, 
  createRepair, 
  updateCarStatus,
  getCarTypes,
  createCarType,
  updateCarType,
  deleteCarType
} from '@/api/car'

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
  CarStatus: '',
  minPrice: undefined,
  maxPrice: undefined
})

// 搜索表单 (Types)
const typeSearchForm = reactive({ keyword: '' })

const loading = ref(false)
const typeLoading = ref(false) // Loading for types
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const typePagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Form Data for Types
const typeFormData = reactive({
  TNo: '',
  TName: '',
  RentRate: 0,
  Image: ''
})

const typeRules = {
  TNo: [{ required: true, message: '请输入类型编号', trigger: 'blur' }],
  TName: [{ required: true, message: '请输入车型名称', trigger: 'blur' }],
  RentRate: [{ required: true, message: '请输入小时租金', trigger: 'blur' }]
}

// 车辆数据
const tableData = ref([])
const typeTableData = ref([])
const carTypeOptions = ref([])

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

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.getFullYear() + '-' + 
         String(date.getMonth() + 1).padStart(2, '0') + '-' + 
         String(date.getDate()).padStart(2, '0') + ' ' +
         String(date.getHours()).padStart(2, '0') + ':' +
         String(date.getMinutes()).padStart(2, '0')
}

// 页面加载时获取车辆数据
onMounted(() => {
  fetchCars()
  fetchCarTypes()
})

// 获取车辆列表
const fetchCars = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage - 1, // 后端从0开始分页
      size: pagination.pageSize,
      // 可以添加其他查询参数
    }
    
    // 添加搜索条件
    if (searchForm.CarID) {
      params.carId = searchForm.CarID
    }
    if (searchForm.TName) {
      params.tName = searchForm.TName
    }
    if (searchForm.CarStatus) {
      params.carStatus = searchForm.CarStatus
    }
    
    const response = await getCars(params)
    const { content, totalElements, totalPages } = response
    
    // 转换数据格式以适配前端显示（注意后端返回的是小写字段名）
    tableData.value = content.map(car => ({
      CarID: car.carId,
      TNo: car.tno,
      TName: car.tname,
      RentRate: car.rentRate,
      CarStatus: car.carStatus,
      PurchaseDate: car.purchaseDate,
      Price: car.price,
      LastRepairDate: car.lastRepairDate,
      RUNKM: car.runKm || 0,
      RunHour: car.runHour || 0
    }))
    
    pagination.total = totalElements
  } catch (error) {
    console.error('获取车辆列表失败:', error)
    ElMessage.error('获取车辆列表失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

// 获取所有车型选项
const fetchCarTypes = async () => {
  try {
    const response = await getCarTypes()
    console.log('车型数据:', response); // 调试信息
    
    // 确保数据有正确的属性名（注意后端返回的是小写字段名）
    carTypeOptions.value = response.map(type => ({
      tNo: type.tno,       // 注意这里使用的是tno而不是tNo
      tName: type.tname,   // 注意这里使用的是tname而不是tName
      rentRate: type.rentRate
    }))
    
    // 更新车型表格数据
    typeTableData.value = response.map(type => ({
      TNo: type.tno,       // 注意这里使用的是tno而不是tNo
      TName: type.tname,   // 注意这里使用的是tname而不是tName
      rentRate: type.rentRate,
      Image: getImageForCarType(type.tname || type.TName)  // 根据车型名称获取对应图片
    }))
    
    typePagination.total = response.length
  } catch (error) {
    console.error('获取车型列表失败:', error)
    ElMessage.error('获取车型列表失败: ' + (error.message || '网络错误'))
  }
}

// 根据车型名称获取对应图片的辅助函数
const getImageForCarType = (typeName) => {
  // 清理可能存在的乱码字符，只保留中文、英文字母和数字
  const cleanTypeName = typeName.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '');
  
  console.log('原始车型名称:', typeName, '清理后:', cleanTypeName); // 调试信息
  
  // 图片URL映射
  const imageMap = {
    '丰田凯美瑞': 'https://images.unsplash.com/photo-1542362567-b07e54358753?auto=format&fit=crop&q=80&w=400',
    '本田雅阁': 'https://images.unsplash.com/photo-1549399542-7e3f8b79578f?auto=format&fit=crop&q=80&w=400',
    '奔驰E级': 'https://images.unsplash.com/photo-1617358038732-7a3d5de4b20f?auto=format&fit=crop&q=80&w=400',
    '宝马5系': 'https://images.unsplash.com/photo-1617395310245-0a558b39c01f?auto=format&fit=crop&q=80&w=400',
    '奥迪A6L': 'https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?auto=format&fit=crop&q=80&w=400',
    '大众帕萨特': 'https://images.unsplash.com/photo-1622340767794-5412803c7344?auto=format&fit=crop&q=80&w=400'
  };
  
  // 返回对应图片URL，如果没有找到则使用默认图片
  const imageUrl = imageMap[cleanTypeName] || 'https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&q=80&w=400';
  console.log('匹配图片URL:', imageUrl);
  return imageUrl;
};

const handleSearch = () => {
  pagination.currentPage = 1
  fetchCars()
}

const resetSearch = () => {
  searchForm.CarID = ''
  searchForm.TName = ''
  searchForm.CarStatus = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  handleSearch()
}

const handleAdd = () => {
  currentEditData.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  // 将前端数据格式转换为后端DTO格式
  const editData = {
    CarID: row.CarID,
    TNo: row.TNo,
    TName: row.TName,
    rentRate: row.rentRate,
    Price: row.Price,
    PurchaseDate: row.PurchaseDate,
    CarStatus: row.CarStatus,
    RUNKM: row.RUNKM,
    RunHour: row.RunHour
  }
  currentEditData.value = editData
  dialogVisible.value = true
}

const handleMaintenance = (row) => {
  currentMaintenanceCar.value = { ...row }
  maintenanceDialogVisible.value = true
}

const handleMaintenanceSubmit = async (formData) => {
  try {
    // 准备维修记录数据
    const repairData = {
      carId: formData.CarID,
      rDate: formData.RDate,
      runKm: formData.RunKM,
      runHour: formData.RunHour
    }
    
    // 创建维修记录
    await createRepair(repairData)
    
    // 更新车辆状态为维修中
    await updateCarStatus(formData.CarID, { carStatus: '2' })
    
    ElMessage.success('车辆已送修')
    
    // 刷新车辆列表以显示最新的状态
    fetchCars()
  } catch (error) {
    console.error('送修失败:', error)
    ElMessage.error('送修失败: ' + (error.message || '未知错误'))
  }
}

// --- Type Management Methods ---
const typeFormRef = ref(null)

const handleTypeSearch = () => {
  typeLoading.value = true
  
  // 实际实现搜索逻辑
  fetchCarTypes()
  
  setTimeout(() => {
    typeLoading.value = false
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
  // 确保字段名匹配后端返回的小写字段名
  Object.assign(typeFormData, {
    TNo: row.TNo || row.tno,
    TName: row.TName || row.tname,
    RentRate: row.rentRate || row.RentRate || 0,
    Image: row.Image || ''
  })
  typeDialogVisible.value = true
}

const handleTypeDelete = (row) => {
  ElMessageBox.confirm(`确定要删除车型 ${row.TName} 吗？`, '警告', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteCarType(row.TNo || row.tno, row.TName || row.tname)
      ElMessage.success('删除成功')
      fetchCarTypes()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }).catch(() => {})
}

const handleTypeClose = () => {
  typeDialogVisible.value = false
}

const handleFileChange = (uploadFile) => {
  // 模拟上传：将图片转换为 Base64 进行预览
  const reader = new FileReader()
  reader.readAsDataURL(uploadFile.raw)
  reader.onload = () => {
    typeFormData.Image = reader.result
  }
}

const handleTypeSubmit = async () => {
  if (!typeFormRef.value) return
  await typeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isTypeEdit.value) {
          // 更新车型
          await updateCarType(typeFormData.TNo, typeFormData.TName, {
            tno: typeFormData.TNo,
            tname: typeFormData.TName,
            rentRate: typeFormData.RentRate
          })
          ElMessage.success('修改成功')
        } else {
          // 创建新车型
          await createCarType({
            tno: typeFormData.TNo,
            tname: typeFormData.TName,
            rentRate: typeFormData.RentRate
          })
          ElMessage.success('新增成功')
        }
        
        typeDialogVisible.value = false
        fetchCarTypes()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + (error.message || '未知错误'))
      }
    }
  })
}

const handleDialogSubmit = async (formData) => {
  try {
    // 将前端表单数据转换为后端DTO格式
    const carDTO = {
      carId: formData.CarID,
      tNo: formData.TNo,
      tName: formData.TName,
      price: formData.Price,
      purchaseDate: formData.PurchaseDate,
      carStatus: formData.CarStatus
    }

    if (currentEditData.value) {
      // 更新车辆
      await updateCar(formData.CarID, carDTO)
      ElMessage.success('车辆信息更新成功')
    } else {
      // 创建车辆
      await createCar(carDTO)
      ElMessage.success('车辆创建成功')
    }
    
    // 关闭对话框并刷新数据
    dialogVisible.value = false
    fetchCars()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(currentEditData.value ? '更新失败' : '创建失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除 ${row.TName} (${row.CarID}) 含？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await deleteCar(row.CarID)
      ElMessage.success('删除成功')
      fetchCars()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchCars()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchCars()
}

const handleTypeSizeChange = (val) => {
  typePagination.pageSize = val
  handleTypeSearch()
}

const handleTypeCurrentChange = (val) => {
  typePagination.currentPage = val
  handleTypeSearch()
}
</script>

<style lang="scss">
/* 全局样式覆盖 - 必须不加 scoped 才能影响到 teleport 到 body 的 dialog */
.custom-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;

  .el-dialog__header {
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

  .el-dialog__body {
    padding: 32px 24px !important;
  }

  .el-dialog__footer {
    padding: 20px 24px;
    border-top: 1px solid #f1f5f9;
    background: #f8fafc;
  }
}
</style>

<style scoped lang="scss">
.type-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #475569;
  }
}

.type-thumb {
  width: 80px;
  height: 50px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  background: #fafafa;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.custom-input,
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
      background: linear-gradient(135deg, #10b981, #059669); /* Green for Add action */
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
        background: linear-gradient(135deg, #34d399, #059669);
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
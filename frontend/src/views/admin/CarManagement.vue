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
              <el-select v-model="searchForm.tNo" placeholder="选择车型" clearable filterable style="width: 180px">
                <el-option
                  v-for="type in carTypeOptions" 
                  :key="type.tNo + type.tName" 
                  :label="type.tName" 
                  :value="type.tNo">
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
              <div class="car-model">
                <span v-if="scope.row.TName">{{ scope.row.TName }}</span>
                <span v-else class="missing-type">未设置车型</span>
              </div>
              <div class="car-code">编号: {{ scope.row.TNo || '—' }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 租金率 (RentRate) -->
        <el-table-column prop="RentRate" label="小时租金" width="150" align="left">
          <template #default="scope">
            <div class="price-tag">
              <span class="currency">¥</span>
              <span class="amount">{{ formatRent(scope.row.RentRate) }}</span>
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
                 @error="() => onTypeImageError(scope.row)"
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
          <el-input v-model="typeFormData.TName" placeholder="例如: 丰田凯美瑞" class="custom-input" :disabled="isTypeEdit" />
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
            <img v-if="typeFormData.Image" :src="typeFormData.Image" alt="车型图片预览" class="avatar" />
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
      :edit-data="currentEditData && currentEditData.value ? currentEditData.value : currentEditData"
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
import { Search, Refresh, Plus, Edit, Delete, Tools } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCars, 
  createCar, 
  updateCar, 
  deleteCar, 
  createRepair,
  updateCarStatus,
  getCarTypes,
  createCarType,
  updateCarType,
  deleteCarType,
  getCarById // Import the new API function
} from '@/api/car'
import request from '@/api/request'

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
  // now store selected type as tNo (primary key)
  tNo: '',
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

// 新增：格式化小时租金显示
const formatRent = (rate) => {
  if (rate === undefined || rate === null || rate === '') return '-'
  // 如果是对象（比如 BigDecimal 转为字符串），转字符串再处理
  try {
    const n = Number(rate)
    if (Number.isNaN(n)) return String(rate)
    // 如果是整数，显示无小数；否则保留两位
    return Number.isInteger(n) ? n.toString() : n.toFixed(2)
  } catch (e) {
    return String(rate)
  }
}

// 根据车型名称获取对应图片的辅助函数（放在 fetchCarTypes 之前，避免未定义）
const getImageForCarType = (typeName) => {
  // 优先使用本地 JPG 资源（位于 src/assets/images），文件名为小写英文标识
  try {
    if (!typeName) {
      // prefer default.jpg if available
      try { return new URL('../../assets/images/default.jpg', import.meta.url).href } catch (e) { return new URL('../../assets/images/default.svg', import.meta.url).href }
    }

    // 仅保留中文/英文字母/数字用于匹配
    const cleanTypeName = typeName.toString().replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '').trim()

    // 使用包含匹配以决定具体图片（优先 jpg）
    const name = cleanTypeName
    if (name.includes('丰田') || name.includes('凯美瑞') || /camry/i.test(name)) {
      return new URL(`../../assets/images/toyota_camry.jpg`, import.meta.url).href
    }
    if (name.includes('本田') || name.includes('雅阁') || /accord/i.test(name)) {
      return new URL(`../../assets/images/honda_accord.jpg`, import.meta.url).href
    }
    if (name.includes('奔驰') || name.includes('E级') || /benz|mercedes/i.test(name)) {
      return new URL(`../../assets/images/benz_e_class.jpg`, import.meta.url).href
    }
    if (name.includes('宝马') || name.includes('5系') || /bmw.*5/i.test(name)) {
      return new URL(`../../assets/images/bmw_5_series.jpg`, import.meta.url).href
    }
    if (name.includes('奥迪') || /audi/i.test(name)) {
      return new URL(`../../assets/images/audi_a6l.jpg`, import.meta.url).href
    }
    if (name.includes('大众') || name.includes('帕萨特') || /passat/i.test(name)) {
      return new URL(`../../assets/images/vw_passat.jpg`, import.meta.url).href
    }

    // 如果本地没有匹配项，尝试按英文关键字构造文件名（先 jpg，再 svg）
    const fallbackBase = cleanTypeName.toLowerCase().replace(/\s+/g, '_')
    try {
      return new URL(`../../assets/images/${fallbackBase}.jpg`, import.meta.url).href
    } catch (e) {
      try { return new URL(`../../assets/images/${fallbackBase}.svg`, import.meta.url).href } catch (e2) { /* fallback below */ }
    }

    // 最后降级到项目自带默认图（优先 jpg）
    try { return new URL('../../assets/images/default.jpg', import.meta.url).href } catch (e) { return new URL('../../assets/images/default.svg', import.meta.url).href }
  } catch (err) {
    // 如果构建时路径解析出错，返回远程占位图作为兜底
    console.error('getImageForCarType error:', err)
    return 'https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&q=80&w=400'
  }
}

// Normalize image URLs returned by backend. If backend sends relative paths like "/api/files/xxx" or "uploads/xxx",
// prefix them with the backend origin derived from the axios instance baseURL.
const getBackendOrigin = () => {
  try {
    const base = (request && request.defaults && request.defaults.baseURL) ? request.defaults.baseURL : ''
    if (!base) return ''
    // if base ends with '/api', drop that to get origin
    if (base.endsWith('/api')) return base.slice(0, -4)
    return base.replace(/\/$/, '')
  } catch (e) {
    return ''
  }
}

const normalizeImageUrl = (url) => {
  if (!url) return ''
  url = url.toString().trim()
  // already a data URL or absolute http(s)
  if (/^data:|^https?:\/\//i.test(url)) return url
  // protocol-relative URL like //example.com/path
  if (/^\/\//.test(url)) return window.location.protocol + url

  const origin = getBackendOrigin()
  if (!origin) return url

  // ensure leading slash
  if (!url.startsWith('/')) url = '/' + url

  return origin + url
}

// 当 el-image 加载失败时使用本地图像作为回退
const onTypeImageError = (row) => {
  if (!row) return
  if (row._imageFallback) return
  row._imageFallback = true
  row.Image = getImageForCarType(row.TName || row.tname)
}

// 搜索按钮触发的函数（确保在模板渲染时已定义）
const handleSearch = () => {
  pagination.currentPage = 1
  fetchCars()
}

// 页面加载时获取车辆数据
onMounted(async () => {
  // 确保先加载车型（用于在fetchCars里做回填），再加载车辆列表
  await fetchCarTypes()
  fetchCars()
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
    if (searchForm.tNo) {
      // 前端选择的是车型编号（tNo），后端 controller 支持 tNo
      params.tNo = searchForm.tNo
    }
    if (searchForm.CarStatus) {
      params.carStatus = searchForm.CarStatus
    }
    // 将前端的价格范围（minPrice/maxPrice）映射为后端的租金率过滤参数（minRent/maxRent）
    if (searchForm.minPrice !== undefined && searchForm.minPrice !== null && searchForm.minPrice !== '') {
      params.minRent = Number(searchForm.minPrice)
    }
    if (searchForm.maxPrice !== undefined && searchForm.maxPrice !== null && searchForm.maxPrice !== '') {
      params.maxRent = Number(searchForm.maxPrice)
    }

    const response = await getCars(params)
    // DEBUG: 打印后端原始响应，帮助排查字段名/结构
    console.debug('GET /api/cars response raw:', response)
    const { content, totalElements } = response

    // 转换数据格式以适配前端显示（注意后端返回的是小写字段名）
    tableData.value = content.map(car => {
      // Accept multiple naming variants returned by backend
      const tNo = car.tNo || car.TNo || car.tno || car.TNO || car.t_no || car.T_no || null
      const tName = car.tName || car.TName || car.tname || car.TNAME || car.t_name || car.T_name || null
      // try to find nested car type object under common keys
      const nestedType = car.carType || car.car_type || car.type || car.carTypeDto || car._embedded?.carType || car.typeInfo || car.typeDto || car.carTypeDTO || null
      let rentRate = car.rentRate || car.RentRate || (nestedType && (nestedType.rentRate || nestedType.RentRate)) || null

      // If rentRate appears as an object (BigDecimal wrapper), unwrap common shapes
      if (rentRate && typeof rentRate === 'object') {
        // e.g., { value: '30.00' } or { amount: '30.00' }
        if (rentRate.value !== undefined) rentRate = rentRate.value
        else if (rentRate.amount !== undefined) rentRate = rentRate.amount
      }

      // coerce BigDecimal-like strings to number when possible
      if (rentRate !== null && rentRate !== undefined) {
        if (typeof rentRate === 'string' && rentRate.trim() !== '') {
          const maybe = Number(rentRate)
          if (!Number.isNaN(maybe)) rentRate = maybe
        }
      }

      // Prefer canonical values from carTypeOptions when available (ensures type edits propagate)
      let finalTName = tName
      let finalRentRate = rentRate
      let finalTNo = tNo

      // If nestedType contains tNo/tName, prefer those
      if (nestedType) {
        finalTNo = finalTNo || nestedType.tNo || nestedType.TNo || nestedType.tno || nestedType.t_no || null
        finalTName = finalTName || nestedType.tName || nestedType.TName || nestedType.tname || nestedType.name || null
        if (finalRentRate === null || finalRentRate === undefined) {
          finalRentRate = nestedType.rentRate || nestedType.RentRate || null
        }
      }

      if (finalTNo) {
        const found = carTypeOptions.value.find(t => (t.tNo || t.tno) === finalTNo || (t.tNo || t.tno) === (finalTNo && finalTNo.toString()))
        if (found) {
          finalTName = found.tName || found.tname || finalTName
          finalRentRate = (found.rentRate !== undefined && found.rentRate !== null) ? found.rentRate : finalRentRate
          finalTNo = found.tNo || found.tno || finalTNo
        }
      }

      // Final fallback: try to lookup by name
      if ((!finalTNo || finalTNo === '') && finalTName) {
        const foundByName = carTypeOptions.value.find(t => (t.tName || t.tname) === finalTName || (t.tName || t.tname) === (finalTName && finalTName.toString()))
        if (foundByName) {
          finalTNo = foundByName.tNo || foundByName.tno
          finalRentRate = finalRentRate || foundByName.rentRate
        }
      }

      // If still missing, try to inspect raw payload for common nested shapes
      if ((!finalTNo || finalTNo === '') && (!finalTName || finalTName === '')) {
        const raw = car || {}
        // common shapes
        const alt = raw.carTypeDto || raw.carTypeDTO || raw.typeInfo || raw.type || raw.car_type || raw._embedded?.carType
        if (alt) {
          finalTNo = finalTNo || alt.tNo || alt.TNo || alt.tno || null
          finalTName = finalTName || alt.tName || alt.TName || alt.tname || alt.name || null
          finalRentRate = finalRentRate || alt.rentRate || alt.RentRate || null
        }
      }

      // Final fallback logging for debugging
      if ((!finalTNo || finalTNo === '') && (!finalTName || finalTName === '')) {
        console.warn('Car row missing both tNo and tName:', car)
        try {
          console.debug('Car raw keys:', Object.keys(car || {}))
        } catch (e) {
          console.debug('Car raw (unable to list keys)', car)
        }
      }

      const normalizedRent = (finalRentRate === null || finalRentRate === undefined) ? null : (typeof finalRentRate === 'number' ? finalRentRate : Number(finalRentRate))

      return {
        CarID: car.carId || car.CarID || car.carID || car.CarId,
        TNo: finalTNo || null,
        TName: finalTName || null,
        RentRate: normalizedRent,
        CarStatus: car.carStatus || car.CarStatus,
        PurchaseDate: car.purchaseDate || car.PurchaseDate,
        Price: car.price || car.Price,
        LastRepairDate: car.lastRepairDate || car.LastRepairDate,
        RUNKM: car.runKm || car.RUNKM || 0,
        RunHour: car.runHour || car.RunHour || 0,
        // include raw car for debugging
        _raw: car
      }
    })
    // DEBUG: 打印处理后的 tableData 中前几行以便调试
    console.debug('Processed tableData (first rows):', tableData.value.slice(0, 5))

    // 补救措施：对于仍然缺失车型信息的行，逐条向后端请求单车详情以回填（避免列表接口部分字段为 null 的情况）
    const missingRows = tableData.value.filter(r => (!r.TNo && !r.TName) || r.TName === null)
    if (missingRows.length > 0) {
      console.debug(`Found ${missingRows.length} rows with missing tNo/tName, fetching individual car details...`)
      await Promise.all(missingRows.map(async (row) => {
        try {
          const detail = await getCarById(row.CarID)
          if (detail) {
            // map backend DTO variants to our normalized fields
            const detailTNo = detail.tNo || detail.TNo || detail.tno || null
            const detailTName = detail.tName || detail.TName || detail.tname || null
            const detailRent = detail.rentRate || detail.RentRate || (detail.carType && (detail.carType.rentRate || detail.carType.RentRate)) || null

            // update the table row in-place
            row.TNo = row.TNo || detailTNo || (detail.carType && (detail.carType.tNo || detail.carType.TNo)) || row.TNo
            row.TName = row.TName || detailTName || (detail.carType && (detail.carType.tName || detail.carType.TName)) || row.TName
            row.RentRate = row.RentRate || (detailRent ? (typeof detailRent === 'number' ? detailRent : Number(detailRent)) : row.RentRate)
            row.RUNKM = row.RUNKM || detail.runKm || detail.RunKM || row.RUNKM
            row.RunHour = row.RunHour || detail.runHour || detail.RunHour || row.RunHour
            row.LastRepairDate = row.LastRepairDate || detail.lastRepairDate || detail.LastRepairDate || row.LastRepairDate
            // store raw detail for debugging
            row._detailFetched = detail
          }
        } catch (err) {
          console.warn('Failed to fetch car detail for', row.CarID, err)
        }
      }))
      console.debug('After detail backfill, tableData (first rows):', tableData.value.slice(0,5))
    }

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
  typeLoading.value = true
  try {
    const response = await getCarTypes()
    console.debug('GET /api/car-types response raw:', response)
    // backend may return an array directly or wrap it as { value: [...], Count: N }
    const payload = Array.isArray(response) ? response : (response && response.value ? response.value : [])

    // backend may use lowercase keys (tno/tname) or camelCase (tNo/tName)
    const allTypes = (payload || []).map(type => ({
      tno: type.tNo || type.tno || type.TNo || type.TNO || type.t_no || '',
      tname: type.tName || type.tname || type.TName || type.TNAME || type.t_name || '',
      rentRate: type.rentRate || type.RentRate || type.Rent || null,
      imageUrl: normalizeImageUrl(type.imageUrl || type.Image || type.image || '')
    }))

    // 用于车辆搜索下拉的完整选项（保持原样）
    carTypeOptions.value = allTypes.map(type => ({
      tNo: type.tno,
      tName: type.tname,
      rentRate: type.rentRate
    }))

    console.debug('Processed carTypeOptions:', carTypeOptions.value)

    // 过滤（按关键字匹配 tno 或 tname，忽略大小写）
    const kw = (typeSearchForm.keyword || '').toString().trim().toLowerCase()
    let filtered = allTypes
    if (kw) {
      filtered = allTypes.filter(t => {
        const name = (t.tname || '').toString().toLowerCase()
        const no = (t.tno || '').toString().toLowerCase()
        return name.includes(kw) || no.includes(kw)
      })
    }

    // 更新分页总数
    typePagination.total = filtered.length

    // 确保当前页在范围内
    const page = Math.max(1, typePagination.currentPage || 1)
    const size = Math.max(1, typePagination.pageSize || 10)
    const start = (page - 1) * size
    const pageSlice = filtered.slice(start, start + size)

    // 更新表格数据（保持字段名兼容）
    typeTableData.value = pageSlice.map(type => ({
      TNo: type.tno,
      TName: type.tname,
      rentRate: type.rentRate,
      // prefer backend-provided (and normalized) image URL; fallback to local asset helper
      Image: type.imageUrl || type.Image || getImageForCarType(type.tname)
    }))
  } catch (error) {
    console.error('获取车型列表失败:', error)
    ElMessage.error('获取车型列表失败: ' + (error.message || '网络错误'))
  } finally {
    typeLoading.value = false
  }
}

const handleTypeSearch = async () => {
  // 当触发搜索时回到第一页，然后重新加载（fetchCarTypes 内部会使用 typePagination）
  typePagination.currentPage = 1
  await fetchCarTypes()
}

const resetSearch = () => {
  searchForm.CarID = ''
  searchForm.tNo = ''
  searchForm.CarStatus = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  handleSearch()
}

const handleAdd = () => {
  currentEditData.value = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  // Before opening the edit dialog, ensure we have the complete latest detail from backend
  try {
    loading.value = true
    // attempt to fetch full detail for this car to avoid missing tNo/tName
    const detail = await getCarById(row.CarID || row.CarId || row.carId)
    if (detail) {
      // normalize fields similar to fetchCars mapping
      const tNo = detail.tNo || detail.TNo || detail.tno || null
      const tName = detail.tName || detail.TName || detail.tname || null
      const rentRate = detail.rentRate || detail.RentRate || (detail.carType && (detail.carType.rentRate || detail.carType.RentRate)) || null

      currentEditData.value = {
        CarID: detail.carId || detail.CarID || detail.carId || row.CarID,
        TNo: tNo,
        TName: tName,
        RentRate: (rentRate !== undefined && rentRate !== null) ? Number(rentRate) : null,
        Price: detail.price || detail.Price || row.Price,
        PurchaseDate: detail.purchaseDate || detail.PurchaseDate || row.PurchaseDate,
        RUNKM: detail.runKm || detail.RUNKM || row.RUNKM,
        RunHour: detail.runHour || detail.RunHour || row.RunHour,
        CarStatus: detail.carStatus || detail.CarStatus || row.CarStatus,
        _raw: detail
      }
    } else {
      // fallback to the row we had
      currentEditData.value = row
    }
  } catch (err) {
    console.warn('Failed to fetch car detail before edit, falling back to row:', err)
    currentEditData.value = row
  } finally {
    loading.value = false
    dialogVisible.value = true
  }
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
    // prefer backend-provided imageUrl when opening edit dialog
    Image: row.imageUrl || row.Image || ''
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
      await deleteCarType(row.TNo || row.tno)
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
        let updatedType
        if (isTypeEdit.value) {
          // 更新车型（现在以 tNo 作为主键）
          updatedType = await updateCarType(typeFormData.TNo, {
            tName: typeFormData.TName,
            rentRate: typeFormData.RentRate
          })
          ElMessage.success('修改成功')
        } else {
           // 创建新车型
           updatedType = await createCarType({
             tno: typeFormData.TNo,
             tname: typeFormData.TName,
             rentRate: typeFormData.RentRate
           })
           ElMessage.success('新增成功')
         }

        typeDialogVisible.value = false
        // 先刷新车型列表，确保下拉/本地缓存是最新的
        await fetchCarTypes()
        // 如果后端返回了 updatedType（create/update 响应），在本地 tableData 中立即同步匹配的车辆行，
        // 以确保 UI 不依赖于后端分页缓存延迟
        try {
          if (updatedType) {
            const canonicalTNo = updatedType.tNo || updatedType.TNo || typeFormData.TNo
            const canonicalTName = updatedType.tName || updatedType.TName || typeFormData.TName
            const canonicalRent = updatedType.rentRate || updatedType.RentRate || typeFormData.RentRate
            tableData.value.forEach(row => {
              if (row && (row.TNo === canonicalTNo || row.TName === canonicalTName)) {
                row.TNo = canonicalTNo
                row.TName = canonicalTName
                row.RentRate = (canonicalRent !== undefined && canonicalRent !== null) ? Number(canonicalRent) : row.RentRate
              }
            })
          }
        } catch (syncErr) {
          console.warn('Failed to sync local tableData after type change:', syncErr)
        }

        // 再刷新车辆列表，保证任何使用车型信息（TName/RentRate）的地方得到最新数据
        try {
          await fetchCars()
        } catch (err) {
          console.warn('Failed to refresh cars after type change:', err)
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + (error.message || '未知错误'))
      }
    }
  })
}

const handleDialogSubmit = async (formData) => {
  try {
    // support both uppercase and lowercase field names emitted by dialog
    const carDTO = {
      carId: (formData.CarID !== undefined ? formData.CarID : formData.carId),
      tNo: (formData.TNo !== undefined ? formData.TNo : formData.tNo),
      tName: (formData.TName !== undefined ? formData.TName : formData.tName),
      price: (formData.Price !== undefined ? formData.Price : formData.price),
      purchaseDate: (formData.PurchaseDate !== undefined ? formData.PurchaseDate : formData.purchaseDate),
      carStatus: (formData.CarStatus !== undefined ? formData.CarStatus : formData.carStatus),
      runKm: (formData.RUNKM !== undefined ? formData.RUNKM : formData.runKm),
      runHour: (formData.RunHour !== undefined ? formData.RunHour : formData.runHour)
    }

    // Ensure we always send a tNo (primary key) to backend when possible. If not provided,
    // try to resolve from tName using loaded carTypeOptions; if still unresolved, load types from backend.
    if ((!carDTO.tNo || carDTO.tNo === '') && carDTO.tName) {
      let found = carTypeOptions.value.find(t => (t.tName || t.tname) === carDTO.tName || (t.tName || t.tname) === (carDTO.tName && carDTO.tName.toString()))
      if (!found) {
        // try to refresh car types once
        try {
          await fetchCarTypes()
          found = carTypeOptions.value.find(t => (t.tName || t.tname) === carDTO.tName || (t.tName || t.tname) === (carDTO.tName && carDTO.tName.toString()))
        } catch (e) { /* ignore */ }
      }
      if (found) carDTO.tNo = found.tNo || found.tno
    }

    // 前端校验：必填字段
    if (!carDTO.carId || carDTO.carId.toString().trim() === '') {
      ElMessage.error('车牌号为必填项')
      return
    }
    // 要求车型至少提供 tNo 与 tName 中的一项；后端在同时提供两项时会进行严格校验
    if ((!carDTO.tNo || carDTO.tNo === '') && (!carDTO.tName || carDTO.tName === '')) {
      ElMessage.error('请选择车型或填写车型信息')
      return
    }

    // 如果提供了 tNo，但 tName 为空或不可信，尝试从已加载的车型选项中填充标准 tName（避免后端严格校验失败）
    if (carDTO.tNo && (!carDTO.tName || carDTO.tName === '')) {
      const found = carTypeOptions.value.find(t => t.tNo === carDTO.tNo || t.tno === carDTO.tNo)
      if (found) {
        carDTO.tName = found.tName || found.tname || found.TName || found.tName
      }
    }

    // 调试：打印要发送到后端的 payload
    console.debug('Creating/updating car with payload (raw):', carDTO)

    // 构建清洗过的 payload：去掉空字符串字段，确保日期/数值格式合适
    const payload = {}
    if (carDTO.carId && carDTO.carId.toString().trim() !== '') payload.carId = carDTO.carId.toString().trim()
    if (carDTO.tNo && carDTO.tNo.toString().trim() !== '') payload.tNo = carDTO.tNo.toString().trim()
    if (carDTO.tName && carDTO.tName.toString().trim() !== '') payload.tName = carDTO.tName.toString().trim()
    if (carDTO.price !== undefined && carDTO.price !== null && carDTO.price !== '') payload.price = Number(carDTO.price)
    if (carDTO.purchaseDate && carDTO.purchaseDate.toString().trim() !== '') payload.purchaseDate = carDTO.purchaseDate.toString().trim()
    if (carDTO.carStatus && carDTO.carStatus.toString().trim() !== '') payload.carStatus = carDTO.carStatus.toString().trim()
    if (carDTO.runKm !== undefined && carDTO.runKm !== null && carDTO.runKm !== '') payload.runKm = Number(carDTO.runKm)
    if (carDTO.runHour !== undefined && carDTO.runHour !== null && carDTO.runHour !== '') payload.runHour = Number(carDTO.runHour)

    console.debug('Creating/updating car with payload (clean):', payload)

    if (currentEditData.value) {
      // 更新车辆：直接使用清洗后的 payload 提交给后端，确保包含 carId/tNo/tName
      try {
        const targetId = payload.carId || (currentEditData.value && (currentEditData.value.CarID || currentEditData.value.carId))
        if (!targetId) {
          ElMessage.error('无法确定要更新的车辆 ID')
          return
        }
        console.debug('Updating car id:', targetId, 'with payload:', payload)
        const resp = await updateCar(targetId, payload)
        console.debug('updateCar response:', resp)
        ElMessage.success('车辆信息更新成功')
      } catch (err) {
        console.error('updateCar failed:', err)
        const msg = (err && err.response && err.response.data && (err.response.data.message || err.response.data.details)) || (err && err.message) || '更新车辆失败'
        ElMessage.error('更新失败: ' + msg)
        return
      }
    } else {
       // 创建车辆
       try {
         await createCar(payload)
         ElMessage.success('车辆创建成功')
       } catch (err) {
         // 显示后端返回的详细错误信息（如果有）
         let backendMsg = null
         if (err && err.response && err.response.data) {
           const data = err.response.data
           // 后端 ErrorResponse 使用字段: timestamp, status, message, details
           backendMsg = data.details || data.message || data.error || JSON.stringify(data)
         }
         if (backendMsg) {
           ElMessage.error('创建失败: ' + backendMsg)
         } else {
           console.error('创建车辆时发生未知错误:', err)
           ElMessage.error('创建失败: 服务器返回未知错误')
         }
         return
       }
    }

    // 关闭对话框并刷新数据
    dialogVisible.value = false
    await fetchCars()
  } catch (error) {
    console.error('操作失败:', error)
    let backendMsg = null
    if (error && error.response && error.response.data) {
      const data = error.response.data
      backendMsg = data.details || data.message || data.error || JSON.stringify(data)
    }
    ElMessage.error(backendMsg || (currentEditData.value ? '更新失败' : '创建失败'))
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
      }

    } /* end .search-form */

  } /* end .search-bar */


  /* Restored UI styles for car table and badges */
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

} /* end .car-management */
</style>

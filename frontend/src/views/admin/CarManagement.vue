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
            <el-form-item label="租金范围">
               <el-input-number v-model="searchForm.minPrice" :min="0" :controls="false" placeholder="最低租金" style="width: 100px" />
               <span style="margin: 0 4px">-</span>
               <el-input-number v-model="searchForm.maxPrice" :min="0" :controls="false" placeholder="最高租金" style="width: 100px" />
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
        @sort-change="handleCarSortChange"
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
        <el-table-column prop="RentRate" label="小时租金" width="150" align="left" sortable>
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
        <el-table-column prop="RUNKM" label="运行公里" width="120" align="right" sortable>
          <template #default="scope">
            <span class="text-secondary">{{ scope.row.RUNKM }} km</span>
          </template>
        </el-table-column>

        <!-- 运行小时 (RunHour) -->
        <el-table-column prop="RunHour" label="运行小时" width="120" align="right" sortable>
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
        <el-table :data="typeTableData" style="width: 100%" v-loading="typeLoading" class="modern-table" @sort-change="handleTypeSortChange">
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
            <el-table-column prop="rentRate" label="基准小时租金" min-width="150" align="right" sortable>
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
          <el-input v-model="typeFormData.TNo" placeholder="例如: T010" class="custom-input" :disabled="isTypeEdit" />
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
        <el-form-item label="车型图片" prop="ImagePath">
          <div style="display:flex;flex-direction:column;gap:12px;width:100%">
            <!-- 移除手动输入路径，只展示预览和上传按钮 -->
            <div style="display:flex;align-items:center;gap:16px">
              <el-image
                :src="previewImageUrl"
                fit="cover"
                style="width:140px;height:100px;border-radius:8px;border:1px dashed #d1d5db"
              >
                <template #error>
                  <div style="width:140px;height:100px;display:flex;align-items:center;justify-content:center;color:#9ca3af;border:1px dashed #d1d5db;border-radius:8px">
                    无法预览
                  </div>
                </template>
              </el-image>
              <div style="flex:1;color:#6b7280">请选择本地图片上传，系统会自动生成图片路径并用于预览与展示</div>
            </div>
            <div style="display:flex;align-items:center;gap:12px;margin-top:8px">
              <input
                type="file"
                accept="image/*"
                ref="typeImageFileInput"
                style="display:none"
                @change="handleTypeImageFileChange"
              />
              <el-button size="small" @click="triggerTypeImageChoose" :loading="uploadingTypeImage">
                选择图片并上传
              </el-button>
              <span style="font-size:12px;color:#9ca3af">上传成功后将自动关联到该车型</span>
            </div>
          </div>
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
import { ref, reactive, defineAsyncComponent, onMounted, computed } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, Tools } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCars, 
  createCar, 
  updateCar, 
  deleteCar, 
  createRepair,
  updateCarStatus,
  getCarTypesAdmin,
  createCarType,
  updateCarType,
  deleteCarType,
  uploadCarTypeImage
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
  RentRate: 100,
  ImagePath: ''
})

const typeImageFileInput = ref(null)
const uploadingTypeImage = ref(false)

const previewImageUrl = computed(() => {
  if (typeFormData.ImagePath && typeFormData.ImagePath.trim()) {
    const normalized = normalizeImageUrl(typeFormData.ImagePath)
    if (normalized) return normalized
  }
  return getImageForCarType(typeFormData.TName)
})

const handleTypeImageFileChange = async (event) => {
  const files = event?.target?.files || event?.dataTransfer?.files
  if (!files || !files[0]) return
  const file = files[0]
  uploadingTypeImage.value = true
  try {
    const resp = await uploadCarTypeImage(file)
    const path = resp?.imagePath || resp?.path || resp?.url
    if (path) {
      typeFormData.ImagePath = path
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败：服务器未返回路径')
    }
  } catch (error) {
    console.error('上传车型图片失败:', error)
    const msg = error?.response?.data?.message || error?.message || '未知错误'
    ElMessage.error('上传车型图片失败：' + msg)
  } finally {
    uploadingTypeImage.value = false
    if (event?.target) {
      event.target.value = ''
    }
  }
}

const triggerTypeImageChoose = () => {
  if (typeImageFileInput.value) {
    typeImageFileInput.value.click()
  }
}

const typeRules = {
  TNo: [
    { required: true, message: '请输入类型编号', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (!value) return callback(new Error('请输入类型编号'))
        const v = String(value).trim()
        const reg = /^T\d{3}$/
        if (!reg.test(v)) {
          return callback(new Error('类型编号格式应为 T 后跟3位数字，例如: T010'))
        }
        callback()
      }, trigger: ['blur', 'change'] }
  ],
  TName: [{ required: true, message: '请输入车型名称', trigger: 'blur' }],
  RentRate: [
    { required: true, message: '请输入小时租金', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === '') {
          return callback(new Error('请输入小时租金'))
        }
        const num = Number(value)
        if (isNaN(num) || num <= 0) return callback(new Error('小时租金必须大于 0'))
        if (num > 9999.99) return callback(new Error('小时租金不能超过 9999.99'))
        const s = String(value)
        const parts = s.split('.')
        if (parts.length === 2 && parts[1].length > 2) return callback(new Error('小时租金最多保留两位小数'))
        return callback()
      }, trigger: ['blur','change'] }
  ]
}

// 车辆数据
const tableData = ref([])
const typeTableData = ref([])
const carTypeOptions = ref([])

// 车辆列表排序状态：记录小时租金、运行公里、运行小时三列的排序方向
const carSortState = reactive({
  rentOrder: null,
  kmOrder: null,
  hourOrder: null
})

// 车型租金表排序状态：目前只对小时租金列记录排序方向
const typeSortState = reactive({
  rentOrder: null
})

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

// 车辆列表多字段排序：
// 1) 如果设置了小时租金排序：以小时租金为主键，运行公里为次键，运行小时为第三键
// 2) 否则如果设置了运行公里排序：以公里为主键，小时为次键
// 3) 否则仅有运行小时排序时：按运行小时排序
const sortCarsByFields = (list, rentOrder, kmOrder, hourOrder) => {
  if (!rentOrder && !kmOrder && !hourOrder) return list

  const rateFactor = rentOrder === 'descending' ? -1 : 1
  const kmFactor = kmOrder === 'descending' ? -1 : 1
  const hourFactor = hourOrder === 'descending' ? -1 : 1

  return [...list].sort((a, b) => {
    const rateA = Number(a.RentRate || 0)
    const rateB = Number(b.RentRate || 0)
    const kmA = Number(a.RUNKM || 0)
    const kmB = Number(b.RUNKM || 0)
    const hourA = Number(a.RunHour || 0)
    const hourB = Number(b.RunHour || 0)

    // 1) 小时租金为主键
    if (rentOrder) {
      if (rateA !== rateB) return (rateA - rateB) * rateFactor
      // 次键：公里
      if (kmOrder && kmA !== kmB) return (kmA - kmB) * kmFactor
      // 第三键：小时
      if (hourOrder && hourA !== hourB) return (hourA - hourB) * hourFactor
      return 0
    }

    // 2) 未设置租金排序，仅有公里/小时
    if (kmOrder) {
      if (kmA !== kmB) return (kmA - kmB) * kmFactor
      if (hourOrder && hourA !== hourB) return (hourA - hourB) * hourFactor
      return 0
    }

    // 3) 仅有小时排序
    if (hourOrder) {
      if (hourA !== hourB) return (hourA - hourB) * hourFactor
      return 0
    }

    return 0
  })
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

// 车辆列表排序变更（多字段前端排序）
const handleCarSortChange = ({ prop, order }) => {
  if (prop === 'RentRate') {
    carSortState.rentOrder = order || null
  } else if (prop === 'RUNKM') {
    carSortState.kmOrder = order || null
  } else if (prop === 'RunHour') {
    carSortState.hourOrder = order || null
  } else {
    return
  }

  // 如果三个排序字段都被清空，则回到接口默认顺序
  if (!carSortState.rentOrder && !carSortState.kmOrder && !carSortState.hourOrder) {
    fetchCars()
    return
  }

  tableData.value = sortCarsByFields(
    tableData.value,
    carSortState.rentOrder,
    carSortState.kmOrder,
    carSortState.hourOrder
  )
}

// 搜索按钮触发的函数（确保在模板渲染时已定义）
const handleSearch = () => {
  pagination.currentPage = 1
  fetchCars()
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
      // 前端选择的是车型名称（tName），后端 controller 支持 tName 和 tNo
      params.tName = searchForm.TName
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
    // 兼容后端返回 Page 对象或直接 List
    let content = []
    let totalElements = 0
    if (response && response.content !== undefined) {
      content = response.content
      totalElements = response.totalElements || 0
    } else if (Array.isArray(response)) {
      content = response
      totalElements = response.length
    } else if (response) {
      // sometimes axios wrapper returns data directly; try to read common keys
      content = response || []
      totalElements = (response && response.length) || 0
    }

    // 转换数据格式以适配前端显示（注意后端返回的是小写字段名）
    tableData.value = content.map(car => ({
      CarID: car.carId || car.CarID || car.carID,
      TNo: car.tNo || car.tNo || car.tno || car.TNo || car.TNO,
      TName: car.tName || car.tname || car.TName || '',
      RentRate: car.rentRate || car.RentRate || 0,
      CarStatus: car.carStatus || car.CarStatus || '0',
      PurchaseDate: car.purchaseDate || car.PurchaseDate || '',
      Price: car.price || car.Price || 0,
      LastRepairDate: car.lastRepairDate || car.LastRepairDate || null,
      RUNKM: car.runKm || car.RUNKM || car.runKm || 0,
      RunHour: car.runHour || car.RunHour || 0
    }))
    
    pagination.total = totalElements
  } catch (error) {
    console.error('获取车辆列表失败:', error)
    ElMessage.error('获取车辆列表失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

// 获取所有车型选项（管理端使用：包含数据库中的全部车型）
const fetchCarTypes = async () => {
  typeLoading.value = true
  try {
    const response = await getCarTypesAdmin()
    const allTypes = (response || []).map(type => {
      const rawImagePath = type.imageUrl || type.Image || type.imageUrl
      const normalizedImage = normalizeImageUrl(rawImagePath)
      return {
        tno: type.tNo || type.tno,
        tname: type.tName || type.tname,
        rentRate: type.rentRate,
        imagePath: rawImagePath || '',
        imageUrl: normalizedImage
      }
    })

    // 用于车辆搜索下拉的完整选项（保持原样）
    carTypeOptions.value = allTypes.map(type => ({
      tNo: type.tno,
      tName: type.tname,
      rentRate: type.rentRate
    }))

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
  
    // 根据当前排序状态对当前页数据做前端排序（小时租金为主键，车型名称为次关键字）
    let sortedSlice = [...pageSlice]
    if (typeSortState.rentOrder) {
      const factor = typeSortState.rentOrder === 'descending' ? -1 : 1
      sortedSlice.sort((a, b) => {
        const rateA = Number(a.rentRate || 0)
        const rateB = Number(b.rentRate || 0)
        if (rateA !== rateB) return (rateA - rateB) * factor
        const nameA = (a.tname || '').toString()
        const nameB = (b.tname || '').toString()
        return nameA.localeCompare(nameB) * factor
      })
    }

    // 更新表格数据（保持字段名兼容）
    typeTableData.value = sortedSlice.map(type => {
      const displayImage = type.imageUrl || getImageForCarType(type.tname)
      return {
        TNo: type.tno,
        TName: type.tname,
        rentRate: type.rentRate,
        imagePath: type.imagePath,
        Image: displayImage
      }
    })
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
  searchForm.TName = ''
  searchForm.CarStatus = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  handleSearch()
}

// 车型租金表排序：仅对当前页的小时租金列做前端排序，车型名称为次关键字
const handleTypeSortChange = ({ prop, order }) => {
  if (prop !== 'rentRate') {
    return
  }

  typeSortState.rentOrder = order || null

  // 当排序被清空时，重新拉取一次数据以恢复接口默认顺序
  if (!typeSortState.rentOrder) {
    fetchCarTypes()
    return
  }

  // 直接对当前页数据重新排序
  const factor = typeSortState.rentOrder === 'descending' ? -1 : 1
  typeTableData.value = [...typeTableData.value].sort((a, b) => {
    const rateA = Number(a.rentRate || 0)
    const rateB = Number(b.rentRate || 0)
    if (rateA !== rateB) return (rateA - rateB) * factor
    const nameA = (a.TName || '').toString()
    const nameB = (b.TName || '').toString()
    return nameA.localeCompare(nameB) * factor
  })
}

const handleAdd = () => {
  currentEditData.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  // 直接设置编辑数据（无需额外中间变量）
  currentEditData.value = {
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
  dialogVisible.value = true
}

const handleMaintenance = (row) => {
  currentMaintenanceCar.value = { ...row }
  maintenanceDialogVisible.value = true
}

const handleMaintenanceSubmit = async (formData) => {
  try {
    const repairData = {
      carId: formData.CarID,
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
  typeFormData.ImagePath = ''
  typeDialogVisible.value = true
}

const handleTypeEdit = (row) => {
  isTypeEdit.value = true
  const rawRate = Number(row.rentRate ?? row.RentRate ?? 0)
  Object.assign(typeFormData, {
    TNo: row.TNo || row.tno,
    TName: row.TName || row.tname,
    RentRate: rawRate > 0 ? rawRate : 100,
    ImagePath: row.imagePath || row.ImagePath || ''
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
      // refresh types and vehicles after deletion (single call)
      fetchCarTypes()
      fetchCars()
    } catch (error) {
      console.error('删除失败:', error)
      // 尝试解析后端返回的结构化错误信息
      const serverMsg = error?.response?.data?.message || error?.response?.data?.details || error?.response?.data?.error || error?.response?.data || error.message
      const combined = '删除失败: ' + (serverMsg || '未知错误')
      // 先关闭已有提示，确保只显示一次（避免重复多条）
      ElMessage.closeAll()
      ElMessage.error(combined)
      window.__lastGlobalMessage = { text: combined, ts: Date.now() }
    }
  }).catch(() => {})
}

const handleTypeClose = () => {
  typeDialogVisible.value = false
}

const handleTypeSubmit = () => {
  if (!typeFormRef.value) return
  typeFormRef.value.validate(async (valid) => {
    if (!valid) return
    const rent = Number(typeFormData.RentRate)
    if (!Number.isFinite(rent) || rent <= 0) {
      ElMessage.error('小时租金必须大于 0')
      return
    }
    try {
      const payload = {
        tNo: typeFormData.TNo,
        tName: typeFormData.TName,
        rentRate: rent,
        imagePath: typeFormData.ImagePath || null
      }
      if (isTypeEdit.value) {
        // 更新车型：需要同时传入类型编号和名称作为路径参数
        await updateCarType(typeFormData.TNo, typeFormData.TName, payload)
        ElMessage.success('车型更新成功')
      } else {
        await createCarType(payload)
        ElMessage.success('车型创建成功')
      }
      typeDialogVisible.value = false
      // 刷新车型列表，并同步刷新车辆列表以反映最新租金
      await fetchCarTypes()
      await fetchCars()
    } catch (error) {
      console.error('保存车型失败:', error)
      const resp = error?.response
      const data = resp?.data || {}
      const status = resp?.status
      let detail = ''

      if (typeof data === 'object') {
        if (data.details) detail = data.details
        else if (data.message) detail = data.message
        else if (data.error) detail = data.error
      }

      const d = (detail || '').toString()
      const isDuplicate = status === 409 || d.includes('已存在') || /duplicate entry/i.test(d)
      if (isDuplicate) {
        if (d.includes('TName') || d.includes('车型名称') || d.includes('名称')) {
          ElMessage.error('保存车型失败：车型名称已存在，请更换后重试')
        } else if (d.includes('TNo') || d.includes('类型编号')) {
          ElMessage.error('保存车型失败：类型编号已存在，请更换后重试')
        } else {
          ElMessage.error('保存车型失败：类型编号或名称已存在，请更换后重试')
        }
        return
      }

      if (status === 400) {
        if (d.includes('TNo') || d.includes('类型编号')) {
          ElMessage.error('保存车型失败：类型编号不能为空或格式不正确')
        } else if (d.includes('TName') || d.includes('车型名称')) {
          ElMessage.error('保存车型失败：车型名称不能为空或不合法')
        } else if (d.includes('RentRate') || d.includes('小时租金')) {
          ElMessage.error('保存车型失败：小时租金必须大于 0')
        } else {
          ElMessage.error('保存车型失败：请求参数不合法')
        }
      } else if (status >= 500) {
        ElMessage.error('保存车型失败：服务器内部错误，请稍后重试')
      } else {
        const fallback = d || error?.message || '未知错误'
        ElMessage.error('保存车型失败：' + fallback)
      }
    }
  })
}

// 处理新增/编辑车型提交
const handleDialogSubmit = async (formData) => {
  try {
    console.log('收到的formData:', formData)
    console.log('是否编辑模式:', !!currentEditData.value)

    // Normalize incoming formData to backend DTO shape (camelCase)
    const carDTO = {
      carId: formData.carId || formData.CarID,
      tNo: formData.tNo || formData.TNo,
      tName: formData.tName || formData.TName,
      price: formData.price || formData.Price || 0,
      purchaseDate: formData.purchaseDate || formData.PurchaseDate || null,
      carStatus: formData.carStatus || formData.CarStatus || '0',
      runKm: formData.runKm || formData.RUNKM || 0,
      runHour: formData.runHour || formData.RunHour || 0
    }

    console.log('准备提交的carDTO:', carDTO)

    if (currentEditData.value) {
      // 更新车辆
      console.log('执行更新操作')
      await updateCar(carDTO.carId || currentEditData.value.CarID, carDTO)
      ElMessage.success('车辆信息更新成功')
    } else {
      // 创建车辆
      console.log('执行创建操作')
      const result = await createCar(carDTO)
      console.log('创建结果:', result)
      ElMessage.success('车辆创建成功')
    }

    // 关闭对话框并刷新数据
    dialogVisible.value = false
    fetchCars()
  } catch (error) {
    console.error('操作失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
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
      // 尝试解析后端返回的结构化错误信息
      const serverMsg = error?.response?.data?.message || error?.response?.data?.details || error?.response?.data?.error || error?.response?.data || error.message
      const combined = '删除失败: ' + (serverMsg || '未知错误')
      // 先关闭已有提示，确保只显示一次（避免重复多条）
      ElMessage.closeAll()
      ElMessage.error(combined)
      window.__lastGlobalMessage = { text: combined, ts: Date.now() }
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

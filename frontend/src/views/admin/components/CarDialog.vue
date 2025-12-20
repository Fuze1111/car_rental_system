<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑车辆' : '新增车辆'"
    width="600px"
    @close="handleClose"
    destroy-on-close
    class="custom-dialog"
    append-to-body
  >
    <el-form 
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="car-form"
    >
      <el-form-item label="车牌号" prop="CarID">
        <el-input v-model="formData.CarID" placeholder="例如: 京A88888" :disabled="isEdit" class="custom-input" />
      </el-form-item>

      <el-form-item label="车型" prop="TNo">
        <el-select 
          v-model="formData.TNo" 
          placeholder="请选择车型" 
          style="width: 100%"
          @change="handleTypeChange"
          class="custom-select"
        >
          <el-option 
            v-for="item in carTypes" 
            :key="item.TNo" 
            :label="`${item.TNo} (${item.TName})`" 
            :value="item.TNo" 
          />
        </el-select>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="购入价格" prop="Price">
            <el-input-number 
              v-model="formData.Price" 
              :min="0"
              :step="1000"
              style="width: 100%"
              controls-position="right"
              class="custom-input-number"
            >
              <template #prefix>¥</template>
            </el-input-number>
            <!-- 自定义错误提示，小红字显示在价格输入框附近 -->
            <div v-if="priceError" class="field-error-text">{{ priceError }}</div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="购入日期" prop="PurchaseDate">
        <el-date-picker
          v-model="formData.PurchaseDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="disableFutureDate"
          style="width: 100%"
          class="custom-date-picker"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="运行公里" prop="RUNKM">
            <el-input-number 
              v-model="formData.RUNKM" 
              :min="0" 
              :step="100" 
              style="width: 100%"
              controls-position="right"
              class="custom-input-number"
            >
              <template #append>km</template>
            </el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="运行小时" prop="RunHour">
            <el-input-number 
              v-model="formData.RunHour" 
              :min="0" 
              :step="10" 
              style="width: 100%"
              controls-position="right"
              class="custom-input-number"
            >
              <template #append>h</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="车辆状态" prop="CarStatus">
        <el-radio-group v-model="formData.CarStatus" class="custom-radio-group">
          <el-radio-button label="0">可租</el-radio-button>
          <el-radio-button label="2">维修中</el-radio-button>
          <el-radio-button label="1" disabled>已租</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose" class="footer-btn">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading" class="footer-btn submit-btn">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref(null)
const loading = ref(false)
const isEdit = ref(false)
const carTypes = ref([])
// 购入价格错误提示文案
const priceError = ref('')

const formData = reactive({
  CarID: '',
  TNo: '',
  TName: '',
  Price: 0,
  PurchaseDate: '',
  RUNKM: 0,
  RunHour: 0,
  CarStatus: '0'
})

// 禁止选择今天之后的日期（只能选择今天及之前）
const disableFutureDate = (time) => {
  if (!time) return false
  const today = new Date()
  // 只允许选择今天及之前：把今天归零到 00:00，再禁止 >= 明天 00:00 的所有日期
  const tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1)
  return time.getTime() >= tomorrow.getTime()
}

const rules = {
  CarID: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  TNo: [{ required: true, message: '请选择车型', trigger: 'change' }],
  Price: [
    { required: true, message: '请输入购入价格', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const num = Number(value)
        if (!Number.isFinite(num) || num <= 0) {
          priceError.value = '购入价格必须大于 0'
          return callback(new Error('购入价格必须大于 0'))
        }
        priceError.value = ''
        return callback()
      },
      trigger: ['blur', 'change']
    }
  ],
  CarStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取车型列表（管理端：包含数据库中的全部车型）
const fetchCarTypes = async () => {
  try {
    const { getCarTypesAdmin } = await import('@/api/car')
    // getCarTypesAdmin() 返回的已经是经过 axios 拦截器处理过的 data
    const resp = await getCarTypesAdmin()
    const typesRaw = resp || []
    // 容错地从后端返回的对象中读取 tNo/tName（可能是 tNo/tName 或 TNo/TName 或 tno/tname）
    const pick = (obj, ...keys) => {
      for (const k of keys) {
        if (obj && Object.prototype.hasOwnProperty.call(obj, k)) return obj[k]
      }
      // case-insensitive fallback
      if (!obj) return undefined
      const lower = Object.keys(obj).reduce((acc, cur) => { acc[cur.toLowerCase()] = obj[cur]; return acc }, {})
      for (const k of keys) {
        const low = k.toString().toLowerCase()
        if (Object.prototype.hasOwnProperty.call(lower, low)) return lower[low]
      }
      return undefined
    }

    carTypes.value = typesRaw.map(type => {
      return {
        TNo: pick(type, 'tNo', 'TNo', 'tno', 'TNO'),
        TName: pick(type, 'tName', 'TName', 'tname', 'TNAME')
      }
    }).filter(t => t.TNo && t.TName)
  } catch (error) {
    console.error('获取车型列表失败:', error)
    ElMessage.error('获取车型列表失败')
  }
}

const resetForm = () => {
  formData.CarID = ''
  formData.TNo = ''
  formData.TName = ''
  formData.Price = 0
  formData.PurchaseDate = ''
  formData.RUNKM = 0
  formData.RunHour = 0
  formData.CarStatus = '0'
  priceError.value = ''
}

// 监听编辑数据变化，回填表单
watch(() => props.editData, (newVal) => {
  if (newVal) {
    isEdit.value = true
    // 将后端数据映射到前端表单
    formData.CarID = newVal.CarID || newVal.carId || ''
    formData.TNo = newVal.TNo || newVal.tNo || ''
    formData.TName = newVal.TName || newVal.tName || ''
    formData.Price = newVal.Price || newVal.price || 0
    formData.PurchaseDate = newVal.PurchaseDate || newVal.purchaseDate || ''
    formData.RUNKM = newVal.RUNKM || 0
    formData.RunHour = newVal.RunHour || 0
    formData.CarStatus = newVal.CarStatus || newVal.carStatus || '0'
  } else {
    isEdit.value = false
    resetForm()
  }
}, { immediate: true })

// 初始化时获取车型列表
watch(() => props.visible, (newVal) => {
  if (newVal) {
    fetchCarTypes()
  }
}, { immediate: true })

const handleTypeChange = (val) => {
  const type = carTypes.value.find(t => t.TNo === val)
  if (type) {
    formData.TName = type.TName
  }
}

const handleClose = () => {
  emit('update:visible', false)
  resetForm()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 模拟请求延迟
      setTimeout(() => {
        loading.value = false
        // 将前端表单数据转换为后端DTO格式
        const submitData = {
          carId: formData.CarID,
          tNo: formData.TNo,
          tName: formData.TName,
          price: formData.Price,
          purchaseDate: formData.PurchaseDate,
          carStatus: formData.CarStatus,
          runKm: formData.RUNKM,
          runHour: formData.RunHour
        }
        emit('submit', submitData)
        handleClose()
      }, 500)
    }
  })
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
.car-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #475569;
  }
}

/* 购入价格下方的小红字错误提示样式 */
.field-error-text {
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
}

.custom-input,
.custom-select,
.custom-input-number,
.custom-date-picker {
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

.custom-radio-group {
  :deep(.el-radio-button__inner) {
    border-radius: 6px;
    border: 1px solid #e2e8f0;
    box-shadow: none !important;
    margin-right: 8px;
    padding: 8px 20px;
    transition: all 0.2s;
    
    &:hover {
      color: #3b82f6;
      border-color: #3b82f6;
    }
  }
  
  :deep(.el-radio-button:first-child .el-radio-button__inner),
  :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-radius: 6px;
  }
  
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background-color: #3b82f6;
    border-color: #3b82f6;
    box-shadow: none;
    color: white;
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

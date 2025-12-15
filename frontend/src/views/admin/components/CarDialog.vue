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
            :label="`${item.TNo}(${item.TName})`"
            :value="item.TNo"
          />
        </el-select>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="小时租金">
            <!-- 小时租金：对新增操作为只读，由车型表决定；对编辑操作显示为只读值 -->
            <div v-if="isEdit">
              <el-input-number
                v-model="formData.RentRate"
                :min="0"
                :step="10"
                style="width: 100%"
                controls-position="right"
                class="custom-input-number"
                disabled
              >
                <template #prefix>¥</template>
                <template #append>/小时</template>
              </el-input-number>
            </div>
            <div v-else>
              <el-input v-model="displayRent" placeholder="由车型决定" disabled class="custom-input" />
            </div>
          </el-form-item>
        </el-col>
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
import { getCarTypes } from '@/api/car'

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

const formData = reactive({
  CarID: '',
  TNo: '',
  TName: '',
  RentRate: 0,
  Price: 0,
  PurchaseDate: '',
  RUNKM: 0,
  RunHour: 0,
  CarStatus: '0'
})

// 显示用小时租金（只读）
const displayRent = ref('')

// 表单校验规则
const rules = {
  CarID: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { validator: validatePlate, trigger: ['blur', 'change'] }
  ],
  TNo: [{ required: true, message: '请选择车型', trigger: 'change' }],
  CarStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 车牌校验器（支持常见的中国车牌格式）
function validatePlate(rule, value, callback) {
  const v = (value || '').toString().trim()
  if (!v) {
    return callback(new Error('车牌号为必填项'))
  }

  // 简单的中国车牌正则（支持普通北京等: 京A12345，新能源：京A·D12345 等）
  // 说明：这是前端友好校验，后端仍以数据库唯一性和长度为准。
  const normalPlate = /^[\u4e00-\u9fa5][A-Z][A-Z0-9]{5}$/i
  const newEnergyPlate = /^[\u4e00-\u9fa5][A-Z][A-Z0-9]{6}$/i
  const hasDot = v.indexOf('·') >= 0 || v.indexOf('.') >= 0

  if (hasDot) {
    // 带分隔符的新能源/港澳车牌也允许
    return callback()
  }

  if (normalPlate.test(v) || newEnergyPlate.test(v)) {
    return callback()
  }

  // 如果不符合上述规则，提示但不阻止提交（可根据需求改为强制）
  return callback(new Error('车牌号格式不符合预期，例如：京A12345'))
}

// 获取车型列表
const fetchCarTypes = async () => {
  try {
    // 从后端获取车型列表，兼容后端返回字段大小写（TNo/TName 或 tNo/tName）
    const resp = await getCarTypes()
    // 后端可能返回数组或封装对象（如 content 或 value）
    const list = Array.isArray(resp) ? resp : (resp && (resp.content || resp.value) ? (resp.content || resp.value) : [])
    const mapped = (list || []).map(item => ({
      TNo: item.TNo || item.tNo || item.tno || item.t_no || '',
      TName: item.TName || item.tName || item.tname || item.t_name || '',
      RentRate: item.rentRate || item.RentRate || item.Rent || null
    }))
    carTypes.value = mapped
    console.debug('CarDialog fetched carTypes:', mapped)
  } catch (error) {
    console.error('获取车型列表失败:', error)
    ElMessage.error('获取车型列表失败')
  }
}

const resetForm = () => {
  formData.CarID = ''
  formData.TNo = ''
  formData.TName = ''
  formData.RentRate = 0
  formData.Price = 0
  formData.PurchaseDate = ''
  formData.RUNKM = 0
  formData.RunHour = 0
  formData.CarStatus = '0'
}

// 监听编辑数据变化，回填表单
watch(() => props.editData, (newVal) => {
  if (newVal) {
    isEdit.value = true
    // 将后端数据映射到前端表单
    formData.CarID = newVal.CarID || newVal.carId || ''
    formData.TNo = newVal.TNo || newVal.tNo || ''
    formData.TName = newVal.TName || newVal.tName || ''
    formData.RentRate = newVal.RentRate || newVal.rentRate || 0
    // 显示用租金
    displayRent.value = formData.RentRate || ''
    formData.Price = newVal.Price || newVal.price || 0
    formData.PurchaseDate = newVal.PurchaseDate || newVal.purchaseDate || ''
    formData.RUNKM = newVal.RUNKM || 0
    formData.RunHour = newVal.RunHour || 0
    formData.CarStatus = newVal.CarStatus || newVal.carStatus || '0'

    // 如果仅有 TName 而没有 TNo，尝试从后端车型表回填对应的 TNo 与 RentRate
    ;(async () => {
      try {
        if ((!formData.TNo || formData.TNo === '') && formData.TName) {
          // 如果本地 carTypes 缓存为空，先加载
          if (!carTypes.value || carTypes.value.length === 0) {
            await fetchCarTypes()
          }
          const match = carTypes.value.find(t => (t.TName || t.tName || '').toString() === formData.TName.toString())
          if (match) {
            formData.TNo = match.TNo || match.tNo || match.tno || formData.TNo
            formData.TName = match.TName || match.tName || formData.TName
            if (match.RentRate !== undefined && match.RentRate !== null) {
              formData.RentRate = Number(match.RentRate)
              displayRent.value = Number(match.RentRate)
            }
          }
        }
      } catch (e) {
        console.warn('Failed to resolve TNo from TName in CarDialog:', e)
      }
    })()

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
    // 将小时租金用于展示；对于编辑模式仍保留在 formData.RentRate（只读）
    if (type.RentRate !== undefined && type.RentRate !== null) {
      displayRent.value = Number(type.RentRate)
      if (isEdit.value) formData.RentRate = Number(type.RentRate)
    } else {
      displayRent.value = ''
    }
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
      setTimeout(async () => {
        loading.value = false
        // 在提交前：如果选了 TNo 但 TName 为空，则从已加载的 carTypes 回填
        if (formData.TNo && (!formData.TName || formData.TName === '')) {
          const found = carTypes.value.find(t => t.TNo === formData.TNo || t.tNo === formData.TNo)
          if (found) {
            formData.TName = found.TName || found.tName || formData.TName
            // Also set displayRent for UX
            if (found.RentRate !== undefined && found.RentRate !== null) displayRent.value = Number(found.RentRate)
          }
        }

        // 如果选了 TName 但没有 TNo，也尝试回填
        if (formData.TName && (!formData.TNo || formData.TNo === '')) {
          // ensure carTypes loaded
          if (!carTypes.value || carTypes.value.length === 0) await fetchCarTypes()
          const foundByName = carTypes.value.find(t => (t.TName || t.tName || '').toString() === formData.TName.toString())
          if (foundByName) {
            formData.TNo = foundByName.TNo || foundByName.tNo || formData.TNo
            if (foundByName.RentRate !== undefined && foundByName.RentRate !== null) displayRent.value = Number(foundByName.RentRate)
          }
        }

        // 将前端表单数据转换为后端DTO格式
        // NOTE: 不向后端提交 rentRate，后端会根据 tNo/tName 从 car_type 回填租金
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
        console.debug('CarDialog final submitData:', submitData)
        // 额外打印方便问题排查
        if (!submitData.tNo && !submitData.tName) {
          console.warn('CarDialog submit missing both tNo and tName, current formData:', JSON.parse(JSON.stringify(formData)), 'available carTypes:', carTypes.value)
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

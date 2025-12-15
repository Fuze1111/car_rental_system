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
            :label="item.TName" 
            :value="item.TNo" 
          />
        </el-select>
      </el-form-item>

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
import { getAllCars } from '@/api/car'

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

const rules = {
  CarID: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  TNo: [{ required: true, message: '请选择车型', trigger: 'change' }],
  CarStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取车型列表
const fetchCarTypes = async () => {
  try {
<<<<<<< HEAD
    const { getCarTypes } = await import('@/api/car')
    const data = await getCarTypes()
    // 统一字段格式
    carTypes.value = data.map(item => ({
      TNo: item.tNo || item.TNo,
      TName: item.tName || item.TName,
      RentRate: item.rentRate || item.RentRate || 0
=======
    // 从后端获取车型列表，兼容后端返回字段大小写（TNo/TName 或 tNo/tName）
    const resp = await getCarTypes()
    // 后端可能返回数组或封装对象（如 content）
    const list = Array.isArray(resp) ? resp : (resp && resp.content) ? resp.content : []
    carTypes.value = list.map(item => ({
      TNo: item.TNo || item.tNo || item.tno || '',
      TName: item.TName || item.tName || item.tname || '',
      RentRate: item.rentRate || item.RentRate || null
>>>>>>> parent of c083572 (编辑按钮实现 与 新增车辆)
    }))
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
    formData.Price = newVal.Price || newVal.price || 0
    formData.PurchaseDate = newVal.PurchaseDate || newVal.purchaseDate || ''
    formData.RUNKM = newVal.RUNKM || 0
    formData.RunHour = newVal.RunHour || 0
    formData.CarStatus = newVal.CarStatus || newVal.carStatus || '0'
    
    // 租金率：优先使用editData中的值，否则从车型列表中查找
    let rentRate = newVal.RentRate || newVal.rentRate || 0
    if (!rentRate && formData.TNo) {
      const type = carTypes.value.find(t => t.TNo === formData.TNo)
      if (type) {
        rentRate = type.RentRate || 0
      }
    }
    formData.RentRate = rentRate
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
    // 选择车型时，自动填充该车型的租金率
    formData.RentRate = type.RentRate || 0
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
      
      console.log('========== CarDialog提交数据 ==========')
      console.log('formData:', formData)
      console.log('formData.TNo:', formData.TNo)
      console.log('formData.TName:', formData.TName)
      
      // 模拟请求延迟
      setTimeout(() => {
        loading.value = false
        // 将前端表单数据转换为后端DTO格式
        // 注意：不提交rentRate，租金率由车型统一管理
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
<<<<<<< HEAD
        console.log('提交的数据:', submitData)
        console.log('========================================')
        
        emit('submit', submitData)
        handleClose()
      }, 500)
    }
  })
=======
        console.debug('CarDialog final submitData:', submitData)
         emit('submit', submitData)
         handleClose()
       }, 500)
     }
   })
>>>>>>> parent of c083572 (编辑按钮实现 与 新增车辆)
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

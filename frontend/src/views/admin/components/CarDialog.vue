<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑车辆' : '新增车辆'"
    width="600px"
    @close="handleClose"
    destroy-on-close
  >
    <el-form 
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="car-form"
    >
      <el-form-item label="车牌号" prop="CarID">
        <el-input v-model="formData.CarID" placeholder="例如: 京A88888" :disabled="isEdit" />
      </el-form-item>

      <el-form-item label="车型" prop="TNo">
        <el-select 
          v-model="formData.TNo" 
          placeholder="请选择车型" 
          style="width: 100%"
          @change="handleTypeChange"
        >
          <el-option 
            v-for="item in carTypes" 
            :key="item.TNo" 
            :label="item.TName" 
            :value="item.TNo" 
          />
        </el-select>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="日租金" prop="RentRate">
            <el-input-number 
              v-model="formData.RentRate" 
              :min="0" 
              :step="50" 
              style="width: 100%"
              controls-position="right"
            >
              <template #prefix>¥</template>
            </el-input-number>
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
        />
      </el-form-item>

      <el-form-item label="车辆状态" prop="CarStatus">
        <el-radio-group v-model="formData.CarStatus">
          <el-radio-button label="0">可租</el-radio-button>
          <el-radio-button label="2">维修中</el-radio-button>
          <el-radio-button label="1" disabled>已租</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
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

// Mock 车型数据
const carTypes = [
  { TNo: 'T001', TName: 'Toyota Camry' },
  { TNo: 'T002', TName: 'Tesla Model 3' },
  { TNo: 'T003', TName: 'Benz S-Class' },
  { TNo: 'T004', TName: 'BMW X5' },
]

const formData = reactive({
  CarID: '',
  TNo: '',
  TName: '',
  RentRate: 0,
  Price: 0,
  PurchaseDate: '',
  CarStatus: '0'
})

const rules = {
  CarID: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  TNo: [{ required: true, message: '请选择车型', trigger: 'change' }],
  RentRate: [{ required: true, message: '请输入日租金', trigger: 'blur' }],
  CarStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const resetForm = () => {
  formData.CarID = ''
  formData.TNo = ''
  formData.TName = ''
  formData.RentRate = 0
  formData.Price = 0
  formData.PurchaseDate = ''
  formData.CarStatus = '0'
}

// 监听编辑数据变化，回填表单
watch(() => props.editData, (newVal) => {
  if (newVal) {
    isEdit.value = true
    Object.assign(formData, newVal)
  } else {
    isEdit.value = false
    resetForm()
  }
}, { immediate: true })

const handleTypeChange = (val) => {
  const type = carTypes.find(t => t.TNo === val)
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
        emit('submit', { ...formData })
        // ElMessage.success(isEdit.value ? '修改成功' : '新增成功') // 让父组件处理提示
        handleClose()
      }, 500)
    }
  })
}
</script>

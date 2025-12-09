<template>
  <el-dialog
    :model-value="visible"
    title="车辆送修"
    width="500px"
    @close="handleClose"
    destroy-on-close
    custom-class="maintenance-dialog"
  >
    <el-form 
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="maintenance-form"
    >
      <el-form-item label="车牌号">
        <el-input v-model="formData.CarID" disabled />
      </el-form-item>

      <el-form-item label="维修日期" prop="RDate">
        <el-date-picker
          v-model="formData.RDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="运行里程" prop="RunKM">
        <el-input-number 
          v-model="formData.RunKM" 
          :min="0" 
          :step="100" 
          style="width: 100%"
          controls-position="right"
        >
          <template #suffix>km</template>
        </el-input-number>
      </el-form-item>

      <el-form-item label="运行时间" prop="RunHour">
        <el-input-number 
          v-model="formData.RunHour" 
          :min="0" 
          :step="10" 
          style="width: 100%"
          controls-position="right"
        >
          <template #suffix>h</template>
        </el-input-number>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确认送修
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  carData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  CarID: '',
  RDate: new Date().toISOString().split('T')[0],
  RunKM: 0,
  RunHour: 0
})

const rules = {
  RDate: [{ required: true, message: '请选择维修日期', trigger: 'change' }],
  RunKM: [{ required: true, message: '请输入运行里程', trigger: 'blur' }],
  RunHour: [{ required: true, message: '请输入运行时间', trigger: 'blur' }]
}

watch(() => props.carData, (newVal) => {
  if (newVal) {
    formData.CarID = newVal.CarID
    // Initialize other fields if needed, maybe fetch last recorded KM?
    // For now, reset them
    formData.RDate = new Date().toISOString().split('T')[0]
    formData.RunKM = 0
    formData.RunHour = 0
  }
}, { immediate: true })

const handleClose = () => {
  emit('update:visible', false)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // Simulate API call
      setTimeout(() => {
        loading.value = false
        emit('submit', { ...formData })
        handleClose()
      }, 500)
    }
  })
}
</script>

<style scoped>
.maintenance-form {
  padding: 20px 0;
}
</style>

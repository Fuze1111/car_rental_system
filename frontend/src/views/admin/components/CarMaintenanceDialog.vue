<template>
  <el-dialog
    :model-value="visible"
    title="车辆送修"
    width="500px"
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
      class="maintenance-form"
    >
      <el-form-item label="车牌号">
        <el-input v-model="formData.CarID" disabled />
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
        <el-button @click="handleClose" class="footer-btn">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading" class="footer-btn submit-btn">
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
  RunKM: 0,
  RunHour: 0
})

const rules = {
  RunKM: [{ required: true, message: '请输入运行里程', trigger: 'blur' }],
  RunHour: [{ required: true, message: '请输入运行时间', trigger: 'blur' }]
}

watch(() => props.carData, (newVal) => {
  if (newVal) {
    formData.CarID = newVal.CarID
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
      try {
        emit('submit', { ...formData })
        handleClose()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.maintenance-form {
  padding: 20px 0;
}

.custom-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;

  :deep(.el-dialog__header) {
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

  :deep(.el-dialog__body) {
    padding: 32px 24px !important;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 24px;
    border-top: 1px solid #f1f5f9;
    background: #f8fafc;
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

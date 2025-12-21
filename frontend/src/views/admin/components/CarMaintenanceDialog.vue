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
      label-width="100px"
      class="maintenance-form"
    >
      <el-form-item label="车牌号">
        <el-input v-model="formData.CarID" disabled />
      </el-form-item>
      <!-- 维修日期输入框已移除，日期在脚本中自动设置为当前日期并随表单提交 -->
      <!-- 运行里程和运行时间字段保留在 formData 中用于提交，但不在弹窗中展示输入控件 -->
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
import { ElMessage } from 'element-plus'

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

// 记录当前车辆的原始里程和小时数，用于校验最小值
const minRunKM = ref(0)
const minRunHour = ref(0)

watch(
  () => props.carData,
  (newVal) => {
    if (newVal) {
      formData.CarID = newVal.CarID
      // 记录当前车辆的里程和小时数作为最小值
      // 兼容各种可能的字段名：RUNKM (全大写), RunKM (帕斯卡), runKm (驼峰)
      minRunKM.value = Number(newVal.RUNKM || newVal.RunKM || newVal.runKm || 0)
      minRunHour.value = Number(newVal.RunHour || newVal.runHour || 0)
      // 默认填充值为当前里程/时间，方便直接送修
      formData.RunKM = minRunKM.value
      formData.RunHour = minRunHour.value
      // 每次打开弹窗时，将维修日期重置为当天
      formData.RDate = new Date().toISOString().split('T')[0]
    }
  },
  { immediate: true }
)

const handleClose = () => {
  emit('update:visible', false)
}

const handleSubmit = async () => {
  // 不再依赖 Element Plus 表单校验，直接进行业务校验
  // 二次校验，确保数据逻辑正确
  if (formData.RunKM < minRunKM.value) {
    ElMessage.error(`运行里程不能小于当前车辆里程（${minRunKM.value} km）`)
    return
  }
  if (formData.RunHour < minRunHour.value) {
    ElMessage.error(`运行时间不能小于当前车辆运行时间（${minRunHour.value} h）`)
    return
  }

  loading.value = true
  try {
    // emit 到父组件；父组件负责调用 API
    emit('submit', { ...formData })
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
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

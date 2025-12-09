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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="formData.brand" placeholder="例如: Toyota" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="型号" prop="model">
            <el-input v-model="formData.model" placeholder="例如: Camry" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="车牌号" prop="plateNumber">
        <el-input v-model="formData.plateNumber" placeholder="请输入车牌号" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="车辆类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
              <el-option label="轿车" value="轿车" />
              <el-option label="SUV" value="SUV" />
              <el-option label="MPV" value="MPV" />
              <el-option label="跑车" value="跑车" />
              <el-option label="电动车" value="电动车" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="日租金" prop="dailyRate">
            <el-input-number 
              v-model="formData.dailyRate" 
              :min="0" 
              :step="50" 
              style="width: 100%"
              controls-position="right"
            >
              <template #prefix>¥</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="车辆状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio-button label="available">空闲</el-radio-button>
          <el-radio-button label="maintenance">维修中</el-radio-button>
          <el-radio-button label="rented" disabled>已租出</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="车辆图片" prop="image">
        <!-- 模拟图片上传，暂时使用 URL 输入 -->
        <div class="image-upload-container">
          <el-input v-model="formData.image" placeholder="请输入图片 URL (模拟上传)" />
          <div class="preview-box" v-if="formData.image">
            <el-image :src="formData.image" fit="cover" class="preview-img" />
          </div>
          <div class="upload-placeholder" v-else>
            <el-icon class="upload-icon"><Plus /></el-icon>
            <span>粘贴 URL 预览</span>
          </div>
        </div>
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
import { Plus } from '@element-plus/icons-vue'
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

const formData = reactive({
  brand: '',
  model: '',
  plateNumber: '',
  type: '',
  dailyRate: 0,
  status: 'available',
  image: ''
})

const rules = {
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  dailyRate: [{ required: true, message: '请输入日租金', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 监听编辑数据变化，回填表单
watch(() => props.editData, (newVal) => {
  if (newVal) {
    isEdit.value = true
    Object.assign(formData, newVal)
  } else {
    isEdit.value = false
    // 重置表单
    resetForm()
  }
}, { immediate: true })

const resetForm = () => {
  formData.brand = ''
  formData.model = ''
  formData.plateNumber = ''
  formData.type = ''
  formData.dailyRate = 0
  formData.status = 'available'
  formData.image = ''
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
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        handleClose()
      }, 500)
    }
  })
}
</script>

<style scoped lang="scss">
.image-upload-container {
  width: 100%;
  
  .preview-box {
    margin-top: 10px;
    width: 200px;
    height: 120px;
    border-radius: 4px;
    overflow: hidden;
    border: 1px solid #dcdfe6;
    
    .preview-img {
      width: 100%;
      height: 100%;
    }
  }
  
  .upload-placeholder {
    margin-top: 10px;
    width: 200px;
    height: 120px;
    background: #f5f7fa;
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 12px;
    
    .upload-icon {
      font-size: 24px;
      margin-bottom: 5px;
    }
  }
}
</style>

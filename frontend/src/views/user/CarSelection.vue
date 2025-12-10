<template>
  <div class="car-selection-page">
    <div class="content-wrapper">
      <el-card class="main-card" shadow="never">
        <!-- 页面标题 -->
        <div class="section-header">
          <div class="header-left">
            <h3>租车大厅</h3>
            <el-tag type="primary" effect="light" round class="count-tag">{{ filteredCars.length }} 辆车待租</el-tag>
          </div>
        </div>

        <!-- 过滤器 -->
        <div class="filter-bar">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索品牌或车型..." 
            prefix-icon="Search"
            class="search-input"
            clearable
          />
          
          <el-select v-model="filterType" placeholder="车辆类型" clearable style="width: 160px">
            <el-option label="轿车" value="sedan" />
            <el-option label="SUV" value="suv" />
            <el-option label="跑车" value="sports" />
            <el-option label="商务车" value="mpv" />
          </el-select>

          <el-select v-model="filterSort" placeholder="排序方式" style="width: 160px">
            <el-option label="价格: 低到高" value="price_asc" />
            <el-option label="价格: 高到低" value="price_desc" />
          </el-select>
        </div>

        <!-- 车辆网格 -->
        <div class="cars-grid" v-loading="loading">
          <div class="car-card" v-for="car in filteredCars" :key="car.id">
            <div class="car-image">
              <el-image :src="car.image" fit="cover" loading="lazy" />
              <div class="car-tag">{{ car.type }}</div>
            </div>
            <div class="car-info">
              <div class="info-header">
                <h3 class="car-name">{{ car.brand }} {{ car.model }}</h3>
                <div class="car-price">
                  <span class="currency">¥</span>
                  <span class="amount">{{ car.price }}</span>
                  <span class="unit">/日</span>
                </div>
              </div>
              
              <div class="car-specs">
                <span class="spec-item"><el-icon><info-filled /></el-icon> {{ car.seats }}座</span>
                <span class="spec-item"><el-icon><cpu /></el-icon> {{ car.transmission }}</span>
                <span class="spec-item"><el-icon><van /></el-icon> {{ car.fuel }}</span>
              </div>

              <el-button type="primary" class="rent-btn" round @click="openRentDialog(car)">
                立即预订
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 租车弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="预订车辆"
      width="500px"
      destroy-on-close
      class="custom-dialog"
    >
      <div class="dialog-content" v-if="selectedCar">
        <div class="selected-car-summary">
          <img :src="selectedCar.image" class="summary-img" />
          <div class="summary-info">
            <h4>{{ selectedCar.brand }} {{ selectedCar.model }}</h4>
            <p class="price">¥{{ selectedCar.price }} / 日</p>
          </div>
        </div>

        <el-form :model="rentForm" label-position="top" class="rent-form">
          <el-form-item label="取还时间">
            <el-date-picker
              v-model="rentForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="取车日期"
              end-placeholder="还车日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled-date="disablePastDate"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="取车地点">
                <el-select v-model="rentForm.loanPlace" filterable allow-create placeholder="选择或输入">
                  <el-option value="北京大兴国际机场" />
                  <el-option value="北京首都国际机场" />
                  <el-option value="上海虹桥国际机场" />
                  <el-option value="上海浦东国际机场" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="还车地点">
                <el-select v-model="rentForm.returnPlace" filterable allow-create placeholder="选择或输入">
                  <el-option value="北京大兴国际机场" />
                  <el-option value="北京首都国际机场" />
                  <el-option value="上海虹桥国际机场" />
                  <el-option value="上海浦东国际机场" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <!-- 费用计算 -->
          <div class="cost-breakdown" v-if="totalDays > 0">
            <div class="cost-row">
              <span>租金 ({{ totalDays }}天)</span>
              <span>¥{{ totalRent }}</span>
            </div>
            <div class="cost-row">
              <span>基础服务费</span>
              <span>¥50</span>
            </div>
            <div class="cost-row total">
              <span>总计预估</span>
              <span class="total-price">¥{{ totalAmount }}</span>
            </div>
          </div>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitting">确认提交订单</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { Search, InfoFilled, Cpu, Van } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const searchKeyword = ref('')
const filterType = ref('')
const filterSort = ref('price_asc')
const dialogVisible = ref(false)
const selectedCar = ref(null)
const submitting = ref(false)

const rentForm = reactive({
  dateRange: [],
  loanPlace: '',
  returnPlace: ''
})

// Mock Car Data
const cars = ref([
  { id: 1, brand: 'Toyota', model: 'Camry', type: '轿车', price: 200, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&q=80&w=400' },
  { id: 2, brand: 'Tesla', model: 'Model 3', type: '轿车', price: 350, seats: 5, transmission: '自动', fuel: '电动', image: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&q=80&w=400' },
  { id: 3, brand: 'Mercedes', model: 'C-Class', type: '轿车', price: 450, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&q=80&w=400' },
  { id: 4, brand: 'BMW', model: 'X5', type: 'SUV', price: 600, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1555215695-3004980adade?auto=format&fit=crop&q=80&w=400' },
  { id: 5, brand: 'Ford', model: 'Mustang', type: '跑车', price: 550, seats: 4, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?auto=format&fit=crop&q=80&w=400' },
  { id: 6, brand: 'Honda', model: 'Odyssey', type: 'MPV', price: 400, seats: 7, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1622340767794-5412803c7344?auto=format&fit=crop&q=80&w=400' }
])

// Filtering
const filteredCars = computed(() => {
  let result = cars.value.filter(car => {
    const matchKeyword = !searchKeyword.value || 
      car.brand.toLowerCase().includes(searchKeyword.value.toLowerCase()) || 
      car.model.toLowerCase().includes(searchKeyword.value.toLowerCase())
      
    // Fix type matching logic
    if (filterType.value) {
      const typeMap = { 'sedan': '轿车', 'suv': 'SUV', 'sports': '跑车', 'mpv': 'MPV' }
      if (car.type !== typeMap[filterType.value]) return false
    }
    return matchKeyword
  })

  // Sorting
  result.sort((a, b) => {
    if (filterSort.value === 'price_asc') return a.price - b.price
    if (filterSort.value === 'price_desc') return b.price - a.price
    return 0
  })

  return result
})

const disablePastDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const totalDays = computed(() => {
  if (!rentForm.dateRange || rentForm.dateRange.length < 2) return 0
  const start = new Date(rentForm.dateRange[0])
  const end = new Date(rentForm.dateRange[1])
  const diffTime = Math.abs(end - start)
  const days = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return days + 1 // Inclusive
})

const totalRent = computed(() => {
  return selectedCar.value ? selectedCar.value.price * totalDays.value : 0
})

const totalAmount = computed(() => {
  return totalRent.value + 50
})

const openRentDialog = (car) => {
  selectedCar.value = car
  rentForm.dateRange = []
  rentForm.loanPlace = ''
  rentForm.returnPlace = ''
  dialogVisible.value = true
}

const submitOrder = () => {
  if (!rentForm.dateRange || !rentForm.dateRange.length) {
    ElMessage.warning('请选择租车时间')
    return
  }
  if (!rentForm.loanPlace || !rentForm.returnPlace) {
    ElMessage.warning('请填写取还地点')
    return
  }

  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    dialogVisible.value = false
    ElMessage.success('订单提交成功！请前往个人中心查看')
  }, 1000)
}
</script>

<style scoped lang="scss">
.car-selection-page {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.main-card {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.02);
  
  :deep(.el-card__body) {
    padding: 32px;
  }
}

.section-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 700;
      color: #334155;
      padding-left: 12px;
      border-left: 4px solid #3b82f6;
    }
  }
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  align-items: center;

  .search-input {
    width: 260px;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 50px;
    background: rgba(248, 250, 252, 0.6);
    box-shadow: none;
    border: 1px solid rgba(0,0,0,0.05);
    padding-left: 16px;
    transition: all 0.3s;
    
    &:hover, &.is-focus {
      background: white;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    }
  }
}

.cars-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.car-card {
  background: rgba(255,255,255,0.6);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid rgba(255,255,255,0.5);

  &:hover {
    transform: translateY(-5px);
    background: white;
    box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  }

  .car-image {
    height: 180px;
    position: relative;
    
    .el-image {
      width: 100%;
      height: 100%;
    }

    .car-tag {
      position: absolute;
      top: 12px;
      left: 12px;
      background: rgba(30, 41, 59, 0.8);
      color: white;
      padding: 4px 10px;
      border-radius: 6px;
      font-size: 12px;
      backdrop-filter: blur(4px);
      font-weight: 600;
    }
  }

  .car-info {
    padding: 20px;

    .info-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;
      
      .car-name {
        margin: 0;
        font-size: 16px;
        color: #1e293b;
        font-weight: 700;
      }

      .car-price {
        color: #ef4444;
        font-weight: 700;
        .currency { font-size: 12px; }
        .amount { font-size: 18px; }
        .unit { font-size: 12px; color: #94a3b8; font-weight: 400; }
      }
    }

    .car-specs {
      display: flex;
      gap: 8px;
      margin-bottom: 20px;
      
      .spec-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #64748b;
        background: rgba(241, 245, 249, 0.8);
        padding: 4px 8px;
        border-radius: 6px;
      }
    }

    .rent-btn {
      width: 100%;
      height: 40px;
      font-weight: 600;
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
    }
  }
}

.selected-car-summary {
  display: flex;
  gap: 16px;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 24px;
  align-items: center;
  border: 1px solid #f1f5f9;

  .summary-img {
    width: 80px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }

  .summary-info {
    h4 { margin: 0 0 4px 0; font-size: 16px; color: #1e293b; }
    .price { margin: 0; color: #ef4444; font-weight: 600; }
  }
}

.cost-breakdown {
  background: #fdf2f2;
  padding: 16px;
  border-radius: 8px;
  margin-top: 24px;
  border: 1px solid #fecaca;

  .cost-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-size: 14px;
    color: #64748b;

    &.total {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px dashed rgba(239, 68, 68, 0.2);
      font-weight: 700;
      color: #1e293b;
      margin-bottom: 0;
      
      .total-price {
        color: #ef4444;
        font-size: 18px;
      }
    }
  }
}
</style>

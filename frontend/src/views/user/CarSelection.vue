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
          <div class="filter-row">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索品牌或车型..." 
              prefix-icon="Search"
              class="search-input"
              clearable
            />
            
            <div class="filter-group sort-group">
              <span class="filter-label">排序</span>
              <el-radio-group v-model="filterSort" size="small" class="custom-radio-group">
                <el-radio-button label="price_asc">价格低到高 <el-icon><ArrowUp /></el-icon></el-radio-button>
                <el-radio-button label="price_desc">价格高到低 <el-icon><ArrowDown /></el-icon></el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <div class="filter-row type-row">
            <span class="filter-label">车辆档次</span>
            <div class="type-options">
              <div 
                class="type-chip" 
                :class="{ active: filterType === '' }"
                @click="filterType = ''"
              >
                全部
              </div>
              <div 
                class="type-chip" 
                :class="{ active: filterType === 'economic' }"
                @click="filterType = 'economic'"
              >
                经济型 (≤300)
              </div>
              <div 
                class="type-chip" 
                :class="{ active: filterType === 'comfort' }"
                @click="filterType = 'comfort'"
              >
                舒适型 (300-500)
              </div>
              <div 
                class="type-chip" 
                :class="{ active: filterType === 'luxury' }"
                @click="filterType = 'luxury'"
              >
                豪华型 (>500)
              </div>
            </div>
          </div>
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
      width="900px"
      destroy-on-close
      class="booking-dialog"
      append-to-body
      @opened="handleDialogOpened"
    >
      <div class="detail-container" v-if="selectedCar">
        <!-- 左侧：预订信息 -->
        <div class="detail-info">
          <!-- 车辆信息块 -->
          <div class="info-block">
            <h4 class="block-title">车辆信息</h4>
            <div class="car-overview">
              <img :src="selectedCar.image" class="car-thumb" />
              <div class="car-meta">
                <h3 class="car-name">{{ selectedCar.brand }} {{ selectedCar.model }}</h3>
                <div class="car-tags">
                  <el-tag size="small" effect="plain">{{ selectedCar.type }}</el-tag>
                  <el-tag size="small" type="info" effect="plain">{{ selectedCar.seats }}座</el-tag>
                  <el-tag size="small" type="info" effect="plain">{{ selectedCar.transmission }}</el-tag>
                </div>
                <div class="price-display">
                  <span class="label">日租金</span>
                  <span class="price">¥{{ selectedCar.price }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 租期选择块 -->
          <div class="info-block">
            <h4 class="block-title">租期选择</h4>
            <el-date-picker
              v-model="rentForm.dateRange"
              type="datetimerange"
              range-separator="→"
              start-placeholder="取车时间"
              end-placeholder="还车时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
              :disabled-date="disablePastDate"
              class="booking-date-picker"
            />
          </div>

          <!-- 行程设置块 -->
          <div class="info-block">
            <h4 class="block-title">行程设置</h4>
            
            <div class="timeline-form">
              <!-- 取车节点 -->
              <div class="timeline-item">
                <div class="node-left">
                  <div class="dot start"></div>
                  <div class="line"></div>
                </div>
                <div class="node-content">
                  <div class="content-header">
                    <span class="label">取车 Pick-up</span>
                    <span class="time" v-if="rentForm.dateRange?.[0]">{{ rentForm.dateRange[0] }}</span>
                  </div>
                  <div class="form-row">
                    <el-select 
                      v-model="loanCity" 
                      placeholder="城市" 
                      class="mini-select city"
                      @change="handleLoanCityChange"
                    >
                      <el-option v-for="city in availableCities" :key="city" :label="city" :value="city" />
                    </el-select>
                    <el-select 
                      v-model="rentForm.loanPlace" 
                      placeholder="请选择取车门店" 
                      class="mini-select store"
                      :disabled="!loanCity"
                      @change="updateMap"
                    >
                      <el-option 
                        v-for="store in loanStores" 
                        :key="store.StoreNo" 
                        :label="store.StoreName" 
                        :value="store.StoreName" 
                      />
                    </el-select>
                  </div>
                </div>
              </div>

              <!-- 还车节点 -->
              <div class="timeline-item">
                <div class="node-left">
                  <div class="dot end"></div>
                </div>
                <div class="node-content">
                  <div class="content-header">
                    <span class="label">还车 Return</span>
                    <span class="time" v-if="rentForm.dateRange?.[1]">{{ rentForm.dateRange[1] }}</span>
                  </div>
                  <div class="form-row">
                    <el-select 
                      v-model="returnCity" 
                      placeholder="城市" 
                      class="mini-select city"
                      @change="handleReturnCityChange"
                    >
                      <el-option v-for="city in availableCities" :key="city" :label="city" :value="city" />
                    </el-select>
                    <el-select 
                      v-model="rentForm.returnPlace" 
                      placeholder="请选择还车门店" 
                      class="mini-select store"
                      :disabled="!returnCity"
                      @change="updateMap"
                    >
                      <el-option 
                        v-for="store in returnStores" 
                        :key="store.StoreNo" 
                        :label="store.StoreName" 
                        :value="store.StoreName" 
                      />
                    </el-select>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 额外服务块 -->
          <div class="info-block">
            <h4 class="block-title">额外服务</h4>
            <div 
              class="service-card" 
              :class="{ active: rentForm.needDriver }"
              @click="rentForm.needDriver = !rentForm.needDriver"
            >
              <div class="service-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="service-info">
                <div class="service-name">司机送车服务</div>
                <div class="service-desc">专业司机为您送车上门 / 代驾服务</div>
              </div>
              <div class="service-price">
                <span class="price">¥200</span>
                <span class="unit">/天</span>
                <el-switch v-model="rentForm.needDriver" @click.stop />
              </div>
            </div>
          </div>

          <!-- 费用明细块 -->
          <div class="info-block" v-if="totalDays > 0">
            <h4 class="block-title">费用明细</h4>
            <div class="cost-details">
              <div class="cost-row">
                <span class="label">租期天数</span>
                <span class="value">{{ totalDays }} 天</span>
              </div>
              <div class="cost-row">
                <span class="label">车辆租金</span>
                <span class="value">¥{{ selectedCar.price }} × {{ totalDays }} = ¥{{ totalRent }}</span>
              </div>
              <div class="cost-row" v-if="rentForm.needDriver">
                <span class="label">司机服务费</span>
                <span class="value">¥200 × {{ totalDays }} = ¥{{ driverFee }}</span>
              </div>
              <div class="cost-row">
                <span class="label">服务费</span>
                <span class="value">¥50</span>
              </div>
              <div class="cost-divider"></div>
              <div class="cost-row total">
                <span class="label">预计总额</span>
                <span class="value amount">¥{{ totalAmount }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：地图预览 -->
        <div class="detail-map">
          <div id="booking-map" class="map-container">
            <div class="map-placeholder" v-if="!hasMapKey || !rentForm.loanPlace || !rentForm.returnPlace">
              <el-icon class="map-icon"><LocationFilled /></el-icon>
              <p>{{ !rentForm.loanPlace || !rentForm.returnPlace ? '请先选择取还门店' : '地图预览区域' }}</p>
              <span class="sub" v-if="rentForm.loanPlace && rentForm.returnPlace">正在加载地图...</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer-content">
          <div class="footer-tip">
            <el-icon><InfoFilled /></el-icon>
            订单提交后请前往个人中心支付
          </div>
          <div class="footer-buttons">
            <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
            <el-button 
              type="primary" 
              @click="submitOrder" 
              :loading="submitting" 
              class="confirm-btn"
              :disabled="!isFormValid"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { Search, InfoFilled, Cpu, Van, Right, LocationFilled, User, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAllCars } from '@/api/car'

const loading = ref(false)
const searchKeyword = ref('')
const filterType = ref('')
const filterSort = ref('price_asc')
const dialogVisible = ref(false)
const selectedCar = ref(null)
const submitting = ref(false)
const hasMapKey = ref(false)
const loanCity = ref('')
const returnCity = ref('')

const rentForm = reactive({
  dateRange: [],
  loanPlace: '',
  returnPlace: '',
  needDriver: false
})

// Mock Store Data
const stores = [
  { StoreNo: 'S001', City: '北京市', StoreName: '北京首都国际机场', Address: '北京市顺义区首都国际机场T3航站楼停车楼' },
  { StoreNo: 'S002', City: '北京市', StoreName: '北京大兴国际机场', Address: '北京市大兴区大兴国际机场停车楼' },
  { StoreNo: 'S003', City: '上海市', StoreName: '上海虹桥国际机场', Address: '上海市长宁区虹桥国际机场T2航站楼' },
  { StoreNo: 'S004', City: '上海市', StoreName: '上海浦东国际机场', Address: '上海市浦东新区浦东国际机场P1停车库' },
  { StoreNo: 'S005', City: '广州市', StoreName: '广州白云国际机场', Address: '广州市白云区白云国际机场T2航站楼' }
]

const availableCities = computed(() => [...new Set(stores.map(s => s.City))])

const loanStores = computed(() => {
  return stores.filter(s => s.City === loanCity.value)
})

const returnStores = computed(() => {
  return stores.filter(s => s.City === returnCity.value)
})

// Car Data from API
const cars = ref([])

// 根据车型名称推断车辆信息
const getCarInfoFromTName = (tName) => {
  const info = {
    brand: '未知品牌',
    model: tName || '未知型号',
    seats: 5,
    transmission: '自动',
    fuel: '汽油'
  }
  
  if (tName) {
    // 从车型名称中提取品牌信息
    if (tName.includes('丰田')) info.brand = '丰田'
    else if (tName.includes('本田')) info.brand = '本田'
    else if (tName.includes('奔驰')) info.brand = '奔驰'
    else if (tName.includes('宝马')) info.brand = '宝马'
    else if (tName.includes('奥迪')) info.brand = '奥迪'
    else if (tName.includes('大众')) info.brand = '大众'
    else if (tName.includes('特斯拉')) info.brand = '特斯拉'
    else if (tName.includes('福特')) info.brand = '福特'
    
    // 根据车型推断座位数
    if (tName.includes('MPV') || tName.includes('商务车')) info.seats = 7
    else if (tName.includes('跑车')) info.seats = 4
    else if (tName.includes('SUV')) info.seats = 5
    
    // 根据车型推断燃油类型
    if (tName.includes('电动') || tName.includes('新能源')) info.fuel = '电动'
  }
  
  return info
}

// Load cars data from API
const loadCars = async () => {
  loading.value = true
  try {
    const response = await getAllCars()
    // 转换API返回的数据格式，确保与前端兼容
    cars.value = response.map(car => {
      const carInfo = getCarInfoFromTName(car.tName)
      return {
        id: car.carId,
        brand: carInfo.brand,
        model: carInfo.model,
        type: car.tName || '轿车',
        price: car.rentRate ? Number(car.rentRate) * 8 : 200, // 租金率转换为日租金（假设8小时/天）
        seats: carInfo.seats,
        transmission: carInfo.transmission,
        fuel: carInfo.fuel,
        image: `https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&q=80&w=400&text=${encodeURIComponent(car.tName || '汽车')}`
      }
    })
  } catch (error) {
    console.error('加载车辆数据失败:', error)
    ElMessage.error('加载车辆数据失败，请稍后重试')
    // 如果API调用失败，使用备用mock数据
    cars.value = [
      { id: '京A12345', brand: '丰田', model: '凯美瑞', type: '轿车', price: 240, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1621007947382-bb3c3968e3bb?auto=format&fit=crop&q=80&w=400' },
      { id: '京B67890', brand: '本田', model: '雅阁', type: '轿车', price: 280, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&q=80&w=400' },
      { id: '京C11111', brand: '奔驰', model: 'E级', type: '轿车', price: 480, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&q=80&w=400' },
      { id: '京D22222', brand: '宝马', model: '5系', type: '轿车', price: 560, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1555215695-3004980adade?auto=format&fit=crop&q=80&w=400' },
      { id: '京E33333', brand: '奥迪', model: 'A6L', type: '轿车', price: 440, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?auto=format&fit=crop&q=80&w=400' },
      { id: '京F44444', brand: '大众', model: '帕萨特', type: '轿车', price: 200, seats: 5, transmission: '自动', fuel: '汽油', image: 'https://images.unsplash.com/photo-1622340767794-5412803c7344?auto=format&fit=crop&q=80&w=400' }
    ]
  } finally {
    loading.value = false
  }
}

// Load cars when component mounted
onMounted(() => {
  loadCars()
})

// Filtering
const filteredCars = computed(() => {
  let result = cars.value.filter(car => {
    const matchKeyword = !searchKeyword.value || 
      car.brand.toLowerCase().includes(searchKeyword.value.toLowerCase()) || 
      car.model.toLowerCase().includes(searchKeyword.value.toLowerCase())
      
    // Price Level Filtering
    if (filterType.value) {
      if (filterType.value === 'economic') {
        if (car.price > 300) return false
      } else if (filterType.value === 'comfort') {
        if (car.price <= 300 || car.price > 500) return false
      } else if (filterType.value === 'luxury') {
        if (car.price <= 500) return false
      }
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
  return days // Precise days might be fractional if we care about hours, but usually daily rate ceil
})

const totalRent = computed(() => {
  return selectedCar.value ? Math.ceil(selectedCar.value.price * totalDays.value) : 0
})

const driverFee = computed(() => {
  return rentForm.needDriver ? 200 * totalDays.value : 0
})

const totalAmount = computed(() => {
  return totalRent.value + 50 + driverFee.value
})

const isFormValid = computed(() => {
  return rentForm.dateRange && rentForm.dateRange.length === 2 && rentForm.loanPlace && rentForm.returnPlace
})

const openRentDialog = (car) => {
  selectedCar.value = car
  rentForm.dateRange = []
  rentForm.loanPlace = ''
  rentForm.returnPlace = ''
  rentForm.needDriver = false
  loanCity.value = ''
  returnCity.value = ''
  dialogVisible.value = true
  hasMapKey.value = false
}

const handleDialogOpened = () => {
  hasMapKey.value = typeof AMap !== 'undefined'
}

const handleLoanCityChange = () => {
  rentForm.loanPlace = ''
  // 默认联动：如果还车城市为空，则跟随取车城市
  if (!returnCity.value) {
    returnCity.value = loanCity.value
    // 还车门店不自动选，还是让用户选比较好，或者可以选第一个
    rentForm.returnPlace = ''
  }
}

const handleReturnCityChange = () => {
  rentForm.returnPlace = ''
}

const updateMap = () => {
  if (!rentForm.loanPlace || !rentForm.returnPlace) return
  
  nextTick(() => {
    initMap()
  })
}

const initMap = () => {
  if (typeof AMap === 'undefined') {
    hasMapKey.value = false
    return
  }
  hasMapKey.value = true
  
  const startStore = stores.find(s => s.StoreName === rentForm.loanPlace)
  const endStore = stores.find(s => s.StoreName === rentForm.returnPlace)
  
  if (!startStore || !endStore) return

  AMap.plugin(['AMap.Geocoder', 'AMap.Polyline'], function() {
    const geocoder = new AMap.Geocoder()
    
    const map = new AMap.Map('booking-map', {
      zoom: 11,
      viewMode: '3D'
    })

    // 解析取车地址
    const p1 = new Promise((resolve) => {
      geocoder.getLocation(startStore.Address, (status, result) => {
        if (status === 'complete' && result.geocodes.length) {
          resolve(result.geocodes[0].location)
        } else {
          console.warn('取车地址解析失败，使用默认坐标')
          resolve(new AMap.LngLat(116.397428, 39.90923))
        }
      })
    })

    // 解析还车地址
    const p2 = new Promise((resolve) => {
      geocoder.getLocation(endStore.Address, (status, result) => {
        if (status === 'complete' && result.geocodes.length) {
          resolve(result.geocodes[0].location)
        } else {
          console.warn('还车地址解析失败，使用默认坐标')
          resolve(new AMap.LngLat(116.46, 39.92))
        }
      })
    })

    Promise.all([p1, p2]).then(([startLngLat, endLngLat]) => {
      const startMarker = new AMap.Marker({
        position: startLngLat,
        title: startStore.StoreName,
        label: { content: '取车: ' + startStore.StoreName, direction: 'top' }
      })
      
      const endMarker = new AMap.Marker({
        position: endLngLat,
        title: endStore.StoreName,
        label: { content: '还车: ' + endStore.StoreName, direction: 'top' }
      })

      const polyline = new AMap.Polyline({
        path: [startLngLat, endLngLat],
        strokeColor: "#3b82f6", 
        strokeWeight: 6,
        strokeStyle: "solid",
        lineJoin: 'round',
        lineCap: 'round'
      })

      map.add([startMarker, endMarker, polyline])
      map.setFitView([startMarker, endMarker], false, [50, 50, 50, 50])
    })
  })
}

const submitOrder = () => {
  if (!isFormValid.value) return

  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    dialogVisible.value = false
    ElMessage.success('订单提交成功！请前往个人中心查看')
  }, 1000)
}
</script>

<style lang="scss">
/* 全局弹窗样式 - 确保样式穿透 */
.booking-dialog {
  border-radius: 20px !important;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;

  .el-dialog__header {
    margin: 0;
    padding: 24px 24px 0;
    border-bottom: none;
    
    .el-dialog__title {
      font-size: 20px;
      font-weight: 700;
      color: #1e293b;
    }
  }

  .el-dialog__body {
    padding: 24px !important;
  }
}
</style>

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
    background: #fff;
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
    border: 1px solid #e2e8f0;
    padding-left: 16px;
    transition: all 0.3s;
    
    &:hover, &.is-focus {
      border-color: #3b82f6;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }
  }
}

.cars-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px; /* More space */
}

.car-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    border-color: #e2e8f0;
  }

  .car-image {
    height: 200px; /* Taller image */
    position: relative;
    overflow: hidden;
    
    .el-image {
      width: 100%;
      height: 100%;
      transition: transform 0.5s;
    }
    
    &:hover .el-image {
      transform: scale(1.05);
    }

    .car-tag {
      position: absolute;
      top: 16px;
      left: 16px;
      background: rgba(255, 255, 255, 0.95);
      color: #3b82f6;
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 700;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }
  }

  .car-info {
    padding: 24px;

    .info-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
      
      .car-name {
        margin: 0;
        font-size: 18px;
        color: #1e293b;
        font-weight: 700;
        line-height: 1.4;
      }

      .car-price {
        color: #ef4444;
        font-weight: 700;
        text-align: right;
        
        .currency { font-size: 12px; margin-right: 2px; }
        .amount { font-size: 20px; }
        .unit { font-size: 12px; color: #94a3b8; font-weight: 400; display: block; }
      }
    }

    .car-specs {
      display: flex;
      gap: 8px;
      margin-bottom: 24px;
      flex-wrap: wrap;
      
      .spec-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: #64748b;
        background: #f8fafc;
        padding: 6px 12px;
        border-radius: 8px;
        border: 1px solid #f1f5f9;
        font-weight: 500;
        
        .el-icon { color: #94a3b8; }
      }
    }

    .rent-btn {
      width: 100%;
      height: 44px;
      font-weight: 600;
      font-size: 14px;
      border-radius: 12px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.2);
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.3);
      }
    }
  }
}

/* Booking Dialog Styles - Enhanced Modern Look */
.booking-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);

  :deep(.el-dialog__header) {
    margin: 0;
    padding: 24px 24px 0;
    border-bottom: none;
    
    .el-dialog__title {
      font-size: 20px;
      font-weight: 700;
      color: #1e293b;
    }
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }
}

.detail-container {
  display: flex;
  gap: 24px;
  height: 520px;
}

.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px; /* Increased gap for breathing room */
  overflow-y: auto;
  padding-right: 16px;
  
  /* Custom Scrollbar */
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  &::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 3px;
    &:hover { background: #cbd5e1; }
  }
}

.detail-map {
  flex: 1;
  background: #f8fafc;
  border-radius: 16px; /* Larger radius */
  overflow: hidden;
  border: 1px solid #e2e8f0;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  background: #f1f5f9;
  
  .map-icon {
    font-size: 48px;
    margin-bottom: 16px;
    color: #cbd5e1;
  }
  
  p {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: #64748b;
  }
  
  .sub {
    font-size: 12px;
    color: #94a3b8;
  }
}

.info-block {
  .block-title {
    margin: 0 0 16px 0;
    font-size: 14px;
    color: #475569; /* Softer color */
    font-weight: 700;
    padding-left: 10px;
    border-left: 3px solid #3b82f6;
    letter-spacing: 0.5px;
  }
}

.car-overview {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  
  .car-thumb {
    width: 90px;
    height: 68px;
    object-fit: cover;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    background: white;
  }
  
  .car-meta {
    flex: 1;
    
    .car-name {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 700;
      color: #1e293b;
    }
    
    .car-tags {
      display: flex;
      gap: 6px;
      margin-bottom: 8px;
    }
    
    .price-display {
      display: flex;
      align-items: baseline;
      gap: 8px;
      
      .label {
        font-size: 12px;
        color: #94a3b8;
      }
      
      .price {
        font-size: 20px;
        font-weight: 700;
        color: #ef4444;
      }
    }
  }
}

.booking-date-picker {
  :deep(.el-input__wrapper) {
    box-shadow: none;
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 0 12px;
    height: 36px;
    transition: all 0.2s;
    
    &:hover {
      background: white;
      border-color: #94a3b8;
    }
    
    &.is-focus {
      background: white;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
    
    .el-input__inner {
      font-size: 14px;
      font-weight: 500;
      color: #1e293b;
    }
  }
}

.timeline-form {
  display: flex;
  flex-direction: column;
  position: relative;
  padding-left: 4px;
}

.timeline-item {
  display: flex;
  gap: 16px;
  position: relative;
  padding-bottom: 24px;
  
  &:last-child {
    padding-bottom: 0;
  }
  
  .node-left {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 16px;
    padding-top: 8px; /* Align dot with content header */
    
    .dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: white;
      border: 3px solid #cbd5e1;
      flex-shrink: 0;
      z-index: 2;
      box-shadow: 0 0 0 2px white;
      transition: all 0.3s;
      
      &.start { border-color: #3b82f6; }
      &.end { border-color: #10b981; }
    }
    
    .line {
      width: 2px;
      flex: 1;
      background: #e2e8f0;
      margin-top: 4px;
      margin-bottom: -12px; /* Extend to next node */
    }
  }
  
  .node-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
    
    .content-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 28px; /* Fixed height for alignment */
      
      .label {
        font-size: 13px;
        font-weight: 700;
        color: #475569;
      }
      
      .time {
        font-size: 12px;
        color: #64748b;
        font-family: 'Inter', monospace;
        background: #f1f5f9;
        padding: 4px 8px;
        border-radius: 6px;
        font-weight: 500;
      }
    }
    
    .form-row {
      display: flex;
      gap: 12px;
      
      .mini-select {
        &.city { width: 110px; flex-shrink: 0; }
        &.store { flex: 1; }
        
        :deep(.el-input__wrapper) {
          box-shadow: none;
          background: #f8fafc;
          border: 1px solid #e2e8f0;
          border-radius: 8px;
          padding: 0 12px;
          height: 36px;
          transition: all 0.2s;
          
          &:hover {
            background: white;
            border-color: #94a3b8;
          }
          
          &.is-focus {
            background: white;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
          }
          
          .el-input__inner {
            font-size: 14px;
            font-weight: 500;
            color: #1e293b;
          }
        }
      }
    }
  }
}

.service-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-color: #cbd5e1;
    background: #f8fafc;
  }
  
  &.active {
    border-color: #3b82f6;
    background: #eff6ff;
    
    .service-icon {
      background: #3b82f6;
      color: white;
    }
  }
  
  .service-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: #f1f5f9;
    color: #64748b;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    transition: all 0.2s;
  }
  
  .service-info {
    flex: 1;
    
    .service-name {
      font-size: 14px;
      font-weight: 600;
      color: #1e293b;
      margin-bottom: 2px;
    }
    
    .service-desc {
      font-size: 12px;
      color: #94a3b8;
    }
  }
  
  .service-price {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .price {
      font-size: 16px;
      font-weight: 700;
      color: #1e293b;
    }
    
    .unit {
      font-size: 12px;
      color: #94a3b8;
      margin-left: -8px;
    }
  }
}

.cost-details {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

  .cost-row {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    font-size: 14px;
    
    .label {
      color: #64748b;
    }
    
    .value {
      color: #1e293b;
      font-weight: 500;
      font-family: 'Inter', sans-serif;
      
      &.amount {
        color: #10b981;
        font-size: 20px;
        font-weight: 700;
      }
    }
    
    &.total {
      padding-top: 12px;
      margin-top: 8px;
      align-items: center;
      
      .label { font-weight: 600; color: #1e293b; font-size: 15px; }
    }
  }
  
  .cost-divider {
    height: 1px;
    background: #e2e8f0;
    margin: 8px 0;
    border-top: 1px dashed #e2e8f0;
    background: transparent;
  }
}

.dialog-footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-top: 8px;
  
  .footer-tip {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #94a3b8;
    font-size: 13px;
    background: #f8fafc;
    padding: 6px 12px;
    border-radius: 6px;
  }
  
  .footer-buttons {
    display: flex;
    gap: 12px;
    
    .cancel-btn {
      border-radius: 8px;
      padding: 10px 20px;
      border: 1px solid #e2e8f0;
      color: #64748b;
      font-weight: 500;
      &:hover {
        color: #1e293b;
        border-color: #cbd5e1;
        background: #f8fafc;
      }
    }
    
    .confirm-btn {
      border-radius: 8px;
      padding: 10px 24px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      font-weight: 600;
      box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3);
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 6px 8px -1px rgba(59, 130, 246, 0.4);
      }
      
      &:disabled {
        background: #cbd5e1;
        box-shadow: none;
        transform: none;
      }
    }
  }
}

/* Filter Bar Optimization */
.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f8fafc;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
  border: 1px solid #f1f5f9;
}

.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  
  &.type-row {
    justify-content: flex-start;
  }
}

.search-input {
  width: 320px;
  
  :deep(.el-input__wrapper) {
    box-shadow: none !important;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    padding-left: 12px;
    background: white;
    
    &:hover, &.is-focus {
      border-color: #3b82f6;
    }
  }
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .filter-label {
    font-size: 14px;
    font-weight: 600;
    color: #64748b;
  }
}

.type-options {
  display: flex;
  gap: 8px;
}

.type-chip {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  color: #64748b;
  background: white;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  
  &:hover {
    color: #3b82f6;
    border-color: #cbd5e1;
  }
  
  &.active {
    background: #3b82f6;
    color: white;
    border-color: #3b82f6;
    box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
  }
}

.custom-radio-group {
  :deep(.el-radio-button__inner) {
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    background: white;
    box-shadow: none !important;
    margin-left: 8px;
    padding: 7px 16px;
    font-size: 13px;
    color: #64748b;
    
    &:hover {
      color: #3b82f6;
    }
  }
  
  :deep(.el-radio-button:first-child .el-radio-button__inner) {
    margin-left: 0;
  }
  
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background-color: #eff6ff;
    color: #3b82f6;
    border-color: #3b82f6;
    font-weight: 600;
  }
}
</style>

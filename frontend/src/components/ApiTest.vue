<template>
  <div class="api-test">
    <h2>前后端API连接测试</h2>
    
    <div class="button-group">
      <button @click="testGet" :disabled="loading">测试 GET 请求</button>
      <button @click="testPost" :disabled="loading">测试 POST 请求</button>
      <button @click="testHello" :disabled="loading">测试 Hello API</button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-if="response" class="response">
      <h3>响应结果：</h3>
      <pre>{{ JSON.stringify(response, null, 2) }}</pre>
    </div>

    <div v-if="error" class="error">
      <h3>错误信息：</h3>
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const loading = ref(false)
const response = ref(null)
const error = ref(null)

const API_BASE_URL = 'http://localhost:8080/api'

const testGet = async () => {
  loading.value = true
  error.value = null
  response.value = null
  
  try {
    const res = await fetch(`${API_BASE_URL}/test`)
    const data = await res.json()
    response.value = data
  } catch (err) {
    error.value = `请求失败: ${err.message}`
  } finally {
    loading.value = false
  }
}

const testPost = async () => {
  loading.value = true
  error.value = null
  response.value = null
  
  try {
    const res = await fetch(`${API_BASE_URL}/echo`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: 'Vue前端',
        message: '这是一条测试消息',
        timestamp: new Date().toISOString()
      })
    })
    const data = await res.json()
    response.value = data
  } catch (err) {
    error.value = `请求失败: ${err.message}`
  } finally {
    loading.value = false
  }
}

const testHello = async () => {
  loading.value = true
  error.value = null
  response.value = null
  
  try {
    const res = await fetch(`${API_BASE_URL}/hello`)
    const data = await res.json()
    response.value = data
  } catch (err) {
    error.value = `请求失败: ${err.message}`
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.api-test {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

h2 {
  color: #42b983;
  margin-bottom: 1.5rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

button {
  padding: 0.75rem 1.5rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
}

button:hover:not(:disabled) {
  background: #35a372;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 1rem;
  color: #666;
}

.response {
  margin-top: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.response h3 {
  margin-top: 0;
  color: #42b983;
}

.response pre {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 0.9rem;
}

.error {
  margin-top: 2rem;
  padding: 1rem;
  background: #fee;
  border: 1px solid #fcc;
  border-radius: 4px;
  color: #c33;
}

.error h3 {
  margin-top: 0;
}
</style>


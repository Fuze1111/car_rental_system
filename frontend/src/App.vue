<template>
  <router-view></router-view>
</template>

<script setup>
// 根组件只需要渲染 router-view
</script>

<style>
/* 全局重置样式 */
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

html, body, #app {
  height: 100%;
}
</style>

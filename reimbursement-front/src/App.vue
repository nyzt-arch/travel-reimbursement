<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>

<template>
  <div class="app-wrapper">
    <!-- Main Content Frame -->
    <!-- 主内容区域 -->
<main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main {
  flex: 1;
  padding: 24px 0 60px 0; /* 底部留白，防止被 fixed 定位的底部操作栏遮挡 */
}
</style>

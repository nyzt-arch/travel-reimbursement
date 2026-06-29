<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>

<template>
  <div class="app-wrapper">
    <!-- Main Content Frame -->
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

.app-header {
  height: var(--header-height);
  background: linear-gradient(135deg, #1e293b, #0f172a); /* Sleek slate gradient */
  color: #ffffff;
  box-shadow: var(--shadow-md);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 24px;
}

.logo-title {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: rgba(255, 255, 255, 0.1);
  padding: 6px 12px;
  border-radius: var(--radius-round);
  font-size: 14px;
}

.avatar {
  font-size: 16px;
}

.username {
  font-weight: 500;
}

.app-main {
  flex: 1;
  padding: 24px 0 60px 0; /* Add bottom padding so elements don't get hidden under absolute/fixed footer */
}
</style>

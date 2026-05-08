<template>
  <el-container class="main-layout">
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <h2>CodeInspire</h2>
        <p class="subtitle">AI个性化顾问</p>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#fff"
      >
        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI对话</span>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>用户画像</span>
        </el-menu-item>
        
        <el-menu-item index="/plans">
          <el-icon><Document /></el-icon>
          <span>学习规划</span>
        </el-menu-item>
        
        <el-menu-item index="/notifications" class="notification-item">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-icon><Bell /></el-icon>
          </el-badge>
          <span>通知中心</span>
        </el-menu-item>
      </el-menu>
      
      <div class="user-info">
        <div class="avatar">{{ userStore.username?.charAt(0)?.toUpperCase() || 'U' }}</div>
        <div class="user-details">
          <p class="username">{{ userStore.username || '用户' }}</p>
          <el-button link type="primary" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-aside>
    
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/api/request'

const userStore = useUserStore()
const unreadCount = ref(0)

onMounted(async () => {
  try {
    const res = await api.get('/notifications/unread-count')
    unreadCount.value = res.count
  } catch (e) {
    console.error('Failed to fetch unread count:', e)
  }
})

function handleLogout() {
  userStore.logout()
}
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #4f46e5 0%, #7c3aed 100%);
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  
  .logo {
    padding: 0 24px 32px;
    
    h2 {
      color: #fff;
      font-size: 24px;
      font-weight: 700;
      margin-bottom: 4px;
    }
    
    .subtitle {
      color: rgba(255, 255, 255, 0.7);
      font-size: 13px;
    }
  }
  
  .sidebar-menu {
    border-right: none;
    flex: 1;
    
    .el-menu-item {
      margin: 4px 12px;
      border-radius: 8px;
      height: 48px;
      line-height: 48px;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.15) !important;
      }
      
      &.is-active {
        background-color: rgba(255, 255, 255, 0.25) !important;
      }
    }
    
    .notification-item {
      position: relative;
      
      .notification-badge {
        position: absolute;
        right: 12px;
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.15);
    
    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.25);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 16px;
    }
    
    .user-details {
      flex: 1;
      
      .username {
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 2px;
      }
      
      :deep(.el-button) {
        color: rgba(255, 255, 255, 0.7);
        font-size: 12px;
        padding: 0;
        
        &:hover {
          color: #fff;
        }
      }
    }
  }
}

.main-content {
  background: #f8fafc;
  overflow-y: auto;
}
</style>

<template>
  <div class="notification-container">
    <div class="page-header">
      <h2>通知中心</h2>
      <el-button
        v-if="unreadCount > 0"
        type="primary"
        link
        @click="markAllAsRead"
      >
        全部已读
      </el-button>
    </div>
    
    <div class="notifications-list" v-if="notifications.length > 0">
      <div
        v-for="notif in notifications"
        :key="notif.id"
        :class="['notification-item', { unread: !notif.isRead }]"
      >
        <div class="icon" :class="notif.type">
          <el-icon v-if="notif.type === 'task_reminder'"><Bell /></el-icon>
          <el-icon v-else-if="notif.type === 'ai_reply'"><ChatDotRound /></el-icon>
          <el-icon v-else><InfoFilled /></el-icon>
        </div>
        
        <div class="content">
          <h4>{{ notif.title }}</h4>
          <p>{{ notif.content }}</p>
          <span class="time">{{ formatTime(notif.createdAt) }}</span>
        </div>
        
        <div class="actions">
          <el-button
            v-if="!notif.isRead"
            size="small"
            @click="markAsRead(notif.id)"
          >
            标记已读
          </el-button>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="暂无通知" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/request'

const notifications = ref<any[]>([])
const unreadCount = ref(0)

onMounted(() => {
  fetchNotifications()
})

async function fetchNotifications() {
  try {
    const [list, count] = await Promise.all([
      api.get('/notifications'),
      api.get('/notifications/unread-count')
    ])
    
    notifications.value = list || []
    unreadCount.value = count?.count || 0
  } catch (e) {
    console.error('Failed to fetch notifications:', e)
  }
}

async function markAsRead(id: number) {
  try {
    await api.put(`/notifications/${id}/read`)
    await fetchNotifications()
  } catch (e) {
    console.error('Failed to mark as read:', e)
  }
}

async function markAllAsRead() {
  try {
    await api.put('/notifications/read-all')
    await fetchNotifications()
  } catch (e) {
    console.error('Failed to mark all as read:', e)
  }
}

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style lang="scss" scoped>
.notification-container {
  max-width: 800px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }
  
  .notifications-list {
    .notification-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      background: #fff;
      border-radius: 12px;
      margin-bottom: 12px;
      box-shadow: var(--shadow-sm);
      transition: all 0.2s;
      
      &:hover {
        box-shadow: var(--shadow-md);
      }
      
      &.unread {
        background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
        border-left: 3px solid var(--primary-color);
        
        .content h4 {
          font-weight: 600;
        }
      }
      
      .icon {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        flex-shrink: 0;
        
        &.task_reminder {
          background: #fef3c7;
          color: #d97706;
        }
        
        &.ai_reply {
          background: #dbeafe;
          color: #2563eb;
        }
        
        &.system,
        &.progress_warning {
          background: #fee2e2;
          color: #dc2626;
        }
      }
      
      .content {
        flex: 1;
        
        h4 {
          font-size: 15px;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        p {
          font-size: 13px;
          color: var(--text-secondary);
          line-height: 1.5;
          margin-bottom: 4px;
        }
        
        .time {
          font-size: 12px;
          color: #94a3b8;
        }
      }
      
      .actions {
        display: flex;
        align-items: center;
      }
    }
  }
}
</style>

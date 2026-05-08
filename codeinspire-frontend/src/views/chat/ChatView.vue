<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>AI智能对话</h2>
      <p class="description">基于你的个人情况，提供精准的技术、学习和职业建议</p>
      
      <div class="quick-actions">
        <el-button 
          v-for="action in quickActions" 
          :key="action.label"
          size="small"
          @click="sendQuickAction(action.prompt)"
        >
          {{ action.label }}
        </el-button>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div class="avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="content">
          <div class="text" v-html="formatMessage(msg.content)"></div>
        </div>
      </div>
      
      <div v-if="loading" class="message assistant">
        <div class="avatar">🤖</div>
        <div class="content">
          <el-skeleton :rows="3" animated />
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="2"
        placeholder="输入你的问题...（例如：帮我制定Java后端学习路线）"
        resize="none"
        @keydown.enter.exact="handleSend"
      />
      <el-button
        type="primary"
        :loading="loading"
        :disabled="!inputMessage.trim()"
        @click="handleSend"
        circle
        class="send-btn"
      >
        <el-icon><Promotion /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { chatApi } from '@/api/chat'
import type { ChatMessage } from '@/api/chat'

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const loading = ref(false)
const sessionId = ref('')
const messagesContainer = ref<HTMLElement>()

const quickActions = [
  { label: '📚 学习路线', prompt: '请根据我的背景，为我推荐合适的技术学习路线' },
  { label: '💼 求职建议', prompt: '我想了解当前就业市场的求职策略和建议' },
  { label: '📝 简历优化', prompt: '请帮我看一下我的简历有什么需要改进的地方' },
  { label: '🎯 面试准备', prompt: '我想准备后端开发岗位的面试，应该怎么准备？' }
]

onMounted(() => {
  addWelcomeMessage()
})

function addWelcomeMessage() {
  messages.value.push({
    role: 'assistant',
    content: `你好！我是 **CodeInspire** AI顾问 🎓\n\n我可以帮助你：\n- 📚 制定个性化的技术学习路线\n- 💼 提供求职和职业发展建议\n- 📝 优化简历和面试准备\n- 🎯 规划学习目标和任务分解\n\n请告诉我你的需求，我会根据你的具体情况给出针对性建议！`,
    timestamp: Date.now()
  })
}

async function handleSend() {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  
  messages.value.push({
    role: 'user',
    content: userMessage,
    timestamp: Date.now()
  })
  
  inputMessage.value = ''
  loading.value = true
  
  scrollToBottom()
  
  try {
    const response = await chatApi.sendMessage({
      message: userMessage,
      sessionId: sessionId.value,
      type: 'consultation'
    })
    
    if (!sessionId.value) {
      sessionId.value = response.sessionId
    }
    
    messages.value.push({
      role: 'assistant',
      content: response.response,
      timestamp: Date.now()
    })
    
    scrollToBottom()
  } catch (error) {
    console.error('Failed to send message:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我暂时无法回答你的问题。请稍后再试。',
      timestamp: Date.now()
    })
  } finally {
    loading.value = false
  }
}

async function sendQuickAction(prompt: string) {
  inputMessage.value = prompt
  await handleSend()
}

function formatMessage(content: string): string {
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style lang="scss" scoped>
.chat-container {
  height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.chat-header {
  padding: 24px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  
  h2 {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 4px;
  }
  
  .description {
    opacity: 0.9;
    font-size: 13px;
    margin-bottom: 16px;
  }
  
  .quick-actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    
    .el-button {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.3);
      color: #fff;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        border-color: rgba(255, 255, 255, 0.4);
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f8fafc;
  
  .message {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    
    &.user {
      flex-direction: row-reverse;
      
      .content {
        background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
        color: #fff;
        border-radius: 16px 16px 4px 16px;
      }
    }
    
    &.assistant {
      .content {
        background: #fff;
        border: 1px solid var(--border-color);
        border-radius: 16px 16px 16px 4px;
      }
    }
    
    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      flex-shrink: 0;
      background: #fff;
      box-shadow: var(--shadow-sm);
    }
    
    .content {
      max-width: 70%;
      padding: 12px 16px;
      
      .text {
        line-height: 1.6;
        font-size: 14px;
        
        :deep(strong) {
          font-weight: 600;
        }
      }
    }
  }
}

.chat-input {
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  background: #fff;
  display: flex;
  align-items: flex-end;
  gap: 12px;
  
  :deep(.el-textarea__inner) {
    border-radius: 12px;
    resize: none;
    font-family: inherit;
  }
  
  .send-btn {
    width: 44px;
    height: 44px;
    flex-shrink: 0;
    
    .el-icon {
      font-size: 18px;
    }
  }
}
</style>

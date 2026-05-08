<template>
  <div class="register-container">
    <div class="register-card">
      <div class="header">
        <h1>注册账号</h1>
        <p class="subtitle">加入CodeInspire，开启AI个性化学习之旅</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="formData.email"
            placeholder="邮箱地址"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="密码（至少6位）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-button
          type="primary"
          size="large"
          class="register-btn"
          :loading="loading"
          @click="handleRegister"
        >
          注册
        </el-button>
      </el-form>
      
      <div class="footer">
        <span>已有账号？</span>
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive({
  username: '',
  email: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在6-100个字符之间', trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  loading.value = true
  try {
    await userStore.register(formData)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px;
  padding: 48px 40px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  
  .header {
    text-align: center;
    margin-bottom: 36px;
    
    h1 {
      font-size: 28px;
      font-weight: 700;
      color: var(--primary-color);
      margin-bottom: 8px;
    }
    
    .subtitle {
      color: var(--text-secondary);
      font-size: 14px;
    }
  }
  
  .register-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    border: none;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 10px 20px -5px rgba(99, 102, 241, 0.4);
    }
  }
  
  .footer {
    text-align: center;
    margin-top: 24px;
    color: var(--text-secondary);
    font-size: 14px;
    
    .link {
      color: var(--primary-color);
      font-weight: 600;
      text-decoration: none;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>

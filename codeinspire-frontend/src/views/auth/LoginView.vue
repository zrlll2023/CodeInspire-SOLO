<template>
  <div class="login-container">
    <div class="login-card">
      <div class="header">
        <h1>CodeInspire</h1>
        <p class="subtitle">AI驱动的计算机专业个性化顾问系统</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="用户名或邮箱"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-button
          type="primary"
          size="large"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form>
      
      <div class="footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="link">立即注册</router-link>
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
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  loading.value = true
  try {
    await userStore.login(formData)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
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
      font-size: 32px;
      font-weight: 700;
      color: var(--primary-color);
      margin-bottom: 8px;
    }
    
    .subtitle {
      color: var(--text-secondary);
      font-size: 14px;
    }
  }
  
  .login-btn {
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

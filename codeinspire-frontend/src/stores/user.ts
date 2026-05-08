import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userId = ref<number | null>(null)
  const username = ref<string>('')

  async function login(loginParams: { username: string; password: string }) {
    const res = await authApi.login(loginParams)
    token.value = res.token
    userId.value = res.userId
    username.value = res.username
    localStorage.setItem('token', res.token)
    router.push('/')
  }

  async function register(registerParams: { username: string; email: string; password: string }) {
    const res = await authApi.register(registerParams)
    token.value = res.token
    userId.value = res.userId
    username.value = res.username
    localStorage.setItem('token', res.token)
    router.push('/')
  }

  function logout() {
    token.value = ''
    userId.value = null
    username.value = ''
    localStorage.removeItem('token')
    router.push('/login')
  }

  return {
    token,
    userId,
    username,
    login,
    register,
    logout
  }
})

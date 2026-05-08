import api from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  userId: number
  username: string
  expiresIn: number
}

export const authApi = {
  login(params: LoginParams): Promise<AuthResponse> {
    return api.post('/auth/login', params)
  },
  
  register(params: RegisterParams): Promise<AuthResponse> {
    return api.post('/auth/register', params)
  }
}

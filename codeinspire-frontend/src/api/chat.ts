import api from './request'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  timestamp: number
}

export interface ChatParams {
  message: string
  sessionId?: string
  type?: string
  provider?: string
}

export interface ChatResponse {
  response: string
  sessionId: string
  conversationId: number
  provider: string
  timestamp: number
}

export const chatApi = {
  sendMessage(params: ChatParams): Promise<ChatResponse> {
    return api.post('/chat/send', params)
  },
  
  getHistory(sessionId: string): Promise<any> {
    return api.get('/chat/history', { params: { sessionId } })
  },
  
  exportConversation(conversationId: number): Promise<string> {
    return api.get('/chat/export', { params: { conversationId } })
  }
}

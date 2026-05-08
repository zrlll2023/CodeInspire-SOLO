<template>
  <div class="profile-container">
    <div class="page-header">
      <h2>用户画像</h2>
      <p>完善你的个人信息，获取更精准的个性化建议</p>
    </div>
    
    <el-card class="profile-card">
      <el-form
        ref="formRef"
        :model="formData"
        label-width="120px"
        label-position="right"
      >
        <el-divider content-position="left">📚 教育背景</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学校层次" prop="schoolLevel">
              <el-select v-model="formData.schoolLevel" placeholder="请选择">
                <el-option label="985" value="985" />
                <el-option label="211" value="211" />
                <el-option label="一本" value="一本" />
                <el-option label="二本" value="二本" />
                <el-option label="独立学院" value="独立学院" />
                <el-option label="民办本科" value="民办本科" />
                <el-option label="专科" value="专科" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-select v-model="formData.grade" placeholder="请选择">
                <el-option label="大一" value="大一" />
                <el-option label="大二" value="大二" />
                <el-option label="大三" value="大三" />
                <el-option label="大四" value="大四" />
                <el-option label="研一" value="研一" />
                <el-option label="研二" value="研二" />
                <el-option label="研三" value="研三" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">🎯 求职目标</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标岗位" prop="targetPosition">
              <el-input v-model="formData.targetPosition" placeholder="如：后端开发工程师" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="目标城市" prop="targetCityLevel">
              <el-select v-model="formData.targetCityLevel" placeholder="请选择">
                <el-option label="一线城市" value="一线" />
                <el-option label="新一线" value="新一线" />
                <el-option label="二三线城市" value="二三线" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">⏰ 时间规划</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="紧迫程度" prop="urgencyLevel">
              <el-select v-model="formData.urgencyLevel" placeholder="请选择">
                <el-option label="紧急（6个月内）" value="紧急（6个月内）" />
                <el-option label="一般（1年内）" value="一般（1年内）" />
                <el-option label="充裕（2年+）" value="充裕（2年+）" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="每周时间" prop="weeklyAvailableHours">
              <el-input-number
                v-model="formData.weeklyAvailableHours"
                :min="1"
                :max="80"
                placeholder="小时"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="专业方向" prop="majorDirection">
              <el-select v-model="formData.majorDirection" placeholder="请选择">
                <el-option label="后端开发" value="后端开发" />
                <el-option label="前端开发" value="前端开发" />
                <el-option label="全栈开发" value="全栈开发" />
                <el-option label="AI/机器学习" value="AI/机器学习" />
                <el-option label="大数据" value="大数据" />
                <el-option label="嵌入式" value="嵌入式" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item style="margin-top: 32px;">
          <el-button type="primary" size="large" @click="handleSave" :loading="saving">
            保存画像
          </el-button>
          <el-button size="large" @click="getSuggestions" :loading="loadingAdvice">
            获取个性化建议
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card v-if="advice" class="advice-card" style="margin-top: 24px;">
      <template #header>
        <div class="card-header">
          <span>💡 个性化建议</span>
          <el-tag type="success">AI生成</el-tag>
        </div>
      </template>
      
      <div class="advice-content">
        <div class="summary">{{ advice.summary }}</div>
        
        <div v-for="(item, index) in advice.advices" :key="index" class="advice-item">
          <h4>{{ item.title }}</h4>
          <p>{{ item.description }}</p>
          
          <el-steps :active="item.actionItems?.length || 0" simple style="margin-top: 12px;">
            <el-step
              v-for="(action, idx) in item.actionItems"
              :key="idx"
              :title="action"
            />
          </el-steps>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api/request'
import { ElMessage } from 'element-plus'

const formRef = ref()
const saving = ref(false)
const loadingAdvice = ref(false)
const advice = ref<any>(null)

const formData = reactive({
  schoolLevel: '',
  grade: '',
  targetPosition: '',
  targetCityLevel: '',
  urgencyLevel: '',
  weeklyAvailableHours: null as number | null,
  majorDirection: ''
})

onMounted(async () => {
  await fetchProfile()
})

async function fetchProfile() {
  try {
    const data = await api.get('/profile')
    Object.assign(formData, data)
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }
}

async function handleSave() {
  saving.value = true
  try {
    await api.put('/profile', formData)
    ElMessage.success('画像保存成功')
  } catch (e) {
    console.error('Failed to save profile:', e)
  } finally {
    saving.value = false
  }
}

async function getSuggestions() {
  loadingAdvice.value = true
  try {
    const data = await api.get('/profile/suggestions')
    advice.value = data
  } catch (e) {
    console.error('Failed to get suggestions:', e)
  } finally {
    loadingAdvice.value = false
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  
  .page-header {
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 4px;
    }
    
    p {
      color: var(--text-secondary);
      font-size: 14px;
    }
  }
  
  .profile-card {
    border-radius: 16px;
    box-shadow: var(--shadow-md);
    
    :deep(.el-divider__text) {
      font-size: 15px;
      font-weight: 600;
      color: var(--primary-color);
    }
  }
  
  .advice-card {
    border-radius: 16px;
    box-shadow: var(--shadow-md);
    
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-weight: 600;
    }
    
    .advice-content {
      .summary {
        background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
        padding: 16px;
        border-radius: 12px;
        margin-bottom: 20px;
        line-height: 1.6;
        color: var(--text-primary);
      }
      
      .advice-item {
        padding: 16px;
        background: #f8fafc;
        border-radius: 12px;
        margin-bottom: 16px;
        
        h4 {
          color: var(--primary-color);
          font-size: 16px;
          margin-bottom: 8px;
        }
        
        p {
          color: var(--text-secondary);
          line-height: 1.6;
          font-size: 14px;
        }
      }
    }
  }
}
</style>

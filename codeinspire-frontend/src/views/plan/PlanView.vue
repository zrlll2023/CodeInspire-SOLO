<template>
  <div class="plan-container">
    <div class="page-header">
      <h2>学习规划</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        新建规划
      </el-button>
    </div>
    
    <div class="plans-list" v-if="plans.length > 0">
      <el-card
        v-for="plan in plans"
        :key="plan.id"
        class="plan-card"
        shadow="hover"
      >
        <template #header>
          <div class="card-header">
            <div class="title-section">
              <h3>{{ plan.title }}</h3>
              <el-tag :type="getStatusType(plan.status)" size="small">
                {{ getStatusText(plan.status) }}
              </el-tag>
            </div>
            <div class="actions">
              <el-button link type="primary" @click="editPlan(plan)">编辑</el-button>
              <el-button link type="danger" @click="deletePlan(plan.id)">删除</el-button>
            </div>
          </div>
        </template>
        
        <p v-if="plan.description" class="description">{{ plan.description }}</p>
        
        <div v-if="plan.tasks && plan.tasks.length > 0" class="tasks-list">
          <div
            v-for="task in plan.tasks"
            :key="task.id"
            :class="['task-item', task.status]"
            @click="toggleTaskStatus(task)"
          >
            <el-icon class="task-icon" v-if="task.status === 'completed'">
              <CircleCheckFilled />
            </el-icon>
            <el-icon class="task-icon" v-else>
              <CircleCheck />
            </el-icon>
            
            <span class="task-title">{{ task.title }}</span>
            
            <el-tag
              v-if="task.priority > 0"
              :type="getPriorityType(task.priority)"
              size="small"
            >
              P{{ task.priority }}
            </el-tag>
            
            <span v-if="task.dueDate" class="due-date">
              {{ formatDate(task.dueDate) }}
            </span>
          </div>
        </div>
        
        <el-empty v-else description="暂无任务，点击添加任务" />
        
        <div class="add-task">
          <el-input
            v-model="newTasks[plan.id]"
            placeholder="输入新任务..."
            @keyup.enter="addTask(plan.id)"
          >
            <template #append>
              <el-button @click="addTask(plan.id)">
                添加
              </el-button>
            </template>
          </el-input>
        </div>
      </el-card>
    </div>
    
    <el-empty v-else description="暂无学习规划，点击上方按钮创建" />
    
    <!-- 创建/编辑规划对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingPlan ? '编辑规划' : '新建规划'"
      width="500px"
    >
      <el-form :model="dialogForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="dialogForm.title" placeholder="规划标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="dialogForm.description"
            type="textarea"
            :rows="3"
            placeholder="规划描述（可选）"
          />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="dialogForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="dialogForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const plans = ref<any[]>([])
const newTasks = ref<Record<number, string>>({})
const dialogVisible = ref(false)
const editingPlan = ref<any>(null)
const dialogForm = reactive({
  title: '',
  description: '',
  startDate: null as Date | null,
  endDate: null as Date | null
})

onMounted(() => {
  fetchPlans()
})

async function fetchPlans() {
  try {
    plans.value = await api.get('/plans')
  } catch (e) {
    console.error('Failed to fetch plans:', e)
  }
}

function showCreateDialog() {
  editingPlan.value = null
  dialogForm.title = ''
  dialogForm.description = ''
  dialogForm.startDate = null
  dialogForm.endDate = null
  dialogVisible.value = true
}

function editPlan(plan: any) {
  editingPlan.value = plan
  dialogForm.title = plan.title
  dialogForm.description = plan.description || ''
  dialogForm.startDate = plan.startDate ? new Date(plan.startDate) : null
  dialogForm.endDate = plan.endDate ? new Date(plan.endDate) : null
  dialogVisible.value = true
}

async function savePlan() {
  if (!dialogForm.title.trim()) {
    ElMessage.warning('请输入规划标题')
    return
  }
  
  try {
    if (editingPlan.value) {
      await api.put(`/plans/${editingPlan.value.id}`, dialogForm)
      ElMessage.success('更新成功')
    } else {
      await api.post('/plans', dialogForm)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    await fetchPlans()
  } catch (e) {
    console.error('Failed to save plan:', e)
  }
}

async function deletePlan(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这个规划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.delete(`/plans/${id}`)
    ElMessage.success('删除成功')
    await fetchPlans()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('Failed to delete plan:', e)
    }
  }
}

async function addTask(planId: number) {
  const title = newTasks.value[planId]
  if (!title?.trim()) return
  
  try {
    await api.post(`/plans/${planId}/tasks`, { planId, title })
    newTasks.value[planId] = ''
    ElMessage.success('任务添加成功')
    await fetchPlans()
  } catch (e) {
    console.error('Failed to add task:', e)
  }
}

async function toggleTaskStatus(task: any) {
  const newStatus = task.status === 'completed' ? 'pending' : 'completed'
  
  try {
    await api.patch(`/plans/tasks/${task.id}/status?status=${newStatus}`)
    await fetchPlans()
  } catch (e) {
    console.error('Failed to update task status:', e)
  }
}

function getStatusType(status: string): string {
  const types: Record<string, string> = {
    active: '',
    completed: 'success',
    paused: 'warning',
    archived: 'info'
  }
  return types[status] || ''
}

function getStatusText(status: string): string {
  const texts: Record<string, string> = {
    active: '进行中',
    completed: '已完成',
    paused: '已暂停',
    archived: '已归档'
  }
  return texts[status] || status
}

function getPriorityType(priority: number): string {
  if (priority >= 4) return 'danger'
  if (priority >= 2) return 'warning'
  return 'info'
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style lang="scss" scoped>
.plan-container {
  max-width: 900px;
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
  
  .plans-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .plan-card {
    border-radius: 16px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title-section {
        display: flex;
        align-items: center;
        gap: 12px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }
    }
    
    .description {
      color: var(--text-secondary);
      line-height: 1.6;
      margin-bottom: 16px;
    }
    
    .tasks-list {
      .task-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 10px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s;
        margin-bottom: 8px;
        
        &:hover {
          background: #f8fafc;
        }
        
        &.completed {
          opacity: 0.6;
          
          .task-title {
            text-decoration: line-through;
          }
        }
        
        .task-icon {
          color: var(--text-secondary);
          font-size: 20px;
          
          &.is-checked {
            color: var(--secondary-color);
          }
        }
        
        .task-title {
          flex: 1;
          font-size: 14px;
          color: var(--text-primary);
        }
        
        .due-date {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }
    
    .add-task {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid var(--border-color);
    }
  }
}
</style>

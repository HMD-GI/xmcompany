<template>
  <el-dialog
    :title="modeTitle"
    v-model="dialogVisible"
    width="600px"
    @close="reset"
  >
    <el-form v-if="mode !== 'detail'" :model="form" label-width="100px" :rules="rules" ref="formRef">
      <el-form-item label="客户名称" prop="name">
        <el-input v-model="form.name" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="form.contactPerson" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" :disabled="mode === 'detail'" placeholder="多个邮箱用逗号分隔" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="客户来源" prop="source">
        <el-input v-model="form.source" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="客户等级" prop="level">
        <el-select v-model="form.level" :disabled="mode === 'detail'" placeholder="选择客户等级">
          <el-option label="潜在客户" :value="1" />
          <el-option label="意向客户" :value="2" />
          <el-option label="VIP客户" :value="3" />
          <el-option label="已成交客户" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户状态" prop="status">
        <el-select v-model="form.status" :disabled="mode === 'detail'" placeholder="选择客户状态">
          <el-option label="潜在客户" :value="1" />
          <el-option label="意向客户" :value="2" />
          <el-option label="VIP客户" :value="3" />
          <el-option label="已成交客户" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" :disabled="mode === 'detail'" />
      </el-form-item>
    </el-form>
    <div v-else class="detail-container">
      <div class="detail-item">
        <div class="detail-label">客户名称：</div>
        <div class="detail-content">{{ form.name }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">联系人：</div>
        <div class="detail-content">{{ form.contactPerson }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">联系电话：</div>
        <div class="detail-content">{{ form.phone }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">邮箱：</div>
        <div class="detail-content">{{ formatEmail(form.email) }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">地址：</div>
        <div class="detail-content">{{ form.address || '无' }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">客户来源：</div>
        <div class="detail-content">{{ form.source || '无' }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">客户等级：</div>
        <div class="detail-content">{{ form.level || '无' }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">客户状态：</div>
        <div class="detail-content">{{ getStatusText(form.status) }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">创建时间：</div>
        <div class="detail-content">{{ formatTime(form.createTime) }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">更新时间：</div>
        <div class="detail-content">{{ formatTime(form.updateTime) }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">备注：</div>
        <div class="detail-content">{{ form.remark || '无' }}</div>
      </div>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">{{ mode === 'detail' ? '关闭' : '取消' }}</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { addCustomer, updateCustomer } from '@/api/customer'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: Boolean,
  mode: {
    type: String,
    default: 'detail' // add | edit | detail
  },
  customer: Object
})
const emit = defineEmits(['update:modelValue', 'refresh'])



// 客户状态文本映射
function getStatusText(status) {
  const statusMap = {
    1: '潜在客户',
    2: '意向客户',
    3: 'VIP客户', 
    4: '已成交客户'
  }
  return statusMap[status] || '未知'
}

// 格式化邮箱显示
function formatEmail(email) {
  if (!email) return '无'
  if (Array.isArray(email)) {
    return email.join(', ')
  }
  return email
}

// 格式化时间显示
function formatTime(time) {
  if (!time) return '无'
  return time.replace('T', ' ')
}

// 创建计算属性代理双向绑定
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 表单引用和定义
const formRef = ref(null)
const form = ref({
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  source: '',
  level: null,
  status: null,
  remark: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入客户名称', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
}

// 替换所有visible引用为modelValue
watch(() => props.modelValue, (val) => {
  if (val && props.customer) {
    form.value = { ...props.customer }
  } else if (val) {
    form.value = {
      name: '',
      contactPerson: '',
      phone: '',
      email: '',
      address: '',
      source: '',
      level: null,
      status: null,
      remark: ''
    }
  }
})

function reset() {
  dialogVisible.value = false
}

const modeTitle = computed(() => {
  if (props.mode === 'add') return '新增客户'
  if (props.mode === 'edit') return '编辑客户'
  return '客户详情'
})

async function onSubmit() {
  try {
    // 表单验证
    await formRef.value.validate()
    
    if (props.mode === 'add') {
      const res = await addCustomer(form.value)
      if (res.data && res.data.code === 0) {
        ElMessage.success('新增成功')
        emit('update:modelValue', false)
        emit('refresh')
      } else {
        ElMessage.error(res.data?.msg || '新增失败')
      }
    } else if (props.mode === 'edit') {
      const res = await updateCustomer(form.value)
      if (res.data && res.data.code === 0) {
        ElMessage.success('编辑成功')
        emit('update:modelValue', false)
        emit('refresh')
      } else {
        ElMessage.error(res.data?.msg || '编辑失败')
      }
    }
  } catch (error) {
    if (error !== false) { // 表单验证失败不显示错误
      console.error('提交失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}
</script>

<style scoped>
.detail-container {
  padding: 10px;
}

.detail-item {
  margin-bottom: 16px;
  display: flex;
}

.detail-label {
  width: 100px;
  color: #606266;
  font-weight: 500;
}

.detail-content {
  color: #303133;
  flex: 1;
}
</style>
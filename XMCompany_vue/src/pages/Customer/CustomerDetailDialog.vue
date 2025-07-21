<template>
  <el-dialog
    :title="modeTitle"
    v-model="visible"
    width="600px"
    @close="reset"
  >
    <el-form v-if="mode !== 'detail'" :model="form" label-width="100px" :rules="rules">
      <el-form-item label="客户名称" prop="name">
        <el-input v-model="form.name" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="form.contactPerson" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" :disabled="mode === 'detail'" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" :disabled="mode === 'detail'" />
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
        <div class="detail-content">{{ form.contactPhone }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">地址：</div>
        <div class="detail-content">{{ form.address }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">邮箱：</div>
        <div class="detail-content">{{ form.email }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">备注：</div>
        <div class="detail-content">{{ form.remark || '无' }}</div>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">{{ mode === 'detail' ? '关闭' : '取消' }}</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { addCustomer, updateCustomer } from '@/api/customer'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  mode: {
    type: String,
    default: 'detail' // add | edit | detail
  },
  customer: Object
})
const emit = defineEmits(['update:visible', 'refresh'])

const form = ref({
  name: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  email: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

watch(() => props.visible, (val) => {
  if (val && props.customer) {
    form.value = { ...props.customer }
  } else if (val) {
    form.value = {
      name: '',
      contactPerson: '',
      contactPhone: '',
      address: '',
      email: '',
      remark: ''
    }
  }
})

function reset() {
  emit('update:visible', false)
}

const modeTitle = computed(() => {
  if (props.mode === 'add') return '新增客户'
  if (props.mode === 'edit') return '编辑客户'
  return '客户详情'
})

async function onSubmit() {
  try {
    if (props.mode === 'add') {
      await addCustomer(form.value)
      ElMessage.success('新增成功')
    } else if (props.mode === 'edit') {
      await updateCustomer(form.value)
      ElMessage.success('编辑成功')
    }
    emit('update:visible', false)
    emit('refresh')
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
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
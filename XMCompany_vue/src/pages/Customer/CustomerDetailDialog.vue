<template>
  <el-dialog
    :title="modeTitle"
    v-model="dialogVisible"
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

// 创建计算属性代理双向绑定
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 替换所有visible引用为modelValue
watch(() => props.modelValue, (val) => {
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
  dialogVisible.value = false
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
    emit('update:modelValue', false)
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
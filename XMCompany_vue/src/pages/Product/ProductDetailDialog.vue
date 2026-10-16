<template>
  <el-dialog
    :title="modeTitle"
    v-model="visible"
    width="600px"
    @close="reset"
  >
    <el-form v-if="mode !== 'detail'" :model="form" label-width="100px" :rules="rules" ref="formRef">
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="form.productName" />
      </el-form-item>
      <el-form-item label="产品类别" prop="category">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="规格" prop="specification">
        <el-input v-model="form.specification" />
      </el-form-item>
      <el-form-item label="单位" prop="unit">
        <el-select v-model="form.unit" placeholder="请选择单位">
          <el-option label="个" value="个" />
          <el-option label="件" value="件" />
          <el-option label="kg" value="kg" />
          <el-option label="米" value="米" />
        </el-select>
      </el-form-item>
      <el-form-item label="单价" prop="price">
        <el-input-number v-model="form.price" :precision="2" :min="0" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="form.status">
          <el-option label="在售" value="在售" />
          <el-option label="停售" value="停售" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    
    <div v-else class="detail-content">
      <p><strong>产品名称：</strong>{{ form.productName }}</p>
      <p><strong>产品类别：</strong>{{ form.category }}</p>
      <p><strong>规格：</strong>{{ form.specification }}</p>
      <p><strong>单位：</strong>{{ form.unit }}</p>
      <p><strong>单价：</strong>￥{{ form.price }}</p>
      <p><strong>状态：</strong>{{ form.status }}</p>
      <p><strong>描述：</strong>{{ form.description }}</p>
    </div>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { addProduct, updateProduct } from '@/api/product'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  mode: String,
  product: Object
})

const emit = defineEmits(['update:visible', 'refresh'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const modeTitle = computed(() => {
  const titles = { add: '新增产品', edit: '编辑产品', detail: '产品详情' }
  return titles[props.mode] || '产品信息'
})

const form = ref({
  productName: '',
  category: '',
  specification: '',
  unit: '',
  price: 0,
  status: '在售',
  description: ''
})

const rules = {
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入产品类别', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const formRef = ref()

watch(() => props.product, (newVal) => {
  if (newVal) {
    Object.assign(form.value, newVal)
  }
}, { immediate: true })

function reset() {
  form.value = {
    productName: '',
    category: '',
    specification: '',
    unit: '',
    price: 0,
    status: '在售',
    description: ''
  }
  formRef.value?.resetFields()
}

function handleSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      const api = props.mode === 'add' ? addProduct : updateProduct
      api(form.value).then(() => {
        ElMessage.success(props.mode === 'add' ? '新增成功' : '更新成功')
        visible.value = false
        emit('refresh')
      })
    }
  })
}
</script>

<style scoped>
.detail-content p {
  margin: 10px 0;
}
</style>
<template>
  <el-dialog
    :title="modeTitle"
    v-model="visible"
    width="700px"
    @close="reset"
  >
    <el-form v-if="mode !== 'detail'" :model="form" label-width="100px" :rules="rules" ref="formRef">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单编号" prop="orderNo">
            <el-input v-model="form.orderNo" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="form.customerName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="产品名称" prop="productName">
            <el-input v-model="form.productName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单价" prop="unitPrice">
            <el-input-number v-model="form.unitPrice" :precision="2" :min="0" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="总金额">
            <el-input :value="totalAmount" readonly />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单日期" prop="orderDate">
            <el-date-picker v-model="form.orderDate" type="date" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="交付日期" prop="deliveryDate">
            <el-date-picker v-model="form.deliveryDate" type="date" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    
    <div v-else class="detail-content">
      <el-row :gutter="20">
        <el-col :span="12">
          <p><strong>订单编号：</strong>{{ form.orderNo }}</p>
          <p><strong>客户名称：</strong>{{ form.customerName }}</p>
          <p><strong>产品名称：</strong>{{ form.productName }}</p>
          <p><strong>数量：</strong>{{ form.quantity }}</p>
        </el-col>
        <el-col :span="12">
          <p><strong>单价：</strong>￥{{ form.unitPrice }}</p>
          <p><strong>总金额：</strong>￥{{ form.totalAmount }}</p>
          <p><strong>订单日期：</strong>{{ form.orderDate }}</p>
          <p><strong>交付日期：</strong>{{ form.deliveryDate }}</p>
        </el-col>
      </el-row>
      <p><strong>备注：</strong>{{ form.remark }}</p>
    </div>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { addSaleOrder, updateSaleOrder } from '@/api/sale'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  mode: String,
  order: Object
})

const emit = defineEmits(['update:visible', 'refresh'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const modeTitle = computed(() => {
  const titles = { add: '新增订单', edit: '编辑订单', detail: '订单详情' }
  return titles[props.mode] || '订单信息'
})

const form = ref({
  orderNo: '',
  customerName: '',
  productName: '',
  quantity: 1,
  unitPrice: 0,
  orderDate: new Date(),
  deliveryDate: new Date(),
  remark: ''
})

const totalAmount = computed(() => {
  return (form.value.quantity * form.value.unitPrice).toFixed(2)
})

const rules = {
  orderNo: [{ required: true, message: '请输入订单编号', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'change' }]
}

const formRef = ref()

watch(() => props.order, (newVal) => {
  if (newVal) {
    Object.assign(form.value, newVal)
  }
}, { immediate: true })

function reset() {
  form.value = {
    orderNo: '',
    customerName: '',
    productName: '',
    quantity: 1,
    unitPrice: 0,
    orderDate: new Date(),
    deliveryDate: new Date(),
    remark: ''
  }
  formRef.value?.resetFields()
}

function handleSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      const submitData = {
        ...form.value,
        totalAmount: totalAmount.value
      }
      const api = props.mode === 'add' ? addSaleOrder : updateSaleOrder
      api(submitData).then(() => {
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
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
            <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="form.customerName" placeholder="请输入客户名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="产品名称" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入产品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" placeholder="请输入数量" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单位" prop="unit">
            <el-input v-model="form.unit" placeholder="请输入单位" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单价" prop="unitPrice">
            <el-input-number v-model="form.unitPrice" :precision="2" :min="0" placeholder="请输入单价" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单日期" prop="orderDate">
            <el-date-picker v-model="form.orderDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="交付日期" prop="deliveryTime">
            <el-date-picker v-model="form.deliveryTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择交付日期" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    
    <div v-else class="detail-content">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ form.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ form.customerName }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ form.productName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ form.quantity }} {{ form.unit }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">￥{{ form.amount }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(form.statusName)">{{ form.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作员">{{ form.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="交付时间">{{ formatTime(form.deliveryTime) }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ formatTime(form.shippingTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(form.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(form.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ form.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="handleSubmit" :loading="saving">确定</el-button>
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
  const titles = { add: '新增订单', edit: '编辑订单', detail: '订单详情', modify: '修改订单' }
  return titles[props.mode] || '订单信息'
})

const form = ref({
  orderNo: '',
  customerName: '',
  productName: '',
  quantity: 1,
  unit: '',
  unitPrice: 0,
  orderDate: new Date(),
  deliveryTime: '',
  remark: '',
  // 详情字段
  id: null,
  customerId: null,
  amount: 0,
  status: 0,
  statusName: '',
  shippingTime: '',
  operatorName: '',
  createTime: '',
  updateTime: ''
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
const saving = ref(false)

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 获取状态标签类型
function getStatusType(statusName) {
  const typeMap = {
    '待处理': 'info',
    '已确认': 'warning',
    '已发货': 'primary',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return typeMap[statusName] || 'info'
}

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
    unit: '',
    unitPrice: 0,
    orderDate: new Date(),
    deliveryTime: '',
    remark: '',
    // 详情字段
    id: null,
    customerId: null,
    amount: 0,
    status: 0,
    statusName: '',
    shippingTime: '',
    operatorName: '',
    createTime: '',
    updateTime: ''
  }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (props.mode === 'detail') return
  
  try {
    await formRef.value.validate()
    saving.value = true
    
    const submitData = {
      ...form.value,
      totalAmount: totalAmount.value
    }
    
    const api = props.mode === 'add' ? addSaleOrder : updateSaleOrder
    const res = await api(submitData)
    
    if (res.data && res.data.code === 0) {
      ElMessage.success(props.mode === 'add' ? '新增成功' : '更新成功')
      visible.value = false
      emit('refresh')
    } else {
      ElMessage.error(res.data?.msg || '操作失败')
    }
  } catch (error) {
    if (error !== false) { // 表单验证失败不显示错误
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.detail-content {
  padding: 10px 0;
}

.detail-content p {
  margin: 10px 0;
}
</style>
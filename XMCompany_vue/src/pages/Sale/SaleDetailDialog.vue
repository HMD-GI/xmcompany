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
          <el-form-item label="客户名称" prop="customerName">
            <el-select 
              v-model="form.customerName" 
              placeholder="请选择客户" 
              filterable
              remote
              :remote-method="searchCustomers"
              :loading="customerLoading"
              @change="handleCustomerChange"
              style="width: 100%"
            >
              <el-option
                v-for="customer in customerList"
                :key="customer.id"
                :label="customer.name"
                :value="customer.name"
              />
            </el-select>
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
          <el-form-item label="订单总价" prop="amount">
            <el-input-number v-model="form.amount" :precision="2" :min="0" placeholder="请输入订单总价" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="交付日期" prop="deliveryTime">
            <el-date-picker v-model="form.deliveryTime" type="date" value-format="YYYY-MM-DD" placeholder="选择交付日期" />
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
import { getCustomerList } from '@/api/customer'
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
  customerName: '',
  customerId: null, // 添加客户ID字段
  productName: '',
  quantity: 1,
  unit: '',
  amount: 0, // 订单总价
  deliveryTime: new Date(), // 交付日期
  remark: '',
  // 详情字段
  id: null,
  status: 0,
  statusName: '',
  shippingTime: '',
  operatorName: '',
  createTime: '',
  updateTime: ''
})

const totalAmount = computed(() => {
  // amount就是订单总价
  return form.value.amount.toFixed(2)
})

const rules = {
  customerName: [{ required: true, message: '请选择客户', trigger: 'change' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入订单总价', trigger: 'blur' }],
  deliveryTime: [{ required: true, message: '请选择交付日期', trigger: 'change' }]
}

const formRef = ref()
const saving = ref(false)

// 当父组件把 visible 设置为 false（例如切换模式前）也要重置表单
watch(() => props.visible, val => {
  if (!val) {
    reset()
  }
})

// 客户相关数据
const customerList = ref([])
const customerLoading = ref(false)

// 格式化时间显示
function formatTime(time) {
  if (time === null || time === undefined || time === '') return '-'
  // 如果是字符串，比如后端返回的 ISO 时间
  if (typeof time === 'string') {
    return time.replace('T', ' ')
  }
  // 如果是 Date 对象
  if (time instanceof Date) {
    const pad = n => (n < 10 ? '0' + n : n)
    return `${time.getFullYear()}-${pad(time.getMonth() + 1)}-${pad(time.getDate())} ${pad(
      time.getHours()
    )}:${pad(time.getMinutes())}:${pad(time.getSeconds())}`
  }
  // 其它类型，尝试转换为字符串
  try {
    return String(time)
  } catch (e) {
    return '-'
  }
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

// 搜索客户列表
async function searchCustomers(query = '') {
  customerLoading.value = true
  try {
    const res = await getCustomerList({
      name: query,
      currentPage: 1,
      pageSize: 50
    })
    if (res.data?.code === 0) {
      customerList.value = res.data.data?.list || []
    } else {
      customerList.value = []
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
    customerList.value = []
  } finally {
    customerLoading.value = false
  }
}

// 客户选择变化处理
function handleCustomerChange(selectedName) {
  if (selectedName) {
    const selectedCustomer = customerList.value.find(customer => customer.name === selectedName)
    if (selectedCustomer) {
      form.value.customerId = selectedCustomer.id
    } else {
      // 如果在当前列表中找不到，重新搜索
      searchCustomers(selectedName).then(() => {
        const foundCustomer = customerList.value.find(customer => customer.name === selectedName)
        if (foundCustomer) {
          form.value.customerId = foundCustomer.id
        } else {
          form.value.customerId = null
          ElMessage.warning('未找到匹配的客户')
        }
      })
    }
  } else {
    form.value.customerId = null
  }
}

watch(() => props.order, (newVal) => {
  if (newVal) {
    // 确保状态值转换为字符串
    const statusValue = Number(newVal.status)
    const statusMap = {
      0: '待处理',
      1: '已确认',
      2: '已发货',
      3: '已完成',
      4: '已取消'
    }
    
    Object.assign(form.value, {
      ...newVal,
      statusName: newVal.statusName || statusMap[statusValue] || '未知状态'
    })
    
    // 如果是新增模式，设置默认值
    if (props.mode === 'add') {
      form.value.deliveryTime = new Date() // 交付日期
      form.value.quantity = 1
      form.value.amount = 0 // 订单总价
      // 预加载客户列表
      searchCustomers()
    }
  } else if (props.mode === 'add') {
    // 新增模式下的默认值
    form.value.deliveryTime = new Date() // 交付日期
    form.value.quantity = 1
    form.value.amount = 0 // 订单总价
    // 预加载客户列表
    searchCustomers()
  }
}, { immediate: true })

function reset() {
  form.value = {
    customerName: '',
    customerId: null,
    productName: '',
    quantity: 1,
    unit: '',
    amount: 0, // 订单总价
    deliveryTime: new Date(), // 交付日期
    remark: '',
    // 详情字段
    id: null,
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
    
    // 验证客户ID是否存在
    if (!form.value.customerId) {
      ElMessage.error('请选择有效的客户')
      return
    }
    
    const submitData = {
      id: form.value.id, // 编辑时需要传递订单 ID
      customerId: form.value.customerId,
      productName: form.value.productName,
      quantity: form.value.quantity,
      unit: form.value.unit,
      amount: totalAmount.value,
      deliveryTime: form.value.deliveryTime,
      remark: form.value.remark
    }
    
    const api = props.mode === 'add' ? addSaleOrder : updateSaleOrder
    console.log('提交数据:', submitData) // 调试用
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
      if (error.response) {
        console.error('响应错误:', error.response.data)
        ElMessage.error(`操作失败: ${error.response.data?.msg || error.message}`)
      } else {
        ElMessage.error(`操作失败: ${error.message}`)
      }
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
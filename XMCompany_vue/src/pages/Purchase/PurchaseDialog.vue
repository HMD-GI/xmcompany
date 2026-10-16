<template>
  <el-dialog
    :title="modeTitle"
    :modelValue="visible"
    @update:modelValue="$emit('update:visible', $event)"
    width="600px"
    @close="reset"
  >
    <el-form v-if="mode !== 'detail'" :model="form" label-width="100px">
      <el-form-item label="订单编号">
        <el-input v-model="form.orderNo" />
      </el-form-item>
      <el-form-item label="供应商ID">
        <el-input v-model="form.supplierId" />
      </el-form-item>
      <el-form-item label="订单日期">
        <el-date-picker v-model="form.orderDate" type="date" />
      </el-form-item>
      <el-form-item label="总金额">
        <el-input v-model="form.totalAmount" />
      </el-form-item>
      <!-- 可扩展采购明细子表 -->
    </el-form>
    <div v-else>
      <p>订单编号：{{ form.orderNo }}</p>
      <p>供应商ID：{{ form.supplierId }}</p>
      <p>订单日期：{{ form.orderDate }}</p>
      <p>总金额：{{ form.totalAmount }}</p>
      <!-- 可扩展采购明细子表 -->
    </div>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button v-if="mode !== 'detail'" type="primary" @click="onSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { addPurchaseOrder, updatePurchaseOrder } from '@/api/purchase'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  mode: String, // add | edit | detail
  order: Object
})
const emit = defineEmits(['update:visible', 'refresh'])

const form = ref({ orderNo: '', supplierId: '', orderDate: '', totalAmount: '' })

watch(() => props.visible, (val) => {
  if (val && props.order) {
    form.value = { ...props.order }
  } else if (val) {
    form.value = { orderNo: '', supplierId: '', orderDate: '', totalAmount: '' }
  }
})

function reset() {
  emit('update:visible', false)
}

const modeTitle = computed(() => {
  if (props.mode === 'add') return '新增采购订单'
  if (props.mode === 'edit') return '编辑采购订单'
  return '采购订单详情'
})

async function onSubmit() {
  if (props.mode === 'add') {
    await addPurchaseOrder(form.value)
    ElMessage.success('新增成功')
  } else if (props.mode === 'edit') {
    await updatePurchaseOrder(form.value)
    ElMessage.success('编辑成功')
  }
  emit('update:visible', false)
  emit('refresh')
}
</script> 
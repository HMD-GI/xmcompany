<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="物料名称">
        <el-input v-model="query.materialName" placeholder="输入物料名称" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="handleStockIn">入库</el-button>
      <el-button type="warning" @click="handleStockOut">出库</el-button>
    </el-form>

    <!-- 库存详情/编辑对话框 -->
    <el-dialog
      :title="dialogMode === 'detail' ? '库存详情' : '编辑库存'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="100px" :disabled="dialogMode === 'detail'">
        <el-form-item label="物料名称">
          <el-input v-model="form.materialName" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number v-model="form.quantity" :min="0" />
        </el-form-item>
        <el-form-item label="最后入库时间">
          <el-input :value="formatTime(form.lastStockInTime)" disabled />
        </el-form-item>
        <el-form-item label="最后出库时间">
          <el-input :value="formatTime(form.lastStockOutTime)" disabled />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input :value="formatTime(form.createTime)" disabled />
        </el-form-item>
        <el-form-item label="更新时间">
          <el-input :value="formatTime(form.updateTime)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode === 'edit'" type="primary" @click="handleSubmit">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 入库对话框 -->
    <el-dialog
      title="物料入库"
      v-model="stockInDialogVisible"
      width="500px"
    >
      <el-form :model="stockInForm" label-width="100px" ref="stockInFormRef">
        <el-form-item label="物料名称" prop="materialName" :rules="[{ required: true, message: '请输入物料名称', trigger: 'blur' }]">
          <el-input v-model="stockInForm.materialName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="单位" prop="unit" :rules="[{ required: true, message: '请输入单位', trigger: 'blur' }]">
          <el-input v-model="stockInForm.unit" placeholder="请输入单位（如：个、件、kg等）" />
        </el-form-item>
        <el-form-item label="入库数量" prop="quantity" :rules="[{ required: true, message: '请输入入库数量', trigger: 'blur' }]">
          <el-input-number v-model="stockInForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockInForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStockInSubmit">确认入库</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      title="物料出库"
      v-model="stockOutDialogVisible"
      width="500px"
    >
      <el-form :model="stockOutForm" label-width="100px" ref="stockOutFormRef">
        <el-form-item label="物料名称">
          <el-input v-model="stockOutForm.materialName" disabled />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="stockOutForm.unit" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="stockOutForm.currentQuantity" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity" :rules="[{ required: true, message: '请输入出库数量', trigger: 'blur' }, { validator: validateOutQuantity, trigger: 'blur' }]">
          <el-input-number v-model="stockOutForm.quantity" :min="1" :max="stockOutForm.currentQuantity" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockOutForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockOutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStockOutSubmit">确认出库</el-button>
        </span>
      </template>
    </el-dialog>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="materialName" label="物料名称" min-width="200" />
      <el-table-column prop="unit" label="单位" width="150" />
      <el-table-column prop="quantity" label="库存数量" width="150" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" size="small" @click="handleRowStockIn(row)">入库</el-button>
          <el-button type="danger" size="small" @click="handleRowStockOut(row)">出库</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.currentPage"
      v-model:page-size="query.pageSize"
      :total="total"
      @current-change="fetchList"
      @size-change="fetchList"
      layout="total, prev, pager, next, sizes"
      :page-sizes="[10, 20, 50]"
      style="margin-top: 16px;"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStockList, getStockById, stockIn, stockOut } from '@/api/stock'
import { ElMessage } from 'element-plus'

const query = ref({ currentPage: 1, pageSize: 10, materialName: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('detail') // 'detail' | 'edit'
const form = ref({
  materialName: '',
  unit: '',
  quantity: 0,
  lastStockInTime: '',
  lastStockOutTime: '',
  createTime: '',
  updateTime: ''
})

// 入库相关数据
const stockInDialogVisible = ref(false)
const stockInFormRef = ref()
const stockInForm = ref({
  materialName: '',
  unit: '',
  quantity: 1,
  remark: ''
})

// 出库相关数据
const stockOutDialogVisible = ref(false)
const stockOutFormRef = ref()
const stockOutForm = ref({
  stockId: 0,
  materialName: '',
  unit: '',
  currentQuantity: 0,
  quantity: 1,
  remark: ''
})

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return ''
  // 将 2025-07-23T18:03:26 格式转换为 2025-07-23 18:03:26
  return timeStr.replace('T', ' ')
}

// 处理查看详情
function handleDetail(row) {
	dialogMode.value = 'detail'
	getStockById(row.id).then(res => {
		const ok = res && res.data && (res.data.code === 0 || res.data.success === true)
		const data = ok ? (res.data.data || {}) : {}
		if (!ok) {
			ElMessage.error((res && res.data && res.data.msg) || '获取库存详情失败')
			return
		}
		form.value = {
			...data,
			lastStockInTime: data.lastStockInTime || '',
			lastStockOutTime: data.lastStockOutTime || '',
			createTime: data.createTime || '',
			updateTime: data.updateTime || ''
		}
		dialogVisible.value = true
	}).catch(() => {
		ElMessage.error('获取库存详情失败')
	})
}

// 处理编辑
function handleEdit(row) {
  dialogMode.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理提交
function handleSubmit() {
  // TODO: 调用更新库存的API
  ElMessage.success('更新成功')
  dialogVisible.value = false
  fetchList() // 刷新列表
}

function fetchList() {
  loading.value = true
  getStockList(query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

// 出库数量验证
function validateOutQuantity(rule, value, callback) {
  if (value > stockOutForm.value.currentQuantity) {
    callback(new Error('出库数量不能大于当前库存'))
  } else {
    callback()
  }
}

// 处理入库按钮点击（顶部按钮）
function handleStockIn() {
  stockInForm.value = {
    materialName: '',
    unit: '',
    quantity: 1,
    remark: ''
  }
  stockInDialogVisible.value = true
}

// 处理出库按钮点击（顶部按钮）
function handleStockOut() {
  if (list.value.length === 0) {
    ElMessage.warning('暂无库存数据，无法进行出库操作')
    return
  }
  ElMessage.info('请在表格中选择具体的库存记录进行出库操作')
}

// 处理行内入库按钮点击
function handleRowStockIn(row) {
  stockInForm.value = {
    materialName: row.materialName,
    unit: row.unit,
    quantity: 1,
    remark: ''
  }
  stockInDialogVisible.value = true
}

// 处理行内出库按钮点击
function handleRowStockOut(row) {
  if (row.quantity <= 0) {
    ElMessage.warning('当前库存为0，无法进行出库操作')
    return
  }
  stockOutForm.value = {
    stockId: row.id,
    materialName: row.materialName,
    unit: row.unit,
    currentQuantity: row.quantity,
    quantity: 1,
    remark: ''
  }
  stockOutDialogVisible.value = true
}

// 处理入库提交
function handleStockInSubmit() {
  stockInFormRef.value.validate((valid) => {
    if (valid) {
      const data = {
        materialName: stockInForm.value.materialName,
        unit: stockInForm.value.unit,
        quantity: stockInForm.value.quantity,
        remark: stockInForm.value.remark
      }
      
      stockIn(data).then(res => {
        const ok = res && res.data && (res.data.code === 0 || res.data.success === true)
        if (ok) {
          ElMessage.success('入库成功')
          stockInDialogVisible.value = false
          fetchList() // 刷新列表
        } else {
          ElMessage.error((res && res.data && res.data.msg) || '入库失败')
        }
      }).catch(() => {
        ElMessage.error('入库失败')
      })
    }
  })
}

// 处理出库提交
function handleStockOutSubmit() {
  stockOutFormRef.value.validate((valid) => {
    if (valid) {
      const data = {
        stockId: stockOutForm.value.stockId,
        quantity: stockOutForm.value.quantity,
        remark: stockOutForm.value.remark,
        materialName: stockOutForm.value.materialName,
        unit: stockOutForm.value.unit
      }
      
      stockOut(data).then(res => {
        const ok = res && res.data && (res.data.code === 0 || res.data.success === true)
        if (ok) {
          ElMessage.success('出库成功')
          stockOutDialogVisible.value = false
          fetchList() // 刷新列表
        } else {
          ElMessage.error((res && res.data && res.data.msg) || '出库失败')
        }
      }).catch(() => {
        ElMessage.error('出库失败')
      })
    }
  })
}

onMounted(fetchList)
</script>
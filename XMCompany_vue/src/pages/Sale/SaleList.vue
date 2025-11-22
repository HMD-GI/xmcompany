<template>
  <div class="sale-list-container">
    <!-- 查询表单 -->
    <el-card class="search-card" shadow="never">
      <el-form inline>
        <el-form-item label="订单编号">
          <el-input 
            v-model="orderNo" 
            placeholder="请输入订单编号" 
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input 
            v-model="customerName" 
            placeholder="请输入客户名称" 
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input 
            v-model="productName" 
            placeholder="请输入产品名称" 
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select 
            v-model="status" 
            placeholder="请选择状态"
            style="width: 200px"
            clearable
            @clear="handleSearch"
          >
            <el-option label="待处理" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">销售订单列表</span>
            <el-tag type="info" effect="plain" round>共 {{ total }} 条</el-tag>
          </div>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增订单</el-button>
        </div>
      </template>

      <el-table 
        :data="list" 
        style="width: 100%" 
        v-loading="loading"
        border
        stripe
        highlight-current-row
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="productName" label="产品名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" min-width="100" align="right">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(getStatusName(row.status))">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="450" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0"
              link 
              type="primary" 
              :icon="EditPen" 
              @click="handleEdit(row)"
            >编辑</el-button>
            <el-button 
              v-if="row.status === 0"
              link 
              type="danger" 
              :icon="Delete" 
              @click="handleDelete(row)"
            >删除</el-button>
            <el-button 
              link 
              type="primary" 
              :icon="Edit" 
              @click="handleModify(row)"
            >修改</el-button>
            <el-dropdown 
              v-if="row.status !== 3 && row.status !== 4"
              @command="(command) => handleStatusUpdate(row, command)"
            >
              <el-button link type="primary" :icon="Setting">
                更新状态<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    v-for="(label, value) in getNextStatusOptions(row.status)"
                    :key="value"
                    :command="value"
                  >
                    {{ label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.currentPage"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <SaleDetailDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :order="currentOrder"
      @refresh="handleSearch"
    />
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSaleOrderList, deleteSaleOrder, updateSaleOrderStatus } from '@/api/sale'
import SaleDetailDialog from './SaleDetailDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  RefreshRight, 
  Plus, 
  View, 
  EditPen, 
  Delete,
  Setting,
  ArrowDown,
  Edit
} from '@element-plus/icons-vue'

// 对话框相关状态
const dialogVisible = ref(false)
const dialogMode = ref('add')
const currentOrder = ref(null)

// 查询条件
const orderNo = ref('') // 订单编号
const customerName = ref('') // 客户名称
const productName = ref('') // 产品名称
const status = ref(null) // 订单状态

// 分页参数
const query = reactive({
  currentPage: 1,
  pageSize: 10
})

// 数据相关
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 获取列表数据
async function fetchList() {
  loading.value = true
  try {
    const res = await getSaleOrderList({
      orderNo: orderNo.value,
      customerName: customerName.value,
      productName: productName.value,
      status: status.value,
      currentPage: query.currentPage,
      pageSize: query.pageSize
    })
    if (res.data?.code === 0) {
      // 确保状态值是数字类型
      list.value = (res.data.data.list || []).map(item => ({
        ...item,
        status: Number(item.status) // 转换为数字
      }))
      total.value = res.data.data.total || 0
      console.log('List data:', list.value) // 调试用
    } else {
      ElMessage.error(res.data?.msg || '获取销售订单列表失败')
    }
  } catch (error) {
    console.error('获取销售订单列表失败:', error)
    ElMessage.error('获取销售订单列表失败')
  } finally {
    loading.value = false
  }
}

// 查询处理
function handleSearch() {
  query.currentPage = 1
  fetchList()
}

// 重置查询
function handleReset() {
  orderNo.value = ''
  customerName.value = ''
  productName.value = ''
  status.value = null
  handleSearch()
}

// 分页处理
function handleSizeChange(val) {
  query.pageSize = val
  fetchList()
}

function handleCurrentChange(val) {
  query.currentPage = val
  fetchList()
}

// 获取状态标签类型
function getStatusType(status) {
  const types = {
    '待处理': 'info',
    '已确认': 'warning',
    '已发货': 'primary',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态名称
function getStatusName(status) {
  status = Number(status) // 确保是数字类型
  const statusMap = {
    0: '待处理',
    1: '已确认',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 操作处理
function handleAdd() {
  dialogMode.value = 'add'
  currentOrder.value = null
  dialogVisible.value = true
}

function handleDetail(row) {
  dialogMode.value = 'detail'
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogMode.value = 'edit'
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

function handleModify(row) {
  dialogMode.value = 'modify'
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(
    '确定要删除该订单吗？此操作不可恢复',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteSaleOrder(row.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 获取可更新的下一状态选项
function getNextStatusOptions(currentStatus) {
  const options = {}
  switch (currentStatus) {
    case 0: // 待处理
      options[1] = '确认订单'
      options[4] = '取消订单'
      break
    case 1: // 已确认
      options[2] = '发货'
      options[4] = '取消订单'
      break
    case 2: // 已发货
      options[3] = '完成订单'
      break
  }
  return options
}

// 处理状态更新
async function handleStatusUpdate(row, newStatus) {
  try {
    const res = await updateSaleOrderStatus({
      id: row.id,
      status: newStatus
    })
    if (res.data?.code === 0) {
      ElMessage.success('状态更新成功')
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('更新订单状态失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch()
})
</script>
<style scoped>
.sale-list-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left .title {
  font-size: 16px;
  font-weight: 500;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-card__header) {
  padding: 12px 20px;
}

:deep(.el-card__body) {
  padding: 16px;
}

:deep(.el-table) {
  border-radius: 4px;
}
</style>
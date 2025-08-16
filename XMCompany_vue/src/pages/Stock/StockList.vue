<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="物料名称">
        <el-input v-model="query.materialName" placeholder="输入物料名称" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
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
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="materialName" label="物料名称" min-width="200" />
      <el-table-column prop="unit" label="单位" width="150" />
      <el-table-column prop="quantity" label="库存数量" width="150" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
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
import { getStockList } from '@/api/stock'
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
  quantity: 0
})

// 处理查看详情
function handleDetail(row) {
  dialogMode.value = 'detail'
  form.value = { ...row }
  dialogVisible.value = true
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

onMounted(fetchList)
</script> 
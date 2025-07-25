<template>
  <el-card class="product-card">
    <div class="card-header">
      <h2 class="card-title">产品管理</h2>
      <el-button type="success" @click="openAdd" icon="el-icon-plus">新增产品</el-button>
    </div>
    
    <el-form :inline="true" @submit.prevent="fetchList" class="search-form">
      <el-form-item label="产品名称">
        <el-input v-model="query.productName" placeholder="输入产品名称" clearable />
      </el-form-item>
      <el-form-item label="产品类别">
        <el-input v-model="query.category" placeholder="输入产品类别" clearable />
      </el-form-item>
      <el-button type="primary" @click="fetchList" icon="el-icon-search">查询</el-button>
      <el-button @click="resetQuery" icon="el-icon-refresh">重置</el-button>
    </el-form>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="产品ID" width="100" />
      <el-table-column prop="productName" label="产品名称" />
      <el-table-column prop="category" label="产品类别" />
      <el-table-column prop="specification" label="规格" />
      <el-table-column prop="unit" label="单位" />
      <el-table-column prop="price" label="单价" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === '在售' ? 'success' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
      class="pagination"
    />
    
    <ProductDetailDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :product="currentProduct"
      @refresh="fetchList"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductList, deleteProduct } from '@/api/product'
import ProductDetailDialog from './ProductDetailDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ currentPage: 1, pageSize: 10, productName: '', category: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('detail')
const currentProduct = ref(null)

function fetchList() {
  loading.value = true
  getProductList(query.value).then(res => {
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

function resetQuery() {
  query.value.productName = ''
  query.value.category = ''
  fetchList()
}

function openAdd() {
  dialogMode.value = 'add'
  currentProduct.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  dialogMode.value = 'edit'
  currentProduct.value = { ...row }
  dialogVisible.value = true
}

function openDetail(row) {
  dialogMode.value = 'detail'
  currentProduct.value = { ...row }
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除这个产品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteProduct(row.id).then(() => {
      ElMessage.success('删除成功')
      fetchList()
    })
  })
}

onMounted(fetchList)
</script>

<style scoped>
.product-card {
  margin: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
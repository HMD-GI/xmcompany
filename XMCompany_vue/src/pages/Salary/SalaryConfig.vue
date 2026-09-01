<template>
  <el-card>
    <div slot="header">
      <span>员工薪资配置</span>
    </div>
    
    <!-- 查询表单 -->
       <el-form :inline="true" :model="queryForm" ref="queryFormRef" style="margin-bottom: 20px;">
      <el-form-item label="员工 ID">
        <el-input v-model="queryForm.employeeId" placeholder="请输入员工 ID" clearable></el-input>
      </el-form-item>
      <el-form-item label="员工姓名">
        <el-input v-model="queryForm.employeeName" placeholder="请输入员工姓名" clearable></el-input>
      </el-form-item>
      <el-form-item label="银行卡号">
        <el-input v-model="queryForm.bankCardNo" placeholder="请输入银行卡号" clearable></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" style="width: 200px" clearable>
          <el-option label="无效" :value="0"></el-option>
          <el-option label="有效" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchSalaryConfigList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增薪资配置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 薪资配置表格 -->
    <el-table :data="salaryList" border v-loading="loading" style="width: 100%">
      <el-table-column prop="employeeId" label="员工ID" width="80"></el-table-column>
      <el-table-column prop="employeeName" label="员工姓名" width="120"></el-table-column>
      <el-table-column prop="bankCardNo" label="银行卡号" width="180"></el-table-column>
      <el-table-column prop="bankName" label="开户银行" width="150"></el-table-column>
      <el-table-column prop="basicSalary" label="基本工资" width="100"></el-table-column>
      <el-table-column prop="performanceBase" label="绩效基数" width="100"></el-table-column>
      <el-table-column prop="allowance" label="固定补贴" width="100"></el-table-column>
      <el-table-column prop="insuranceAmount" label="五险一金" width="100"></el-table-column>
      <el-table-column prop="totalBase" label="薪资合计" width="100"></el-table-column>
      <el-table-column prop="statusDesc" label="状态" width="80"></el-table-column>
      <el-table-column prop="effectiveDate" label="生效日期" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="viewSalaryConfig(row)">查看</el-button>
          <el-button size="small" @click="editSalaryConfig(row)">编辑</el-button>
          <el-button 
            size="small" 
            :type="row.statusDesc === '有效' ? 'danger' : 'success'"
            @click="toggleSalaryStatus(row)">
            {{ row.statusDesc === '有效' ? '设为无效' : '设为有效' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryForm.currentPage"
      v-model:page-size="queryForm.pageSize"
      :total="total"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      layout="total, prev, pager, next, sizes"
      :page-sizes="[10, 20, 50]"
      style="margin-top: 16px; text-align: right;"
    />
  </el-card>
  
  <!-- 新增/编辑薪资配置对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
    <el-form :model="salaryForm" :rules="rules" ref="salaryFormRef" label-width="120px" :disabled="isViewing">
      <el-form-item label="员工ID" prop="employeeId">
        <el-input v-model.number="salaryForm.employeeId" placeholder="请输入员工ID" :disabled="isEdit"></el-input>
      </el-form-item>
      
      <el-form-item label="银行卡号" prop="bankCardNo">
        <el-input v-model="salaryForm.bankCardNo" placeholder="请输入银行卡号"></el-input>
      </el-form-item>
      
      <el-form-item label="开户银行" prop="bankName">
        <el-input v-model="salaryForm.bankName" placeholder="请输入开户银行"></el-input>
      </el-form-item>
      
      <el-form-item label="基本工资" prop="basicSalary">
        <el-input 
          v-model.number="salaryForm.basicSalary" 
          placeholder="请输入基本工资"
          style="width: 100%">
        </el-input>
      </el-form-item>
      
      <el-form-item label="绩效基数" prop="performanceBase">
        <el-input 
          v-model.number="salaryForm.performanceBase" 
          placeholder="请输入绩效基数"
          style="width: 100%">
        </el-input>
      </el-form-item>
      
      <el-form-item label="固定补贴" prop="allowance">
        <el-input 
          v-model.number="salaryForm.allowance" 
          placeholder="请输入固定补贴"
          style="width: 100%">
        </el-input>
      </el-form-item>
      
      <el-form-item label="五险一金" prop="insuranceAmount">
        <el-input 
          v-model.number="salaryForm.insuranceAmount" 
          placeholder="请输入五险一金金额"
          style="width: 100%">
        </el-input>
      </el-form-item>
      
      <el-form-item label="备注" prop="remark">
        <el-input v-model="salaryForm.remark" type="textarea" placeholder="请输入备注"></el-input>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="handleCloseDialog">关闭</el-button>
      <el-button v-if="!isEdit && !isViewing" type="primary" @click="submitForm">添加</el-button>
      <el-button v-if="isEdit" type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { setSalary, getSalaryConfig, getSalaryConfigList, updateSalaryStatus } from '@/api/salary'

const queryForm = ref({
  employeeId: '',
  employeeName: '',
  bankCardNo: '',
  status: null,
  currentPage: 1,
  pageSize: 10
})

const salaryForm = ref({
  employeeId: '',
  employeeName: '',
  bankCardNo: '',
  bankName: '',
  basicSalary: 0,
  performanceBase: 0,
  allowance: 0,
  insuranceAmount: 0,
  remark: ''
})
// template refs for the form components
const queryFormRef = ref(null)
const salaryFormRef = ref(null)

const salaryList = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const isViewing = ref(false)

const dialogTitle = computed(() => {
  if (isEdit.value) {
    return '更新薪资配置'
  } else if (isViewing.value) {
    return '查看薪资配置'
  } else if (dialogVisible.value) {
    return '新增薪资配置'
  }
  return ''
})

const rules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' },
    { type: 'number', message: '员工ID必须为数字', trigger: 'blur' }
  ],
  bankCardNo: [
    { required: true, message: '请输入银行卡号', trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请输入开户银行', trigger: 'blur' }
  ],
  basicSalary: [
    { required: true, message: '请输入基本工资', trigger: 'blur' },
    { type: 'number', message: '基本工资必须为数字', trigger: 'blur' }
  ],
  performanceBase: [
    { required: true, message: '请输入绩效基数', trigger: 'blur' },
    { type: 'number', message: '绩效基数必须为数字', trigger: 'blur' }
  ],
  allowance: [
    { type: 'number', message: '固定补贴必须为数字', trigger: 'blur' }
  ],
  insuranceAmount: [
    { type: 'number', message: '五险一金必须为数字', trigger: 'blur' }
  ]
}

// 页面加载时获取数据
onMounted(() => {
  fetchSalaryConfigList()
})

// 获取薪资配置列表
const fetchSalaryConfigList = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: queryForm.value.currentPage,
      pageSize: queryForm.value.pageSize,
      employeeId: queryForm.value.employeeId ? Number(queryForm.value.employeeId) : null,
      employeeName: queryForm.value.employeeName,
      bankCardNo: queryForm.value.bankCardNo,
      status: queryForm.value.status
    }
    
    const res = await getSalaryConfigList(params)
    if (res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      salaryList.value = data.list || []
      total.value = data.total || 0
    } else {
      salaryList.value = []
      total.value = 0
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    salaryList.value = []
    total.value = 0
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询条件
const resetQuery = () => {
  // 直接重置每个字段，确保响应式更新
  queryForm.value.employeeId = ''
  queryForm.value.employeeName = ''
  queryForm.value.bankCardNo = ''
  queryForm.value.status = null
  queryForm.value.currentPage = 1
  queryForm.value.pageSize = 10
  
  // 如果有表单引用，也重置表单
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  
  // 重新获取列表
  fetchSalaryConfigList()
}

// 分页相关方法
const handlePageChange = (page) => {
  queryForm.value.currentPage = page
  fetchSalaryConfigList()
}

const handleSizeChange = (size) => {
  queryForm.value.pageSize = size
  queryForm.value.currentPage = 1
  fetchSalaryConfigList()
}

const openAddDialog = () => {
  isEdit.value = false
  isViewing.value = false
  salaryForm.value = {
    employeeId: '',
    employeeName: '',
    bankCardNo: '',
    bankName: '',
    basicSalary: 0,
    performanceBase: 0,
    allowance: 0,
    insuranceAmount: 0,
    remark: ''
  }
  dialogVisible.value = true
  
  // 清除表单验证状态
  if (salaryFormRef.value) {
    salaryFormRef.value.clearValidate()
  }
}

const handleCloseDialog = () => {
  dialogVisible.value = false
  isViewing.value = false
}

const querySalaryConfig = async () => {
  if (!queryForm.value.employeeId) {
    ElMessage.error('请输入员工ID')
    return
  }
  
  loading.value = true
  try {
    const res = await getSalaryConfig(queryForm.value.employeeId)
    if (res.data.code === 0 && res.data.data) {
      salaryList.value = [res.data.data]
      // 填充表单用于编辑
      salaryForm.value = {
        employeeId: res.data.data.employeeId,
        employeeName: res.data.data.employeeName,
        bankCardNo: res.data.data.bankCardNo,
        bankName: res.data.data.bankName,
        basicSalary: res.data.data.basicSalary,
        performanceBase: res.data.data.performanceBase,
        allowance: res.data.data.allowance,
        insuranceAmount: res.data.data.insuranceAmount,
        remark: res.data.data.remark
      }
    } else {
      salaryList.value = []
      ElMessage.error(res.data.msg || '未找到该员工的薪资配置')
    }
  } catch (err) {
    salaryList.value = []
    ElMessage.error('查询薪资配置失败')
  } finally {
    loading.value = false
  }
}

const viewSalaryConfig = async (row) => {
  isEdit.value = false
  isViewing.value = true
  loading.value = true
  try {
    const res = await getSalaryConfig(row.employeeId)
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      salaryForm.value = {
        employeeId: data.employeeId,
        employeeName: data.employeeName,
        bankCardNo: data.bankCardNo,
        bankName: data.bankName,
        basicSalary: data.basicSalary,
        performanceBase: data.performanceBase,
        allowance: data.allowance,
        insuranceAmount: data.insuranceAmount,
        remark: data.remark || ''
      }
      await nextTick()
      dialogVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取薪资配置详情失败')
    }
  } catch (err) {
    ElMessage.error('获取薪资配置详情失败')
  } finally {
    loading.value = false
  }
}

const editSalaryConfig = async (row) => {
  isEdit.value = true
  isViewing.value = false
  loading.value = true
  try {
    const res = await getSalaryConfig(row.employeeId)
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      salaryForm.value = {
        employeeId: data.employeeId,
        employeeName: data.employeeName,
        bankCardNo: data.bankCardNo,
        bankName: data.bankName,
        basicSalary: data.basicSalary,
        performanceBase: data.performanceBase,
        allowance: data.allowance,
        insuranceAmount: data.insuranceAmount,
        remark: data.remark || ''
      }
      await nextTick()
      dialogVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取薪资配置详情失败')
    }
  } catch (err) {
    ElMessage.error('获取薪资配置详情失败')
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  if (!salaryForm.value.employeeId) {
    ElMessage.error('请输入员工ID')
    return
  }
  
  const data = {
    employeeId: salaryForm.value.employeeId,
    bankCardNo: salaryForm.value.bankCardNo,
    bankName: salaryForm.value.bankName,
    basicSalary: salaryForm.value.basicSalary,
    performanceBase: salaryForm.value.performanceBase,
    allowance: salaryForm.value.allowance,
    insuranceAmount: salaryForm.value.insuranceAmount,
    remark: salaryForm.value.remark
  }
  
  try {
    const res = await setSalary(data)
    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || '薪资配置保存成功')
      dialogVisible.value = false
      // 查询并更新列表
      fetchSalaryConfigList()
    } else {
      ElMessage.error(res.data.msg || '薪资配置保存失败')
    }
  } catch (err) {
    ElMessage.error('薪资配置保存失败')
  }
}

const toggleSalaryStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要将该薪资配置${row.statusDesc === '有效' ? '设为无效' : '设为有效'}吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 计算新的状态值：如果当前是有效的(1)，则设为无效(0)；如果当前是无效的(0)，则设为有效(1)
    const newStatus = row.statusDesc === '有效' ? 0 : 1
    
    const res = await updateSalaryStatus(row.id, newStatus)
    if (res.data.code === 0) {
      ElMessage.success('状态更新成功')
      // 重新获取列表数据
      fetchSalaryConfigList()
    } else {
      ElMessage.error(res.data.msg || '状态更新失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('状态更新失败')
    }
  }
}

</script>

<style scoped>
.el-card {
  margin-bottom: 20px;
}
</style>
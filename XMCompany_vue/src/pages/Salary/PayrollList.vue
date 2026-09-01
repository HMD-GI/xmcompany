<template>
  <el-card>
    <div slot="header">
      <span>工资单管理</span>
    </div>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 按员工查询工资单 -->
      <el-tab-pane label="按员工查询" name="employee">
        <el-form :inline="true" :model="employeeQuery" ref="employeeQueryForm" style="margin-bottom: 20px;">
          <el-form-item label="员工ID">
            <el-input v-model.number="employeeQuery.employeeId" placeholder="请输入员工ID"></el-input>
          </el-form-item>
          <el-form-item label="员工姓名">
            <el-input v-model="employeeQuery.employeeName" placeholder="请输入员工姓名"></el-input>
          </el-form-item>
          <el-form-item label="薪资月份">
            <el-date-picker
              v-model="employeeQuery.payrollMonth"
              type="month"
              placeholder="选择月份"
              format="YYYY-M"
              value-format="YYYY-MM">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="银行卡号">
            <el-input v-model="employeeQuery.bankCardNo" placeholder="请输入银行卡号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryEmployeePayroll">查询</el-button>
            <el-button @click="resetEmployeeQuery">重置</el-button>
          </el-form-item>
        </el-form>
        
        <el-table :data="payrollList" border v-loading="loading" style="width: 100%">
          <el-table-column label="序号" width="80" align="center">
            <template #default="{ $index }">
              {{ $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="employeeName" label="员工姓名" width="120"></el-table-column>
          <el-table-column prop="payrollMonth" label="薪资月份" width="100"></el-table-column>
          <el-table-column prop="bankCardNo" label="银行卡号" width="200"></el-table-column>
          <el-table-column prop="actualAmount" label="实发金额" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{row}">
              <el-tag :type="getStatusLabel(row.status).type">{{ getStatusLabel(row.status).text }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template #default="{row}">
              <el-button size="small" @click="viewPayrollDetail(row)">查看</el-button>
              <el-button size="small" type="primary" @click="openAdjustDialog(row)" :disabled="row.status !== 0">调整</el-button>
              <el-button size="small" type="success" @click="paySinglePayroll(row)" :disabled="row.status !== 0">发放</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="employeeQuery.currentPage"
          v-model:page-size="employeeQuery.pageSize"
          :total="total"
          @current-change="handleEmployeePageChange"
          @size-change="handleEmployeeSizeChange"
          layout="total, prev, pager, next, sizes"
          :page-sizes="[10, 20, 50]"
          style="margin-top: 16px; text-align: right;"
        />
      </el-tab-pane>
      
      <!-- 按月份查询工资单 -->
      <el-tab-pane label="按月份查询" name="month">
        <el-form :inline="true" :model="monthQuery" ref="monthQueryForm" style="margin-bottom: 20px;">
          <el-form-item label="薪资月份">
            <el-date-picker
              v-model="monthQuery.month"
              type="month"
              placeholder="选择月份"
              format="YYYY-M"
              value-format="YYYY-MM">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="员工ID">
            <el-input v-model.number="monthQuery.employeeId" placeholder="请输入员工ID"></el-input>
          </el-form-item>
          <el-form-item label="员工姓名">
            <el-input v-model="monthQuery.employeeName" placeholder="请输入员工姓名"></el-input>
          </el-form-item>
          <el-form-item label="银行卡号">
            <el-input v-model="monthQuery.bankCardNo" placeholder="请输入银行卡号"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="monthQuery.status" placeholder="请选择状态" style="width: 150px" clearable>
              <el-option label="待发放" value="0"></el-option>
              <el-option label="已发放" value="1"></el-option>
              <el-option label="已撤销" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryMonthlyPayroll">查询</el-button>
            <el-button @click="resetMonthQuery">重置</el-button>
            <el-button type="success" @click="openGenerateDialog" :loading="generating">生成工资单</el-button>
            <el-button type="warning" @click="payAllByMonthHandler" :disabled="!monthQuery.month" :loading="payingAll">批量发放</el-button>
          </el-form-item>
        </el-form>
        
        <el-table :data="monthlyPayrollList" border v-loading="loading" style="width: 100%">
          <el-table-column label="序号" width="80" align="center">
            <template #default="{ $index }">
              {{ $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="employeeName" label="员工姓名" width="120"></el-table-column>
          <el-table-column prop="payrollMonth" label="薪资月份" width="100"></el-table-column>
          <el-table-column prop="bankCardNo" label="银行卡号" width="200"></el-table-column>
          <el-table-column prop="actualAmount" label="实发金额" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{row}">
              <el-tag :type="getStatusLabel(row.status).type">{{ getStatusLabel(row.status).text }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template #default="{row}">
              <el-button size="small" @click="viewPayrollDetail(row)">查看</el-button>
              <el-button size="small" type="primary" @click="openAdjustDialog(row)" :disabled="row.status !== 0">调整</el-button>
              <el-button size="small" type="success" @click="paySinglePayroll(row)" :disabled="row.status !== 0">发放</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="monthQuery.currentPage"
          v-model:page-size="monthQuery.pageSize"
          :total="monthlyTotal"
          @current-change="handleMonthlyPageChange"
          @size-change="handleMonthlySizeChange"
          layout="total, prev, pager, next, sizes"
          :page-sizes="[10, 20, 50]"
          style="margin-top: 16px; text-align: right;"
        />
      </el-tab-pane>
    </el-tabs>
  </el-card>
  
  <!-- 工资单详情对话框 -->
  <el-dialog v-model="detailDialogVisible" title="工资单详情" width="600px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="工资单ID">{{ currentPayroll.id }}</el-descriptions-item>
      <el-descriptions-item label="员工姓名">{{ currentPayroll.employeeName }}</el-descriptions-item>
      <el-descriptions-item label="薪资月份">{{ currentPayroll.payrollMonth }}</el-descriptions-item>
      <el-descriptions-item label="银行卡号">{{ currentPayroll.bankCardNo }}</el-descriptions-item>
      <el-descriptions-item label="基本工资">{{ currentPayroll.basicSalary }}</el-descriptions-item>
      <el-descriptions-item label="绩效工资">{{ currentPayroll.performance }}</el-descriptions-item>
      <el-descriptions-item label="补贴">{{ currentPayroll.allowance }}</el-descriptions-item>
      <el-descriptions-item label="加班费">{{ currentPayroll.overtime }}</el-descriptions-item>
      <el-descriptions-item label="奖金">{{ currentPayroll.bonus }}</el-descriptions-item>
      <el-descriptions-item label="扣款">{{ currentPayroll.deduction }}</el-descriptions-item>
      <el-descriptions-item label="五险一金">{{ currentPayroll.insuranceAmount }}</el-descriptions-item>
      <el-descriptions-item label="应纳税额">{{ currentPayroll.taxableAmount }}</el-descriptions-item>
      <el-descriptions-item label="个人所得税">{{ currentPayroll.tax }}</el-descriptions-item>
      <el-descriptions-item label="实发金额">{{ currentPayroll.actualAmount }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ getStatusLabel(currentPayroll.status).text }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ currentPayroll.remark }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
  
  <!-- 调整工资单对话框 -->
  <el-dialog v-model="adjustDialogVisible" title="调整工资单" width="500px">
    <el-form :model="adjustForm" label-width="100px">
      <el-form-item label="绩效工资">
        <el-input-number v-model="adjustForm.performance" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="加班费">
        <el-input-number v-model="adjustForm.overtime" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="奖金">
        <el-input-number v-model="adjustForm.bonus" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="扣款">
        <el-input-number v-model="adjustForm.deduction" :precision="2" :step="100" :min="0" controls-position="right" style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="调整说明">
        <el-input v-model="adjustForm.remark" type="textarea" placeholder="请输入调整说明"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="adjustDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAdjustment" :loading="adjusting">确定</el-button>
    </template>
  </el-dialog>
  
  <!-- 生成工资单对话框 -->
  <el-dialog v-model="generateDialogVisible" title="生成工资单" width="400px">
    <el-form :model="generateForm" label-width="100px">
      <el-form-item label="薪资月份">
        <el-date-picker
          v-model="generateForm.month"
          type="month"
          placeholder="选择月份"
          format="YYYY-M"
          value-format="YYYY-MM">
        </el-date-picker>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="generateDialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="confirmGenerate" :loading="generating">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getEmployeePayrollList, 
  getMonthlyPayrollList, 
  getPayrollDetail, 
  generatePayroll, 
  adjustPayroll, 
  payPayroll, 
  payAllByMonth 
} from '@/api/salary'

const activeTab = ref('employee')

const employeeQuery = ref({
  employeeId: '',
  employeeName: '',
  payrollMonth: '',
  bankCardNo: '',
  currentPage: 1,
  pageSize: 10
})

const monthQuery = ref({
  month: '',
  employeeId: '',
  employeeName: '',
  bankCardNo: '',
  status: '',
  currentPage: 1,
  pageSize: 20
})

const payrollList = ref([])
const monthlyPayrollList = ref([])
const total = ref(0)
const monthlyTotal = ref(0)
const loading = ref(false)
const generating = ref(false)
const payingAll = ref(false)
const adjusting = ref(false)

const detailDialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const generateDialogVisible = ref(false)

const currentPayroll = ref({})
const adjustForm = ref({
  payrollId: '',
  performance: 0,
  overtime: 0,
  bonus: 0,
  deduction: 0,
  remark: ''
})

const generateForm = ref({
  month: ''
})

const getStatusLabel = (status) => {
  const statusMap = {
    0: { text: '待发放', type: 'warning' },
    1: { text: '已发放', type: 'success' },
    2: { text: '已撤销', type: 'info' }
  }
  return statusMap[status] || { text: '未知', type: '' }
}

// 获取上个月的月份
const getLastMonth = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() // 当前月份（0-11）
  
  let lastYear = year
  let lastMonth = month - 1
  
  if (lastMonth < 0) {
    lastMonth = 11
    lastYear = year - 1
  }
  
  // 格式化为 YYYY-MM，月份需要补零
  const monthStr = String(lastMonth + 1).padStart(2, '0')
  return `${lastYear}-${monthStr}`
}

// 打开生成工资单对话框
const openGenerateDialog = () => {
  generateForm.value.month = getLastMonth()
  generateDialogVisible.value = true
}

// 确认生成工资单
const confirmGenerate = async () => {
  if (!generateForm.value.month) {
    ElMessage.error('请选择月份')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确认生成 ${generateForm.value.month} 月份的工资单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    generating.value = true
    const data = {
      month: generateForm.value.month
    }
    
    const res = await generatePayroll(data)
    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || '生成成功')
      generateDialogVisible.value = false
      // 重新查询
      queryMonthlyPayroll()
    } else {
      ElMessage.error(res.data.msg || '生成失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('生成失败')
    }
  } finally {
    generating.value = false
  }
}

// 在组件挂载时加载初始数据
onMounted(() => {
  // 默认加载按员工查询的数据
  queryEmployeePayroll()
})

const handleTabChange = (tabName) => {
  if (tabName === 'employee') {
    queryEmployeePayroll()
  } else {
    queryMonthlyPayroll()
  }
}

const queryEmployeePayroll = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: employeeQuery.value.currentPage,
      pageSize: employeeQuery.value.pageSize,
      employeeId: employeeQuery.value.employeeId || null,
      employeeName: employeeQuery.value.employeeName || null,
      payrollMonth: employeeQuery.value.payrollMonth || null,
      bankCardNo: employeeQuery.value.bankCardNo || null
    }
    
    const res = await getEmployeePayrollList(params)
    if (res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      payrollList.value = data.list || []
      total.value = data.total || 0
    } else {
      payrollList.value = []
      total.value = 0
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    payrollList.value = []
    total.value = 0
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const queryMonthlyPayroll = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: monthQuery.value.currentPage,
      pageSize: monthQuery.value.pageSize,
      payrollMonth: monthQuery.value.month || null,
      employeeId: monthQuery.value.employeeId || null,
      employeeName: monthQuery.value.employeeName || null,
      bankCardNo: monthQuery.value.bankCardNo || null,
      status: monthQuery.value.status ? parseInt(monthQuery.value.status) : null
    }
    
    const res = await getMonthlyPayrollList(params)
    if (res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      monthlyPayrollList.value = data.list || []
      monthlyTotal.value = data.total || 0
    } else {
      monthlyPayrollList.value = []
      monthlyTotal.value = 0
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    monthlyPayrollList.value = []
    monthlyTotal.value = 0
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const resetEmployeeQuery = () => {
  employeeQuery.value = {
    employeeId: '',
    employeeName: '',
    payrollMonth: '',
    bankCardNo: '',
    currentPage: 1,
    pageSize: 10
  }
  queryEmployeePayroll()
}

const resetMonthQuery = () => {
  monthQuery.value = {
    month: '',
    employeeId: '',
    employeeName: '',
    bankCardNo: '',
    status: '',
    currentPage: 1,
    pageSize: 20
  }
  queryMonthlyPayroll()
}

const handleEmployeePageChange = (page) => {
  employeeQuery.value.currentPage = page
  queryEmployeePayroll()
}

const handleEmployeeSizeChange = (size) => {
  employeeQuery.value.pageSize = size
  employeeQuery.value.currentPage = 1
  queryEmployeePayroll()
}

const handleMonthlyPageChange = (page) => {
  monthQuery.value.currentPage = page
  queryMonthlyPayroll()
}

const handleMonthlySizeChange = (size) => {
  monthQuery.value.pageSize = size
  monthQuery.value.currentPage = 1
  queryMonthlyPayroll()
}

const viewPayrollDetail = async (row) => {
  try {
    const res = await getPayrollDetail(row.id)
    if (res.data.code === 0 && res.data.data) {
      currentPayroll.value = res.data.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取详情失败')
    }
  } catch (err) {
    ElMessage.error('获取详情失败')
  }
}

const openAdjustDialog = (row) => {
  adjustForm.value = {
    payrollId: row.id,
    performance: row.performance || 0,
    overtime: row.overtime || 0,
    bonus: row.bonus || 0,
    deduction: row.deduction || 0,
    remark: ''
  }
  adjustDialogVisible.value = true
}

const submitAdjustment = async () => {
  adjusting.value = true
  try {
    const res = await adjustPayroll(adjustForm.value)
    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || '调整成功')
      adjustDialogVisible.value = false
      // 重新查询当前列表
      if (activeTab.value === 'employee') {
        queryEmployeePayroll()
      } else {
        queryMonthlyPayroll()
      }
    } else {
      ElMessage.error(res.data.msg || '调整失败')
    }
  } catch (err) {
    ElMessage.error('调整失败')
  } finally {
    adjusting.value = false
  }
}

const paySinglePayroll = async (row) => {
  try {
    await ElMessageBox.confirm('确认发放该工资单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await payPayroll(row.id)
    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || '发放成功')
      // 重新查询当前列表
      if (activeTab.value === 'employee') {
        queryEmployeePayroll()
      } else {
        queryMonthlyPayroll()
      }
    } else {
      ElMessage.error(res.data.msg || '发放失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('发放失败')
    }
  }
}

const payAllByMonthHandler = async () => {
  if (!monthQuery.value.month) {
    ElMessage.error('请选择月份')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确认发放 ${monthQuery.value.month} 月份的所有待发工资单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    payingAll.value = true
    const res = await payAllByMonth(monthQuery.value.month)
    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || '批量发放成功')
      // 重新查询
      queryMonthlyPayroll()
    } else {
      ElMessage.error(res.data.msg || '批量发放失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('批量发放失败')
    }
  } finally {
    payingAll.value = false
  }
}

// 在组件挂载时加载初始数据
onMounted(() => {
  // 默认加载按员工查询的数据
  queryEmployeePayroll()
})

</script>

<style scoped>
.el-card {
  margin-bottom: 20px;
}
</style>

<template>
  <el-card>
    <div slot="header">
      <span>薪资管理</span>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="薪资配置" name="config">
        <salary-config />
      </el-tab-pane>
      <el-tab-pane label="工资单管理" name="payroll">
        <payroll-list />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import SalaryConfig from './SalaryConfig.vue'
import PayrollList from './PayrollList.vue'

const activeTab = ref('config')
</script>

<style scoped>
.el-card {
  min-height: calc(100vh - 100px);
}
</style>

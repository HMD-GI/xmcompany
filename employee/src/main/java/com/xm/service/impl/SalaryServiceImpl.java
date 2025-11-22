package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.PayrollAdjustmentDTO;
import com.xm.dto.PayrollGenerationDTO;
import com.xm.dto.SalaryDTO;
import com.xm.entity.Employee;
import com.xm.entity.Payroll;
import com.xm.entity.Salary;
import com.xm.mapper.EmployeeMapper;
import com.xm.mapper.PayrollMapper;
import com.xm.mapper.SalaryMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SalaryService;
import com.xm.utils.RedisIdGenerator;
import com.xm.vo.PayrollVO;
import com.xm.vo.SalaryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪资服务实现类
 */
@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;
    
    @Autowired
    private PayrollMapper payrollMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private RedisIdGenerator redisIdGenerator;
    
    // 薪资状态映射
    private static final Map<Integer, String> SALARY_STATUS_MAP = new HashMap<>();
    static {
        SALARY_STATUS_MAP.put(0, "无效");
        SALARY_STATUS_MAP.put(1, "有效");
    }
    
    // 工资单状态映射
    private static final Map<Integer, String> PAYROLL_STATUS_MAP = new HashMap<>();
    static {
        PAYROLL_STATUS_MAP.put(0, "待发放");
        PAYROLL_STATUS_MAP.put(1, "已发放");
        PAYROLL_STATUS_MAP.put(2, "已撤销");
    }
    
    /**
     * 个税起征点
     */
    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");
    
    /**
     * 设置员工薪资配置
     * @param salaryDTO 薪资配置数据
     * @return 设置结果
     */
    @Override
    @Transactional//“先读后写” 场景
    public Result setSalary(SalaryDTO salaryDTO) {
        // 验证员工是否存在
        Employee employee = employeeMapper.selectById(salaryDTO.getEmployeeId());
        if (employee == null) {
            return Result.error("员工不存在");
        }
        
        // 查询是否已有薪资配置
        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salary::getEmployeeId, salaryDTO.getEmployeeId())
                   .eq(Salary::getStatus, 1);
        Salary existingSalary = salaryMapper.selectOne(queryWrapper);
        
        Salary salary;
        LocalDateTime now = LocalDateTime.now();
        
        if (existingSalary != null) {
            // 更新现有薪资配置
            salary = existingSalary;
            BeanUtils.copyProperties(salaryDTO, salary, "id", "employeeId", "employeeName", "createTime");
            salary.setUpdateTime(now);
        } else {
            // 创建新的薪资配置
            salary = new Salary();
            BeanUtils.copyProperties(salaryDTO, salary);
            
            // 设置员工姓名
            salary.setEmployeeName(employee.getName());
            
            // 设置状态为有效
            salary.setStatus(1);
            
            // 设置生效日期
            salary.setEffectiveDate(now);
            
            // 设置创建时间和更新时间
            salary.setCreateTime(now);
            salary.setUpdateTime(now);
            
            // 使用Redis生成ID
            int salaryId = redisIdGenerator.generateId("salary");
            salary.setId(salaryId);
        }
        
        // 保存薪资配置
        if (existingSalary != null) {
            if (salaryMapper.updateById(salary) > 0) {
                return Result.success("薪资配置更新成功");
            }
        } else {
            if (salaryMapper.insert(salary) > 0) {
                return Result.success("薪资配置设置成功");
            }
        }
        
        return Result.error("薪资配置操作失败");
    }
    
    /**
     * 根据员工ID获取薪资配置
     * @param employeeId 员工ID
     * @return 薪资配置详情
     */
    @Override
    public Result<SalaryVO> getSalaryByEmployeeId(int employeeId) {
        // 查询员工薪资配置
        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salary::getEmployeeId, employeeId)
                   .eq(Salary::getStatus, 1);
        Salary salary = salaryMapper.selectOne(queryWrapper);
        
        if (salary == null) {
            return Result.error("员工薪资配置不存在");
        }
        
        // 转换为VO
        SalaryVO salaryVO = convertToSalaryVO(salary);
        
        return Result.success(salaryVO);
    }
    
    /**
     * 生成月度工资单
     * @param generationDTO 工资单生成参数，包含月份和可选的员工ID
     * @return 生成结果
     */
    @Override
    @Transactional
    public Result generateMonthlyPayroll(PayrollGenerationDTO generationDTO) {
        // 验证月份格式
        String month = generationDTO.getMonth();
        if (!month.matches("\\d{4}-\\d{2}")) {
            return Result.error("月份格式不正确，应为yyyy-MM");
        }
        
        // 解析月份
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<Employee> employees;
        if (generationDTO.getEmployeeId() != null) {
            // 指定员工
            Employee employee = employeeMapper.selectById(generationDTO.getEmployeeId());
            if (employee == null) {
                return Result.error("员工不存在");
            }
            employees = new ArrayList<>();
            employees.add(employee);
        } else {
            // 所有员工
            LambdaQueryWrapper<Employee> employeeQueryWrapper = new LambdaQueryWrapper<>();
            employeeQueryWrapper.eq(Employee::getEnabled, 1); // 只处理启用状态的员工
            employees = employeeMapper.selectList(employeeQueryWrapper);
        }
        
        // 开始生成工资单
        int successCount = 0;
        int skipCount = 0;
        
        for (Employee employee : employees) {
            // 查询员工薪资配置
            LambdaQueryWrapper<Salary> salaryQueryWrapper = new LambdaQueryWrapper<>();
            salaryQueryWrapper.eq(Salary::getEmployeeId, employee.getId())
                           .eq(Salary::getStatus, 1);
            Salary salary = salaryMapper.selectOne(salaryQueryWrapper);
            
            if (salary == null) {
                // 无薪资配置，跳过
                skipCount++;
                continue;
            }
            
            // 检查是否已生成该月工资单
            LambdaQueryWrapper<Payroll> payrollQueryWrapper = new LambdaQueryWrapper<>();
            payrollQueryWrapper.eq(Payroll::getEmployeeId, employee.getId())
                             .eq(Payroll::getPayrollMonth, month);
            Payroll existingPayroll = payrollMapper.selectOne(payrollQueryWrapper);
            
            if (existingPayroll != null) {
                // 已存在该月工资单，跳过
                skipCount++;
                continue;
            }
            
            // 创建工资单
            Payroll payroll = new Payroll();
            payroll.setEmployeeId(employee.getId());
            payroll.setEmployeeName(employee.getName());
            payroll.setBankCardNo(salary.getBankCardNo());
            payroll.setBankName(salary.getBankName());
            payroll.setPayrollMonth(month);
            
            // 设置基本薪资数据
            payroll.setBasicSalary(salary.getBasicSalary());
            
            // 默认绩效为绩效基数
            payroll.setPerformance(salary.getPerformanceBase());
            
            // 设置补贴
            payroll.setAllowance(salary.getAllowance());
            
            // 初始化其他字段为零
            payroll.setOvertime(BigDecimal.ZERO);
            payroll.setBonus(BigDecimal.ZERO);
            payroll.setDeduction(BigDecimal.ZERO);
            
            // 设置五险一金
            payroll.setInsuranceAmount(salary.getInsuranceAmount());
            
            // 计算应纳税额和个税
            BigDecimal taxableIncome = payroll.getBasicSalary()
                                     .add(payroll.getPerformance())
                                     .add(payroll.getAllowance())
                                     .add(payroll.getOvertime())
                                     .add(payroll.getBonus())
                                     .subtract(payroll.getInsuranceAmount())
                                     .subtract(TAX_THRESHOLD);
            
            if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
                payroll.setTaxableAmount(taxableIncome);
                payroll.setTax(calculateTax(taxableIncome));
            } else {
                payroll.setTaxableAmount(BigDecimal.ZERO);
                payroll.setTax(BigDecimal.ZERO);
            }
            
            // 计算实发金额
            BigDecimal actualAmount = payroll.getBasicSalary()
                                    .add(payroll.getPerformance())
                                    .add(payroll.getAllowance())
                                    .add(payroll.getOvertime())
                                    .add(payroll.getBonus())
                                    .subtract(payroll.getDeduction())
                                    .subtract(payroll.getInsuranceAmount())
                                    .subtract(payroll.getTax());
            
            payroll.setActualAmount(actualAmount);
            
            // 设置状态为待发放
            payroll.setStatus(0);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            payroll.setCreateTime(now);
            payroll.setUpdateTime(now);
            
            // 使用Redis生成ID
            int payrollId = redisIdGenerator.generateId("payroll");
            payroll.setId(payrollId);
            
            // 保存工资单
            if (payrollMapper.insert(payroll) > 0) {
                successCount++;
            }
        }
        
        if (successCount > 0) {
            return Result.success("成功生成" + successCount + "条工资单，跳过" + skipCount + "条");
        } else if (skipCount > 0) {
            return Result.error("没有生成任何工资单，跳过" + skipCount + "条（已存在或无薪资配置）");
        } else {
            return Result.error("没有找到符合条件的员工");
        }
    }

    /**
     * 调整工资单
     * @param adjustmentDTO 调整信息
     * @return Result
     */
    @Override
    @Transactional
    public Result adjustPayroll(PayrollAdjustmentDTO adjustmentDTO) {
        // 查询工资单
        Payroll payroll = payrollMapper.selectById(adjustmentDTO.getPayrollId());
        if (payroll == null) {
            return Result.error("工资单不存在");
        }
        
        // 检查工资单状态
        if (payroll.getStatus() != 0) {
            return Result.error("只能调整待发放状态的工资单");
        }
        
        // 更新工资单
        if (adjustmentDTO.getPerformance() != null) {
            payroll.setPerformance(adjustmentDTO.getPerformance());
        }
        
        if (adjustmentDTO.getOvertime() != null) {
            payroll.setOvertime(adjustmentDTO.getOvertime());
        }
        
        if (adjustmentDTO.getBonus() != null) {
            payroll.setBonus(adjustmentDTO.getBonus());
        }
        
        if (adjustmentDTO.getDeduction() != null) {
            payroll.setDeduction(adjustmentDTO.getDeduction());
        }
        
        if (adjustmentDTO.getRemark() != null) {
            String remark = (payroll.getRemark() == null ? "" : payroll.getRemark() + "；") 
                          + "调整：" + adjustmentDTO.getRemark();
            payroll.setRemark(remark);
        }
        
        // 重新计算应纳税额和个税
        BigDecimal taxableIncome = payroll.getBasicSalary()
                                 .add(payroll.getPerformance())
                                 .add(payroll.getAllowance())
                                 .add(payroll.getOvertime())
                                 .add(payroll.getBonus())
                                 .subtract(payroll.getInsuranceAmount())
                                 .subtract(TAX_THRESHOLD);
        
        if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
            payroll.setTaxableAmount(taxableIncome);
            payroll.setTax(calculateTax(taxableIncome));
        } else {
            payroll.setTaxableAmount(BigDecimal.ZERO);
            payroll.setTax(BigDecimal.ZERO);
        }
        
        // 重新计算实发金额
        payroll.setActualAmount(payroll.getBasicSalary()
                             .add(payroll.getPerformance())
                             .add(payroll.getAllowance())
                             .add(payroll.getOvertime())
                             .add(payroll.getBonus())
                             .subtract(payroll.getDeduction())
                             .subtract(payroll.getInsuranceAmount())
                             .subtract(payroll.getTax()));
        
        // 更新时间
        payroll.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        if (payrollMapper.updateById(payroll) > 0) {
            return Result.success("工资单调整成功");
        }
        
        return Result.error("工资单调整失败");
    }
    
    @Override
    @Transactional
    public Result payPayroll(int payrollId) {
        // 查询工资单
        Payroll payroll = payrollMapper.selectById(payrollId);
        if (payroll == null) {
            return Result.error("工资单不存在");
        }
        
        // 检查工资单状态
        if (payroll.getStatus() != 0) {
            return Result.error("只能发放待发放状态的工资单");
        }
        
        // 更新工资单状态
        payroll.setStatus(1); // 已发放
        payroll.setPayTime(LocalDateTime.now());
        payroll.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        if (payrollMapper.updateById(payroll) > 0) {
            return Result.success("工资发放成功");
        }
        
        return Result.error("工资发放失败");
    }
    
    @Override
    @Transactional
    public Result payAllPayrollByMonth(String month) {
        // 验证月份格式
        if (!month.matches("\\d{4}-\\d{2}")) {
            return Result.error("月份格式不正确，应为yyyy-MM");
        }
        
        // 查询指定月份的待发放工资单
        LambdaQueryWrapper<Payroll> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payroll::getPayrollMonth, month)
                   .eq(Payroll::getStatus, 0);
        List<Payroll> payrolls = payrollMapper.selectList(queryWrapper);
        
        if (payrolls.isEmpty()) {
            return Result.error("没有待发放的工资单");
        }
        
        // 更新所有工资单状态
        LocalDateTime now = LocalDateTime.now();
        int successCount = 0;
        
        for (Payroll payroll : payrolls) {
            payroll.setStatus(1); // 已发放
            payroll.setPayTime(now);
            payroll.setUpdateTime(now);
            
            if (payrollMapper.updateById(payroll) > 0) {
                successCount++;
            }
        }
        
        if (successCount > 0) {
            return Result.success("批量发放成功，共发放 " + successCount + " 笔工资");
        }
        
        return Result.error("工资发放失败");
    }
    
    @Override
    public Result<Payroll> getPayrollById(int payrollId) {
        // 查询工资单
        Payroll payroll = payrollMapper.selectById(payrollId);
        if (payroll == null) {
            return Result.error("工资单不存在");
        }
        
        return Result.success(payroll);
    }
    
    @Override
    public Result<page<PayrollVO>> getPayrollList(int employeeId, Integer year, Integer month, int currentPage, int pageSize) {
        // 查询员工工资单
        LambdaQueryWrapper<Payroll> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payroll::getEmployeeId, employeeId);
        
        // 按年月筛选
        if (year != null) {
            String yearStr = String.valueOf(year);
            queryWrapper.likeRight(Payroll::getPayrollMonth, yearStr);
        }
        
        if (month != null) {
            String monthStr = String.format("%02d", month);
            queryWrapper.likeRight(Payroll::getPayrollMonth, "-" + monthStr);
        }
        
        // 按发放时间降序排序
        queryWrapper.orderByDesc(Payroll::getPayrollMonth);
        
        // 执行分页查询
        Page<Payroll> pageInfo = new Page<>(currentPage, pageSize);
        Page<Payroll> payrollPage = payrollMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为VO列表
        List<PayrollVO> payrollVOList = payrollPage.getRecords().stream()
                .map(this::convertToPayrollVO)
                .collect(Collectors.toList());
        
        // 创建自定义分页对象
        page<PayrollVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) payrollPage.getTotal());
        result.setList(payrollVOList);
        
        return Result.success(result);
    }
    
    @Override
    public Result<page<PayrollVO>> getPayrollListByMonth(String month, Integer status, int currentPage, int pageSize) {
        // 验证月份格式
        if (!month.matches("\\d{4}-\\d{2}")) {
            return Result.error("月份格式不正确，应为yyyy-MM");
        }
        
        // 查询月度工资单
        LambdaQueryWrapper<Payroll> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payroll::getPayrollMonth, month);
        
        // 按状态筛选
        if (status != null) {
            queryWrapper.eq(Payroll::getStatus, status);
        }
        
        // 按员工ID排序
        queryWrapper.orderByAsc(Payroll::getEmployeeId);
        
        // 执行分页查询
        Page<Payroll> pageInfo = new Page<>(currentPage, pageSize);
        Page<Payroll> payrollPage = payrollMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为VO列表
        List<PayrollVO> payrollVOList = payrollPage.getRecords().stream()
                .map(this::convertToPayrollVO)
                .collect(Collectors.toList());
        
        // 创建自定义分页对象
        page<PayrollVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) payrollPage.getTotal());
        result.setList(payrollVOList);
        
        return Result.success(result);
    }
    
    /**
     * 将薪资配置实体转换为VO
     */
    private SalaryVO convertToSalaryVO(Salary salary) {
        SalaryVO salaryVO = new SalaryVO();
        BeanUtils.copyProperties(salary, salaryVO);
        
        // 隐藏部分银行卡号
        if (salary.getBankCardNo() != null && salary.getBankCardNo().length() > 8) {
            String cardNo = salary.getBankCardNo();
            String maskedCardNo = cardNo.substring(0, 4) + "****" + cardNo.substring(cardNo.length() - 4);
            salaryVO.setBankCardNo(maskedCardNo);
        }
        
        // 计算薪资基数合计
        BigDecimal totalBase = salary.getBasicSalary()
                            .add(salary.getPerformanceBase())
                            .add(salary.getAllowance());
        salaryVO.setTotalBase(totalBase);
        
        // 设置状态描述
        salaryVO.setStatusDesc(SALARY_STATUS_MAP.getOrDefault(salary.getStatus(), "未知"));
        
        return salaryVO;
    }
    
    /**
     * 将工资单实体转换为VO
     */
    private PayrollVO convertToPayrollVO(Payroll payroll) {
        PayrollVO payrollVO = new PayrollVO();
        BeanUtils.copyProperties(payroll, payrollVO);
        
        // 隐藏部分银行卡号
        if (payroll.getBankCardNo() != null && payroll.getBankCardNo().length() > 8) {
            String cardNo = payroll.getBankCardNo();
            String maskedCardNo = cardNo.substring(0, 4) + "****" + cardNo.substring(cardNo.length() - 4);
            payrollVO.setBankCardNo(maskedCardNo);
        }
        
//        // 计算收入合计
//        BigDecimal totalIncome = payroll.getBasicSalary()
//                              .add(payroll.getPerformance())
//                              .add(payroll.getAllowance())
//                              .add(payroll.getOvertime())
//                              .add(payroll.getBonus());
//        payrollVO.setTotalIncome(totalIncome);
        
//        // 计算扣除合计
//        BigDecimal totalDeduction = payroll.getDeduction()
//                                 .add(payroll.getInsuranceAmount())
//                                 .add(payroll.getTax());
//        payrollVO.setTotalDeduction(totalDeduction);
        
        // 设置状态描述
//        payrollVO.setStatusDesc(PAYROLL_STATUS_MAP.getOrDefault(payroll.getStatus(), "未知"));
        
        return payrollVO;
    }
    
    /**
     * 计算个人所得税
     * 注意：这是一个简化的计算方法，实际税率和计算方法可能更复杂
     */
    private BigDecimal calculateTax(BigDecimal taxableIncome) {
        // 个税起征点已经在调用处减去，这里直接计算税额
        BigDecimal tax;
        
        if (taxableIncome.compareTo(new BigDecimal("3000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.03"));
        } else if (taxableIncome.compareTo(new BigDecimal("12000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.1")).subtract(new BigDecimal("210"));
        } else if (taxableIncome.compareTo(new BigDecimal("25000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.2")).subtract(new BigDecimal("1410"));
        } else if (taxableIncome.compareTo(new BigDecimal("35000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.25")).subtract(new BigDecimal("2660"));
        } else if (taxableIncome.compareTo(new BigDecimal("55000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.3")).subtract(new BigDecimal("4410"));
        } else if (taxableIncome.compareTo(new BigDecimal("80000")) <= 0) {
            tax = taxableIncome.multiply(new BigDecimal("0.35")).subtract(new BigDecimal("7160"));
        } else {
            tax = taxableIncome.multiply(new BigDecimal("0.45")).subtract(new BigDecimal("15160"));
        }
        
        return tax.setScale(2, RoundingMode.HALF_UP);
    }
} 
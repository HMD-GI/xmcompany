package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.LeaveApplyDTO;
import com.xm.dto.LeaveReviewDTO;
import com.xm.entity.Employee;
import com.xm.entity.Leave;
import com.xm.mapper.EmployeeMapper;
import com.xm.mapper.LeaveMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.LeaveService;
import com.xm.utils.RedisIdGenerator;
import com.xm.vo.LeaveVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请假服务实现类
 */
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private RedisIdGenerator redisIdGenerator;
    
    // 请假类型映射
    private static final Map<String, String> LEAVE_TYPE_MAP = new HashMap<>();
    static {
        LEAVE_TYPE_MAP.put("1", "事假");
        LEAVE_TYPE_MAP.put("2", "病假");
        LEAVE_TYPE_MAP.put("3", "年假");
        LEAVE_TYPE_MAP.put("4", "调休");
        LEAVE_TYPE_MAP.put("5", "婚假");
        LEAVE_TYPE_MAP.put("6", "产假");
        LEAVE_TYPE_MAP.put("7", "丧假");
    }
    
    // 请假状态映射
    private static final Map<Integer, String> LEAVE_STATUS_MAP = new HashMap<>();
    static {
        LEAVE_STATUS_MAP.put(0, "待审核");
        LEAVE_STATUS_MAP.put(1, "已批准");
        LEAVE_STATUS_MAP.put(2, "已拒绝");
        LEAVE_STATUS_MAP.put(3, "已取消");
    }

    @Override
    @Transactional
    public Result applyLeave(LeaveApplyDTO leaveApplyDTO) {
        // 验证员工是否存在
        Employee employee = employeeMapper.selectById(leaveApplyDTO.getEmployeeId());
        if (employee == null) {
            return Result.error("员工不存在");
        }
        
        // 验证请假时间
        if (leaveApplyDTO.getStartTime() == null || leaveApplyDTO.getEndTime() == null) {
            return Result.error("请假时间不能为空");
        }
        
        if (leaveApplyDTO.getStartTime().isAfter(leaveApplyDTO.getEndTime())) {
            return Result.error("开始时间不能晚于结束时间");
        }
        
        if (leaveApplyDTO.getStartTime().isBefore(LocalDateTime.now())) {
            return Result.error("开始时间不能早于当前时间");
        }
        
        // 验证请假类型
        if (!LEAVE_TYPE_MAP.containsKey(leaveApplyDTO.getLeaveType())) {
            return Result.error("无效的请假类型");
        }
        
        // 创建请假记录
        Leave leave = new Leave();
        BeanUtils.copyProperties(leaveApplyDTO, leave);
        
        // 设置员工姓名
        leave.setEmployeeName(employee.getName());
        
        // 设置默认状态为待审核
        leave.setStatus(0);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        leave.setCreateTime(now);
        leave.setUpdateTime(now);
        
        // 使用Redis生成ID
        int leaveId = redisIdGenerator.generateId("leave");
        leave.setId(leaveId);
        
        // 保存请假记录
        if (leaveMapper.insert(leave) > 0) {
            return Result.success("请假申请提交成功");
        }
        
        return Result.error("请假申请提交失败");
    }

    @Override
    @Transactional
    public Result reviewLeave(LeaveReviewDTO leaveReviewDTO) {
        // 验证请假记录是否存在
        Leave leave = leaveMapper.selectById(leaveReviewDTO.getLeaveId());
        if (leave == null) {
            return Result.error("请假记录不存在");
        }
        
        // 验证状态是否为待审核
        if (leave.getStatus() != 0) {
            return Result.error("只能审核待审核状态的请假申请");
        }
        
        // 验证审核状态
        if (leaveReviewDTO.getStatus() != 1 && leaveReviewDTO.getStatus() != 2) {
            return Result.error("无效的审核状态");
        }
        
        // 更新请假记录
        leave.setStatus(leaveReviewDTO.getStatus());
        leave.setReviewerName(leaveReviewDTO.getReviewerName());
        leave.setReviewComment(leaveReviewDTO.getReviewComment());
        leave.setReviewTime(LocalDateTime.now());
        leave.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        if (leaveMapper.updateById(leave) > 0) {
            return Result.success("请假审核完成");
        }
        
        return Result.error("请假审核失败");
    }

    @Override
    public Result<LeaveVO> getLeaveById(int id) {
        // 查询请假记录
        Leave leave = leaveMapper.selectById(id);
        if (leave == null) {
            return Result.error("请假记录不存在");
        }
        
        // 转换为VO
        LeaveVO leaveVO = convertToVO(leave);
        
        return Result.success(leaveVO);
    }

    @Override
    public Result<page<LeaveVO>> getLeaveList(int currentPage, int pageSize, Integer employeeId, Integer status) {
        // 创建查询条件
        LambdaQueryWrapper<Leave> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加筛选条件
        if (employeeId != null) {
            queryWrapper.eq(Leave::getEmployeeId, employeeId);
        }
        
        if (status != null) {
            queryWrapper.eq(Leave::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Leave::getCreateTime);
        
        // 执行分页查询
        Page<Leave> pageInfo = new Page<>(currentPage, pageSize);
        Page<Leave> leavePage = leaveMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为VO列表
        List<LeaveVO> leaveVOList = leavePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 创建自定义分页对象
        page<LeaveVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) leavePage.getTotal());
        result.setList(leaveVOList);
        
        return Result.success(result);
    }
    
    /**
     * 将Leave实体转换为LeaveVO
     */
    private LeaveVO convertToVO(Leave leave) {
        LeaveVO leaveVO = new LeaveVO();
        BeanUtils.copyProperties(leave, leaveVO);
        
        // 设置请假类型描述
        leaveVO.setLeaveTypeDesc(LEAVE_TYPE_MAP.getOrDefault(leave.getLeaveType(), "未知"));
        
        // 设置状态描述
        leaveVO.setStatusDesc(LEAVE_STATUS_MAP.getOrDefault(leave.getStatus(), "未知"));
        
        // 计算请假天数
        if (leave.getStartTime() != null && leave.getEndTime() != null) {
            long days = Duration.between(leave.getStartTime(), leave.getEndTime()).toDays() + 1;
            leaveVO.setDays((int) days);
        }
        
        return leaveVO;
    }
} 
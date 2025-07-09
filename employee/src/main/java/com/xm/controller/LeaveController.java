package com.xm.controller;

import com.xm.dto.LeaveApplyDTO;
import com.xm.dto.LeaveReviewDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.LeaveService;
import com.xm.vo.LeaveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 请假管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/xm/leave")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    /**
     * 提交请假申请
     * @param leaveApplyDTO 请假申请信息
     * @return Result
     */
    @PostMapping("/apply")
    public Result applyLeave(@RequestBody LeaveApplyDTO leaveApplyDTO) {
        log.info("提交请假申请：{}", leaveApplyDTO);
        return leaveService.applyLeave(leaveApplyDTO);
    }
    
    /**
     * 审核请假申请
     * @param leaveReviewDTO 请假审核信息
     * @return Result
     */
    @PostMapping("/review")
    public Result reviewLeave(@RequestBody LeaveReviewDTO leaveReviewDTO) {
        log.info("审核请假申请：{}", leaveReviewDTO);
        return leaveService.reviewLeave(leaveReviewDTO);
    }
    
    /**
     * 查询请假详情
     * @param id 请假记录ID
     * @return Result<LeaveVO>
     */
    @GetMapping("/{id}")
    public Result<LeaveVO> getLeaveById(@PathVariable int id) {
        log.info("查询请假详情：{}", id);
        return leaveService.getLeaveById(id);
    }
    
    /**
     * 分页查询请假列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param employeeId 员工ID（可选）
     * @param status 状态（可选）
     * @return Result<page<LeaveVO>>
     */
    @GetMapping("/list")
    public Result<page<LeaveVO>> getLeaveList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer status) {
        log.info("查询请假列表：页码={}, 每页记录数={}, 员工ID={}, 状态={}", currentPage, pageSize, employeeId, status);
        return leaveService.getLeaveList(currentPage, pageSize, employeeId, status);
    }
} 
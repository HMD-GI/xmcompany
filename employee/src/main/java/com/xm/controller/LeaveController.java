package com.xm.controller;

import com.xm.dto.LeaveApplyDTO;
import com.xm.dto.LeaveReviewDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.LeaveService;
import com.xm.vo.LeaveVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 请假管理控制器
 */
@Slf4j
@Tag(name = "请假管理", description = "员工请假申请和审核相关接口")
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
    @Operation(summary = "提交请假申请", description = "员工提交请假申请，包含请假类型、时间段和原因等信息")
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
    @Operation(summary = "审核请假申请", description = "管理员审核员工的请假申请，可通过或驳回")
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
    @Operation(summary = "查询请假详情", description = "根据请假记录ID查询请假的详细信息")
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
    @Operation(summary = "分页查询请假列表", description = "分页查询请假记录，可按员工ID和申请状态筛选")
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
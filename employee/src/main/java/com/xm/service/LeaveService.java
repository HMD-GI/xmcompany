package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.LeaveApplyDTO;
import com.xm.dto.LeaveQueryDTO;
import com.xm.dto.LeaveReviewDTO;
import com.xm.entity.Leave;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.LeaveVO;

/**
 * 请假服务接口
 */
public interface LeaveService extends IService<Leave> {
    
    /**
     * 申请请假
     * @param leaveApplyDTO 请假申请信息
     * @return Result
     */
    Result applyLeave(LeaveApplyDTO leaveApplyDTO);
    
    /**
     * 审核请假申请
     * @param leaveReviewDTO 请假审核信息
     * @return Result
     */
    Result reviewLeave(LeaveReviewDTO leaveReviewDTO);
    
    /**
     * 查询请假信息
     * @param id 请假记录ID
     * @return Result<LeaveVO>
     */
    Result<Leave> getLeaveById(int id);
    
    /**
     * 分页查询请假列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件（员工 ID、员工姓名、状态）
     * @return Result<page<LeaveVO>>
     */
    Result<page<LeaveVO>> getLeaveList(int currentPage, int pageSize, LeaveQueryDTO queryDTO);
} 
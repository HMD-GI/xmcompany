package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.ProductionProgressUpdateDTO;
import com.xm.entity.ProductionProgress;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.ProductionProgressVO;
import com.xm.vo.ProgressVO;

/**
 * 生产进度服务接口
 */
public interface ProductionProgressService extends IService<ProductionProgress> {
    /**
     * 更新生产进度
     * @param progressUpdateDTO 进度更新信息
     * @return Result
     */
    Result updateProgress(ProductionProgressUpdateDTO progressUpdateDTO);

    /**
     * 查询进度记录详情
     * @param id 记录ID
     * @return Result<ProductionProgressVO>
     */
    Result<ProductionProgressVO> getProgressById(int id);

    /**
     * 分页查询项目进度记录
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param projectId 项目ID
     * @return Result<page<ProductionProgressVO>>
     */
    Result<page<ProgressVO>> getProgressList(int currentPage, int pageSize, int projectId);
    
    /**
     * 添加进度记录
     * @param progress 进度记录
     * @return Result
     */
    Result addProgressRecord(ProductionProgress progress);
}
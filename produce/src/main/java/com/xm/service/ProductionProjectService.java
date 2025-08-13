package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.ProductionProjectAddDTO;
import com.xm.dto.ProductionProjectQueryDTO;
import com.xm.dto.ProductionStatusUpdateDTO;
import com.xm.entity.ProductionProject;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.ProductionProjectVO;
import com.xm.vo.ProjectVO;

/**
 * 生产项目服务接口
 */
public interface ProductionProjectService extends IService<ProductionProject> {
    /**
     * 新增生产项目
     * @param projectAddDTO 项目信息
     * @return Result
     */
    Result addProject(ProductionProjectAddDTO projectAddDTO);

    /**
     * 更新项目状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    Result updateProjectStatus(ProductionStatusUpdateDTO statusUpdateDTO);

    /**
     * 查询项目详情
     * @param id 项目ID
     * @return Result<ProductionProjectVO>
     */
    Result<ProductionProjectVO> getProjectById(int id);

    /**
     * 分页查询项目列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<ProductionProjectVO>>
     */
    Result<page<ProjectVO>> getProjectList(int currentPage, int pageSize, ProductionProjectQueryDTO queryDTO);
}
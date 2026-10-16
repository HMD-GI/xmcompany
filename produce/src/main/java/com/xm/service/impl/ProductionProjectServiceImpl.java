package com.xm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.ProductionProjectAddDTO;
import com.xm.dto.ProductionProjectQueryDTO;
import com.xm.dto.ProductionStatusUpdateDTO;
import com.xm.entity.ProductionProject;
import com.xm.mapper.ProductionProjectMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.ProductionProgressService;
import com.xm.service.ProductionProjectService;
import com.xm.utils.RedisIdGenerator;
import com.xm.utils.UserContext;
import com.xm.vo.ProductionProjectVO;
import com.xm.vo.ProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生产项目服务实现类
 */
@Slf4j
@Service
public class ProductionProjectServiceImpl extends ServiceImpl<ProductionProjectMapper, ProductionProject> implements ProductionProjectService {

    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis ID生成器

    @Autowired
    private  ProductionProjectMapper productionProjectMapper;



    /**
     * 新增生产项目
     * @param projectAddDTO 项目信息
     * @return Result
     */
    @Override
    @Transactional
    public Result addProject(ProductionProjectAddDTO projectAddDTO) {
        log.info("新增生产项目: {}", projectAddDTO);
        
        // 检查目标数量是否有效
        if (projectAddDTO.getTargetQuantity() <= 0) {
            return Result.error("目标生产数量必须大于0");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 创建新的生产项目
        ProductionProject project = new ProductionProject();
        project.setId(redisIdGenerator.generateId("production_project"));
        project.setProjectName(projectAddDTO.getProjectName());
        project.setStatus("进行中"); // 默认状态为进行中
        project.setCurrentQuantity(0); // 初始生产数量为0
        project.setTargetQuantity(projectAddDTO.getTargetQuantity());
        project.setProductName(projectAddDTO.getProductName());
        project.setUnit(projectAddDTO.getUnit());
        project.setStartTime(projectAddDTO.getStartTime());
        project.setExpectedEndTime(projectAddDTO.getExpectedEndTime());
        project.setRemark(projectAddDTO.getRemark());
        project.setCreateTime(now);
        project.setUpdateTime(now);
        
        // 保存项目信息
        this.save(project);
        
        return Result.success();
    }

    /**
     * 更新项目状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProjectStatus(ProductionStatusUpdateDTO statusUpdateDTO) {
        log.info("更新项目状态: {}", statusUpdateDTO);

        // 1. 加行锁读取
        ProductionProject project = this.lambdaQuery()
                .eq(ProductionProject::getId, statusUpdateDTO.getProjectId())
                .last("LIMIT 1 FOR UPDATE")
                .one();

        // 检查项目是否存在
        if (project == null) {
            return Result.error("生产项目不存在");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 更新项目状态
        project.setStatus(statusUpdateDTO.getStatus());
        project.setUpdateTime(now);
        
        // 如果状态为已完成或已取消，设置实际结束时间
        if ("已完成".equals(statusUpdateDTO.getStatus()) || "已取消".equals(statusUpdateDTO.getStatus())) {
            project.setActualEndTime(now);
        }
        
        // 更新项目信息
        this.updateById(project);
        
        return Result.success();
    }

    /**
     * 查询项目详情
     * @param id 项目ID
     * @return Result<ProductionProjectVO>
     */
    @Override
    public Result<ProductionProjectVO> getProjectById(int id) {
        log.info("查询项目详情, ID: {}", id);
        
        // 查询项目详情
        ProductionProjectVO projectVO = this.baseMapper.selectProjectById(id);
        if (projectVO == null) {
            return Result.error("生产项目不存在");
        }
        
        return Result.success(projectVO);
    }

    /**
     * 分页查询项目列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<ProductionProjectVO>>
     */
    @Override
    public Result<page<ProjectVO>> getProjectList(int currentPage, int pageSize, ProductionProjectQueryDTO queryDTO) {
        log.info("分页查询项目列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        
        // 创建分页参数
        Page<ProductionProject> pageParam = new Page<>(currentPage, pageSize);
        
        // 执行查询
        IPage<ProductionProjectVO> projectPage = productionProjectMapper.selectProjectPage(pageParam, queryDTO);

        List<ProjectVO> voList = projectPage.getRecords().stream().map(
                p -> {
                    ProjectVO projectVO = new ProjectVO();
                    BeanUtils.copyProperties(p, projectVO);
                    return projectVO;
                }
        ).collect(Collectors.toList());

        // 封装结果
        page<ProjectVO> resultPage = new page<>();
        resultPage.setPageSize(pageSize);
        resultPage.setTotal((int) projectPage.getTotal());
        resultPage.setList(voList);
        
        return Result.success(resultPage);
    }
}
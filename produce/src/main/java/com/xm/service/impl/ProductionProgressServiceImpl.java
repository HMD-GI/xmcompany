package com.xm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.ProductionProgressUpdateDTO;
import com.xm.dto.StockInDTO;
import com.xm.entity.ProductionProgress;
import com.xm.entity.ProductionProject;
import com.xm.mapper.ProductionProgressMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.ProductionProgressService;
import com.xm.service.ProductionProjectService;
import com.xm.service.StockService;
import com.xm.utils.RedisIdGenerator;
import com.xm.utils.UserContext;
import com.xm.vo.ProductionProgressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 生产进度服务实现类
 */
@Slf4j
@Service
public class ProductionProgressServiceImpl extends ServiceImpl<ProductionProgressMapper, ProductionProgress> implements ProductionProgressService {

    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis ID生成器
    
    @Autowired
    private ProductionProjectService productionProjectService; // 注入生产项目服务
    
    @Autowired
    private StockService stockService; // 注入库存服务

    /**
     * 更新生产进度
     * @param progressUpdateDTO 进度更新信息
     * @return Result
     */
    @Override
    @Transactional
    public Result updateProgress(ProductionProgressUpdateDTO progressUpdateDTO) {
        log.info("更新生产进度: {}", progressUpdateDTO);
        
        // 检查生产数量是否有效
        if (progressUpdateDTO.getQuantity() <= 0) {
            return Result.error("生产数量必须大于0");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        // 查询项目信息
        ProductionProject project = productionProjectService.getById(progressUpdateDTO.getProjectId());
        if (project == null) {
            return Result.error("生产项目不存在");
        }
        
        // 检查项目状态
        if (!"进行中".equals(project.getStatus())) {
            return Result.error("只有进行中的项目才能更新进度");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 更新项目进度
        int beforeQuantity = project.getCurrentQuantity();
        int afterQuantity = beforeQuantity + progressUpdateDTO.getQuantity();
        
        // 检查是否超过目标数量
        if (afterQuantity > project.getTargetQuantity()) {
            return Result.error("更新后的生产数量超过目标数量");
        }
        
        // 更新项目当前生产数量
        project.setCurrentQuantity(afterQuantity);
        project.setUpdateTime(now);
        
        // 如果达到目标数量，自动将状态更新为已完成
        if (afterQuantity == project.getTargetQuantity()) {
            project.setStatus("已完成");
            project.setActualEndTime(now);
        }
        
        // 更新项目信息
        productionProjectService.updateById(project);
        
        // 记录进度更新
        ProductionProgress progress = new ProductionProgress();
        progress.setId(redisIdGenerator.generateId("production_progress"));
        progress.setProjectId(project.getId());
        progress.setQuantity(progressUpdateDTO.getQuantity());
        progress.setBeforeQuantity(beforeQuantity);
        progress.setAfterQuantity(afterQuantity);
        progress.setOperatorId(operatorId);
        progress.setOperatorName(operatorName);
        progress.setRemark(progressUpdateDTO.getRemark());
        progress.setOperationTime(now);
        progress.setCreateTime(now);
        
        // 保存进度记录
        this.save(progress);
        
        // 调用库存模块的入库操作
        StockInDTO stockInDTO = new StockInDTO();
        stockInDTO.setMaterialName(project.getProductName());
        stockInDTO.setUnit(project.getUnit());
        stockInDTO.setQuantity(progressUpdateDTO.getQuantity());
        stockInDTO.setRemark("生产项目[" + project.getProjectName() + "]入库");
        
        Result stockInResult = stockService.stockIn(stockInDTO);
        if (stockInResult.getCode()!=0) {
            log.error("产品入库失败");
            return Result.error("产品入库失败 " );
        }
        
        return Result.success();
    }

    /**
     * 查询进度记录详情
     * @param id 记录ID
     * @return Result<ProductionProgressVO>
     */
    @Override
    public Result<ProductionProgressVO> getProgressById(int id) {
        log.info("查询进度记录详情, ID: {}", id);
        
        // 查询进度记录详情
        ProductionProgressVO progressVO = this.baseMapper.selectProgressById(id);
        if (progressVO == null) {
            return Result.error("进度记录不存在");
        }
        
        return Result.success(progressVO);
    }

    /**
     * 分页查询项目进度记录
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param projectId 项目ID
     * @return Result<page<ProductionProgressVO>>
     */
    @Override
    public Result<page<ProductionProgressVO>> getProgressList(int currentPage, int pageSize, int projectId) {
        log.info("分页查询项目进度记录: 页码={}, 每页数量={}, 项目ID={}", currentPage, pageSize, projectId);
        
        // 检查项目是否存在
        ProductionProject project = productionProjectService.getById(projectId);
        if (project == null) {
            return Result.error("生产项目不存在");
        }
        
        // 创建分页参数
        Page<ProductionProgress> pageParam = new Page<>(currentPage, pageSize);
        
        // 执行查询
        IPage<ProductionProgressVO> progressPage = this.baseMapper.selectProgressPage(pageParam, projectId);
        
        // 封装结果
        page<ProductionProgressVO> resultPage = new page<>();
        resultPage.setPageSize(pageSize);
        resultPage.setTotal((int) progressPage.getTotal());
        resultPage.setList(progressPage.getRecords());
        
        return Result.success(resultPage);
    }

    /**
     * 添加进度记录
     * @param progress 进度记录
     * @return Result
     */
    @Override
    public Result addProgressRecord(ProductionProgress progress) {
        log.info("添加进度记录: {}", progress);
        
        // 保存进度记录
        this.save(progress);
        
        return Result.success();
    }
}
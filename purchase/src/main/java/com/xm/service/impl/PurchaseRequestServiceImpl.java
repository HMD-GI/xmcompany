package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.PurchaseRequestQueryDTO;
import com.xm.dto.PurchaseRequestStatusDTO;
import com.xm.entity.PurchaseRequest;
import com.xm.mapper.PurchaseRequestMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.PurchaseRequestService;
import com.xm.vo.PurchaseRequestVO;
import com.xm.utils.RedisIdGenerator;
import com.xm.vo.SimplePurchaseRequestVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 采购申请服务实现类
 */
@Service
public class PurchaseRequestServiceImpl extends ServiceImpl<PurchaseRequestMapper, PurchaseRequest> implements PurchaseRequestService {

    @Autowired
    private PurchaseRequestMapper purchaseRequestMapper; // 注入采购申请Mapper
    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis主键生成器

    /**
     * 新增采购申请
     * @param request 采购申请信息
     * @return Result
     */
    //TODO 采购申请成功时，使用WebSocket发送信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addPurchaseRequest(PurchaseRequest request) {
        // 通过Redis生成采购申请主键ID
        int id = redisIdGenerator.generateId("purchase_request");
        request.setId(id); // 设置主键ID
        // 设置创建和更新时间
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        // 默认状态为草稿
        request.setStatus(0);
        this.save(request);
        return Result.success(id);
    }

    /**
     * 更新采购申请
     * @param request 采购申请信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePurchaseRequest(PurchaseRequest request) {
        // 只允许草稿或驳回状态下修改
        //PurchaseRequest old = this.getById(request.getId());
        PurchaseRequest old = this.lambdaQuery()
                .eq(PurchaseRequest::getId, request.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (old == null) {
            return Result.error("采购申请不存在");
        }
        if (old.getStatus() != 0 && old.getStatus() != 3) {
            return Result.error("当前状态不可修改");
        }
        request.setUpdateTime(LocalDateTime.now());
        this.updateById(request);
        return Result.success(request.getId());
    }

    /**
     * 撤回采购申请
     * @param id 采购申请ID
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result withdrawPurchaseRequest(int id) {
        //PurchaseRequest request = this.getById(id);
        PurchaseRequest request = this.lambdaQuery()
                .eq(PurchaseRequest::getId, id)
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (request == null) {
            return Result.error("采购申请不存在");
        }
        // 只允许审批中、已通过状态撤回
        if (request.getStatus() != 1 && request.getStatus() != 2) {
            return Result.error("当前状态不可撤回");
        }
        request.setStatus(4); // 设置为已撤回
        request.setUpdateTime(LocalDateTime.now());
        this.updateById(request);
        return Result.success("撤回采购申请成功");
    }

    /**
     * 变更采购申请状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePurchaseRequestStatus(PurchaseRequestStatusDTO statusDTO) {
        //PurchaseRequest request = this.getById(statusDTO.getId());
        PurchaseRequest request = this.lambdaQuery()
                .eq(PurchaseRequest::getId, statusDTO.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (request == null) {
            return Result.error("采购申请不存在");
        }
        request.setStatus(statusDTO.getStatus());
        request.setUpdateTime(LocalDateTime.now());
        this.updateById(request);
        return Result.success("变更采购申请状态成功");
    }

    /**
     * 查询采购申请详情
     * @param id 采购申请ID
     * @return Result<PurchaseRequestVO>
     */
    @Override
    public Result<PurchaseRequestVO> getPurchaseRequestById(int id) {
        PurchaseRequest entity = this.getById(id);
        if (entity == null) {
            return Result.error("采购申请不存在");
        }
        // VO转换
        PurchaseRequestVO vo = new PurchaseRequestVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setStatusName(getStatusName(entity.getStatus()));
        return Result.success(vo);
    }

    /**
     * 分页查询采购申请列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseRequestVO>>
     */
    @Override
    public Result<page<SimplePurchaseRequestVO>> getPurchaseRequestList(int currentPage, int pageSize, PurchaseRequestQueryDTO queryDTO) {
        Page<PurchaseRequestVO> pageObj = new Page<>(currentPage, pageSize);
        IPage<PurchaseRequestVO> iPage = purchaseRequestMapper.selectPurchaseRequestPage(pageObj, queryDTO);

        //转换为SimplePurchaseRequestVO对象
        List<SimplePurchaseRequestVO> voList = iPage.getRecords().stream().map(
                request -> {
                    SimplePurchaseRequestVO vo = new SimplePurchaseRequestVO();
                    BeanUtils.copyProperties(request, vo);
                    return vo;
                }
        ).collect(Collectors.toList());

        // 封装自定义page对象
        page<SimplePurchaseRequestVO> resultPage = new page<>();
        resultPage.setList(voList); // 当前页数据
        resultPage.setTotal((int) iPage.getTotal());// 总条数
        resultPage.setPageSize(pageSize);// 每页条数
        return Result.success(resultPage);
    }

    /**
     * 获取状态名称
     * @param status 状态码
     * @return 状态名称
     */
    private String getStatusName(int status) {
        switch (status) {
            case 0: return "草稿";
            case 1: return "审批中";
            case 2: return "已通过";
            case 3: return "已驳回";
            case 4: return "已撤回";
            case 5: return "已完成";
            default: return "未知状态";
        }
    }
} 
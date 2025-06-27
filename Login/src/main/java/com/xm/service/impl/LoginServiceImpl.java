package com.xm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.entity.employeeLogin;
import com.xm.mapper.LoginMapper;
import com.xm.result.Result;
import com.xm.service.LoginService;
import com.xm.utils.JwtUtils;
import com.xm.vo.employeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, employeeLogin> implements LoginService {
    @Autowired
    private LoginMapper loginMapper;

    @Override
    public Result login(String username, String password) {
        // 根据用户名查询用户
        employeeLogin user = loginMapper.selectBy(username);
        if (user == null) {
            return null; // 用户不存在
        }
        // 简单验证密码是否匹配（实际应加密）
        if (!user.getPassword().equals(password)) {
            return Result.error("用户名或密码错误");
        }
        //判断账号是否被封禁
        if (user.getEnabled() == 0) {
            return Result.error("账号被封禁,请联系管理员");
        }

        // 登录成功，更新最近登录时间
        user.setLastLoginTime(java.time.LocalDateTime.now());
        loginMapper.updateById(user); // 更新用户信息
        employeeLoginVO vo = new employeeLoginVO();
        BeanUtils.copyProperties(user,vo);

        //生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        String jwt = JwtUtils.generateJwt(claims);
        vo.setToken(jwt);

        return Result.success(vo); // 登录成功
    }
}
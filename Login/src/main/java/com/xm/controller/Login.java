package com.xm.controller;

import com.xm.result.Result;
import com.xm.service.LoginService;
import com.xm.entity.employeeLogin;
import com.xm.vo.employeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "登录管理", description = "员工登录相关接口")
@RestController
@RequestMapping(value = "/xm")
public class Login {
    @Autowired
    private LoginService loginService;

    /**
     * 员工登录
     * @return
     */
    @Operation(summary = "员工登录", description = "通过用户名和密码进行员工登录认证")
    @GetMapping(value = "/login")
    public Result login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        log.info("用户名：{}，密码：{}", username, password);
        return loginService.login(username, password);
    }

    @Operation(summary = "登录测试", description = "简单的登录测试接口")
    @GetMapping(value = "/l")
    public Result l() {
        return Result.success();
    }
}
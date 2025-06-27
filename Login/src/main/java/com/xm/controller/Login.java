package com.xm.controller;

import com.xm.result.Result;
import com.xm.service.LoginService;
import com.xm.entity.employeeLogin;
import com.xm.vo.employeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/xm")
public class Login {
    @Autowired
    private LoginService loginService;

    /**
     * 员工登录
     * @return
     */
    @GetMapping(value = "/login")
    public Result login(@RequestParam(value = "username") String username,@RequestParam(value = "password") String password) {
        log.info("用户名：{}，密码：{}", username, password);
        return loginService.login(username, password);
    }

    @GetMapping(value = "/l")
    public Result l() {
        return Result.success();
    }
}
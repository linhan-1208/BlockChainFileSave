package com.controller;

import com.CommonMethods.IdWorker;
import com.alibaba.fastjson.JSONObject;
import com.pojo.LoginInfo;
import com.pojo.Result;
import com.pojo.StatusCode;
import com.receive.receiveBody;
import com.service.LoginService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
@RequestMapping("/login/api")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private IdWorker idWorker;

    @PostMapping("/signup")
    @ApiOperation("注册")
    public Result<Object> signup_api(@RequestBody receiveBody rec) {
        String accountId = String.valueOf(idWorker.nextId());
        if (!loginService.isUserExist(rec.getUsername()))
            return new Result(false, StatusCode.LOGIN_ERROR, "该用户名已经注册");
        if (loginService.addUser(accountId, rec.getUsername(), rec.getPassword()) &&
                loginService.addUserInfo(accountId, rec.getAuthor(), rec.getIdCard(), rec.getPhoneNumber(), new Date()))
            return new Result("注册成功");
        else
            return new Result(false, StatusCode.LOGIN_ERROR, "注册失败");
    }

    @GetMapping("/login")
    @ApiOperation("登录")
    public Result<Object> login_api(@RequestParam("username") @ApiParam("用户名") String username,
                                    @RequestParam("password") @ApiParam("密码") String password) {
        if (loginService.isUserExist(username))
            return new Result(false, StatusCode.LOGIN_ERROR, "用户不存在");
        if (loginService.login(username, password)) {
            JSONObject json = new JSONObject();
            LoginInfo userInfo = loginService.getUserInfo(loginService.getUser(username).getAccountId());
            json.put("token", loginService.getToken(userInfo));
            json.put("body", userInfo);
            return new Result(true,StatusCode.OK,"登录成功", json);
        }
        else
            return new Result(false, StatusCode.LOGIN_ERROR, "密码错误");
    }

    @GetMapping
    public String test(){
        return "test";
    }
}

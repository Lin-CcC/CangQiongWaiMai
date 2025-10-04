package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static String wechatURL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String json = wechatLogin(userLoginDTO);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        //是否登录成功
        if (openid == null && openid.length() == 0){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //是否为新用户，新用户就注册并填入相关信息
        User user = userMapper.select(openid);
        if (user == null){
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
            log.info("插入成功：{}", user);
        }
        return user;
    }

    private String wechatLogin(UserLoginDTO userLoginDTO) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(wechatURL, map);
        return json;
    }
}

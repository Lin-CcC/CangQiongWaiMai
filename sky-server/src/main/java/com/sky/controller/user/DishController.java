package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        //判断本地是否有缓存，有则直接在缓存中查询，没有就查询数据库，然后将查出来的数据放到缓存里
        //redis是键值对，key-value，要构造一个key，key是String类型
        String key = "dish_" + categoryId;
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && list.size() > 0){
            return Result.success(list);
        }
        //没有查到
        list = dishService.list(categoryId);
        log.info("当前菜品的列表为：{}", list);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}

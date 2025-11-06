package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    @GetMapping("/list")
    public Result<Setmeal[]> listOfUser(long categoryId){
        List<Setmeal> setmeals = setmealService.list(categoryId);
        // 关键修改：指定 Setmeal 数组类型，长度为 0 即可（会自动扩容）
        Setmeal[] setmealArray = setmeals.toArray(new Setmeal[0]);
        log.info("当前的菜品为：{}", Arrays.toString(setmealArray));  // 建议用 Arrays.toString 打印数组
        return Result.success(setmealArray);  // 无需强制转换，直接返回
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> selectBySetmealId(@PathVariable Long id){
        List<DishItemVO> dishItemVOList = setmealService.selectBySetmealId(id);
        log.info("根据setmeal_id查询列表的结果为：{}", dishItemVOList);
        return Result.success(dishItemVOList);
    }
}

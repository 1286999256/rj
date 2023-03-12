package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.Cuexception;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl <CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(queryWrapper);
        if (count1 >0){
              throw new Cuexception("不能删，关联了菜品");
        }

        LambdaQueryWrapper<Setmeal> SetmealQueryWrapper = new LambdaQueryWrapper<>();
        SetmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(SetmealQueryWrapper);
        if (count2 >0){
            throw new Cuexception("不能删，关联了套餐");
        }


        super.removeById(id);
    }
}

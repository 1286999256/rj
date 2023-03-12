package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

@PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("保存成功");
    }

    @GetMapping("/page")
//    分页查询
    public R<Page> page(int page,int pageSize){

//        分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
//      条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//      查询条件
        queryWrapper.orderByAsc(Category::getSort);
//         分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


            @DeleteMapping
            public R<String> delete(Long id){

//                categoryService.removeById(id);
                 categoryService.remove(id);


                return R.success("成功删除");
            }


            @PutMapping
        public R<String> update(@RequestBody Category category){

                categoryService.updateById(category);
        return R.success("修改成功");}
}
package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//@Api()用于类；表示标识这个类是swagger的资源,tags–表示说明
@Api(tags = "商品分类接口")
public interface CategoryService {

//   @ApiOperation()用于方法；表示一个http请求的操作
    @ApiOperation(value = "通过查询pid商品分类")
    @GetMapping(value = "/category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "通过id删除")
    @DeleteMapping(value = "/category/delete")
    Result<JsonObject> deleteCategoryById(Integer id);
}

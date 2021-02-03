package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2 * @ClassName BrandService
 * 3 * @Description: TODO
 * 4 * @Author jiahang
 * 5 * @Date 2020/12/25
 * 6 * @Version V1.0
 * 7
 **/
@Api(tags = "品牌接口")//表示标识这个类是swagger的资源
public interface BrandService {
    @ApiOperation(value = "获得品牌信息")
    @GetMapping(value = "/brand/list")
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    //接口的默认修饰符为public
    @ApiOperation(value = "新增品牌")
    @PostMapping(value = "/brand/save")
    Result<JsonObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌信息")
    @PutMapping(value = "/brand/save")
    Result<JsonObject> editBrand(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);


    @ApiOperation(value = "删除品牌信息")
    @DeleteMapping(value = "/brand/deleteBrand")
    Result<JsonObject> deleteBrandId(Integer id);

    @ApiOperation(value = "根据分类查询品牌")
    @GetMapping(value = "/brand/getBrandInfoByCategoryId")
    Result<List<BrandEntity>> getBrandInfoByCategoryId(Integer cid);
}

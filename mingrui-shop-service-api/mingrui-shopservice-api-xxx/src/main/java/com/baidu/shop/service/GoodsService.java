package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "/goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation(value = "新增商品")
    @PostMapping(value = "/goodss/save")
    Result<JsonObject> saveGoods(@Validated({MingruiOperation.Add.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "/goodss/save")
    Result<JsonObject> editGoods(@Validated({MingruiOperation.Update.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuId查询spudetail信息")//spudetail是基本信息表
    @GetMapping(value = "goods/getSpuDetailBySpuId")
    Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId);

    @ApiOperation(value = "通过spuId查询sku信息")
    @GetMapping(value = "/goods/getSkusBySpuId")
    Result<List<SkuDTO>> getSkusBySpuId(Integer spuId);

    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "/goods/deleteGoods")
    Result<JsonObject> deleteGoods(Integer spuId);

    @ApiOperation(value = "上下架")
    @PutMapping(value = "/goods/downOrUp")
    Result<JsonObject> upOrDown(@Validated(MingruiOperation.Update.class) @RequestBody SpuDTO spuDTO);
}

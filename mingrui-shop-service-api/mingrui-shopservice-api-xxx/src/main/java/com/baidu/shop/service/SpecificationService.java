package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpecificationService {

    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "spec/groups")
    Result<List<SpecGroupEntity>> getSepcGroupInfo(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "增加")
    @PostMapping(value = "spec/save")
    Result<JsonObject> saveSpecGroup(@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格")
    @PutMapping(value = "spec/save")
    Result<JsonObject> editSpecGroup(@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "spec/delete/{id}")
    Result<JsonObject> deleteSpecGroup(@PathVariable Integer id);
}

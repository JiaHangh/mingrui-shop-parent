package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName BrandServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author jiahang
 * 5 * @Date 2020/12/25
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<JsonObject> deleteBrandId(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
        //封装方法-----通过brandId删除中间表的数据
        this.deleteCategoryBrandList(id);
        return this.setResultSuccess();
    }



    @Transactional
    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //先通过brandId删除中间表的数据
        //封装的方法
        this.deleteCategoryBrandList(brandEntity.getId());

        //批量新增 / 新增
        //String categories = brandDTO.getCategories();//得到分类集合字符串

        this.insertBrandId(brandDTO.getCategories(),brandEntity.getId());

        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> save(BrandDTO brandDTO) {
        //新增返回主键
        //两种方式实现 select-key insert加两个属性

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //处理品牌首字母
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        brandMapper.insertSelective(brandEntity);

        //维护中间表数据
        //String categories = brandDTO.getCategories();//得到分类集合字符串
        //if (StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("新球，错了");
//        ArrayList<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();

        this.insertBrandId(brandDTO.getCategories(),brandEntity.getId());


        return this.setResultSuccess();
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        Example example=new Example(BrandEntity.class);
        //排序
//        if (!StringUtils.isEmpty(brandDTO.getOrder()))
//            example.setOrderByClause(brandDTO.getOrderByClause());
        if(!StringUtils.isEmpty(brandDTO.getSort())) PageHelper.orderBy(brandDTO.getOrderBy());

        example.createCriteria().andLike("name","%"+brandEntity.getName()+"%");

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);
    }

    //通过brandId删除中间表的数据
    private void deleteCategoryBrandList(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    private void insertBrandId(String categories,Integer brandId){

        if (StringUtils.isEmpty(categories)) throw new RuntimeException();
        if (categories.contains(",")){
            categoryBrandMapper.insertList(
                    Arrays.asList(categories.split(","))
                            .stream()
                            .map(categoryIds->new CategoryBrandEntity(Integer.valueOf(categoryIds),brandId))
                            .collect(Collectors.toList())
            );
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandEntity.setBrandId(brandId);
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }

}

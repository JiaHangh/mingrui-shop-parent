package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName GoodsServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author jiahang
 * 5 * @Date 2021/1/5
 * 6 * @Version V1.0
 * 7
 **/
@RestController
@Slf4j
public class GoodsServiceImpl extends BaseApiService implements GoodsService {
    @Resource
    private SpuMapper spuMapper;
    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private StockMapper stockMapper;

    @Override
    @Transactional
    public Result<JsonObject> upOrDown(SpuDTO spuDTO) {
        //如果只传id和saleable,且前台修改上下架的值
        //spuMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO,SpuEntity.class));

        String suc="";
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        if (ObjectUtil.isNull(spuEntity.getSaleable()) && spuEntity.getSaleable()>1) return this.setResultError("失败");
        if (spuEntity.getSaleable()==1){
            spuEntity.setSaleable(0);
            suc="已下架";
        }else{
            spuEntity.setSaleable(1);
            suc="已上架";
        }
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess(suc);
    }

    @Override
    @Transactional
    public Result<JsonObject> deleteGoods(Integer spuId) {
        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);
        //删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //删除sku 和 stock
        //通过spuId查询sku信息
        this.deleteSkusAndStock(spuId);
        return this.setResultSuccess();
    }
    //封装删除sku，stock信息的方法
    private void deleteSkusAndStock(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        //得到skuId集合
        List<Long> skuIdArr = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuIdArr);//通过skuId集合删除sku信息
        stockMapper.deleteByIdList(skuIdArr);//通过skuId集合删除stock信息
    }
    //封装新增sku和stock方法
    private void saveSkusAndStockInfo(SpuDTO spuDTO,Integer spuId,Date date){
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    @Override
    @Transactional
    public Result<JsonObject> editGoods(SpuDTO spuDTO) {
        final Date date = new Date();
        //修改spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO,SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKey(spuEntity);
        //修改spuDetail
        spuDetailMapper.updateByPrimaryKey(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class));
        //修改sku 和 stock，sku和spu是多对一，sku和stock是一对多
        //遍历取出每个sku
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuEntity.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        //得到skuId集合
        List<Long> skuIdArr = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuIdArr);//通过skuId集合删除sku信息
        stockMapper.deleteByIdList(skuIdArr);//通过skuId集合删除stock信息
        //新增
        this.saveSkusAndStockInfo(spuDTO,spuEntity.getId(),date);




        return this.setResultSuccess();
    }

    @Override
    public Result<List<SkuDTO>> getSkusBySpuId(Integer spuId) {
        List<SkuDTO> list= skuMapper.getSkusAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        //查询基本信息
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<JsonObject> saveGoods(SpuDTO spuDTO) {
        final Date date = new Date();
        //新增spu,新增返回主键, 给必要字段赋默认值
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);//是否上架给默认值
        spuEntity.setValid(1);//是否有效给默认值
        spuEntity.setCreateTime(date);//创建时间
        spuEntity.setLastUpdateTime(date);//结束时间
        spuMapper.insertSelective(spuEntity);

        //新增spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增tb_sku   list插入顺序有序 b,a set a,b treeSet b,a
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            //spuDTO中包含stock表新增需要的字段
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuEntity.getId());
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });


        return this.setResultSuccess();//往tb_spu表中新增数据
    }

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {
        //分页
        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();
        //判断是否上架
        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable()<2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        //根据title条件查询
        if (!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%"+spuDTO.getTitle()+"%");
        //排序
        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder()))
            example.setOrderByClause(spuDTO.getOrderBy());

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //分类名称 cid1 + cid2 + cid3
            /*CategoryEntity categoryEntity1 = categoryMapper.selectByPrimaryKey(spuEntity.getCid1());
            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(spuEntity.getCid2());
            CategoryEntity categoryEntity3 = categoryMapper.selectByPrimaryKey(spuEntity.getCid3());
            spuDTO1.setCategoryName(categoryEntity1+"/"+categoryEntity2+"/"+categoryEntity3);*/

            /*List<Integer> cidList = new ArrayList<>();
            cidList.add(spuEntity.getCid1());
            cidList.add(spuEntity.getCid2());
            cidList.add(spuEntity.getCid3());*/
//            List<Integer> cidList = Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3());

            //通过分类id集合查询数据
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
            // 遍历集合并且将分类名称用 / 拼接
            // 3/1/2
            //ajax --> 并不是所有情况都要用异步 jquery.validate 验证用户名存在不存在
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);
//            String categoryName="";
//            List<String> strings = new ArrayList<>();
//            strings.set(0,"");
//            categoryEntities.stream().forEach(categoryEntity -> {
//                strings.set(0,strings.get(0) + categoryEntity.getName() + "/");
//            });
//            categoryName=strings.get(0).substring(0,strings.get(0).length());



            //查询品牌名字,并赋值给spuDto中的品牌名字
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());
        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);
        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal()+"",spuDTOList);
    }
}

package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 2 * @ClassName CategoryEntity
 * 3 * @Description: TODO
 * 4 * @Author jiahang
 * 5 * @Date 2020/12/22
 * 6 * @Version V1.0
 * 7
 **/
//@ApiModel（）用于类：表示对类进行说明，用于参数用实体类接收
@ApiModel(value = "分类实体类")
@Data
@Table(name="tb_category")//spring @Table注解  作用是 ： 声明此对象映射到数据库的数据表，通过它可以为实体指定表
public class CategoryEntity {


//  @ApiModelProperty（）：用于方法，字段；表示对model属性的说明或者是数据操作的更改
//  Value-字段说明  name-重写属性名字 dataType-重写属性类型,required-是否必填,example-举例说明,hidden-隐藏
    @Id
    @ApiModelProperty(value = "分类主键",example = "1")
    @NotNull(message = "ID不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "分类名称")
    @NotEmpty(message = "分类名称不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "父级分类",example = "1")
    @NotNull(message = "父级分类不能为null",groups = {MingruiOperation.Add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否是父级节点",example = "1")
    @NotNull(message = "是否为父级节点不能为null",groups = {MingruiOperation.Add.class})
    private Integer isParent;

    @ApiModelProperty(value = "排序",example = "1")
    @NotNull(message = "排序字段不能为null",groups = {MingruiOperation.Add.class})
    private Integer sort;



}

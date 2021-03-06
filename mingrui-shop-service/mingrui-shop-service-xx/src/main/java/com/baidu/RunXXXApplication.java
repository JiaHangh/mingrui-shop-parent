package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 2 * @ClassName RunXXXApplication
 * 3 * @Description: TODO
 * 4 * @Author jiahang
 * 5 * @Date 2020/12/22
 * 6 * @Version V1.0
 * 7
 **/
@SpringBootApplication
@EnableEurekaClient
//指定要变成实现类的接口所在的包，然后包下面的所有接口在编译之后都会生成相应的实现类
@MapperScan("com.baidu.shop.mapper")// 添加对mapper包扫描
public class RunXXXApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunXXXApplication.class);
    }
}

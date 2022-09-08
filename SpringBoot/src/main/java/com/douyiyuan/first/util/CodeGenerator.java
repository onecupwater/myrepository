package com.douyiyuan.first.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * 代码生成器
 */
public class CodeGenerator {
    public static void main(String[] args) {
        generate();
    }
    private static void generate(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/springboot_vue?serverTimezone=GMT%2b8", "root", "root")
                .globalConfig(builder -> {
                    builder.author("baomidou") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\Idea\\workspace\\SpringBoot\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.douyiyuan.first") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\Idea\\workspace\\SpringBoot\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.controllerBuilder()
                            .enableHyphenStyle() //开启controller中各种mapping路径值的驼峰映射
                            .enableRestStyle();   //开启生成@RestController控制器
                    builder.addInclude("email_verification") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

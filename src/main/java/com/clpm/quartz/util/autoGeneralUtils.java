package com.clpm.quartz.util;

import cn.hutool.extra.template.TemplateException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author 86178
 * @create 2021/11/30 7:18
 */
@Component
public class autoGeneralUtils {
    /**
     * 简单的代码生成器.
     *
     * @param rootPath       maven 的  java 目录
     * @param templatePath   模板存放的文件夹
     * @param templateName   模板的名称
     * @param javaProperties 需要渲染对象的封装
     */
    public static void autoCodingJavaEntity(String rootPath,
                                            String templatePath,
                                            String templateName,
                                            JavaProperties javaProperties) throws IOException, TemplateException, freemarker.template.TemplateException {

        // freemarker 配置
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        configuration.setDefaultEncoding("UTF-8");
        // 指定模板的路径
        configuration.setDirectoryForTemplateLoading(new File(templatePath));
        // 根据模板名称获取路径下的模板
        Template template = configuration.getTemplate(templateName);
        // 处理路径问题
        final String ext = ".java";
        String javaName = javaProperties.getEntityName().concat(ext);
        String packageName = javaProperties.getPkg();

        String out = rootPath.concat(Stream.of(packageName.split("\\."))
                .collect(Collectors.joining("/", "/", "/" + javaName)));

        // 定义一个输出流来导出代码文件
        FileOutputStream fileOutputStream = new FileOutputStream(out);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

        // freemarker 引擎将动态数据绑定的模板并导出为文件
        template.process(javaProperties, outputStreamWriter);

        fileOutputStream.close();
        outputStreamWriter.close();

    }
}

package cn.yswg.codeproduction.module.mybatisgenerator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zch
 * @Date: 2019/5/16 08:40
 * @Description: 逆向工程代码生成器
 */
public class MyGenerator {


    /**
     * 逆向工程generatorConfig.xml配置文件路径
     * @param configPath
     * @throws Exception
     */
    public static void doGenerator(String configPath) throws Exception {

        /*MBG 执行过程中的警告信息*/
        List<String> warnings = new ArrayList<String>();
        /*覆盖原代码*/
        boolean overwrite = true;
        /*读取generatorConfig.xml配置文件*/
        InputStream is = MyGenerator.class.getResourceAsStream(configPath);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        is.close();

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        /*生产文件*/
        myBatisGenerator.generate(null);
        /*打印信息*/
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

}

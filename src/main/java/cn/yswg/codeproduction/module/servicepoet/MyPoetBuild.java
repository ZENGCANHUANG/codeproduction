package cn.yswg.codeproduction.module.servicepoet;




import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 生成service serviceImpl
 */
public class MyPoetBuild {

    /**
     * 生产service serviceImpl文件
     * @param mapperClazz   Mapper类
     * @param packagePath   service的包路径
     */
    public static void doBuild(Class mapperClazz,String packagePath){
        buildServiceDM(mapperClazz, packagePath);
        System.out.println("生成成功");
    }



    /**
     * 自动生成service层代码
     *
     * @param mapper      需要处理的对象 对应的mapper 如：UserMapper
     * @param packageName service层所在包名 如：com.test.service
     */
    private static void buildServiceDM(Class<?> mapper, String packageName) {
        buildService(mapper, packageName);
        buildServiceImpl(mapper, packageName + ".impl");
    }

    /**
     * 自动生成service层代码
     *
     * @param mapper      需要处理的对象 对应的mapper 如：UserMapper
     * @param packageName service层所在包名 如：com.test.service
     */
    private static void buildService(Class<?> mapper, String packageName) {

        Service service = new Service(mapper, packageName);
        String path = getPath();
        String servicePath = path + "/" + getPackageName(packageName) + "/" + service.getClassName() + ".java";

        File file = new File(servicePath);

        FileWriter out = null;
        try {
            out = new FileWriter(file);
            service.serviceFile().writeTo(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 自动生成serviceImpl层代码
     *
     * @param mapper      需要处理的对象 对应的mapper 如：UserMapper
     * @param packageName service层所在包名 如：com.test.service
     */
    private static void buildServiceImpl(Class<?> mapper, String packageName) {

        ServiceImpl serviceImpl = new ServiceImpl(mapper, packageName);
        String path = getPath();
        String servicePath = path + "/" + getPackageName(packageName) + "/" + serviceImpl.getClassName() + ".java";

        File file = new File(servicePath);

        FileWriter out = null;
        try {
            out = new FileWriter(file);
            serviceImpl.serviceFile().writeTo(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取项目物理位置
     *
     * @return
     */
    private static String getPath() {
        String path = null;
        try {
            path = MyPoetBuild.class.getResource("/").toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        path = path.substring(1).replace("target/classes/", "src/main/java");
        return path;
    }

    private static String getPackageName(String packageName) {
        return packageName.replace(".", "/");
    }
}

package cn.relyy.servlet;

import cn.relyy.annotation.ReAutoWired;
import cn.relyy.annotation.ReController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * 自定义一个前端控制器
 *
 * @author cairuirui
 * @create 2018-01-24
 */
public class ReDispatcherServlet extends HttpServlet {

    private Properties p = new Properties();

    private Set<String> classNameSet = new HashSet<String>();

    public static Map<String,Object> ioc = new HashMap<String,Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("servlet初始化开始-------------");

        /**加载配置文件**/
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        /**初始化所有相关的类，扫描用户指定包下所有的类**/
        doScanner(p.getProperty("scannerPage"));

        /**将所有的bean放入IOC容器中**/
        doInstance();
        /**实现依赖注入**/
        doReAutoWired();
        /**实现handleMapping**/
        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


    private void doLoadConfig(String location) {

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location.split(":")[1]);
        try{
            p.load(in);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (null != in) {
                    in.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    private void doScanner(String packageName) {

        //扫描配置的包名，获取该包下所有的类
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()){
                this.doScanner(packageName + "." + file.getName());
            }else {
                String className = packageName + "." + file.getName().replace(".class","");
                classNameSet.add(className);
            }
        }
    }

    private void doInstance() {
        if (CollectionUtils.isNotEmpty(classNameSet)){
            return;
        }

        for (String className : classNameSet) {
            try{
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(ReController.class)){
                    String beanName = toLowFirstChar(clazz.getSimpleName());
                }else if(clazz.isAnnotationPresent(ReAutoWired.class)){

                }else {
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    private void doReAutoWired() {
        
        if(MapUtils.isEmpty(ioc)){
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ReAutoWired.class)) {
                    ReAutoWired autoWired = field.getAnnotation(ReAutoWired.class);
                    String beanName = autoWired.value();
                    if ("" != beanName) {
                        beanName = field.getName();
                    }
                    //允许修改私用成员变量的值
                    field.setAccessible(true);
                    try {
                        field.set(field.getName(),ioc.get(beanName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initHandlerMapping() {
    }

    private String toLowFirstChar(String simpleName) {
        return null;
    }
}

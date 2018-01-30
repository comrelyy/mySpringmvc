package cn.relyy.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 自定义一个前端控制器
 *
 * @author cairuirui
 * @create 2018-01-24
 */
public class ReDispatcherServlet extends HttpServlet {

    private Properties p = new Properties();

    private Set<String> classNameSet = new HashSet<String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("servlet初始化开始-------------");

        /**加载配置文件**/
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        /**初始化所有相关的类，扫描用户指定包下所有的类**/
        doScanner(p.getProperty("scannerPage"));

        /**将说有的bean放入IOC容器中**/
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
        String packageDir = packageName.replaceAll(".","/");
        System.out.println(packageDir);
        File packageUrl = new File(packageDir);
        if (packageUrl.exists()){
            if (packageUrl.isDirectory()) {
                File[] files = packageUrl.listFiles();
            }else {

            }
        }

    }

    private void doInstance() {
    }

    private void doReAutoWired() {
    }

    private void initHandlerMapping() {
    }
}

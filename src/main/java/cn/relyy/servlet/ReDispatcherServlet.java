package cn.relyy.servlet;

import cn.relyy.annotation.*;
import cn.relyy.model.Json;
import cn.relyy.model.MethodModel;
import cn.relyy.model.ParamModel;
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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    public Map<String,Object> ioc = new HashMap<String,Object>();

    public Map<String,MethodModel> urlMapping = new HashMap<String,MethodModel>();

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
        try{
            resp.setContentType("application/json;charset=utf-8");

            String uri  = req.getRequestURI().toString(); //访问路径 不包含ip
//            String requestURL = req.getRequestURL().toString();//访问路径 包含ip
            String contextPath = req.getContextPath();//项目根路径
            //去掉根路径
            String handleUrl = uri.replace(contextPath,"");

            if (!urlMapping.containsKey(handleUrl)){
                resp.getWriter().print("404,请求url有误，请检查！");
                return;
            }
            MethodModel methodModel = (MethodModel) urlMapping.get(handleUrl);
            //获取请求url对应的方法
            Method method = methodModel.getMethod();
            //获取请求url对应的controller
            Object controller = methodModel.getController();
            //获取参数列表
            Map<String,ParamModel> paramMap = methodModel.getParamMap();
            //获取参数值
            Object[] paramObject = getParamValueObj(req, paramMap);

            Object result = null;
            if (MapUtils.isNotEmpty(paramMap)){
                result = method.invoke(controller,paramObject);
            }else {
                result = method.invoke(controller);
            }

            Json json = new Json();
            json.setSuccessValue(result);
            resp.getWriter().print(json);
        }catch(IllegalArgumentException e1){
            resp.getWriter().print(e1.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 加载配置文件
     * @param location
     */
    private void doLoadConfig(String location) {

        //读取配置文件
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

    /**
     * 扫描包
     * @param packageName
     */
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

    /**
     * 初始化ioc容器
     */
    private void doInstance() {
        if (CollectionUtils.isEmpty(classNameSet)){
            return;
        }
        for (String className : classNameSet) {
            try{
                Class<?> clazz = Class.forName(className);
                //forName方法会触发static方法，没有loadClass()方法干净
                //Class<?> clazz = Class.class.getClassLoader().loadClass(className);
                if (clazz.isAnnotationPresent(ReController.class)){
                    String beanName = toLowFirstChar(clazz.getSimpleName());
                    ioc.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(ReService.class)){
                    //如果有别名
                    ReService service = clazz.getAnnotation(ReService.class);
                    Class<?>[] interfaces = clazz.getInterfaces();
                    Object instance = clazz.newInstance();
                    //不是接口的实现
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        //没有设置别名
                        beanName = toLowFirstChar(clazz.getSimpleName());
                    }
                    ioc.put(beanName, instance);

                    //如果是接口的实现
                    for (Class<?> interfaceName : interfaces) {
                        ioc.put(toLowFirstChar(interfaceName.getSimpleName().substring(1)), instance);
                    }
                }else {
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 注入对象
     */
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
                        field.set(entry.getValue(),ioc.get(beanName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    continue;
                }
            }
        }
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()){
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(ReRequestMapping.class)){
                continue;
            }
            StringBuffer requestUrl = new StringBuffer();
            requestUrl.append("/");
            String reqUrl = clazz.getAnnotation(ReRequestMapping.class).value();

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ReRequestMapping.class)){
                    continue;
                }
                String methodUrl = method.getAnnotation(ReRequestMapping.class).value();
                requestUrl.append("/").append(reqUrl).append("/").append(methodUrl);
                String url = requestUrl.toString().replaceAll("//","/");
                requestUrl.setLength(0);

                //找到方法，controller，参数
                MethodModel methodModel = new MethodModel();
                methodModel.setMethod(method);
                methodModel.setController(entry.getValue());

                Map<String,ParamModel> paramModelMap = bulidParamMap(method);
                methodModel.setParamMap(paramModelMap);
                urlMapping.put(url,methodModel);
            }
        }
    }

    private Map<String, ParamModel> bulidParamMap(Method method) {
        Map<String, ParamModel> paramModelMap = new HashMap<String, ParamModel>();

        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0){
            ParamModel paramModel = null;
            for (Parameter param : parameters) {
                if (param.isAnnotationPresent(ReRequestParam.class)) {
                    ReRequestParam reRequestParam = param.getAnnotation(ReRequestParam.class);
                    String value = reRequestParam.value();
                    Boolean require = reRequestParam.required();
                    Class<?> paramType = param.getType();
                    String paramName = param.getName();

                    paramModel = new ParamModel(paramName,value,require,paramType);
                    if ("".equals(value)) {
                        paramModelMap.put(paramName,paramModel);
                    }else {
                        paramModelMap.put(value,paramModel);
                    }
                }else {
                    continue;
                }
            }
        }

        return paramModelMap;
    }

    private Object[] getParamValueObj(HttpServletRequest req, Map<String, ParamModel> paramMap)
            throws IllegalArgumentException{
        Object[] paramObject = new Object[paramMap.size()];

        Map parameterMap = req.getParameterMap();
        int index = 0;
        for (Map.Entry<String, ParamModel> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            ParamModel paramModel = entry.getValue();
            boolean required = paramModel.getRequired();
            if (required && !parameterMap.containsKey(key)){
                throw new IllegalArgumentException("缺少必要参数");
            }
            paramObject[index] = req.getParameterValues(key)[0];
            index++;
        }
        return paramObject;
    }

    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    private String toLowFirstChar(String simpleName) {
        if (null == simpleName && "" == simpleName){
            return simpleName;
        }
        char[] chars = simpleName.trim().toCharArray();
        chars[0] = (char)((int)chars[0] + 32);
        return String.valueOf(chars);
    }
}

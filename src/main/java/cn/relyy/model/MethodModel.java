package cn.relyy.model;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * $DISCRIPTION
 *
 * @author cairuirui
 * @create 2018-02-01
 */
public class MethodModel {

    private Method method;

    private Object controller;

    private Map<String,ParamModel> paramMap;

    public MethodModel() {
    }

    public MethodModel(Method method, Object controller, Map<String,ParamModel> paramMap) {
        this.method = method;
        this.controller = controller;
        this.paramMap = paramMap;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Map<String,ParamModel> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String,ParamModel> paramMap) {
        this.paramMap = paramMap;
    }
}

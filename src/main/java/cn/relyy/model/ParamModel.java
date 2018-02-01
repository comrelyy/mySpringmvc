package cn.relyy.model;

/**
 * $DISCRIPTION
 *
 * @author cairuirui
 * @create 2018-02-01
 */
public class ParamModel {

    private String paramName;

    private String alisName;

    private Boolean required = true;

    private Class<?> type;

    public ParamModel() {
    }

    public ParamModel(String paramName, String alisName, Boolean required, Class<?> type) {
        this.paramName = paramName;
        this.alisName = alisName;
        this.required = required;
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getAlisName() {
        return alisName;
    }

    public void setAlisName(String alisName) {
        this.alisName = alisName;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}

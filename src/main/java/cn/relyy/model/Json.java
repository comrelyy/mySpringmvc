package cn.relyy.model;

import java.util.HashMap;

/**
 * 返回的json数据，标准格式
 * 
 * @author zuxin
 * 
 *         2015年8月17日
 */
public class Json extends HashMap<String, Object> {

	private static final long serialVersionUID = -1765444265016311004L;

	/**
	 * 正常数据处理
	 */
	public void setSuccessValue(Object rt) {
		put("status", "200");
		put("currentTime", System.currentTimeMillis());
		put("msg", "请求成功");
		put("rt", rt);
		
	}

	/**
	 * 异常数据处理，自定义的异常信息
	 */
	public void setExceptionValue(String msg) {
		put("status", "-1");
		put("currentTime", System.currentTimeMillis());
		put("msg", msg == null ? "服务器暂无回应，请稍后重试" : msg);
	}

	/**
	 * 异常数据处理，输出异常类信息
	 */
	public void setExceptionValue(Exception e) {
		put("status", "-1");
		put("currentTime", System.currentTimeMillis());
		put("msg", "服务器暂无回应，请稍后重试");
		if (e == null) {
			put("info", null);
		} else {
			String eMsg = e.toString();
			StackTraceElement[] sts = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append(eMsg + "\n");
			if (sts != null && sts.length != 0) {
				for (StackTraceElement st : sts) {
					if (st != null) {
						sb.append(st.getClassName() + "." + st.getMethodName() + "(" + st.getLineNumber() + ")\n");
					}
				}
			}
			put("info", sb.toString());
		}
	}

	/**
	 * 异常数据处理，输出自定义异常信息和异常类信息
	 */
	public void setExceptionValue(String msg, Exception e) {
		put("status", "-1");
		put("currentTime", System.currentTimeMillis());
		put("msg", msg == null ? "服务器暂无回应，请稍后重试" : msg);
		if (e == null) {
			put("info", null);
		} else {
			String eMsg = e.toString();
			StackTraceElement[] sts = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append(eMsg + "\n");
			if (sts != null && sts.length != 0) {
				for (StackTraceElement st : sts) {
					if (st != null) {
						sb.append(st.getClassName() + "." + st.getMethodName() + "(" + st.getLineNumber() + ")\n");
					}
				}
			}
			put("info", sb.toString());
		}
	}
}

package cn.relyy.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义一个前端控制器
 *
 * @author cairuirui
 * @create 2018-01-24
 */
public class ReDispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("servlet初始化开始-------------");

        /**加载配置文件**/

        /**初始化所有相关的类，扫描用户指定包下所有的类**/

        /**将说有的bean放入IOC容器中**/

        /**实现依赖注入**/

        /**实现handleMapping**/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}

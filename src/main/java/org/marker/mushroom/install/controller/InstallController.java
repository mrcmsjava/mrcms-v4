package org.marker.mushroom.install.controller;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.marker.mushroom.core.AppStatic;
import org.marker.mushroom.core.DataSourceProxy;
import org.marker.mushroom.core.config.ConfigDBEngine;
import org.marker.mushroom.core.config.impl.SystemBaseConfig;
import org.marker.mushroom.core.config.impl.SystemConfig;
import org.marker.mushroom.core.domain.MessageResult;
import org.marker.mushroom.ext.message.MessageDBContext;
import org.marker.mushroom.holder.SpringContextHolder;
import org.marker.mushroom.install.domain.InstallParams;
import org.marker.mushroom.support.SupportController;
import org.marker.mushroom.utils.*;
import org.marker.security.DES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Map;

import static org.marker.mushroom.core.DataSourceProxy.DATASOURCE_PROXY_BEAN_NAME;


/**
 * 安装MRCMS引导
 *
 * @author marker
 */
@Controller
@Slf4j
@RequestMapping("/install")
public class InstallController extends SupportController {


    public InstallController() {
        this.viewPath = "/install/";
    }



    @Resource
    private SystemConfig systemConfig;

    /**
     * 添加用户
     */
    @RequestMapping("/index")
    public ModelAndView installIndex(HttpServletResponse response) {
        if (WebUtils.checkInstall()) {
            response.setStatus(404);
            return null;
        }
        ModelAndView view = new ModelAndView(this.viewPath + "index");
        return view;
    }


    /**
     * 安装界面（配置数据库信息）
     */
    @RequestMapping("/install")
    public ModelAndView install(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {
        if (WebUtils.checkInstall()) {
            response.setStatus(404);
            return null;
        }
        ModelAndView view = new ModelAndView(this.viewPath + "install");

        String secretKey = DES.getSecretKey(null);
        view.addObject("secretKey", secretKey);

        return view;
    }

    /**
     * 获取配置文件中的驱动
     */
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * 数据库检查
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST )
    @ResponseBody
    public MessageResult check(@RequestBody InstallParams installParams, HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {
        if (WebUtils.checkInstall()) {
            response.setStatus(404);
            return null;
        }
        SpringUtils.getBean(DataSourceProxy.class); // 检查是否存在代理，方便判断bean是否就绪

        Connection conn = null;
        try {
            String host = installParams.getDbHost();
            int port = Integer.valueOf(installParams.getDbPort());
            String user = installParams.getDbUser();
            String password = installParams.getDbPassword();

            String url = "jdbc:mysql://" + host + ":" + port + "/" + "?useUnicode=true&characterEncoding=UTF-8";
            Class.forName(driverClassName);

            conn = DriverManager.getConnection(url, user, password);
            conn.createStatement().execute("select sysdate();");
            return MessageResult.success();
        } catch (Exception e) {
            log.error("数据库连接失败！{}", e.getMessage());
            return MessageResult.error("数据库连接失败! <br/>" + StringUtils.substring(e.getMessage(),0,100));
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("数据库连接关闭失败！");
            }
        }
    }


    /**
     * 执行安装
     */
    @RequestMapping("/progress")
    public synchronized ModelAndView progress(HttpServletRequest
                                          request) throws NoSuchAlgorithmException {
        // 获取 ApplicationContext
        ApplicationContext context = SpringContextHolder.getApplicationContext();

        String exceptionStr = "";
        ModelAndView view = new ModelAndView(this.viewPath + "complete");
        // 项目真实路径
        String WebRootRealPath = application.getRealPath(File.separator);
        // 虚拟路径
        String WebRootPath = HttpUtils.getRequestURL(request);

        if (WebUtils.checkInstall()) {
            view.addObject("install",true);
            view.addObject("exceptionStr", "已经安装过了！");
            view.addObject("WebRootPath", WebRootPath);
            return view;
        }

        String host = request.getParameter("DB_HOST");
        String name = request.getParameter("DB_NAME");
        String port = request.getParameter("DB_PORT");
        String user = request.getParameter("DB_USER");
        String pass = request.getParameter("DB_PWD");
        String spot = request.getParameter("spot");//加密Key
        String prefix = request.getParameter("DB_PREFIX");//表前缀

        boolean status = (host != null) && name != null &&
                port != null && user != null && pass != null && spot != null && prefix != null;

        if (status) {
            String jdbcurl = "jdbc:mysql://" + host + ":" + port + "/" + "?useUnicode=true&characterEncoding=UTF-8";
            String jdbcDBurl = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useUnicode=true&characterEncoding=UTF-8";

            try {

                /* ==============================================
                 *              1. 获取数据库链接
                 * ==============================================
                 */
                Class.forName(driverClassName);
                Connection conn = DriverManager.getConnection(jdbcurl, user, pass);

                /* =======================================================
                 *   2. 获取数据库链接，并判断数据库是否存在，不存在就创建
                 * =======================================================
                 */
                String checkAndCreateSql = "CREATE database IF NOT EXISTS " + name;
                PreparedStatement ps = conn.prepareStatement(checkAndCreateSql);
                ps.executeUpdate();
                ps.close();

                /* =======================================================
                 *   3. 读取建表SQL信息，并创建表
                 * =======================================================
                 */
                String sql = FileUtils.getResourceFile("/data/sql/db_app.sql");
                sql = sql.replaceAll("`mr_", "`"+prefix);//替换前缀
                sql = sql.replace("\r\n","\n"); // 统一转换unix换行格式
                sql = sql.replace("\r","\n"); // 统一转换unix换行格式
//                System.out.println(sql);

                String[] sqla = sql.split(";\n");
                conn.setCatalog(name);
                Statement statement = conn.createStatement();
                log.info("===========SQL执行开始===========");
                for (int i = 0; i < sqla.length; i++) {
                    String a = sqla[i];
                    if (a != null && !"".equals(a.trim())) {
                        log.info("SQL_{}:{}", (i+1),a);
                        statement.executeLargeUpdate(a);
                    }
                }
                log.info("===========SQL执行完成===========");
                statement.close();


                /* =======================================================
                 *   5. 数据库链接
                 * =======================================================
                 */
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setDriverClassName(driverClassName);
                dataSource.setJdbcUrl(jdbcDBurl);
                dataSource.setUsername(user);
                dataSource.setPassword(pass);
//                dataSource.setMaximumPoolSize(Integer.parseInt(dbc.getProperties().getProperty("mushroom.druid.maxActive")));
//                dataSource.setMinimumIdle(Integer.parseInt(dbc.getProperties().getProperty("mushroom.druid.initialSize")));
                DataSourceProxy proxy = SpringContextHolder.getBean(DATASOURCE_PROXY_BEAN_NAME);
                proxy.setDataSource(dataSource);

                // 6 load 数据库配置
                // 获取所有 ConfigDBEngine 类型的 Bean
                Map<String, ConfigDBEngine> configDBEngineMap = context.getBeansOfType(ConfigDBEngine.class);
                configDBEngineMap.values().forEach(config->{
                    log.info("加载DB配置文件：{}", config.getClass().getSimpleName());
                    config.read();
                });


                MessageDBContext messageDBContext = MessageDBContext.getInstance();
                if(!messageDBContext.isInit()){
                    try {
                        messageDBContext.init();
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }



                /* ==============================================
                 *              7. 系统加密Key持久化
                 * ==============================================
                 */
                systemConfig.set("secret_key", spot);//更新Key
                systemConfig.store();//保存

                // 更新密码默认用户admin
                String pass2 = GeneratePass.encode("mrcms");
                ps = conn.prepareStatement("update " + prefix + "user set pass='" + pass2 + "' where id=1");
                ps.executeUpdate();
                ps.close();
                conn.close();

                // 设置安装状态文件(降级判断会通过文件判断)
                application.setAttribute(AppStatic.WEB_APP_INSTALL, true);
//                String BasePath = application.getRealPath("/data/");
//                OutputStream os = new FileOutputStream(new File(BasePath + "/install.lock"));
//                os.write(0);
//                os.flush();
//                os.close();

                /* ==============================================
                 *       数据库设置持久化
                 * ==============================================
                 */
                SystemBaseConfig dbc = SystemBaseConfig.getInstance();
                dbc.set("mushroom.db.host", host);
                dbc.set("mushroom.db.port", port);
                dbc.set("mushroom.db.demo", name);
                dbc.set("mushroom.db.user", user);
                dbc.set("mushroom.db.pass", pass);
                dbc.set("mushroom.db.prefix", prefix);
                dbc.set("mrcms.install", "true");  // 设置安装状态(必须)
                dbc.store();//保存
            } catch (Exception e) {
                view.addObject("install", false);
                exceptionStr = e.getMessage();
                view.addObject("exceptionStr", exceptionStr);
                view.addObject("WebRootPath", WebRootPath);
                log.error("mrcms install exception", e);
                return view;
            }
        }

        view.addObject("install", true);
        view.addObject("WebRootPath", WebRootPath);

        return view;
    }


}

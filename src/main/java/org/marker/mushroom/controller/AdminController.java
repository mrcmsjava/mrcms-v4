package org.marker.mushroom.controller;

import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.marker.mushroom.beans.LoginRequestDTO;
import org.marker.mushroom.beans.ResultMessage;
import org.marker.mushroom.beans.User;
import org.marker.mushroom.beans.UserLoginLog;
import org.marker.mushroom.core.AppStatic;
import org.marker.mushroom.core.config.impl.SystemConfig;
import org.marker.mushroom.dao.IMenuDao;
import org.marker.mushroom.dao.IUserDao;
import org.marker.mushroom.dao.IUserLoginLogDao;
import org.marker.mushroom.support.SupportController;
import org.marker.mushroom.utils.GeneratePass;
import org.marker.mushroom.utils.HttpUtils;
import org.marker.mushroom.utils.WebUtils;
import org.marker.qqwryip.IPLocation;
import org.marker.qqwryip.IPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;



/**
 * 后台管理主界面控制器
 * @author marker
 * 
 * */
@Controller
@RequestMapping("/admin")
public class AdminController extends SupportController {

	/** 日志记录器 */ 
	private Logger logger =  LoggerFactory.getLogger(AdminController.class);
	
	@Autowired IUserDao userDao;
	@Autowired IUserLoginLogDao userLoginLogDao;
	@Autowired IMenuDao menuDao;
	@Autowired
	ServletContext application;
	@Resource
	private SystemConfig syscfg;


	
	/** 构造方法初始化一些成员变量 */
	public AdminController() {
		this.viewPath = "/admin/";
	} 
	
	 
	
	/** 后台主界面 */ 
	@RequestMapping("/index")
	public String index(HttpServletRequest request){
		// 如果没有安装系统
		if(!WebUtils.checkInstall())
			return "redirect:../install/index.do";
		
		request.setAttribute("url", HttpUtils.getRequestURL(request)); 
		HttpSession session = request.getSession(false);
		if(session != null){
			try{
				int groupId = (Integer) session.getAttribute(AppStatic.WEB_APP_SESSSION_USER_GROUP_ID);
				request.setAttribute("topmenus", menuDao.findTopMenuByGroupId(groupId)); 
			}catch (Exception e) {
				log.error("因为没有登录，在主页就不能查询到分组ID");
				return "redirect:login.do";
			}
		} else {

			log.error("因为没有登录，在主页就不能查询到分组ID");
			return "redirect:login.do";
		}
		
		return this.viewPath + "index";
	}

	
	/**
	 * 登录操作
	 * @param request
	 * @return
	 */
	@RequestMapping("/login")
	public String login(
			@RequestParam(value = "safe", defaultValue = "") String safe,
			HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		// 如果没有安装系统
		if(!WebUtils.checkInstall())
			return "redirect:../install/index.do";

        request.setAttribute("url", HttpUtils.getRequestURL(request));

        String systemLoginSafe = syscfg.getLoginSafe();

		if(!systemLoginSafe.equals(safe)){// 验证登录路径
            return this.viewPath + "404";
		}

		
		HttpSession session = request.getSession(true);
		if(session != null){
			try{
				User user = (User) session.getAttribute(AppStatic.WEB_APP_SESSION_ADMIN);
				if(null != user){
					return "redirect:index.do";
				}
			}catch (Exception e) {}
		} 
		return this.viewPath + "login";
	}
	
	

	
	/**
	 * 注销
	 * */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession(false);
		if(session != null) session.invalidate();

        String systemLoginSafe = syscfg.getLoginSafe();


        return "redirect:login.do?safe="+systemLoginSafe;
	}
	
	
	
	/**
	 * 系统信息
	 * */
	@RequestMapping("/systeminfo")
	public ModelAndView systeminfo(){
		ModelAndView view = new ModelAndView(this.viewPath + "systeminfo");
		String os = System.getProperty("os.name");//操作系统名称
		String osVer = System.getProperty("os.version"); //操作系统版本    
		String javaVer = System.getProperty("java.version"); //操作系统版本
		String javaVendor = System.getProperty("java.vendor"); //操作系统版本
		
		Runtime runTime = Runtime.getRuntime();
		
		long freeM = runTime.freeMemory() / 1024 / 1024;
        long maxM  = runTime.maxMemory() / 1024 / 1024;
        long tM    = runTime.totalMemory() / 1024 / 1024; 
        view.addObject("freememory", freeM);
        view.addObject("maxmemory", maxM);
        view.addObject("totalmemory", tM);
		view.addObject("os", os);
		view.addObject("osver", osVer);
		view.addObject("javaver", javaVer);
		view.addObject("javavendor", javaVendor);
		view.addObject("currenttime", new Date());
		
		view.addObject("serverinfo", application.getServerInfo());
		view.addObject("dauthor", "marker");
		view.addObject("email", "admin@wuweibi.com");
		view.addObject("version", "3.0");
		view.addObject("qqqun","331925386");
		view.addObject("uxqqqun","181150189");
		
		return view;
	}
	
}

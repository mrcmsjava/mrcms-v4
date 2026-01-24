package org.marker.mushroom.core.config.impl;

import org.marker.mushroom.core.config.ConfigEngine;
import org.marker.mushroom.holder.SpringContextHolder;
import org.marker.mushroom.utils.PathUtils;
import org.marker.mushroom.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 系统基本配置（包含数据库的配置、缓存配置等）
 * 
 * 说明：获取数据库链接信息的途径
 * 实现：继承ConfigEngine实现的
 * 
 * @author marker
 * */
public class SystemBaseConfig extends ConfigEngine {

	// 表前缀变量
	public static final String DB_TABLE_PREFIX = "mushroom.db.prefix";

	/**
	 * 默认构造方法
	 * @throws IOException 
	 * */
	public SystemBaseConfig() {
		this.properties = SpringContextHolder.getBean("configProperties");
	}

	
	/**
	 * 获取数据库配置实例
	 * */
	public static SystemBaseConfig getInstance() {
		return SpringContextHolder.getApplicationContext().getBean(SystemBaseConfig.class);
	}

	/**
	 * 获取表前缀
	 * */
	public String getPrefix(){
		return this.getProperty(DB_TABLE_PREFIX,"mr_");
		
	}

	/**
	 * 是否安装
	 * @return boolean
	 */
	public boolean isInstall() {
		String isInstall = (String) this.getProperty("mrcms.install", "false");
		logger.info("mrcms.install = {}", isInstall);
		return Boolean.parseBoolean(isInstall);
	}

	public interface Names{
		String DESCRIPTION = "desription";
	}


	/**
	 * debug模式
	 * @return  boolean
	 */
	public boolean debug() {
		String debug = (String) this.properties.get("mushroom.db.debug");
		if(debug != null){
			return Boolean.valueOf(debug);
		}
		return false;// 默认情况为false
	}


	
	/**
	 * 是否为debug模式
	 * @return
	 */
	public boolean isDebug() {
		String debug = (String) this.properties.get("mushroom.db.debug");
		if(debug != null){
			return Boolean.valueOf(debug);
		}
		return false;
	}

 
 
	
}

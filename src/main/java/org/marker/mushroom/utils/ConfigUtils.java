package org.marker.mushroom.utils;

import java.io.File;

public class ConfigUtils {



    /**
     * 获取自定义配置文件
     * @return
     */
    public static String getCustomConfigFile(){
        String baseConfigFile =  System.getProperty("mrcms.config");
        return StringUtil.isBlank(baseConfigFile)? PathUtils.getHomePath() + File.separator+ "conf"+ File.separator+"config.properties": baseConfigFile;
    }

}

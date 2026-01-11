package org.marker.mushroom.utils;

import org.apache.commons.lang3.SystemUtils;
import org.marker.mushroom.MrcmsApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class PathUtils {


    /**
     * 标准化文件路径
     */
    public static String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return path;
        }

        // 处理路径分隔符
        String normalized = path.replace("\\", File.separator)
                .replace("/", File.separator);

        // 使用Path API进行标准化
        try {
            Path pathObj = Paths.get(normalized);
            return pathObj.normalize().toString();
        } catch (Exception e) {
            // 如果Path.get失败，尝试基本的字符串处理
            return basicNormalize(normalized);
        }
    }

    /**
     * 基本的路径标准化（当Path API不可用时）
     */
    private static String basicNormalize(String path) {
        // 处理 "./" 和 "../"
        String[] parts = path.split(File.separator.replace("\\", "\\\\"));
        List<String> result = new ArrayList<>();

        for (String part : parts) {
            if (part.equals("..")) {
                if (!result.isEmpty() && !result.get(result.size() - 1).equals("..")) {
                    result.remove(result.size() - 1);
                } else {
                    result.add("..");
                }
            } else if (!part.equals(".") && !part.isEmpty()) {
                result.add(part);
            }
        }

        return String.join(File.separator, result);
    }

    /**
     * 确保路径以分隔符结尾
     */
    public static String ensureEndsWithSeparator(String path) {
        if (path == null) return null;
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    /**
     * 检查路径是否在指定基路径内（安全考虑）
     */
    public static boolean isPathInBaseDirectory(String path, String baseDir) {
        try {
            Path normalizedPath = Paths.get(path).normalize().toAbsolutePath();
            Path normalizedBase = Paths.get(baseDir).normalize().toAbsolutePath();
            return normalizedPath.startsWith(normalizedBase);
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * 将路径退一个目录
     */
    public static String goBackOneDirectory(String filePath) {
        // 移除file:前缀
        String pathWithoutPrefix = filePath.replaceFirst("^file://", "");

        // 使用Path API处理
        Path path = Paths.get(pathWithoutPrefix);

        // 获取父目录
        Path parentPath = path.getParent();

        if (parentPath != null) {
            // 重新添加file:前缀
            return "file:"+parentPath.toString();
        } else {
            throw new IllegalArgumentException("无法获取父目录: " + filePath);
        }
    }


    /**
     * 获取mrcms运行的根路径
     */
    public static String getHomePath() {
        String runningfilePath =getApplicationDir() ;
        if (isRunningInJar()) { // jar包运行
            return runningfilePath.replace("\\libs","");
        } else {// 源码运行 target目录
            return runningfilePath.replace("\\target\\classes","\\build");
        }
    }
    /**
     * 获取 Spring Boot 应用运行目录
     * 适用于 JAR 包和 IDE 运行
     */
    public static String getApplicationDir() {
        // ApplicationHome 会自动判断运行环境
        ApplicationHome home = new ApplicationHome(MrcmsApplication.class);
        File dir = home.getDir();  // 获取目录
        return dir.getAbsolutePath();
    }



    /**
     * 获取当前运行的JAR文件路径
     */
    public static String getJarFilePath() {
        // 获取主类的位置
        ProtectionDomain protectionDomain = PathUtils.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();

        if (codeSource != null && codeSource.getLocation() != null) {
            try {
                File jarFile = new File(codeSource.getLocation().toURI());
                return jarFile.getAbsolutePath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取JAR包所在目录
     */
    public static String getJarDirectory() {
        String jarPath = getJarFilePath();
        if (jarPath != null) {
            File jarFile = new File(jarPath);
            return jarFile.getParent();
        }
        return null;
    }


    /**
     * Spring Boot官方推荐的方法
     */
    public static boolean isRunningInJar() {
        try {
            ApplicationHome home = new ApplicationHome(MrcmsApplication.class);
            File source = home.getSource();
            if (source == null) {
                return false;
            }
            return source.getName().toLowerCase().endsWith(".jar");
        } catch (Exception e) {
            return false;
        }
    }

}
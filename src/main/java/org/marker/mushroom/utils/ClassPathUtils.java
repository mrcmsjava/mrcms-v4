package org.marker.mushroom.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassPathUtils {
    private final static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    /**
     *
     // 复制 config 目录下所有文件
     // copier.copyFilesFromClasspath("config/*", "/tmp/configBackup");
     * @param classpathPattern
     * @param targetDirectory
     * @throws IOException
     */
    public static void copyFilesFromClasspath(String classpathPattern, String targetDirectory) throws IOException {
        copyWithStructure(classpathPattern, targetDirectory);
    }


    /**
     * 按照模块相对路径结构复制 classpath 下的文件
     * @param classpathBaseDir classpath 中的基础目录，如 "modules/"
     * @param targetBaseDir 目标基础目录，如 "/tmp/output/"
     * @throws IOException
     */
    public static void copyWithStructure(String classpathBaseDir, String targetBaseDir) throws IOException {
        // 确保目标基础目录存在
        Path targetBasePath = Paths.get(targetBaseDir);
        if (Files.notExists(targetBasePath)) {
            Files.createDirectories(targetBasePath);
        }

        // 搜索 classpathBaseDir 下的所有文件
        String searchPattern = "classpath*:" + classpathBaseDir + "**/*";
        Resource[] resources = resolver.getResources(searchPattern);

        for (Resource resource : resources) {
            if (resource.isReadable() && !isDirectoryResource(resource)) {
                copySingleFile(resource, classpathBaseDir, targetBasePath);
            }
        }
    }

    /**
     * 判断资源是否表示目录
     */
    private static boolean isDirectoryResource(Resource resource) throws IOException {
        // 根据文件名和 URI 判断是否为目录
        String filename = resource.getFilename();
        return filename == null || resource.getURI().toString().endsWith("/");
    }

    /**
     * 复制单个文件并保持目录结构
     */
    private static void copySingleFile(Resource resource, String classpathBaseDir, Path targetBasePath) throws IOException {
        // 获取资源的完整 classpath URI
        String resourceUri = resource.getURI().toString();

        // 提取相对于 classpathBaseDir 的相对路径
        String relativePath = extractRelativePath(resourceUri, classpathBaseDir);

        if (relativePath != null) {
            // 构建目标文件路径
            Path targetFilePath = targetBasePath.resolve(relativePath);

            // 创建目标目录（如果不存在）
            Path targetDir = targetFilePath.getParent();
            if (targetDir != null && Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 复制文件内容
            StreamUtils.copy(resource.getInputStream(), Files.newOutputStream(targetFilePath));
            System.out.println("Copied: " + relativePath + " to " + targetFilePath);
        }
    }

    /**
     * 从资源 URI 中提取相对于基础目录的路径
     */
    private static String extractRelativePath(String resourceUri, String classpathBaseDir) {
        // 查找基础目录在 URI 中的位置
        int baseDirIndex = resourceUri.indexOf(classpathBaseDir);
        if (baseDirIndex == -1) {
            return null;
        }

        // 提取相对路径部分
        String relativePath = resourceUri.substring(baseDirIndex + classpathBaseDir.length());

        // 处理 JAR 文件中的路径（包含 "!/" 分隔符的情况）
        if (relativePath.contains("!/")) {
            relativePath = relativePath.substring(relativePath.indexOf("!/") + 2);
        }

        // 移除可能的查询参数
        if (relativePath.contains("?")) {
            relativePath = relativePath.substring(0, relativePath.indexOf("?"));
        }

        return relativePath;
    }

}

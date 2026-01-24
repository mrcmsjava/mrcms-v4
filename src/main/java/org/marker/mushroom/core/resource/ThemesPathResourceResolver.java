package org.marker.mushroom.core.resource;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.marker.mushroom.core.config.impl.SystemBaseConfig;
import org.marker.mushroom.core.config.impl.SystemConfig;
import org.marker.mushroom.utils.PathUtils;
import org.marker.mushroom.utils.StringUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThemesPathResourceResolver extends PathResourceResolver {



    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {

        List<Resource> locations2 = new ArrayList<>();
        SystemConfig systemConfig = SystemConfig.getInstance();
        String themesPath = systemConfig.getThemesPath();
        if (StringUtils.isNotBlank(themesPath)) {
            locations2.add(new FileSystemResource(themesPath));
        }
        return super.resolveResourceInternal(request,resourcePath,locations2,chain);
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return null;
    }
}

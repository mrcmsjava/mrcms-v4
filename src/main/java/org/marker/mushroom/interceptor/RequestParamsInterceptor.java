package org.marker.mushroom.interceptor;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;


/**
 * 请求信息打印拦截器
 *
 * 目前答应了请求参数
 *
 *
 * @author marker
 *
 * */
public class
RequestParamsInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(RequestParamsInterceptor.class);

    @Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute("requestTime", System.currentTimeMillis());

		return true;
	}


	private void warnInfo(HttpServletRequest request,
                     HttpServletResponse response) throws IOException {


    }

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (long)request.getAttribute("requestTime");
		String METHOD = request.getMethod();
		String uri = request.getRequestURI();
		long time = System.currentTimeMillis()- startTime;
		logger.debug("{}\t{}\t{}\t{}", time,METHOD, uri,  JSON.toJSON(request.getParameterMap()));
	}

}

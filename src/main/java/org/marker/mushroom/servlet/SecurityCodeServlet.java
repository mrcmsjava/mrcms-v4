package org.marker.mushroom.servlet;

import com.google.code.kaptcha.Producer;
import jakarta.servlet.annotation.WebServlet;
import org.marker.mushroom.core.AppStatic;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;



/**
 * 系统验证码
 * 此验证码接口可以可以在需要使用验证码的地方调用。
 * 内置配置文件，可以配置验证码的难易度、生成内容等等为系统安全提供解决方案。
 * url:/SecurityCode
 * @author marker
 * @version 1.0
 */
@WebServlet("/SecurityCode")
public class SecurityCodeServlet extends HttpServlet {

	private static final long serialVersionUID = 4898141833479363528L;



    @Autowired
    private Producer producer;
	/**
	 * 
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        String text = producer.createText();
		// 将四位数字的验证码保存到Session中。
		HttpSession session = request.getSession(true);
		session.setAttribute(AppStatic.WEB_APP_AUTH_CODE, text.toLowerCase());
		BufferedImage image = producer.createImage(text);
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
	}

	/**
	 * 
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	
	/**
	 * 获取给定范围的随机颜色
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) { 
        Random random = new Random();   
        if (fc > 255)   
            fc = 255;   
        if (bc > 255)   
            bc = 255;   
        int r = fc + random.nextInt(bc - fc);   
        int g = fc + random.nextInt(bc - fc);   
        int b = fc + random.nextInt(bc - fc);   
        return new Color(r, g, b);   
    }   


}

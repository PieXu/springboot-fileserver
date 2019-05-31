package com.innovate.filseserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.easysoft.commons.cons.Constants;

/**
 * 
* Title: LoginInterceptor
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月28日
 */
public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse, Object paramObject, Exception paramException)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Object paramObject, ModelAndView paramModelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object paramObject) throws Exception {
        HttpSession session = request.getSession();
        Object userInfo = session.getAttribute(Constants.SESSION_USER_KEY);
        if (userInfo == null) {
        	session.setAttribute("preurl",request.getRequestURI());
            StringBuffer url = request.getRequestURL();
            String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append(request.getServletContext().getContextPath()).append("/").toString();
            response.sendRedirect(tempContextUrl);
            return false;
        }
        return true;
	}

}

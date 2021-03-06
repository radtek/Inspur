package com.system;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
public class LoginFilter implements Filter{
	
	private FilterConfig config;
	
	@Override
	public void destroy(){
		System.out.println("XuHui: Filter has been destroyed successfully.");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException{
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		/*
		 * 	如果是接口操作，则不进行过滤
		 * 
		 * */
		if(httpRequest.getParameter("isInterface")!=null && "Y".equals(httpRequest.getParameter("isInterface"))){
			chain.doFilter(request,response);
		}else{	
			String isLogin = httpRequest.getParameter("isLogin");
			if((isLogin==null || !"Y".equals(isLogin))&&(httpRequest.getSession().getAttribute("LoginUserInfo")==null)){
				/*如果当前进行的不是登录操作、且登录信息为空、则跳转至登录界面*/
				String loginPage= config.getInitParameter("loginPage"); 
				request.getRequestDispatcher(loginPage).forward(request, response);
			}else{
				chain.doFilter(request,response);
			}
		}	
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.config = arg0; 
		System.out.println("XuHui: Filter has been inited successfully.");		
	}	
}

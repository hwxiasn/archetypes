package com.qingbo.gingko.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Service("excetionResolver")
public class ExceptionResolver extends DefaultHandlerExceptionResolver {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		boolean admin = request.getRequestURI().contains("/admin/");
		String VM_404 = (admin?"admin/":"")+"404";
		
		if(ex instanceof AuthorizationException) {
			return new ModelAndView(VM_404);
		}
		
		return super.doResolveException(request, response, handler, ex);
	}

}

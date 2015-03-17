package com.qingbo.ginkgo.ygb.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.ShiroException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Service("excetionResolver")
public class ExceptionResolver extends DefaultHandlerExceptionResolver {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		boolean admin = request.getRequestURI().contains("/admin/");
		String VM_404 = (admin?"admin/":"")+"404";
		
		if(ex instanceof ShiroException) {
//			return new ModelAndView(VM_404);
			return new ModelAndView("redirect:"+(admin ? "/admin/login.html" : "/login.html"));
		}
		
//		return super.doResolveException(request, response, handler, ex);
		ModelAndView mav = new ModelAndView(admin ? VM_404 : "redirect:/404.html");
		mav.addObject("error", ex.getMessage());
		return mav;
	}

}

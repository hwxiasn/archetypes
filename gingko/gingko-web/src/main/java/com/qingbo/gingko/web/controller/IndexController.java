package com.qingbo.gingko.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.gingko.web.service.UserService;

@Controller
public class IndexController {
	@Autowired private UserService userService;
	
	@RequestMapping("index")
	public String index() {
		return "index";
	}
	
	@RequestMapping(value="register", method=RequestMethod.GET)
	public String register() {
		return "register";
	}
	
	@RequestMapping(value="register", method=RequestMethod.POST)
	public String register(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "redirect:/login.html";
	}
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(String userName, String password) {
		boolean[] login = userService.login(userName, password);
		if(login[0]) {
			if(login[1]) {
				return "redirect:/user/home.html";
			}else {
				return "redirect:/admin/home.html";
			}
		}
		else return "login";
	}
	
	@RequestMapping("logout")
	public String logout() {
		userService.logout();
		return "index";
	}
}

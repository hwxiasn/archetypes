package com.qingbo.gingko.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.gingko.entity.User;
import com.qingbo.gingko.integration.shiro.common.RequiresFrontUser;
import com.qingbo.gingko.integration.shiro.common.ShiroTool;
import com.qingbo.gingko.web.service.UserService;

@Controller
@RequiresFrontUser
@RequestMapping("user")
public class UserController {
	@Autowired private UserService userService;
	private String VM_Path = "user/";
	
	@RequestMapping("home")
	public String home(Model model) {
		User user = userService.getUser(ShiroTool.userName());
		model.addAttribute("user", user);
		return VM_Path + "home";
	}
}

package com.qingbo.project.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class CallbackController {

	@RequestMapping("json")
	public Object json(String msg) {
		return "callback/"+msg;
	}
	@RequestMapping("qdd/json")
	public Object qddJson(String msg) {
		return "callback/qdd/"+msg;
	}
	@RequestMapping("qdd/return/json")
	public Object qddReturnJson(String msg) {
		return "callback/qdd/return/"+msg;
	}
	@RequestMapping("qdd/call/json")
	public Object qddCallJson(String msg) {
		return "callback/qdd/call/"+msg;
	}
	@RequestMapping("qdd/return/call/json")
	public Object qddReturnCallJson(String msg) {
		return "callback/qdd/return/call/"+msg;
	}
}

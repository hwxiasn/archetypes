package com.qingbo.gingko.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class CallbackController {

	@RequestMapping("json")
	public Object json(String msg) {
		return "callback/"+msg;
	}
}

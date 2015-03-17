package com.qingbo.ginkgo.ygb.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailUtil {
	private static String host = "smtp.exmail.qq.com";
	private static String username = "admin@qingber.com";
//	private static String usernameBY = "service@caifuxiang.com";
//	private static String usernameGH = "service@ghcaiyuan.com";
	private static String password = "qb123456";
//	private static String passwordGH = "ghcaiyuan123";
	
	private static String hostBYN = "smtp.sina.net";
	private static String usernameBYN = "beiying@caifuxiang.com";
	private static String passwordBYN = "beiying888";
	
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	
	private static JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//	private static JavaMailSenderImpl mailSenderBY = new JavaMailSenderImpl();
//	private static JavaMailSenderImpl mailSenderGH = new JavaMailSenderImpl();
	private static JavaMailSenderImpl mailSenderBYN = new JavaMailSenderImpl();
	private static BlockingQueue<Map<String, String>> pendingEmails = new LinkedBlockingQueue<>();
	
	static {
		initialize(mailSender, host, username, password);
//		initialize(mailSenderBY, host, usernameBY, password);
//		initialize(mailSenderGH, host, usernameGH, passwordGH);
		initialize(mailSenderBYN, hostBYN, usernameBYN, passwordBYN);
		TaskUtil.submitKeepRunning(new Runnable() {
			public void run() {
				try {
					while(true) {
						Map<String, String> pendingEmail = pendingEmails.take();
						
						JavaMailSenderImpl sender = mailSender;
						if(GinkgoConfig.platform.equalsIgnoreCase("by")) sender = mailSenderBYN;
//						else if(GinkgoConfig.platform.equalsIgnoreCase("gh")) sender = mailSenderGH;
						
						String toEmail = pendingEmail.get("toEmail");
						String title = pendingEmail.get("title");
						String text = pendingEmail.get("text");
						
						try{
							MimeMessage mail = sender.createMimeMessage();
							MimeMessageHelper helper = new MimeMessageHelper(mail, "utf-8");
							helper.setFrom(sender.getUsername());
							helper.setTo(toEmail);
							helper.setSubject(title);
							helper.setText(text, true);
							logger.info("send email to " + toEmail);
//							sender.send(mail);
							logger.info("success to send email to " + toEmail);
						}catch(MailException | MessagingException e) {
							logger.warn("fail to send email to " + toEmail, e);
						}
						
						Thread.sleep(TimeUnit.SECONDS.toMillis(NumberUtil.parseLong(GinkgoConfig.getProperty("mail.thread.sleep.seconds"),10L)));
					}
				} catch (InterruptedException e) {
					logger.warn("pendingEmails blocking queue exception: ", e);
				}
			}
		});
	}
	private static void initialize(JavaMailSenderImpl mailSender, String host, String username, String password) {
		mailSender.setHost(host);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		Properties mailProperties = mailSender.getJavaMailProperties();
		mailProperties.setProperty("mail.smtp.auth", "true");
	}
	
	public static String sendSmsMessage(String mobile, String message) {
		String result = null;
		
		return result;
	}
	
	public static boolean sendHtmlEmail(String toEmail, String text, String title) {
		if(filterEmailAddress(toEmail) == false) {
			logger.warn("filter failed to send email to "+toEmail);
			return false;
		}
		
		Map<String, String> pendingEmail = new HashMap<>();
		pendingEmail.put("toEmail", toEmail);
		pendingEmail.put("title", title);
		pendingEmail.put("text", text);
		
		pendingEmails.offer(pendingEmail);
		
		return true;
	}
	
	/**
	 * 过滤邮箱域名，避免用户乱填邮箱从而发送失败导致退信等
	 */
	public static boolean filterEmailAddress(String toEmail) {
		String toEmailDomain = toEmail.substring(toEmail.indexOf('@')+1);
		String availableEmailDomains = GinkgoConfig.getProperty("available_email_domains","qq.com,163.com,sina.com");
		for(String domain:availableEmailDomains.split(",")) {
			if(domain.equalsIgnoreCase(toEmailDomain))
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws InterruptedException {
		sendHtmlEmail("xhongwei@qingber.com", "能发送邮件吗？", "can i send u an email?");
		Thread.sleep(3000);
		System.err.println("finish");
	}
}

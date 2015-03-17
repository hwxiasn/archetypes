package com.qingbo.ginkgo.ygb.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;

@RestController
@RequestMapping(value="", method=RequestMethod.POST)
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	private static SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	//上传相关http://host:port/upload/temp
//	private static String upload = "upload";
//	private static String upload_temp = "upload_temp";
//	private static String upload_confirm = "upload_confirm";
//	private static String upload_trash = "upload_trash";
	
	//存储相关/uploads/temp
	private static String uploads_file = "uploads_file";
	private static String uploads_temp_file = "uploads_temp_file";
	private static String uploads_trash_file = "uploads_trash_file";
	
	//访问相关http://host:port/uploads/temp
	private static String uploads = "uploads";
	private static String uploads_temp = "uploads_temp";
	
	/**
	 * 上传文件至/uploads
	 */
	@RequestMapping("")
	public Object upload(@RequestParam(defaultValue="image") String type, MultipartFile file) {
		String path = path(type, file.getOriginalFilename());
		File target = new File(GinkgoConfig.getProperty(uploads_file), path);
		boolean save = save(target, file);
		if(save) {
			logger.info("upload "+file.getOriginalFilename()+" ("+target.length()/1024+"K) to: " + path);
			return string(GinkgoConfig.getProperty(uploads)+","+path);
		}
		return null;
	}
	
	/**
	 * 临时上传文件至/uploads/temp
	 */
	@RequestMapping("temp")
	public Object temp(@RequestParam(defaultValue="image") String type, MultipartFile file) {
		String path = path(type, file.getOriginalFilename());
		File target = new File(GinkgoConfig.getProperty(uploads_temp_file), path);
		boolean save = save(target, file);
		if(save) {
			logger.info("temp upload "+file.getOriginalFilename()+" ("+target.length()/1024+"K) to: " + path);
			return string(GinkgoConfig.getProperty(uploads_temp)+","+path);
		}
		return null;
	}
	
	/**
	 * 确认文件后从/uploads/temp移动至/uploads
	 */
	@RequestMapping("confirm")
	public Object confirm(String path) {
		boolean move = move(GinkgoConfig.getProperty(uploads_temp_file), GinkgoConfig.getProperty(uploads_file), path);
		return move ? "1" : "0";
	}
	
	/**
	 * 过时文件从/uploads移动至/uploads/trash
	 */
	@RequestMapping("trash")
	public Object trash(String path) {
		boolean move = move(GinkgoConfig.getProperty(uploads_file), GinkgoConfig.getProperty(uploads_trash_file), path);
		return move ? "1" : "0";
	}
	
	private String path(String type, String originalFilename) {
		String header = (type!=null&&(type=type.trim()).length()>0) ? type+"/" : "";
		String fileName = (originalFilename!=null&&(originalFilename=originalFilename.trim()).length()>0) 
				? FilenameUtils.getBaseName(originalFilename) + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(originalFilename)
						: fileNameDateFormat.format(new Date());
		return header + fileName;
	}
	
	private String string(String string) {
		return StringUtils.newStringIso8859_1(StringUtils.getBytesUtf8(string));
	}
	
	private boolean move(String from, String to, String path) {
		File source = new File(from, path);
		File target = new File(to, path);
		if(!source.exists()) {
			logger.info("move "+path+" from: "+from+" to: " + to + ", source not exist.");
			return false;
		}
		if(target.getParentFile().exists()==false)
			target.getParentFile().mkdirs();
		try {
			FileUtils.copyFile(source, target);
			source.delete();
			logger.info("move "+path+" from: "+from+" to: " + to);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean save(File target, MultipartFile file) {
		if(target.getParentFile().exists()==false)
			target.getParentFile().mkdirs();
		try {
			FileOutputStream out = FileUtils.openOutputStream(target);
			IOUtils.copy(file.getInputStream(), out);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

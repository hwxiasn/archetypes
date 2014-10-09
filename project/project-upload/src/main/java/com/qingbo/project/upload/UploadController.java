package com.qingbo.project.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

import com.qingbo.project.common.util.PropertiesUtil;

@RestController
@RequestMapping(value="", method=RequestMethod.POST)
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	private static final Properties props = PropertiesUtil.get("upload.properties", "utf-8");
	
	/**
	 * 上传文件至/uploads
	 */
	@RequestMapping("")
	public Object upload(@RequestParam String type, MultipartFile file) {
		String path = path(type, file.getOriginalFilename());
		File target = new File(props.getProperty("uploads"), path);
		boolean save = save(target, file);
		if(save) {
			logger.info("upload "+file.getOriginalFilename()+" ("+target.length()/1024+"K) to: " + path);
			return string(props.getProperty("uploads_domain")+","+path);
		}
		return null;
	}
	
	/**
	 * 上传文件至/uploads/images
	 */
	@RequestMapping("image")
	public Object image(MultipartFile file) {
		return upload("image", file);
	}
	
	/**
	 * 上传文件至/uploads/pdfs
	 */
	@RequestMapping("pdf")
	public Object pdf(MultipartFile file) {
		return upload("pad", file);
	}
	
	/**
	 * 临时上传文件至/uploads/temp
	 */
	@RequestMapping("temp")
	public Object temp(@RequestParam String type, MultipartFile file) {
		String path = path(type, file.getOriginalFilename());
		File target = new File(props.getProperty("uploads_temp"), path);
		boolean save = save(target, file);
		if(save) {
			logger.info("temp upload "+file.getOriginalFilename()+" ("+target.length()/1024+"K) to: " + path);
			return string(props.getProperty("uploads_temp_domain")+","+path);
		}
		return null;
	}
	
	/**
	 * 确认文件后从/uploads/temp移动至/uploads
	 */
	@RequestMapping("confirm")
	public Object confirm(String path) {
		boolean move = move(props.getProperty("uploads_temp"), props.getProperty("uploads"), path);
		return move ? "1" : "0";
	}
	
	/**
	 * 过时文件从/uploads移动至/uploads/trash
	 */
	@RequestMapping("trash")
	public Object trash(String path) {
		boolean move = move(props.getProperty("uploads"), props.getProperty("uploads_trash"), path);
		return move ? "1" : "0";
	}
	
	private String path(String type, String originalFilename) {
		String header = null;
		switch(type) {
		case "image": header = "image/"; break;
		case "pdf": header = "pdf/"; break;
		default: header = ""; break;
		}
		String fileName = FilenameUtils.getBaseName(originalFilename) + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(originalFilename);
		return header + fileName;
	}
	
	private String string(String string) {
		return StringUtils.newStringIso8859_1(StringUtils.getBytesUtf8(string));
	}
	
	private boolean move(String from, String to, String path) {
		File source = new File(from, path);
		File target = new File(to, path);
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
			logger.info("saving file: "+file.getOriginalFilename());
			FileOutputStream out = FileUtils.openOutputStream(target);
			IOUtils.copy(file.getInputStream(), out);
			out.close();
			logger.info("saved file size: "+(target.length()/1024)+"K");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("saving file exception: "+e.getMessage());
		}
		return false;
	}
}

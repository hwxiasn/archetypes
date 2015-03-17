package com.qingbo.ginkgo.ygb.web.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.qingbo.ginkgo.ygb.common.util.RequestUtil;

public class Image {
	//检查相似字符不能连续出现
	private static String[] similarPairsString = {"0O", "1I", "IL", "1L", "2Z", "VY"};
	private static char[][] similarPairs = null;
	private static String badChars = "012iolvyz";
	private static boolean badCharsFilter = true;
	static {
		similarPairs = new char[similarPairsString.length*2][];
		for(int i = 0; i < similarPairsString.length; i++) {
			similarPairs[2*i] = similarPairsString[i].toCharArray();
			similarPairs[2*i + 1] = new StringBuilder(similarPairsString[i]).reverse().toString().toCharArray();
		}
	}
	private static boolean checkNoSimilarPairs(String source) {
		return stepFirstMatch(source.toCharArray(), 0, similarPairs)[0] == -1;
	}
	public static int[] stepFirstMatch(char[] sources, int from, char[][] chars) {
	    int[] indices = { -1, -1 };
	    for (int i = from; i < sources.length; i++) {//从头至尾依次查找
	        for (int j = 0; j < chars.length; j++) {
	            if (sources[i] == chars[j][0]) {
	                int k = 1;
	                while (k < chars[j].length && i + k < sources.length && sources[i + k] == chars[j][k])
	                    k++;
	                if (k == chars[j].length) {
	                    indices[0] = i;
	                    indices[1] = k;
	                    return indices;//找到就立即返回
	                }
	            }
	        }
	    }
	    return indices;
	}
	
	public static Color getRandColor(int fc, int bc) {
		// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	// 生成随机字符
	private static String getRandChar(int length) {
		String code = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
//			String charOrNum = "char";
			String c = null;
			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				c = String.valueOf((char)(choice + random.nextInt(26)));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				c = String.valueOf(random.nextInt(10));
			}
			if(badCharsFilter && badChars.contains(c.toLowerCase())) {
				i--;
			}else {
				code += c;
			}
		}
		return code;
	}
	
	public static  BufferedImage creatImage(HttpSession session) {
		//初始化验证码
		String sRand = "";
		// 在内存中创建图象
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 115; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		// 取随机产生的认证码(4位数字)
		sRand = getRandChar(4);
		while(!badCharsFilter && checkNoSimilarPairs(sRand)==false) {
			System.err.println(sRand);
			sRand = getRandChar(4);
		}
		for (int i = 0; i < 4; i++) {
//			String rand = getRandChar(1);
//			sRand += rand;
			String rand = sRand.substring(i, i+1);
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 13 * i + 6, 16);
		}
		// 图象生效
		g.dispose();
		session.setAttribute("imgCode", sRand);
		return image;
	}
	
	public static boolean checkImgCode(HttpSession session, String imgCode) {
		boolean bool = false;
		String oldImgCode = (String) session.getAttribute("imgCode");
		if (imgCode.equalsIgnoreCase(oldImgCode)) {
			bool = true;
		}
		return bool;
	}
	
	public static boolean checkImgCode(HttpServletRequest request) {
		HttpSession httpSession = request.getSession(true);
		String imgCode = RequestUtil.getStringParam(request, "checkcode", "");
		return checkImgCode(httpSession, imgCode);
	}
}

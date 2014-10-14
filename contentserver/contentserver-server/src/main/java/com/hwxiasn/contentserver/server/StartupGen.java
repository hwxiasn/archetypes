package com.hwxiasn.contentserver.server;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class StartupGen {

	public static void main(String[] args) throws IOException {
		File lib = new File("target/contentserver-server/WEB-INF/lib");
		System.out.println(lib.getAbsolutePath());
		StringBuilder cp = new StringBuilder();
		for(File jar : lib.listFiles()) {
			if(jar.getName().endsWith(".jar")) {
				cp.append(jar.getName());
				cp.append(File.pathSeparator);
			}
		}
		String mainClass = "com.sohu.wap.cms.content.ServerLoader";
		
		String command = "java -cp "+cp.toString()+" "+mainClass;
		System.out.println(command);
		
		String shell = "setsid java -cp "+cp.toString()+" "+mainClass;
		System.out.println(shell);
		
		File bat = new File(lib, "startup.bat");
		FileWriter writer = new FileWriter(bat);
		writer.write(command);
		writer.close();
		
		File sh = new File(lib, "startup.sh");
		writer = new FileWriter(sh);
		writer.write(shell);
		writer.close();
	}

}

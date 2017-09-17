package com.centili.ftp.appcore;
/**
 * 
 */

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.centili.ftp.datatransfer.DataTransfer;

/**
 * @author antic
 *
 */
public class AppCore {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String username = null;
		String password = null;
		String server = null;
		String files = null;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			switch (arg) {
			case "-u":
				i++;
				username = args[i];
				break;
			case "-p":
				i++;
				password = args[i];
				break;
			case "-server":
				i++;
				server = args[i];
				break;
			case "-files":
				i++;
				files = args[i];
				break;
			}
		}
		
		if(username == null)
			username = "user";
		if(password == null)
			password = "pass";
		if(server == null)
			server = "127.0.0.1";
		
		String[] paths = files.split(";");
		try {
			
			ExecutorService executorService = Executors.newFixedThreadPool(paths.length);
			for (String path : paths) {
				FTP protocol = new FTP(username, password, server, files);
				protocol.connect();
				protocol.login();
				DataTransfer trans = new DataTransfer(protocol, path);
				executorService.execute(trans);

			}
			
			executorService.shutdown();
			
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

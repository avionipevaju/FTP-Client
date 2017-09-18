package com.centili.ftp.appcore;
/**
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
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

		if (username == null)
			username = "user";
		if (password == null)
			password = "pass";
		if (server == null)
			server = "127.0.0.1";

		String[] paths = files.split(";");
		ArrayList<FTP> connections = new ArrayList<>();
		try {

			ExecutorService executorService = Executors.newFixedThreadPool(paths.length);
			for (String path : paths) {
				FTP protocol = new FTP(username, password, server, path);
				connections.add(protocol);
				protocol.connect();
				protocol.login();
				DataTransfer trans = new DataTransfer(protocol);
				executorService.execute(trans);
			}

			while (true) {
				try {
					Thread.sleep(100);
					int counter = 0;
					for (FTP ftp : connections) {
						if(ftp.getPercentage() == 100)
							counter++;
						System.out.printf("%s %.2f%% %.2fs %.2fKb/s | ", ftp.getFile().getName(), ftp.getPercentage(), ftp.getElapsedTime()/1000, ftp.getTransferRate());
					}
					System.out.printf("\r");
					if (counter == connections.size()) {
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			executorService.shutdown();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

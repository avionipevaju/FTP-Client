package com.centili.ftp.appcore;
/**
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.centili.ftp.datatransfer.DataTransfer;
import com.centili.ftp.models.FTP;

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
		System.out.println("\nList of files to transfer: ");
		System.out.println("--------------------------------------");
		try {
			int counter = 1;
			ExecutorService executorService = Executors.newFixedThreadPool(paths.length);
			for (String path : paths) {
				FTP protocol = new FTP(username, password, server, path);
				connections.add(protocol);
				protocol.connect();
				protocol.login();
				System.out.println(counter + ". " + protocol.getFile().getName() + " "
						+ protocol.getFile().length() / 1024 + " Kb");
				counter++;
				DataTransfer trans = new DataTransfer(protocol);
				executorService.execute(trans);
			}
			System.out.println("--------------------------------------");
			System.out.println("Live upload stats:");

			while (true) {
				try {
					Thread.sleep(100);
					counter = 0;
					for (FTP ftp : connections) {
						if (ftp.getPercentage() == 100)
							counter++;
						System.out.printf("%s %.2f%% %.2f s %.2f Kb/s | ", ftp.getFile().getName(), ftp.getPercentage(),
								ftp.getElapsedTime() / 1000, ftp.getTransferRate());
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

			double totalTimeOfUpload = 0;
			double averageTransferRate = 0;

			for (FTP ftp : connections) {
				totalTimeOfUpload += ftp.getElapsedTime();
				averageTransferRate += ftp.getFile().length()/1024;
			}

			totalTimeOfUpload /= 1000;
			averageTransferRate = averageTransferRate / totalTimeOfUpload;

			System.out.println("\n--------------------------------------");
			System.out.println("Cumulative upload stats: ");
			System.out.printf("Total time of upload: %.2f s \n", totalTimeOfUpload);
			System.out.printf("Average transfer rate: %.2f Kb/s \n", averageTransferRate);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

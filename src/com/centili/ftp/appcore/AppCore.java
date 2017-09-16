package com.centili.ftp.appcore;
/**
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

		FTP protocol = new FTP(username, password, server, files);
		System.out.println(protocol);
		try {
			protocol.connect();
			protocol.login();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

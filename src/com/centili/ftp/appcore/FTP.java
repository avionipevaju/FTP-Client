/**
 * 
 */
package com.centili.ftp.appcore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Simple File Transfer Protocol client implementation based on RFC 959.
 * 
 * @author Antic Nikola
 * 
 */
public class FTP {

	private String mUsername, mPassword, mServerIP, mFiles;
	private String mRequest, mResponse;
	private Socket mSocket;
	private BufferedReader mReader;
	private BufferedWriter mWriter;

	public FTP(String mUsername, String mPassword, String mServerIP, String mFiles) {
		super();
		this.mUsername = mUsername;
		this.mPassword = mPassword;
		this.mServerIP = mServerIP;
		this.mFiles = mFiles;
	}

	private void sendLine(String request) {
		try {
			mWriter.write(request + "\r\n");
			mWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void check(String errorCode) {
		if (!mResponse.startsWith(errorCode + " ")) {
			System.err.println("Unknown Response " + mResponse);
			System.exit(1);
		}
	}

	public boolean connect() throws IOException {
		mSocket = new Socket(mServerIP, 21);
		mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

		mResponse = mReader.readLine();
		check("220");
		System.out.println(mResponse);
		System.out.println("---CONNECTED---");
		return true;
	}

	public boolean login() {
		try {

			mRequest = "USER " + mUsername;
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("331");

			mRequest = "PASS " + mPassword;
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("230");
			System.out.println(mResponse);

			System.out.println("---LOGED IN---");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean uploadFile(String filePath) {
		try {
			File file = new File(filePath);
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
			mRequest = "PASV";
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("227");
			// System.out.println(mResponse);

			int beg = mResponse.indexOf("(");
			String s = mResponse.substring(beg + 1, mResponse.length() - 2);

			String[] tok = s.split(",");

			String ip = tok[0] + "." + tok[1] + "." + tok[2] + "." + tok[3];
			int port = Integer.parseInt(tok[4]) * 256 + Integer.parseInt(tok[5]);

			mRequest = "STOR " + file.getName();
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("150");

			Socket dataSocket = new Socket(ip, port);

			BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
			byte[] buffer = new byte[4096];
			int bytesRead = 0;
			System.out.println("Transfering file " + file.getName());
			float i = 0;
			int loaded = 0;
			while ((bytesRead = input.read(buffer)) != -1) {
				loaded += bytesRead;
				output.write(buffer, 0, bytesRead);
				i = ((float)loaded/file.length()*100);
				System.out.printf(file.getName() + " %.2f %% \r",i);
				
			}
			System.out.printf(file.getName() + " %.2f %% %n",i);
			output.flush();
			output.close();
			input.close();
			dataSocket.close();

			mResponse = mReader.readLine();
			// System.out.println(mResponse);
			return mResponse.startsWith("226 ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FTP [mUsername=" + mUsername + ", mPassword=" + mPassword + ", mServerIP=" + mServerIP + ", mFiles="
				+ mFiles + "]";
	}

}

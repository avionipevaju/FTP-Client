/**
 * 
 */
package com.centili.ftp.appcore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Simple File Transfer Protocol client implementation based on RFC 959
 * 
 * @author antic
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
	
	private void sendLine(String request){
		try {
			mWriter.write(request + "\r\n");
			mWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void check(String errorCode){
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
			
			System.out.println("---LOGED IN---");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
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

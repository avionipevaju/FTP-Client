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

	private String mUsername, mPassword, mServerIP, mFilePath;
	private String mRequest, mResponse;
	private Socket mSocket;
	private BufferedReader mReader;
	private BufferedWriter mWriter;
	private float mPercentage = 0;
	private File mFile;
	private double mElapsedTime = 0;
	private double mTransferRate = 0;

	public FTP(String mUsername, String mPassword, String mServerIP, String mFilePath) {
		super();
		this.mUsername = mUsername;
		this.mPassword = mPassword;
		this.mServerIP = mServerIP;
		this.mFilePath = mFilePath;
		mFile = new File(mFilePath);
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

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean uploadFile() {
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(mFile));
			mRequest = "PASV";
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("227");

			int beg = mResponse.indexOf("(");
			String s = mResponse.substring(beg + 1, mResponse.length() - 2);

			String[] tok = s.split(",");

			String ip = tok[0] + "." + tok[1] + "." + tok[2] + "." + tok[3];
			int port = Integer.parseInt(tok[4]) * 256 + Integer.parseInt(tok[5]);

			mRequest = "STOR " + mFile.getName();
			sendLine(mRequest);
			mResponse = mReader.readLine();
			check("150");

			Socket dataSocket = new Socket(ip, port);

			BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
			byte[] buffer = new byte[4096];
			int bytesRead = 0;
			int loaded = 0;
			double bytesInOneSecond = 0;
			double oneSecond = 0;
			while ((bytesRead = input.read(buffer)) != -1) {
				double start = System.nanoTime();
				loaded += bytesRead;
				bytesInOneSecond += bytesRead;
				output.write(buffer, 0, bytesRead);
				mPercentage = ((float) loaded / mFile.length() * 100);
				double end = System.nanoTime();
				mElapsedTime += (end - start) / 1000000;
				oneSecond += (end - start) / 1000000;
				if (oneSecond>= 1000) {
					mTransferRate = bytesInOneSecond / 1024;
					bytesInOneSecond = 0;
					oneSecond=0;
				}
				// mTransferRate = ((double) loaded / 1024) / (mElapsedTime /
				// 1000);

			}
			
			mTransferRate = (double)mFile.length()/mElapsedTime;
			
			output.flush();
			output.close();
			input.close();
			dataSocket.close();

			mResponse = mReader.readLine();
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
				+ mFilePath + "]";
	}

	/**
	 * @return the mPercentage
	 */
	public float getPercentage() {
		return mPercentage;
	}

	/**
	 * @return the mFile
	 */
	public File getFile() {
		return mFile;
	}

	/**
	 * @return the mFilePath
	 */
	public String getFilePath() {
		return mFilePath;
	}

	/**
	 * @return the mElapsedTime
	 */
	public double getElapsedTime() {
		return mElapsedTime;
	}

	/**
	 * @return the mTransferRate
	 */
	public double getTransferRate() {
		return mTransferRate;
	}

}

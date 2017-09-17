/**
 * 
 */
package com.centili.ftp.datatransfer;

import com.centili.ftp.appcore.FTP;

/**
 * @author antic
 *
 */
public class DataTransfer implements Runnable {
	
	private FTP mClient;
	private String mFilePath;

	public DataTransfer(FTP client, String filePath) {
		mClient = client;
		mFilePath = filePath;
	}

	@Override
	public void run() {
		System.out.println("STARTED " + mFilePath);
		mClient.uploadFile(mFilePath);
		System.out.println("END " +mFilePath);
		
	}

}

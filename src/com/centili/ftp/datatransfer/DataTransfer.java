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

	public DataTransfer(FTP client) {
		mClient = client;
	}

	@Override
	public void run() {
		mClient.uploadFile();
		
	}

}

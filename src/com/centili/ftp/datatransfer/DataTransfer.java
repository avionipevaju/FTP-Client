/**
 * 
 */
package com.centili.ftp.datatransfer;

import com.centili.ftp.models.FTP;

/**
 * Represents a thread that uploads a file
 * 
 * @author antic
 *
 */
public class DataTransfer implements Runnable {
	
	private FTP mClient;
	
	/**
	 * Instantiates a class for parallel data transfer
	 * 
	 * @param client FTP client that provides the transfer
	 */
	public DataTransfer(FTP client) {
		mClient = client;
	}

	@Override
	public void run() {
		mClient.uploadFile();
		
	}

}

package com.example.remoteclient;

import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;


public class TCPClient {

	
	public static  String SERVERIP; 
	public static  int SERVERPORT ;	
	private boolean bRunning; 
	
	
	Socket mSocket ;
	
	public static final byte DATA  = (byte) 254;	    
	public static final byte COMMAND = (byte) 253;
	public static final byte C_GET_HOST_RESOLUTION  = 10;	
	public static final byte DATA_LENGTH = 10;

	
	private static byte[] outbuff = new byte[DATA_LENGTH] ;
	private static byte[] intbuff = new byte[DATA_LENGTH] ;
	
	
	/**
	 *  Constructor of the class. OnMessagedReceived listens for the messages received from server
	 */
	public TCPClient(String szServerip, int uiServerPort) 
	{
		//Save desired ip and port 
		SERVERIP = szServerip;
		SERVERPORT = uiServerPort;
		bRunning = false;
	}



	public boolean startComm() 
	{
		try
		{			
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			//create a socket to make the connection with the server
			mSocket = new Socket(serverAddr, SERVERPORT);			
			bRunning = true;
			return true;
			
		}
		catch (Exception e)
		{
			Log.e("TCP", "S: Error", e);
			return false;
		}
		finally 
		{
		
		}			
	}
	
	public boolean sendBytes(byte[] aub_BytesToSend)
	{
		if(bRunning)
		{
			try
			{
				OutputStream mOutStream = mSocket.getOutputStream();
				InputStream mInstream = mSocket.getInputStream();
				
				
				mOutStream.write(aub_BytesToSend);	
				mInstream.read(intbuff, 0, DATA_LENGTH);
				Log.i("TCP", Arrays.toString(intbuff));
				return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
						
		}
		else
		{
			return false;
		}
	}

	
	public void waitForResponse()
	{
		
	}

	//Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
	//class at on asynckTask doInBackground
	public interface OnMessageReceived 
	{
		public void messageReceived(String message);
	}
}

package com.example.remoteclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

//import com.example.reoteclient.R;

public class MainActivity extends Activity 
{

	public static final String SERVERIP = "192.168.0.102"; //your computer IP address
	public static final int SERVERPORT = 13000;
	private TCPClient mTcpClient;
	
	 final byte DATA  = (byte) 254;	    
	 final byte COMMAND = (byte) 253;
	 final byte C_GET_HOST_RESOLUTION  = 10;	
	 final byte DATA_LENGTH = 10;
	 
	  byte[] plm = new byte[DATA_LENGTH] ;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
				
		
		//
	}
	
	 /**
     handler pt btnConnect
     */
	public void btnConnectOnClick(View view)
	{
		//Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
		//attempt server connection
		
		
		new connectTask().execute();
	}
	
	 /**
    handler pt btnSend
    */
	public void btnSendOnClick(View view)
	{
		//Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
		//attempt server connection
		plm[0]= COMMAND;
		plm[1]= C_GET_HOST_RESOLUTION;
		new sendTask().execute(plm);
	}
	
	
	/**
	 * Handler pt settings menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())
	    {
	    case R.id.action_settings:
	    	setContentView(R.layout.settings);
	    }
	    return true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		/*Future use
		menu.add("hello").setIcon(R.drawable.ic_launcher);
    	menu.add("patel").setIcon(R.drawable.ic_launcher);
    	menu.add("abc").setIcon(R.drawable.ic_launcher);
    	menu.add("hello").setIcon(R.drawable.ic_launcher);
    	menu.add("").setIcon(R.drawable.ic_launcher);
    	menu.add("").setIcon(R.drawable.ic_launcher);
		 */
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
	       //Things to Do
	    	//if (R.layout.settings ==  getContentResolver()
	       // return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public class connectTask extends AsyncTask<Void,Void,Void> 
	{
		@Override
		protected Void doInBackground(Void... params)
		{		
			mTcpClient = new TCPClient(SERVERIP,SERVERPORT);
			mTcpClient.startComm();
			return null;
		}

		
	}
	
	public class sendTask extends AsyncTask<byte[],String,TCPClient>
	{
		@Override
		protected TCPClient doInBackground(byte[]... aaub_BytesToSend)
		{		
			
			aaub_BytesToSend[0][0]= COMMAND;
			aaub_BytesToSend[0][1]= C_GET_HOST_RESOLUTION;
			mTcpClient.sendBytes(aaub_BytesToSend[0]);
			return null;
		}
		
	}



}









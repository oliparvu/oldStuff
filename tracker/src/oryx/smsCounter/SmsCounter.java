package oryx.smsCounter;

import java.util.HashMap;
import java.util.Map;

import oryx.tracker.R;
//import android.R;
//import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class SmsCounter extends ListActivity  
{
	/** Called when the activity is first created. */

	//TextView mXcoord,mYcoord, tvMessageList; 
	RelativeLayout mLayout;
	Boolean bColorChangingDisabled=false;
	ToggleButton btnColorChanger;
	AlertDialog ad;
	ListView lvStats;

	final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	static int  PHONE_NUMBER = 2;
	Map<String,Integer> dPersons = new HashMap<String,Integer>();
	String text;
	//ArrayList<String> lMessages;
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
	
		//setContentView(0);
		//getListview();
		ArrayList<String> lMessages = new ArrayList<String>();
		 ListView listView = (ListView) findViewById(R.id.listView1);
		 listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, lMessages));
		//this.setTitle("TouchCoord");
		
		//mLayout = (RelativeLayout) findViewById(R.id.top);
		//tvMessageList = (TextView) findViewById(R.id.unreadCount);

		//lvStats=(ListView)findViewById(R.id.lvStats);
		

		//ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, lMessages);
		//lvStats.setAdapter(myarrayAdapter);
		
		populateDictionary(SMS_INBOX);
		
		
		Map<String, Integer> sortedMapAsc = sortByComparator(dPersons,false);
		int counter=0;
		
		for (Map.Entry<String,Integer> entry : sortedMapAsc.entrySet()) //fancy way to use "for each"
		{
			counter++;
			String key = entry.getKey();
			Integer value = entry.getValue();
			//Log.w(key,value.toString());
			try
			{
				if (tryParse(key))
				{
					Uri mUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(key));
					Cursor mCursor = getContentResolver().query(mUri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
					if (mCursor != null)
					{
						mCursor.moveToFirst();
						key = mCursor.getString(mCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					}
					else
					{
						int  a= 3;//breakpoint code
						
					}
				}
			}
			catch (Exception e){}
			text = key + " " + value  ;
			lMessages.add(text);
			//myarrayAdapter.notifyDataSetChanged();
			((ArrayAdapter<Object>) listView.getAdapter()).notifyDataSetChanged();

	        
	        
		}

		
		lMessages.add(String.valueOf(sortedMapAsc.size()) + "Conversations");
		//tvMessageList.setMovementMethod(new ScrollingMovementMethod());
		//tvMessageList.setText(text);
	
		//myarrayAdapter.notifyDataSetChanged();
		((ArrayAdapter<Object>) listView.getAdapter()).notifyDataSetChanged();
	//	setContentView(R.layout.activity_list_item);
		setContentView(R.id.listView1);

	}

	private void populateDictionary(Uri sMS_INBOX2) {
		
		Cursor c = getContentResolver().query(sMS_INBOX2, null, null, null, null); //get all messages from sms inbox

		c.moveToFirst();
		
		for (int i=0 ; i < c.getCount() ; i++)
		{    		
			if (dPersons.containsKey(c.getString(PHONE_NUMBER)))
			{
				int iCurrentVal=dPersons.get(c.getString(PHONE_NUMBER));
				iCurrentVal++;
				dPersons.put(c.getString(PHONE_NUMBER), iCurrentVal);
			}
			else
			{
				dPersons.put(c.getString(PHONE_NUMBER), 1);
			}
			c.moveToNext();
		}
		c.deactivate();
	}
	

	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
	{

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
				{
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2)
			{
				if (order)
				{
					return o1.getValue().compareTo(o2.getValue());
				}
				else
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
				});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
	
	/**Determine if a given string can be parsed as a phone number
	 * @param s String to check
	 * @return True if it`s a valid phone number
	 */
	private static boolean tryParse (String s)
	{
		return s.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public void updateCoords(int x,int y)
	{
		//mXcoord.setText("X= " + String.valueOf(x));
		//mYcoord.setText("Y= " + String.valueOf(y));	

		if (btnColorChanger.isChecked())
		{
			//Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
			// ad.setMessage("pressed");  
			if (y>0 && y<400)
			{
				mLayout.setBackgroundColor(Color.RED);

			}
			else
			{
				mLayout.setBackgroundColor(Color.BLUE);
			}
		}

		// ad.show();  

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		//int x = (int) event.getX();
		//int y = (int) event.getY();
		//updateCoords((int) event.getX(),(int) event.getY());
		return super.onTouchEvent(event);
	}



}
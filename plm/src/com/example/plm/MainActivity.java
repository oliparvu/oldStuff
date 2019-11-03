package com.example.plm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends ListActivity {
	

	final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	static int  PHONE_NUMBER = 2;
	Map<String,Integer> dRawData = new HashMap<String,Integer>();
	//	Map<String,Integer> dAfterPhonebook = new HashMap<String,Integer>();
	Map<String,Integer> dSortedPersons = new HashMap<String,Integer>();

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setContentView(R.layout.activity_main);
		
		setContentView(R.layout.activity_main);

     doStuff();
		

	}

	private void populateDictionary(Uri sMS_INBOX2) {

		Cursor c = getContentResolver().query(sMS_INBOX2, null, null, null, null); //get all messages from sms inbox

		c.moveToFirst();

		for (int i=0 ; i < c.getCount() ; i++)
		{    		
			String szCurrentPerson; 

			szCurrentPerson = c.getString(c.getColumnIndex("address"));//actual field contents is phone number

			try
			{//will throw exception if number is not found in phone book
				if (tryParse(szCurrentPerson))
				{//number is valid, try finding it in phone book

					Uri mUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(szCurrentPerson));
					Cursor mCursor = getContentResolver().query(mUri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);

					if (mCursor != null)
					{
						mCursor.moveToFirst();						
						//we have a name for the current person
						szCurrentPerson = mCursor.getString(mCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));        			       			        			
					}
				}
			}
			catch (Exception e)
			{
				//do nothing
			}

			if (dRawData.containsKey(szCurrentPerson))
			{
				int iCurrentVal=dRawData.get(szCurrentPerson);
				iCurrentVal++;
				dRawData.put(szCurrentPerson, iCurrentVal);
			}
			else
			{
				dRawData.put(szCurrentPerson, 1);
			}
			c.moveToNext();
			//for (int k = 0 ; k<c.getColumnCount();k++)
			//{
			//c.getString(k);
			//}
		}
		c.deactivate();
	}	
	private static Map<String, Integer> sortDictionary(Map<String, Integer> unsortMap, final boolean order)
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
	private static boolean tryParse (String s)
{
	return s.matches("[+-]?\\d*(\\.\\d+)?");
}	
	
	private void onClick(Context c)
	{
		Toast.makeText(c, "clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void doStuff()
	{
		
		
		populateDictionary(SMS_INBOX);
		dSortedPersons = sortDictionary(dRawData, false);
		dRawData = null; 

		//from here http://stackoverflow.com/questions/7916834/android-adding-listview-sub-item-text
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (Map.Entry<String,Integer> entry : dSortedPersons.entrySet())
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("name", entry.getKey());
			datum.put("times", entry.getValue().toString());
			data.add(datum);

		}
		dSortedPersons = null;

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.two_line_list_item ,
				new String[] {"name", "times"},
				new int[] {android.R.id.text1,
				android.R.id.text2});     
		
		
		setListAdapter(adapter);
		
	}
	
	



}

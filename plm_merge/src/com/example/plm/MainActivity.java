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
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;


public class MainActivity extends ListActivity {
	

	final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	static int  PHONE_NUMBER = 2;
	Map<String,Integer> dRawData = new HashMap<String,Integer>();
	Map<String,Integer> dAfterPhonebook = new HashMap<String,Integer>();
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
		
        setContentView(R.layout.activity_main);
    	populateDictionary(SMS_INBOX);
        
    //	ContentResolver contentResolver = getContentResolver();
    //	Cursor cursor = contentResolver.query(
   // 	    Uri.parse("content://sms/inbox"), null, null, null, null);

    //	String[] columnNames = cursor.getColumnNames();
     // cursor.getString(cursor.getColumnIndex("address"));
    	//	  cursor.getColumnIndex("address");
        int counter=0;
     //   cursor.moveToFirst();
    //    cursor.getString(3);
		
        for (Map.Entry<String,Integer> entry : dRawData.entrySet()) //fancy way to use "for each"
        {
        	counter++;			
        	String key = entry.getKey();
        	Integer value = entry.getValue();
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
        				//dAfterPhonebook.put(key, value);
        				Log.w("cursor not null ",key);
        				//mCursor.ge
        			}
        		}
        	}
        		catch (Exception e)
        		{//number not found in phonebook

        			//dAfterPhonebook.put(key, value);
        			Log.w("in exceptie ",key);
        		}

        		if(dAfterPhonebook.containsKey(key)) //needed when contact has multiple numbers and sms were recieved from all numbers (treated like diff conversations inside andro)
        		{	
        			key+="_"+counter;
        		}
        		dAfterPhonebook.put(key, value);
        		//text = key + " " + value  ;
        	}//end for

		dSortedPersons = sortDictionary(dAfterPhonebook, false);
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
                android.R.layout.simple_list_item_2,
                new String[] {"name", "times"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
       
        
        
		//ListAdapter adapter = new SimpleAdapter( this,lMessages,android.R.layout.simple_list_item_2, new String[] { "item1", "items2" },new int[] {android.R.id.text1, android.R.id.text2});  
        //ListAdapter adapter;
        //setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, lMessages));
        // Bind to our new adapter.
        setListAdapter(adapter);
     
        
	}
	
private void populateDictionary(Uri sMS_INBOX2) {
		
		Cursor c = getContentResolver().query(sMS_INBOX2, null, null, null, null); //get all messages from sms inbox

		c.moveToFirst();
		
		for (int i=0 ; i < c.getCount() ; i++)
		{    		
			if (dRawData.containsKey(c.getString(c.getColumnIndex("address"))))
			{
				int iCurrentVal=dRawData.get(c.getString(c.getColumnIndex("address")));
				iCurrentVal++;
				dRawData.put(c.getString(c.getColumnIndex("address")), iCurrentVal);
			}
			else
			{
				dRawData.put(c.getString(c.getColumnIndex("address")), 1);
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
	
	
	
	
	



}

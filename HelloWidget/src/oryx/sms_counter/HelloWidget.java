package oryx.sms_counter;



import java.util.HashMap;
import java.util.Map;
import com.example.hellowidget.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;


public class HelloWidget extends AppWidgetProvider {

	final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	static int  PHONE_NUMBER = 2;
	Map<String,Integer> dPersons = new HashMap<String,Integer>();

	TextView tvMessageList; 
	RemoteViews remoteViews;
	AppWidgetManager appWidgetManager;
	ComponentName thisWidget;
	AlertDialog ad;

	@Override
	public void onEnabled(Context context)
	{
		
		ad = new AlertDialog.Builder(context).create();  
		ad.setCancelable(false); // This blocks the 'BACK' button  
		ad.setButton("OK", new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				dialog.dismiss();                      
			}  
		}); 
		 ad.setMessage("start"); 
		ad.show();
		
		Toast.makeText(context, "plm", Toast.LENGTH_SHORT).show();

		Cursor c = context.getContentResolver().query(SMS_INBOX, null, null, null, null); //get all messages from sms inbox

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


		String text="";
		Map<String, Integer> sortedMapAsc = sortByComparator(dPersons,false);

		for (Map.Entry<String,Integer> entry : sortedMapAsc.entrySet()) //fancy way to use "for each"
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			//Log.w(key,value.toString());

			try
			{
				if (tryParse(key))
				{
					Uri mUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(key));
					Cursor mCursor = context.getContentResolver().query(mUri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
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
			text += key + " " + value + "\n";
		}


		text+=String.valueOf(sortedMapAsc.size()) + "Conversations";
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		views.setTextViewText(R.id.widget_textview, text);



	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		//this.appWidgetManager = appWidgetManager;
		//remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		//thisWidget = new ComponentName(context, HelloWidget.class);
		final int N = appWidgetIds.length;

		ad = new AlertDialog.Builder(context).create();  
		ad.setCancelable(false); // This blocks the 'BACK' button  
		ad.setButton("OK", new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				dialog.dismiss();                      
			}  
		}); 
		 ad.setMessage("update"); 
		ad.show();
		
		Toast.makeText(context, "plm", Toast.LENGTH_SHORT).show();
		Log.v("pmsssss","plmmm");
		Arrays.asList(appWidgetIds);

		for (int j = 0; j < N; j++) {

			int appWidgetId = appWidgetIds[j];

			// Create an Intent to launch ExampleActivity

			Intent intent = new Intent(context, HelloWidget.class);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

			// Get the layout for the App Widget and attach an on-click listener

			// to the button

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);

			views.setOnClickPendingIntent(R.id.widget_textview, pendingIntent);

			
			// To update a label

			
			// Tell the AppWidgetManager to perform an update on the current app

			// widget

			appWidgetManager.updateAppWidget(appWidgetId, views);


			//remoteViews.setTextViewText(R.id.widget_textview,text);
			//appWidgetManager.updateAppWidget(thisWidget, remoteViews);
			//tvMessageList.setMovementMethod(new ScrollingMovementMethod());
			//tvMessageList.setText(text);
		}
	}


	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		
		ad = new AlertDialog.Builder(context).create();  
		ad.setCancelable(false); // This blocks the 'BACK' button  
		ad.setButton("OK", new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				dialog.dismiss();                      
			}  
		}); 
		 ad.setMessage("recieve"); 
		ad.show();
		
		
		Toast.makeText(context, "plm sterg", Toast.LENGTH_SHORT).show();
		
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
		} else {
			super.onReceive(context, intent);
		}
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

}

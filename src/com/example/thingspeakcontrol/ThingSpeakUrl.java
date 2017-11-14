package com.example.thingspeakcontrol;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ThingSpeakUrl {
	static final String thingspeak_ip = "http://api.thingspeak.com";
	static final String thingspeak_chid = "";  // channel id
	static final String thingspeak_api = "";  // api key

	static public String getLast(int num){		
		String url = thingspeak_ip+"/channels/"+thingspeak_chid+"/fields/"+num+"/last?key="+thingspeak_api;		
		return url;
	}
	
	static public String updata(int num1,int num2){		
		String url = thingspeak_ip+"/update?key="+thingspeak_api+"&field1="+num1+"&field2="+num2;
		return url;
	}
	
	static public String feedJson(){
		String url = thingspeak_ip+"/channels/"+thingspeak_chid+"/feed.json?key="+thingspeak_api;	
		return url;
	}
	
	static public ThingSpeakChannelJson parserJson(String json){
		ThingSpeakChannelJson feed = new ThingSpeakChannelJson();
		try {
			JSONObject obj = new JSONObject(json);
			JSONObject channel = obj.getJSONObject("channel");		
			feed.channel_id = channel.getString("id");			
			feed.channel_name = channel.getString("name");
			feed.channel_field1 = channel.getString("field1");
			feed.channel_field2 = channel.getString("field2");
			feed.channel_last_entry_id =channel.getString("last_entry_id");
			Log.d("channel_id", feed.channel_id);
			Log.d("channel_name", feed.channel_name);
			Log.d("channel_field1", feed.channel_field1);
			Log.d("channel_field2", feed.channel_field2);
			Log.d("channel_last_entry_id", feed.channel_last_entry_id);
			JSONArray feeds_array = obj.getJSONArray("feeds");
			feed.feeds = new ArrayList<ThingSpeakFeedsJson>(); 		
			for(int i=0;i<feeds_array.length();i++){
				ThingSpeakFeedsJson feeds_item = new ThingSpeakFeedsJson();
				feeds_item.entry_id = feeds_array.getJSONObject(i).getString("entry_id");
				feeds_item.channel_field1 = feeds_array.getJSONObject(i).getString("field1");
				feeds_item.channel_field2 = feeds_array.getJSONObject(i).getString("field2");
				feed.feeds.add(feeds_item);
				Log.d("Item", i+"");
				Log.d("entry_id", feeds_item.entry_id);
				Log.d("channel_field1", feeds_item.channel_field1);
				Log.d("channel_field2", feeds_item.channel_field2);
			}
		} catch (JSONException e) {
			feed = null;
		}
		return feed;
	}
	
}

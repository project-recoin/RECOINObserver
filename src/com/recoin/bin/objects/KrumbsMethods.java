package com.recoin.bin.objects;

import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class KrumbsMethods {



	
	
	public static JSONObject convertKrumbsObjectToBinObject(JSONObject krumbsObj){
		
		JSONObject binObj = new JSONObject();

		try{
		String id = krumbsObj.getJSONArray("media").getJSONObject(0).getJSONObject("media_source").getString("raw");
		id = id.replace("https://", "");
		
		
		String iden = krumbsObj.getJSONArray("media").getJSONObject(0).getJSONArray("why").getJSONObject(0).getString("intent_name");

		
		
		binObj.put("source", "krumbs");
		binObj.put("id", id);
		binObj.put("timestamp", krumbsObj.getString("start_time"));
		binObj.put("text", krumbsObj.getJSONArray("media").getJSONObject(0).getString("caption"));
		binObj.put("screen_name", krumbsObj.getJSONArray("media").getJSONObject(0).getJSONObject("who").getJSONArray("creator").getJSONObject(0).getString("username"));
		binObj.put("media_type", krumbsObj.getJSONArray("media").getJSONObject(0).getString("media_type"));
		
		JSONArray urls = new JSONArray();
		urls.add(krumbsObj.getJSONArray("media").getJSONObject(0).getJSONObject("media_source").getString("raw"));
		binObj.put("urls", urls);

		
		JSONArray mentions = new JSONArray();
		mentions.add("");
		binObj.put("mentions", mentions);

		
		JSONArray hashtags = new JSONArray();
		hashtags.add(iden);
		binObj.put("hashtags", hashtags);

		
		JSONObject geo = new JSONObject();
		geo.put("lat", krumbsObj.getJSONArray("media").getJSONObject(0).getJSONObject("where").getJSONObject("geo_location").getDouble("latitude"));
		geo.put("lng", krumbsObj.getJSONArray("media").getJSONObject(0).getJSONObject("where").getJSONObject("geo_location").getDouble("longitude"));
		
		binObj.put("geo", geo);

		
		
		binObj.put("status_raw", krumbsObj);
		

		}catch(Exception e){
			e.printStackTrace();
		}
		

		
		
		
		return binObj;
	}


}

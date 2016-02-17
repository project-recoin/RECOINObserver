package com.recoin.bin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.recoin.bin.objects.TwitterBin;

public class BinManager {
	
	
	
	private HashMap<String,TwitterBin> tweetBins;
	private int binThreshold;
	
	public BinManager(int binThreshold) {
		
		tweetBins = new HashMap<String, TwitterBin>();
		this.binThreshold = binThreshold;
	}
	
	
	
	public void processData(JSONObject incommingData){
		
		StringTokenizer tokens = new StringTokenizer(incommingData.getString("text"));
		String word = "";
		while (tokens.hasMoreTokens()) {
	        word = tokens.nextToken();	
			if(word.startsWith("#")){
				addDataToBin(word, incommingData);
			}
//			if(word.startsWith("@")){
//				addDataToBin(word, incommingData);
//			}
	     }

	}
	
	
	private void addDataToBin(String binID, JSONObject data){
		
		if(tweetBins.containsKey(binID)){
			tweetBins.get(binID).getBinItems().add(data);
			//System.out.println("Active Bin Size: "+tweetBins.get(binID).getBinItems().size());
		}else{
			TwitterBin newBin = new TwitterBin();
			newBin.setBinName(binID);
			newBin.setBinStartTimestamp(new Date());
			newBin.getBinItems().add(data);
			tweetBins.put(binID, newBin);
			//System.out.println("new Bin Created for String:"+binID);
			
		}
		
		if(checkBinsforSize()){
			
		}
		
		//System.out.println("Bin Manager Size:"+tweetBins.size());

	}
	
	
	
	
	private boolean checkBinsforSize(){
		
		
		for(Entry<String, TwitterBin> bin : tweetBins.entrySet()){
			
			if(bin.getValue().getBinItems().size()>binThreshold){
				
				System.out.println("threshold met: "+bin.getKey());
				//send the item to 
				
			}
								
			
		}
		
		return true;
		
	}
	
	
	/*
	* {
	 "project_id": pybossa_id
	 "project_name": "String",
	 "project_start_timestamp": yyyy:mm:ddThh:mm:ss,
	 "project_end_timestamp": yyyy:mm:ddThh:mm:ss,
	 "project_started:" boolean,
	 "project_completed:" boolean,
	 "observed": yyyy:mm:ddThh:mm:ss,
	 "identifier": "String",
	 "category": "String",
	 "bin_ids": [],
	}
	 */
	private JSONObject createProjectObject(String binName){
		
		JSONObject proj = new JSONObject();
		proj.put("project_id","");
		proj.put("project_name", binName);
		proj.put("project_start_timestamp", binName);
		proj.put("project_end_timestamp", binName);
		proj.put("project_started", binName);
		proj.put("project_completed", binName);
		proj.put("observed", binName);
		proj.put("identifier", binName);
		proj.put("bin_ids", new JSONArray().add(binName));
		return proj;
	}
	
	
	
	

}

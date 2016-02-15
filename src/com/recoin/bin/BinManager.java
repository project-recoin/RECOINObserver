package com.recoin.bin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

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
				
				System.out.println("threshold met");
				
				
			}
								
			
		}
		
		return true;
		
	}
	
	
	
	
	

}

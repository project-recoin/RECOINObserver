package com.recoin.REST;


import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.recoin.bin.BinManager;
import com.recoin.bin.objects.TwitterBin;

/***
 * 
 * @author Ramine
 *
 *
 * Bindings to access and Configure the BinManager
 */
public class ObserverRESTAPI {
	
	
	
	
	BinManager binManager;
	
	public ObserverRESTAPI(BinManager binManager) {

		this.binManager = binManager;
		
		
		createAllBindings();
	
	}
	

	/**
	 * create all bindings for REST service 
	 * 
	 */
	public void createAllBindings(){
		
		
		
		createGetObserverConfigBinding();

		createGetActiveBinsBindings();
		
		createGetBlackListWords();
		
		createGetBinThresholdBindings();
		
		createPUTbinThreshholdBindings();
        
		createPUTresetBins();

		
		
	}
	
	
	

	private void createPUTresetBins() {

		get("/observer/reset_bins", (req, res) -> {
		    
			binManager.getTweetBinsActive().clear();
			binManager.getTweetBins().clear();
        	return "bins reset";
        });		
		
	}


	private void createPUTbinThreshholdBindings() {
		// TODO Auto-generated method stub
		
	}


	private void createGetBinThresholdBindings() {
		// TODO Auto-generated method stub
		
	}


	private void createGetBlackListWords() {
		get("/observer/blacklist_words", (req, res) -> {
		    
			return binManager.getBlacklistwordsJSON().toString();
        	
        });				
	}


	private void createGetActiveBinsBindings() {

		get("/observer/active_bins", (req, res) -> {
	    
			return convertTweetBinsHashMapToJSON(binManager.getTweetBins()).toString();
        	
        });				
	}




	private void createGetObserverConfigBinding() {
		get("/observer/observer_config", (req, res) -> {
	        
        	return binManager.getObserverConfig().getJSONRepresentation();
        	
        });		
	}


	
	
	

	private JSONObject convertTweetBinsHashMapToJSON(
			HashMap<String, TwitterBin> tweetBins) {

		JSONObject json = new JSONObject();
		for(Entry<String, TwitterBin>  bin : tweetBins.entrySet()){
			
			json.put(bin.getKey(), bin.getValue().getJSONRepresentation());
			
			
		}
		
		return json;
	}
	

}

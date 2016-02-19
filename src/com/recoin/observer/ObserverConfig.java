package com.recoin.observer;

import net.sf.json.JSONObject;

public class ObserverConfig {
	
	
	private int binthreshHoldValue_tweets;
	private int binDropTimeout_hours;
	private int maxBins;
	private boolean includeRetweets;

	
	
	public ObserverConfig() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ObserverConfig(JSONObject keys) {
		
		this.binthreshHoldValue_tweets = keys.getInt("binThresholdValue_tweets");
		this.binDropTimeout_hours = keys.getInt("timeout_hours");
		this.maxBins = keys.getInt("max_bins");
		this.includeRetweets = keys.getBoolean("includeRetweets_bool");
	
	}
	
	
	
	
	
	public void setMaxBins(int maxBins) {
		this.maxBins = maxBins;
	}public void setIncludeRetweets(boolean includeRetweets) {
		this.includeRetweets = includeRetweets;
	}public void setBinthreshHoldValue_tweets(int binthreshHoldValue_tweets) {
		this.binthreshHoldValue_tweets = binthreshHoldValue_tweets;
	}public void setBinDropTimeout_hours(int binDropTimeout_hours) {
		this.binDropTimeout_hours = binDropTimeout_hours;
	}public int getMaxBins() {
		return maxBins;
	}public int getBinthreshHoldValue_tweets() {
		return binthreshHoldValue_tweets;
	}public int getBinDropTimeout_hours() {
		return binDropTimeout_hours;
	}public boolean getIncludeRetweets() {
		return includeRetweets;
	}
	
	
	

}

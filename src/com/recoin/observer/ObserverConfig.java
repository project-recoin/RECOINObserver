package com.recoin.observer;

import net.sf.json.JSONObject;

public class ObserverConfig {
	
	
	private int binthreshHoldValue_tweets;
	private int binDropTimeout_hours;
	private int maxBins;
	private boolean includeRetweets;
	private String observer_mongodb_hostname;
	private String observer_mongodb_username;
	private String observer_mongodb_password;
	private int observer_mongodb_port;


	
	
	public ObserverConfig() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ObserverConfig(JSONObject keys) {
		
		this.binthreshHoldValue_tweets = keys.getInt("binThresholdValue_tweets");
		this.binDropTimeout_hours = keys.getInt("timeout_hours");
		this.maxBins = keys.getInt("max_bins");
		this.includeRetweets = keys.getBoolean("includeRetweets_bool");
		this.observer_mongodb_hostname = keys.getString("observer_mongodb_hostname");
		this.observer_mongodb_username = keys.getString("observer_mongodb_username");
		this.observer_mongodb_password = keys.getString("observer_mongodb_password");
		this.observer_mongodb_port = keys.getInt("observer_mongodb_port");
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
	}public void setObserver_mongodb_username(String observer_mongodb_username) {
		this.observer_mongodb_username = observer_mongodb_username;
	}public void setObserver_mongodb_password(String observer_mongodb_password) {
		this.observer_mongodb_password = observer_mongodb_password;
	}public void setObserver_mongodb_hostname(String observer_mongodb_hostname) {
		this.observer_mongodb_hostname = observer_mongodb_hostname;
	}public String getObserver_mongodb_username() {
		return observer_mongodb_username;
	}public String getObserver_mongodb_password() {
		return observer_mongodb_password;
	}public String getObserver_mongodb_hostname() {
		return observer_mongodb_hostname;
	}public int getObserver_mongodb_port() {
		return observer_mongodb_port;
	}public void setObserver_mongodb_port(int observer_mongodb_port) {
		this.observer_mongodb_port = observer_mongodb_port;
	}
	
	
	

}

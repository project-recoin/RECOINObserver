package com.recoin.bin.objects;

import java.util.ArrayList;
import java.util.Date;

import net.sf.json.JSONObject;

public class TwitterBin {
	
	
	private String ID;
	private String BinName;
	private ArrayList<JSONObject> binItems;
	private Date binStartTimestamp;
	private boolean triggerThreshold;
	
	
	
	public TwitterBin() {

		binItems = new ArrayList<JSONObject>();
	
	}
	
	public void setID(String iD) {
		ID = iD;
	}public void setBinStartTimestamp(Date binStartTimestamp) {
		this.binStartTimestamp = binStartTimestamp;
	}public void setBinName(String binName) {
		BinName = binName;
	}public void setBinItems(ArrayList<JSONObject> binItems) {
		this.binItems = binItems;
	}public String getID() {
		return ID;
	}public Date getBinStartTimestamp() {
		return binStartTimestamp;
	}public ArrayList<JSONObject> getBinItems() {
		return binItems;
	}public String getBinName() {
		return BinName;
	}
	public void setTriggerThresholdMet(boolean triggerThresholdMet) {
		this.triggerThreshold = triggerThresholdMet;
	}
	
	
	

}

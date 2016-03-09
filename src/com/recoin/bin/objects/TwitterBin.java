package com.recoin.bin.objects;

import java.util.ArrayList;
import java.util.Date;

import com.ramine.functions.MiscFunctions;

import net.sf.json.JSONObject;

public class TwitterBin {

	private String ID;
	private String BinName;
	private ArrayList<JSONObject> binItems;
	private Date binStartTimestamp;
	private boolean triggerThreshold;
	private int submittedToDatabaseBinSize;
	private String binIdentfier;
	private boolean newDatatoInsert;
	private JSONObject jsonRepresentation;
	private ArrayList<String> identifiers;


	public TwitterBin() {

		binItems = new ArrayList<JSONObject>();
		submittedToDatabaseBinSize = 0;
		newDatatoInsert = false;
		identifiers = new ArrayList<String>();
	}

	public void setID(String iD) {
		ID = iD;
	}

	public void setBinStartTimestamp(Date binStartTimestamp) {
		this.binStartTimestamp = binStartTimestamp;
	}

	public void setBinName(String binName) {
		BinName = binName;
	}

	public void setBinItems(ArrayList<JSONObject> binItems) {
		this.binItems = binItems;
	}

	public String getID() {
		return ID;
	}

	public Date getBinStartTimestamp() {
		return binStartTimestamp;
	}

	public ArrayList<JSONObject> getBinItems() {
		return binItems;
	}

	public String getBinName() {
		return BinName;
	}

	public void setTriggerThresholdMet(boolean triggerThresholdMet) {
		this.triggerThreshold = triggerThresholdMet;
	}

	public void setTriggerThreshold(boolean triggerThreshold) {
		this.triggerThreshold = triggerThreshold;
	}

	public void setSubmittedToDatabaseBinSize(int submittedToDatabaseBinSize) {
		this.submittedToDatabaseBinSize = submittedToDatabaseBinSize;
	}

	public boolean isTriggerThreshold() {
		return triggerThreshold;
	}

	public int getSubmittedToDatabaseBinSize() {
		return submittedToDatabaseBinSize;
	}

	public void setBinIdentfier(String binIdentfier) {
		this.binIdentfier = binIdentfier;
	}

	public String getBinIdentfier() {
		return binIdentfier;
	}
	
	public void setNewDatatoInsert(boolean newDatatoInsert) {
		this.newDatatoInsert = newDatatoInsert;
	}
	
	public boolean isNewDatatoInsert() {
		return newDatatoInsert;
	}
	
	
	public JSONObject getJSONRepresentation(){
		
		jsonRepresentation = new JSONObject();
		jsonRepresentation.put("bin_name", BinName);
		jsonRepresentation.put("bin_size", binItems.size());
		jsonRepresentation.put("timestamp", MiscFunctions.convertDateTimeToString(binStartTimestamp));
		return jsonRepresentation;
	}
	
	public void setIdentifiers(ArrayList<String> identifiers) {
		this.identifiers = identifiers;
	}public ArrayList<String> getIdentifiers() {
		return identifiers;
	}
	
}

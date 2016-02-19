package com.recoin.bin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.recoin.bin.objects.TwitterBin;
import com.recoin.database.mongo.MongoDBController;
import com.recoin.observer.ObserverConfig;

public class BinManager {

	private HashMap<String, TwitterBin> tweetBins;
	private HashMap<String, Boolean> tweetBinsActive;

	private int binThreshold;
	private int maxBins;
	private boolean includeRetweets;
	private int binDropHours;

	MongoDBController dbControllerProjects;
	MongoDBController dbControllerBins;

	Pattern p = Pattern.compile("[^a-zA-Z0-9]");

	public BinManager(int binThreshold) {

		tweetBins = new HashMap<String, TwitterBin>();
		tweetBinsActive = new HashMap<String, Boolean>();
		this.binThreshold = binThreshold;

		// this needs a config file to make it more configurable
		if (initProjDBConnection()) {
			System.out.println("Connected to RECOIN Projects Database");
		}

		if (initBinsDBConnection()) {
			System.out.println("Connected to Bins Database");
		}
	}

	public BinManager(ObserverConfig config) {
		
		this.binThreshold = config.getBinthreshHoldValue_tweets();
		this.maxBins = config.getMaxBins();
		this.includeRetweets = config.getIncludeRetweets();
		this.binDropHours = config.getBinDropTimeout_hours();
	}

	private boolean initProjDBConnection() {
		try {
			dbControllerProjects = new MongoDBController("recoin.cloudapp.net",
					"observer", "password12345");
			dbControllerProjects.connectToDatabase("RECOIN_projects");
			dbControllerProjects.connectToCollection("project_list");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean initBinsDBConnection() {
		try {
			dbControllerBins = new MongoDBController("recoin.cloudapp.net",
					"observer", "password12345");
			dbControllerBins.connectToDatabase("RECOIN_bins");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void processData(JSONObject incommingData) {

		if(!includeRetweets && incommingData.getBoolean("isRetweet")){
			//avoid it's a retweet
		}else{
//		//first check retweet flag...
//		if((incommingData.getBoolean("isRetweet") && includeRetweets) 
//				|| (!incommingData.getBoolean("isRetweet") && includeRetweets)
//				|| (!incommingData.getBoolean("isRetweet") && !includeRetweets)){
			
			StringTokenizer tokens = new StringTokenizer(incommingData.getString("text"));
			String word = "";
			while (tokens.hasMoreTokens()) {
				try {
					word = tokens.nextToken();
					if (word.startsWith("#")) {
						word = word.replace("#", "").toLowerCase();
	
						boolean hasSpecialChar = p.matcher(word).find();
						if (!hasSpecialChar) {
							// System.out.println("Matches: "+word);
							addDataToBin(word, incommingData, "#");
						}
	
						//
					}
					// if(word.startsWith("@")){
					// addDataToBin(word, incommingData);
					// }
				} catch (Exception e) {
	
				}
			}
		}

	}

	private void addDataToBin(String binID, JSONObject data, String identifier) {

		if (tweetBins.containsKey(binID)) {
			tweetBins.get(binID).getBinItems().add(data);
			tweetBins.get(binID).setNewDatatoInsert(true);
			// System.out.println("Active Bin Size: "+tweetBins.get(binID).getBinItems().size());
		} else {
			TwitterBin newBin = new TwitterBin();
			newBin.setBinName(binID);
			newBin.setBinStartTimestamp(new Date());
			newBin.setBinIdentfier("#");
			newBin.getBinItems().add(data);
			newBin.setNewDatatoInsert(true);
			tweetBins.put(binID, newBin);
			// System.out.println("new Bin Created for String:"+binID);

		}

		if (checkBinsforSize()) {
			// holding loop!
		}

		// System.out.println("Bin Manager Size:"+tweetBins.size());

	}

	private boolean checkBinsforSize() {

		for (Entry<String, TwitterBin> bin : tweetBins.entrySet()) {
			if (bin.getValue().getBinItems().size() > binThreshold) {
				//System.out.println("threshold met: " + bin.getKey());
				// send the item to
				
				//don't cycle through the data if there isn't new documents to insert!
				if(bin.getValue().isNewDatatoInsert()){
					processBinForDataStorage(bin.getValue());
				}
			}

		}

		return true;

	}

	private void processBinForDataStorage(TwitterBin binToProcess) {

		if (tweetBinsActive.containsKey(binToProcess.getBinName())) {
			// This is not a new project, so we dont need to insert a new
			// project Object!
			insertNewbinData(binToProcess);
		} else {
			tweetBinsActive.put(binToProcess.getBinName(), true);
			// create new project entry!
			if (createNewProjectDBEntry(binToProcess.getBinName())) {
				// then insert the bin.
				insertNewbinData(binToProcess);
			}

		}

	}

	private void insertNewbinData(TwitterBin binToProcess) {

		//First set the bin count size.
//		binToProcess.setSubmittedToDatabaseBinSize(binToProcess.getBinItems().size());
		//System.out.printf("Bin: %s, size:%d \n",binToProcess.getBinName(), binToProcess.getSubmittedToDatabaseBinSize());
		// add last index into Bin.
//		dbControllerBins.InsertDataIntoCollection(
//				binToProcess.getBinName(),
//				binToProcess.getBinItems().get(binToProcess.getSubmittedToDatabaseBinSize()-1));

		// //first need to check the index of data that needs to be inserted
		// from
		 for(int i = binToProcess.getSubmittedToDatabaseBinSize(); i<binToProcess.getBinItems().size(); i++){
		
			 
			 System.out.printf("Bin index: %d, Document: %s \n",i,binToProcess.getBinItems().get(i).get("timestamp"));
			 dbControllerBins.InsertDataIntoCollection(binToProcess.getBinName(),binToProcess.getBinItems().get(i));		
		 }
		 binToProcess.setSubmittedToDatabaseBinSize(binToProcess.getBinItems().size());
		 binToProcess.setNewDatatoInsert(false);
		//

	}

	private boolean createNewProjectDBEntry(String binName) {

		// first we need to a check to see if the project already exists in the
		// DB...
		if (!dbControllerProjects.queryCollectionForExistingProjectName(
				"project_list", binName)) {

			try {
				dbControllerProjects.InsertDataIntoCollection("project_list",
						createProjectObject(binName));
				dbControllerBins.connectToCollection(binName);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			//connect to the collection and you're ready to go!
			dbControllerBins.connectToCollection(binName);
			return true;
		}

	}
	
	
	/**
	 * A method to clean up both the cached bins....
	 * relies on config max bin value
	 * @return Boolean if cleaned.
	 */
	private boolean binCleanup(){
		try{
			if(tweetBins.size()> maxBins){
				//need to remove bins which have been active but are not large....
				for(Entry<String, TwitterBin> bin : tweetBins.entrySet()){
					if(bin.getValue().getBinItems().size() <binThreshold){
						//it's below the threshold, so we can delete it, probably...
						tweetBins.remove(bin.getKey());
						tweetBinsActive.remove(bin.getKey());	
					}					
				}
			}
			
			return true;
			
		}catch(Exception e){
			System.out.println("Bin Cleanup: Something went wrong, oops!");
			return false;
		}
		
	}

	/*
	 * { "project_id": pybossa_id "project_name": "String",
	 * "project_start_timestamp": yyyy:mm:ddThh:mm:ss, "project_end_timestamp":
	 * yyyy:mm:ddThh:mm:ss, "project_started:" boolean, "project_completed:"
	 * boolean, "observed": yyyy:mm:ddThh:mm:ss, "identifier": "String",
	 * "category": "String", "bin_ids": [], }
	 */
	private JSONObject createProjectObject(String binName) {

		JSONObject proj = new JSONObject();
		proj.put("project_id", "");
		proj.put("project_name", binName);
		proj.put("project_start_timestamp", "");
		proj.put("project_end_timestamp", "");
		proj.put("project_started", "false");
		proj.put("project_completed", "false");
		proj.put("observed", new Date().toGMTString());
		proj.put("identifier", binName);
		JSONArray binIDs = new JSONArray();
		binIDs.add(binName);
		proj.put("bin_ids", binIDs);
		return proj;
	}

}

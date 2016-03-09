package com.recoin.bin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.recoin.bin.objects.TwitterBin;
import com.recoin.database.mongo.MongoDBController;
import com.recoin.functions.MiscFunctions;
import com.recoin.observer.ObserverConfig;
import com.recoin.observer.api.ObserverRabbitServer;

public class BinManager {

	private HashMap<String, TwitterBin> tweetBins;
	private HashMap<String, Boolean> tweetBinsActive;

	private int binThreshold;
	private int maxBins;
	private boolean includeRetweets;
	private int binDropHours;
	private ObserverRabbitServer rabbitMQServerOutbound;

	private HashMap<String, Boolean> blackListWords;
	
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
		
		tweetBins = new HashMap<String, TwitterBin>();
		tweetBinsActive = new HashMap<String, Boolean>();
		
		this.binThreshold = config.getBinthreshHoldValue_tweets();
		this.maxBins = config.getMaxBins();
		this.includeRetweets = config.getIncludeRetweets();
		this.binDropHours = config.getBinDropTimeout_hours();
		
		
		System.out.printf("Bin Manager Configured, threshold: %d \n"
				+ "maxBins %d \n"
				+ "includeRetweets: %s \n"
				+ "binDrop: %d hours \n",
				binThreshold, maxBins, includeRetweets,binDropHours);

		
		// this needs a config file to make it more configurable
				if (initProjDBConnection()) {
					System.out.println("Connected to RECOIN Projects Database");
				}

				if (initBinsDBConnection()) {
					System.out.println("Connected to Bins Database");
				}
		
	}
	
	private boolean resetDatabaseConnections() {
		try {
			if (initProjDBConnection()) {
				System.out.println("ReConnected to RECOIN Projects Database");
			}

			if (initBinsDBConnection()) {
				System.out.println("ReConnected to Bins Database");
			}
			return true;
		} catch (Exception e) {
			return false;
		}
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
			//System.out.println("It's a retweet!");
		}else{
//		//first check retweet flag...
//		if((incommingData.getBoolean("isRetweet") && includeRetweets) 
//				|| (!incommingData.getBoolean("isRetweet") && includeRetweets)
//				|| (!incommingData.getBoolean("isRetweet") && !includeRetweets)){
			
			
			ArrayList<String> hashtags= new ArrayList<String>();
			HashMap<String, Boolean> hashtags_map = new HashMap<String, Boolean>();
			StringTokenizer tokens = new StringTokenizer(incommingData.getString("text"));
			String word = "";
			boolean toProcess = false;
			while (tokens.hasMoreTokens()) {
				try {
					word = tokens.nextToken();
					
					//check against blacklist
					if(!blackListWords.containsKey(word.replace("#", "").toLowerCase())){
					
						if (word.startsWith("#")) {
							word = word.replace("#", "").toLowerCase();
							boolean hasSpecialChar = p.matcher(word).find();
							if (!hasSpecialChar) {
								toProcess = true;
								hashtags_map.put(word, true);
								//
								//System.out.println("Matches: "+word);
								//addDataToBin(word, incommingData, "#");
							}
		
							//
						}
					}
					// if(word.startsWith("@")){
					// }
				} catch (Exception e) {
	
				}
			}
			if(toProcess){
				
				for(Entry<String, Boolean> wrd: hashtags_map.entrySet()){
					hashtags.add(wrd.getKey());
				}
				
				String iden = "";
				Collections.sort(hashtags, String.CASE_INSENSITIVE_ORDER);
				
				for(String ht : hashtags){
					iden= iden+"_"+ht;
				}
				
				try{
					iden = iden.substring(1, iden.length());
					System.out.println(iden);
					incommingData.put("media_url", "");
					addDataToBin(iden, incommingData, hashtags);
				}catch(Exception e){
					
					
				}
			}
		}

	}

	private void addDataToBin(String binID, JSONObject data, ArrayList<String> hashtags) {

		if (tweetBins.containsKey(binID)) {
			tweetBins.get(binID).getBinItems().add(data);
			tweetBins.get(binID).setNewDatatoInsert(true);
			getRabbitMQServerOutbound().emitDataToChannel("internal_cache", tweetBins.get(binID).getJSONRepresentation());
		    //System.out.println("Active Bin Size: "+tweetBins.get(binID).getBinItems().size());
		} else {
			TwitterBin newBin = new TwitterBin();
			newBin.setBinName(binID);
			newBin.setBinStartTimestamp(new Date());
			newBin.setBinIdentfier("#");
			newBin.getBinItems().add(data);
			newBin.setIdentifiers(hashtags);
			newBin.setNewDatatoInsert(true);
			tweetBins.put(binID, newBin);
			getRabbitMQServerOutbound().emitDataToChannel("internal_cache", newBin.getJSONRepresentation());
			//System.out.println("new Bin Created for String:"+binID);
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
				//RabbitMQ it!
				getRabbitMQServerOutbound().emitDataToChannel("active_bins", bin.getValue().getJSONRepresentation());
				//don't cycle through the data if there isn't new documents to insert!
				if(bin.getValue().isNewDatatoInsert()){
					processBinForDataStorage(bin.getValue());
				}
			}else{
				//report to RabbitMQ 

			}

		}

		return true;

	}

	private void processBinForDataStorage(TwitterBin binToProcess) {

		if (tweetBinsActive.containsKey(binToProcess.getBinName())) {
			// This is not a new project, so we dont need to insert a new
			// project Object!
			getRabbitMQServerOutbound().emitDataToChannel("active_project", createProjectObject(binToProcess.getBinName(), binToProcess.getIdentifiers()));
			insertNewbinData(binToProcess);
		} else {
			// create new project entry!
			if (createNewProjectDBEntry(binToProcess.getBinName(), binToProcess.getIdentifiers())) {
				// then insert the bin.
				insertNewbinData(binToProcess);
			}
			tweetBinsActive.put(binToProcess.getBinName(), true);


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
			 System.out.printf("Bin Name: %s, Document: %s \n",binToProcess.getBinName(),binToProcess.getBinItems().get(i).get("timestamp"));
			 dbControllerBins.InsertDataIntoCollection(binToProcess.getBinName(),binToProcess.getBinItems().get(i));		
		 }
		 binToProcess.setSubmittedToDatabaseBinSize(binToProcess.getBinItems().size());
		 binToProcess.setNewDatatoInsert(false);
		//
		 
		 //need to complete empty bin becasue of heap size issues
		 if(binToProcess.getSubmittedToDatabaseBinSize() > 100000){
			 try{
				 binToProcess.getBinItems().clear();
				 binToProcess.setSubmittedToDatabaseBinSize(binToProcess.getBinItems().size());
			 }catch(Exception e1){
				 
			 }
			 
		 }
		 

	}

	private boolean createNewProjectDBEntry(String binName, ArrayList<String> identifiers) {

		// first we need to a check to see if the project already exists in the
		// DB...
		if (!dbControllerProjects.queryCollectionForExistingProjectName("project_list", binName)) {
			try {
				dbControllerProjects.InsertDataIntoCollection("project_list",createProjectObject(binName,identifiers));
				dbControllerBins.connectToCollection(binName);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				try{
					if(resetDatabaseConnections()){
						//recurseive.
						createNewProjectDBEntry(binName, identifiers);
					}
					
				}catch(Exception e1){
					
				}
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
	private JSONObject createProjectObject(String binName, ArrayList<String> identifiers) {

		JSONObject proj = new JSONObject();
		proj.put("project_id", "");
		proj.put("project_name", binName);
		proj.put("project_start_timestamp", "");
		proj.put("project_end_timestamp", "");
		proj.put("project_status", "empty");
		proj.put("observed", MiscFunctions.convertDateTimeToString(new Date()));
		proj.put("bin_id", binName);
		JSONArray binIDs = new JSONArray();
		for(String hashtag : identifiers){
			binIDs.add(hashtag);
		}
		proj.put("identifiers", binIDs);
		return proj;
	}

	public void setRabbitMQController(ObserverRabbitServer outboundRabbitServer) {
		this.rabbitMQServerOutbound = outboundRabbitServer;	
	}
	
	public ObserverRabbitServer getRabbitMQServerOutbound() {
		return rabbitMQServerOutbound;
	}
	
	
	public void setBlackListWords(JSONObject blacklistConfig) {
		
		blackListWords = new HashMap<String, Boolean>();
		JSONArray words = blacklistConfig.getJSONArray("blacklist_words");
		for(int i=0; i<words.size(); i++){
			blackListWords.put(words.getString(i), true);
		}
		
		
		this.blackListWords = blackListWords;
	}public HashMap<String, Boolean> getBlackListWords() {
		return blackListWords;
	}
	

}

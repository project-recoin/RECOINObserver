package com.recoin.database.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;



public class FileCache {
	
	
	FileWriter writer;
	BufferedWriter bufferedWriter;
	
	FileReader reader;
	BufferedReader bufferedReader;
	
	HashMap<String, Object> internalCache;
	String cacheFileNameAndPath;
	
	
	public FileCache(String cacheFileNameAndPath) {
		
		this.cacheFileNameAndPath = cacheFileNameAndPath;
		
		try{
			File file =new File(cacheFileNameAndPath);
    		
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			System.out.println("Cache file did not exist, creating a new one for you");
    			file.createNewFile();
    		}
    		
		}catch (Exception e) {
			System.out.println("For some reason i couldn't create a cache file for you, do you have write privd?");
		}
		
	
		try{
//			writer = new FileWriter(cacheFileNameAndPath, true);
//			bufferedWriter = new BufferedWriter(writer);
//			
//			reader = new FileReader(cacheFileNameAndPath);
//			bufferedReader = new BufferedReader(reader);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Init cache
		internalCache = new HashMap<String, Object>();
		
		
	}
	
	
	
	
	
	public boolean loadStoredCacheIntoMemory(){
		
		HashMap<String, String> objectstoLoad = loadCacheObjects();
		System.out.printf("Existing Cache found. Adding %d objects into internal cache \n",objectstoLoad.size());
		
		for(Entry<String, String> item : objectstoLoad.entrySet()){
			try{
				internalCache.put(item.getKey(), item.getValue());
				System.out.printf("Adding into Cache ID %s\n",item.getKey());

			}catch(Exception e){
			}
		}
		return true;
		
		
	}
	
	
	public boolean addItemToInMemoryCache(String id){
		
		try{
			
			internalCache.put(id, convertDateTimeToString(new Date()));
			
			//also save to cache
			
			if(saveCacheTofile(id)){
				System.out.println("saved item to cache on Disc.");
			};
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
			
		}
		
	}
	
	
	public boolean isItemInCache(String id){
		
		if(internalCache.containsKey(id)){
			return true;
		}else{
			return false;
		}
		
	}
	
	
	private boolean saveCacheTofile(String id){
		
		try{
			//Hacky, delete total cache and rewrite. Not good practice.
			String toWrite = id + "," + convertDateTimeToString(new Date()); 
			System.out.printf("Saving to Cache on Disk: %s\n",toWrite);

			BufferedWriter bw = new BufferedWriter(new FileWriter(cacheFileNameAndPath,true));
			bw.write(toWrite);
			bw.newLine();
			bw.flush();
			bw.close();
		
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	
	/*
	 * input needs to be JSONArray of objects, with minumum of fields, id:(string) and object: string
	 */
	private HashMap<String, String> loadCacheObjects() {

		try {
			HashMap<String, String> cacheFromFile = new HashMap<String, String>();
			File f = new File(cacheFileNameAndPath);
			if (f.exists()) {
				reader = new FileReader(cacheFileNameAndPath);
				bufferedReader = new BufferedReader(reader);
				String currentLine = "";
				while ((currentLine=bufferedReader.readLine()) !=null){
					try{
						String[] tmp = currentLine.split(",");
						cacheFromFile.put(tmp[0], tmp[1]);
					}catch(Exception e){
						
					}
				}
			
			}
			bufferedReader.close();
			reader.close();
			
			System.out.println("Loaded Cache File");
			return cacheFromFile;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Looks like your cache file is missing, or wrongly formatted. "
					+ "I'd go and check if I was you....");
			return new HashMap<String, String>();
		}
	}
	
	
	public static String convertDateTimeToString(Date date) {
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
				"yyyy-MM-ddHH:mm:ss");
		String date_to_string = dateformatyyyyMMdd.format(date);
		return date_to_string;

	}

}

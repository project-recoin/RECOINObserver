package com.recoin.database.mongo;

import java.util.Arrays;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;

import net.sf.json.JSONObject;

public class MongoDBController {

	private String serverAddress;
	private String username;
	private String password;
	private String databaseName;
	private DB database;
	private MongoClient client;
	private HashMap<String, DBCollection> collections;

	// private String collectionName;

	public MongoDBController(String serverAddress, String username,
			String password) {

		this.serverAddress = serverAddress;
		this.username = username;
		this.password = password;
		// collectionName = "";
		collections = new HashMap<String, DBCollection>();

	}

	public boolean connectToDatabase(String databaseName) {

		try {
			this.databaseName = databaseName;
			MongoCredential credentials = MongoCredential
					.createMongoCRCredential(username, databaseName,
							password.toCharArray());
			// String conn =
			// "mongodb://"+username+":"+password+"@"+serverAddress+"/"+databaseName;
			client = new MongoClient(new ServerAddress(serverAddress),
					Arrays.asList(credentials));
			// System.out.println(client.getDatabaseNames());
			database = client.getDB(databaseName);

			// BasicDBObject dbObject = new BasicDBObject("_id", "123345")
			// .append("created_at", "a time");
			// DBCollection coll = database.getCollection("test");
			// coll.insert(dbObject);
			// client.close();
			System.out.println("Successfully connected to Database: "
					+ database.getName());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err
					.println("Couldn't connect to MongoDB, check your settings!");
			return false;
		}

	}

	public boolean connectToCollection(String collectionName) {

		try {
			DBCollection coll = database.getCollection(collectionName);
			collections.put(collectionName, coll);
			System.out.println("Successfully connected to Collection: "
					+ coll.getName());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err
					.println("Couldn't connect to MongoDB, check your settings!");
			return false;
		}

	}

	public boolean InsertDataIntoCollection(String collectionName, JSONObject document) {

		try {
			Object o = com.mongodb.util.JSON.parse(document.toString());
			DBObject dbObj = (DBObject) o;

			// BasicDBObject dbObject = new BasicDBObject("_id", "123345")
			// .append("created_at", "a time");
			collections.get(collectionName).insert(dbObj);
			// client.close();
//			System.out.println("Successfully Inserted Data into Collection: "
//					+ collectionName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err
					.println("Couldn't connect to MongoDB, check your settings!");
			return false;
		}

	}

	/**
	 * Queries the project_list collection to see if the project name exists
	 * 
	 * @param collectionName
	 *            - The name of the collection, default it will be
	 *            "project_list" in the RECOIN_projects database
	 * @param projectName
	 *            - the string identifer related to the project name
	 * @return Boolean. True if the record already exists!
	 */
	public boolean queryCollectionForExistingProjectName(String collectionName, String projectName) {

		try {
			System.out.println("Querying for record in collection: "+ collectionName);

			JSONObject proj = new JSONObject();
			proj.put("project_name", projectName);	
			Object o = com.mongodb.util.JSON.parse(proj.toString());
			DBObject dbObj = (DBObject) o;
			
			//DBObject iterable = collections.get(collectionName).findOne(com.mongodb.client.model.Filters.eq("project_name","projectName"));
			DBCursor iterable = collections.get(collectionName).find(dbObj);

			if(iterable.hasNext()){
				System.out.println("Successfully found Project in RECOIN_projects: "+ iterable.next().toString());
				return true;

			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't find existing project entry!");
			return false;
		}

	}

	public static void main(String[] args) {

		MongoDBController test_controller = new MongoDBController(
				"recoin.cloudapp.net", "observer", "password12345");
		test_controller.connectToDatabase("RECOIN_bins");
		test_controller.connectToCollection("test");

		JSONObject obj = new JSONObject();
		obj.put("project_name", "testProject");
		test_controller.InsertDataIntoCollection("test", obj);
		
		
		test_controller.queryCollectionForExistingProjectName("test", "testProject");
		
//		int cnt = 0;
//		while (cnt < 1000) {
//			JSONObject obj = new JSONObject();
//			obj.put("project_name", cnt);
//			test_controller.InsertDataIntoCollection("test", obj);
//			cnt++;
//		}
	}
	
	
	public DB getDatabase() {
		return database;
	}

}

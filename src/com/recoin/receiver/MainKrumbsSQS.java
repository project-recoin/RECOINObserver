package com.recoin.receiver;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.sqs.model.Message;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.recoin.amazon.sqs.SQSManager;
import com.recoin.bin.BinManager;
import com.recoin.database.cache.FileCache;
import com.recoin.functions.MiscFunctions;
import com.recoin.observer.ObserverConfig;
import com.recoin.observer.api.ObserverRabbitServer;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class MainKrumbsSQS {

	private static String EXCHANGE_NAME = "krumbs_RECOIN";

	public static void main(String[] argv) throws Exception {

		System.out.println("RECOIN Observer Starting Up.");

		try {
			if (argv[0].length() > 1) {
				EXCHANGE_NAME = argv[0];

			}
		} catch (Exception e) {

		}
		
		ObserverConfig config = null;
		try{
			config = new ObserverConfig(MiscFunctions.loadKeys("config/observer_config.json"));
			System.out.println("Observer Config found/Loaded");
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		JSONObject rabbitConfig = null;
		try{
			rabbitConfig = MiscFunctions.loadKeys("config/rabbitMQ_config.json");
			System.out.println("RabbitMQ server Config found");
		}catch(Exception e){
			System.exit(1);
		}
		
		
		//blacklist_words
		JSONObject blacklistWords = null;
		try{
			blacklistWords = MiscFunctions.loadKeys("config/blacklist_config.json");
			System.out.println("Blacklist server Config found");
		}catch(Exception e){
			System.exit(1);
		}

		
		ObserverRabbitServer outboundRabbitServer =  new ObserverRabbitServer(rabbitConfig);
		
		
//		//inbound RabbitMQ connection
//		ConnectionFactory factory = new ConnectionFactory();
//		// factory.setHost("localhost");
//		factory.setHost("socpub.cloudapp.net");
//		// factory.setPort(34156);
//		factory.setUsername("guest");
//		factory.setPassword("sociam2015");

//		Connection connection = factory.newConnection();
//		Channel channel = connection.createChannel();

//		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//		String queueName = channel.queueDeclare().getQueue();
//		channel.queueBind(queueName, EXCHANGE_NAME, "");
//
//		QueueingConsumer consumer = new QueueingConsumer(channel);
//		channel.basicConsume(queueName, true, consumer);

		
		//Load the cache
		FileCache cache = new FileCache("cache/krumbs.cache");
		
		cache.loadStoredCacheIntoMemory();
		
		

		SQSManager sqsManager = new SQSManager();
		sqsManager.addQueue("io-krumbs-sdk-mediajson_webobs");
		
		
		
		// this kicks everything off!!!
		BinManager binManager = new BinManager(config);
		
		binManager.setRabbitMQController(outboundRabbitServer);
		
		binManager.setBlackListWords(blacklistWords);
		
		
		
		JSONObject dataToProcess;
		while (true) {
			//QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			
			List<Message> messages  = sqsManager.getMessagesFromQueue("io-krumbs-sdk-mediajson_webobs");
			if(messages.size()>0){
				for(Message msg : messages){
					
					dataToProcess = (JSONObject) JSONSerializer.toJSON(msg.getBody());
					
					
					
					//System.out.println(dataToProcess);
					
					String id = dataToProcess.getJSONArray("media").getJSONObject(0).getJSONObject("media_source").getString("raw");
					
					
					//check if data is in cache...
					
					if(cache.isItemInCache(id)){
//						System.out.println("Item is in cache, don't bother processing.");

					}else{
						try{
							binManager.processDataFromKrumbs(dataToProcess);
							cache.addItemToInMemoryCache(id);
						}catch(Exception e){
							e.printStackTrace();
						}

					}
					
					
					//System.out.println(jsonClassEntry.toString(4));
				}
			}else{
				//empty queue, should really wait
				
			}
			

		}
	}
	
	
	
	
}

package com.recoin.receiver;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.recoin.bin.BinManager;
import com.recoin.functions.MiscFunctions;
import com.recoin.observer.ObserverConfig;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Main {

	private static String EXCHANGE_NAME = "twitter_RECOIN";

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
		}catch(Exception e){
			System.exit(1);
		}

		ConnectionFactory factory = new ConnectionFactory();
		// factory.setHost("localhost");
		factory.setHost("socpub.cloudapp.net");
		// factory.setPort(34156);
		factory.setUsername("guest");
		factory.setPassword("sociam2015");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "");

		// System.out.println(" [*] Waiting for messages. To exit press CTRL+C. ExchangeName: "+EXCHANGE_NAME);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);

		// this kicks everything off!!!
		BinManager binManager = new BinManager(config);
		JSONObject dataToProcess;
		while (true) {
			
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			dataToProcess = (JSONObject) JSONSerializer.toJSON(message);
			// System.out.println(jsonClassEntry.toString(4));
			binManager.processData(dataToProcess);
		}
	}
	
	
	
	
}

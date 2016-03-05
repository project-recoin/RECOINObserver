package com.recoin.observer.api;

import java.io.IOException;
import java.util.HashMap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ObserverRabbitServer {

	private Connection connection;
	private HashMap<String, Channel> channel_map;

	public ObserverRabbitServer(JSONObject serverConfig) throws IOException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		// factory.setUsername("guest");
		// factory.setPassword("sociam2015");
		connection = factory.newConnection();

		channel_map = new HashMap<>();
		// create all the channels based on the config
		JSONArray channelList = serverConfig.getJSONArray("channel_names");
		for (int i = 0; i < channelList.size(); i++) {

			channel_map.put(channelList.getString(i), createChannel(channelList.getString(i)));
			System.out.printf("Added new RabbitMQ Broadcast channel: %s", channelList.get(i));
		}

	}

	public Channel createChannel(String channelName) {
		try {
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(channelName, "fanout");
			return channel;

		} catch (Exception e) {
			return null;
		}

	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setChannel_map(HashMap<String, Channel> channel_map) {
		this.channel_map = channel_map;
	}

	public HashMap<String, Channel> getChannel_map() {
		return channel_map;
	}

	public Connection getConnection() {
		return connection;
	}

}

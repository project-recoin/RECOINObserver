package com.recoin.amazon.sqs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSMessageConsumer;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;



public class SQSManager {
	
	private final static String ACCESS_KEY_ID = "AKIAJJRXME5KPLI5ZFEQ"; 
	private final static String SECRET_ACCESS_KEY = "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"; 

    private BasicAWSCredentials awsCreds;
    private AmazonSQS sqs;

	private HashMap<String, ReceiveMessageRequest> queueList;
	
	
	public SQSManager() {

        BasicAWSCredentials awsCreds = null;
        try {     
            awsCreds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
            sqs = new AmazonSQSClient(awsCreds);
            Region usWest2 = Region.getRegion(Regions.US_WEST_2);
            sqs.setRegion(usWest2);
            
            queueList = new HashMap<String, ReceiveMessageRequest>();
        }catch (Exception e) {
  
        }

	
	}
	
	
	public void reconnectSQSConnection(){
		
		BasicAWSCredentials awsCreds = null;
        try {     
            awsCreds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
            sqs = new AmazonSQSClient(awsCreds);
            Region usWest2 = Region.getRegion(Regions.US_WEST_2);
            sqs.setRegion(usWest2);
        }catch (Exception e) {
  
        }

	}	
   public void resetQueueList(){
	   
       queueList = new HashMap<String, ReceiveMessageRequest>();

	   
   }
		
		
		
	
	public void addQueue(String queueName){
		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueName);
        queueList.put(queueName, receiveMessageRequest);
		
	}
	
	
	public List<Message> getMessagesFromQueue(String queueName){
		ArrayList<Message> messages = new ArrayList<Message>();
		try{
			return sqs.receiveMessage(queueList.get(queueName)).getMessages();
		}catch(Exception e){
			return  messages;
		}
		
		
	}
			
			
			
			
	
	
	
	
	public static void main(String[] args) throws Exception {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        BasicAWSCredentials awsCreds = null;
        try {
            //credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
            
            
            awsCreds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY);

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        AmazonSQS sqs = new AmazonSQSClient(awsCreds);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(usWest2);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {
            
//        	        	String queueName = "io-krumbs-sdk-mediajson_webobs";

        	String queueName = "io-krumbs-sdk-mediajson_webobs";
        	
            // List queues
            System.out.println("Listing all queues in your account.\n");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();

           
            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueName);
            
            
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
                System.out.println("  Message");
                System.out.println("    MessageId:     " + message.getMessageId());
                System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                System.out.println("    Body:          " + message.getBody());
                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                    System.out.println("  Attribute");
                    System.out.println("    Name:  " + entry.getKey());
                    System.out.println("    Value: " + entry.getValue());
                }
            }
            System.out.println();

//            // Delete a message
//            System.out.println("Deleting a message.\n");
//            String messageReceiptHandle = messages.get(0).getReceiptHandle();
//            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
          
            	
            
//            SQSMessageConsumer consumer = sqs.createQueue("d").
            

          
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

}

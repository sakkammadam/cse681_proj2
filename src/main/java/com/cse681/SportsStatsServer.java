package com.cse681;

// This class serves as the Server code that takes in a user request (team name) from a RabbitMQ queue, processes it
// by pinging the corresponding SportsSnozzle API and publishes to same response queue where its gets consumed
// by the Client

// Jackson library for JSON processing
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Rabbit MQ library
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;

// Java Utility libraries
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;
import java.lang.Runtime;

public class SportsStatsServer {
    private final static String REQUEST_QUEUE_NAME = "request_queue";

    // method to return a JSON Node enriched with necessary information
    private static JsonNode buildStats(JsonNode teamStats1, JsonNode teamStats2, String matchup){
        // modify
        ObjectNode teamStatObj = (ObjectNode) teamStats1;
        // add match up element -
        teamStatObj.put("matchup", matchup);
        // add opponent score
        teamStatObj.put("opponentScore", teamStats2.get("score").asInt());
        // add matchResult
        String matchResult;
        if (teamStats1.get("score").asInt() > teamStats2.get("score").asInt()){
            matchResult = "Team Win ";
        } else {
            matchResult = "Team Loss";
        }
        // add match result
        teamStatObj.put("matchResult", matchResult);
        // return
        return teamStatObj;
    }

    public static void main(String[] args) throws Exception {
        // Factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Use Factory to create connection
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for requests...");

        // Reference to main method as current thread
        Thread mainThread = Thread.currentThread();

        // Attach a shutdown hook (CTRL + C) to stop the server - implemented as lambda
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            try {
                System.out.println("\n [*] Shutting down gracefully...");
                // Close the channel
                if(channel != null && channel.isOpen()){
                    channel.close();
                }
                // Close the connection
                if(connection != null && connection.isOpen()){
                    connection.close();
                }
                System.out.println(" [✓] RabbitMQ connection closed.");
                // Wake up waiting main thread
                synchronized (SportsStatsServer.class){
                    SportsStatsServer.class.notify();
                }
            } catch (Exception e){
                System.err.println(" [✗] Error while closing RabbitMQ connection: " + e.getMessage());
            }
        }));

        // Create lambda expression for delivery callback when reading a message from the queue
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // retrieve message
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            // Get originating properties
            AMQP.BasicProperties properties = delivery.getProperties();
            // Find associated queue to write back to
            String replyQueue = properties.getReplyTo();
            // Find associated correlationId from originating client
            String correlation_Id = properties.getCorrelationId();
            // Indicate a message that processing is going on...
            System.out.println(" [x] Processing request: " + message);

            // Parse message and get id and name
            String[] parts = message.split(",");
            int teamId = Integer.parseInt(parts[0]);
            String teamName = parts[1];

            // Fetch API Data
            String url = "https://sports.snoozle.net/search/nfl/searchHandler?" +
                    "fileType=inline&statType=teamStats&season=2020&teamName=" + teamId;
            String response = InvokeHttpClient.sendRequest(url);

            // Leverage JSON and parse results
            JsonNode jsonResponse = ParseJson.parse(response);
            JsonNode matchUpStats = jsonResponse.get("matchUpStats");
            List<JsonNode> allTeamStatsArray = new ArrayList<>();

            // Parse response and save it to allTeamStatsArray
            if(matchUpStats.isArray()){
                for(JsonNode stat: matchUpStats){
                    JsonNode homeStats = stat.get("homeStats");
                    JsonNode visStats = stat.get("visStats");
                    // add stats to allTeamStatsArray
                    if (homeStats != null && homeStats.get("teamCode").asInt() == teamId) {
                        allTeamStatsArray.add(buildStats(homeStats, visStats, "home team"));
                    }
                    if (visStats != null && visStats.get("teamCode").asInt() == teamId) {
                        allTeamStatsArray.add(buildStats(visStats, homeStats, "away team"));
                    }
                }
            }

            // Send results back to Queue
            String jsonResponseString = allTeamStatsArray.toString();
            AMQP.BasicProperties replyProperties = new AMQP.BasicProperties.Builder()
                    .correlationId(correlation_Id)
                    .build();
            // publish
            channel.basicPublish("", replyQueue, replyProperties, jsonResponseString.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [✓] Sent response for " + teamName);
        };
        // consume using the lambda deliverCallback
        channel.basicConsume(REQUEST_QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        // keep the server main thread running!
        synchronized (SportsStatsServer.class){
            SportsStatsServer.class.wait();
        }
    }
}


package com.cse681;

// This class serves as the Client code that takes in a user request (team name) and sends it a RabbitMQ queue

// Rabbit MQ library for Message Queue
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;

// Java Utility libraries
import java.lang.String;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.lang.Thread;



public class SportsStatsClient {
    // declare maps that will map team names to their corresponding sports nozzle ids
    private static final Map<String, Integer> teamMap = new TreeMap<>();
    // Queue name
    private static final String REQUEST_QUEUE_NAME = "request_queue";

    private static final Map<Integer, String> teamMapReverse = new HashMap<>();

    // Total width for the line separator
    private static int width = 80;

    // load the teamMap
    static {
        teamMap.put("Arizona Cardinals",1);
        teamMap.put("Atlanta Falcons",2);
        teamMap.put("Baltimore Ravens",3);
        teamMap.put("Buffalo Bills",4);
        teamMap.put("Carolina Panthers",5);
        teamMap.put("Chicago Bears",6);
        teamMap.put("Cleveland Browns",7);
        teamMap.put("Dallas Cowboys",8);
        teamMap.put("Denver Broncos",9);
        teamMap.put("Detroit Lions",10);
        teamMap.put("Green Bay Packers",11);
        teamMap.put("New York Giants",12);
        teamMap.put("Indianapolis Colts",13);
        teamMap.put("Jacksonville Jaguars",14);
        teamMap.put("Kansas City Chiefs",15);
        teamMap.put("Miami Dolphins",16);
        teamMap.put("Minnesota Vikings",17);
        teamMap.put("New England Patriots",18);
        teamMap.put("New Orleans Saints",19);
        teamMap.put("New York Jets",20);
        teamMap.put("Las Vegas Raiders",21);
        teamMap.put("Philadelphia Eagles",22);
        teamMap.put("Pittsburgh Steelers",23);
        teamMap.put("Los Angeles Chargers",24);
        teamMap.put("Seattle Seahawks",25);
        teamMap.put("San Francisco 49ers",26);
        teamMap.put("Los Angeles Rams",27);
        teamMap.put("Tampa Bay Buccaneers",28);
        teamMap.put("Tennessee Titans",29);
        teamMap.put("Washington Commanders",30);
        teamMap.put("Cincinnati Bengals",31);
        teamMap.put("Houston Texans",32);
    }

    // load the teamMapReverse
    static {
        teamMapReverse.put(1,"Arizona Cardinals");
        teamMapReverse.put(2,"Atlanta Falcons");
        teamMapReverse.put(3,"Baltimore Ravens");
        teamMapReverse.put(4,"Buffalo Bills");
        teamMapReverse.put(5,"Carolina Panthers");
        teamMapReverse.put(6,"Chicago Bears");
        teamMapReverse.put(7,"Cleveland Browns");
        teamMapReverse.put(8,"Dallas Cowboys");
        teamMapReverse.put(9,"Denver Broncos");
        teamMapReverse.put(10,"Detroit Lions");
        teamMapReverse.put(11,"Green Bay Packers");
        teamMapReverse.put(12,"New York Giants");
        teamMapReverse.put(13,"Indianapolis Colts");
        teamMapReverse.put(14,"Jacksonville Jaguars");
        teamMapReverse.put(15,"Kansas City Chiefs");
        teamMapReverse.put(16,"Miami Dolphins");
        teamMapReverse.put(17,"Minnesota Vikings");
        teamMapReverse.put(18,"New England Patriots");
        teamMapReverse.put(19,"New Orleans Saints");
        teamMapReverse.put(20,"New York Jets");
        teamMapReverse.put(21,"Las Vegas Raiders");
        teamMapReverse.put(22,"Philadelphia Eagles");
        teamMapReverse.put(23,"Pittsburgh Steelers");
        teamMapReverse.put(24,"Los Angeles Chargers");
        teamMapReverse.put(25,"Seattle Seahawks");
        teamMapReverse.put(26,"San Francisco 49ers");
        teamMapReverse.put(27,"Los Angeles Rams");
        teamMapReverse.put(28,"Tampa Bay Buccaneers");
        teamMapReverse.put(29,"Tennessee Titans");
        teamMapReverse.put(30,"Washington Commanders");
        teamMapReverse.put(31,"Cincinnati Bengals");
        teamMapReverse.put(32,"Houston Texans");
    }

    // Center-align values to print to screen
    private static String centerText(String text, int width) {
        if (text.length() >= width) return text; // If text is longer, return as is
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    // method to parse stats array and print results to screen
    private static void printResults(List<JsonNode> TeamStatsArray){
        ObjectMapper mapper = new ObjectMapper();
        for(JsonNode stat: TeamStatsArray){
            try{
                TeamStats teamStatsRec = mapper.readValue(stat.toString(), TeamStats.class);
                // debug only
                // System.out.println(teamStatsRec.toString());
                // Get team name, ensuring it's not null
                String team = teamMapReverse.getOrDefault(teamStatsRec.getTeamCode(), "Unknown Team");
                int teamCode = teamStatsRec.getTeamCode();
                String matchUp = teamStatsRec.getMatchup();
                int teamScore = teamStatsRec.getScore();
                int opponentScore = teamStatsRec.getOpponentScore();
                String matchResult = teamStatsRec.getMatchResult();

                // Define column width
                int teamWidth = 25;
                int codeWidth = 5;
                int matchUpWidth = 12;
                int scoreWidth = 10;
                int resultWidth = 12;

                // Print separator
                //System.out.println("=".repeat(width));
                // Print formatted row with centered values
                System.out.printf("| %-"+teamWidth+"s | %-"+codeWidth+"d | %-"+matchUpWidth+"s | %-"+scoreWidth+"s | %-"+resultWidth+"s |\n",
                        centerText(team, teamWidth), teamCode, centerText(matchUp, matchUpWidth),
                        centerText(teamScore + "-" + opponentScore, scoreWidth),
                        centerText(matchResult, resultWidth)
                );
                // Print separator
                //System.out.println("=".repeat(width));
            } catch (JsonProcessingException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Create a connection factory for Rabbit MQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Print out the map object to show all NFL teams
        Scanner scanner = new Scanner(System.in);
        System.out.println("NFL teams:");
        for (String team : teamMap.keySet()) {
            System.out.println("- " + team);
        }

        // try with resources block to automatically close connection and channel
        try(
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        ){
            while(true){
                // take user input against the team names
                System.out.print("\nEnter a team name (or type exit to quit): ");
                String teamName = scanner.nextLine().trim();

                // Exit condition
                if (teamName.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting program...");
                    break;
                }

                // Restart the loop if the teamName is not present
                if (!teamMap.containsKey(teamName)) {
                    System.out.println("Invalid team name! Please enter a valid team.");
                    continue;
                }

                // Capture the corresponding id by looking up the team Map
                int teamId = teamMap.get(teamName);
                // Concatenating id and message
                String message = teamId + "," + teamName;

                // Create a temporary queue for response
                String replyQueue = channel.queueDeclare().getQueue();
                // Create a uuid for each publish
                String correlationId = UUID.randomUUID().toString();

                // Leverages Rabbit MQ's AMQP protocol and create a builder for setting message properties
                // We add a correlation Id to track the request-response pair from publisher (client) -> consumer (server) -> publisher (client)
                // We also specify the queue where the message must be sent back to
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .correlationId(correlationId)
                        .replyTo(replyQueue)
                        .build();

                // Send request to publish
                channel.basicPublish("", REQUEST_QUEUE_NAME, props, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [✓] Sent request to queue for " + teamName + ", waiting for response...");

                // Declare an array with a boolean to track message receieved
                final boolean[] responseReceived = {false};
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(correlationId)){
                        String response = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        System.out.println("=".repeat(width));
                        // Deserialize response to List<JsonNode>
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<JsonNode> jsonResponseList = objectMapper.readValue(
                                response,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, JsonNode.class)
                        );
                        printResults(jsonResponseList);
                        System.out.println("=".repeat(width));
                        responseReceived[0] = true;
                    }
                };

                // set channel status
                channel.basicConsume(replyQueue, true, deliverCallback, consumerTag -> {});

                // Invoke a simple thread and sleep for 100 milliseconds
                while (!responseReceived[0]) {
                    Thread.sleep(100); // Wait for the response
                }

            }

        } catch (Exception e) {
            System.err.println(" Ran into issues!: " + e.getMessage());
        }

        // close out the scanner
        scanner.close();
    }
}

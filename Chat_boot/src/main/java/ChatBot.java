import java.io.*;
import java.util.Scanner;

import org.alicebot.ab.*;
import org.alicebot.ab.utils.IOUtils;
/**
 * 
 * @author stefan.tutu
 *
 */
public class ChatBot {
    private static final String WRONG_ANSWER = "I have no answer for that.";
    private static final boolean TRACE_MODE = false;
    private static String botName = "Alice";
    private final static File newTextFile = new File("C:/Users/stefan.tutu/Desktop/Chat_boot/src/main/resources/bots/super/user-data.txt");
    private static String userName = WRONG_ANSWER, userAge = WRONG_ANSWER, userOccupation = WRONG_ANSWER;

    public static void main(String[] args) {
        try {
            String resourcesPath = getResourcesPath();
            MagicBooleans.trace_mode = TRACE_MODE;
            Bot bot = new Bot("super", resourcesPath);
            Chat chatSession = new Chat(bot);
            initChatSession(chatSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
	private static void initChatSession(Chat chatSession) {
        String textLine = "";
        ChatResponse chatResponse = new ChatResponse();
        System.out.print("\nRobot: Hi! My name is " + botName + ".What is your name?\n");
        
        String botResponse = WRONG_ANSWER;
        while (botResponse.equalsIgnoreCase(WRONG_ANSWER)) {
            chatResponse = handleCommunication(chatSession);
            userName = chatResponse.userResponse;
            botResponse = chatResponse.botResponse;
        }
        userName = extractName();

        botResponse = WRONG_ANSWER;
        while (botResponse.equalsIgnoreCase(WRONG_ANSWER)) {
            chatResponse = handleCommunication(chatSession);
            userAge = chatResponse.userResponse;
            botResponse = chatResponse.botResponse;
        }
        userAge = extractAge();
        
        botResponse = WRONG_ANSWER;
        while (botResponse.equalsIgnoreCase(WRONG_ANSWER)) {
            chatResponse = handleCommunication(chatSession);
            userOccupation = chatResponse.userResponse;
            botResponse = chatResponse.botResponse;
        }
        userOccupation = extractOccupation();
        saveUserToDatabase();
        while(true) {
            System.out.print("Human : ");
            textLine = IOUtils.readInputTextLine();
            if (textLine.contains("What is my name")) {
                System.out.println("Robot : Your name is " + userName);    
                continue;
            }
            if (textLine.contains("What is my age")) {
                System.out.println("Robot : Your age is " + userAge);     
                continue;
            }
            if (textLine.contains("What is my occupation")) {
                System.out.println("Robot : Your occupation is " + userOccupation);   
                continue;
            }
            if ((textLine == null) || (textLine.length() < 1))
                textLine = MagicStrings.null_input;
            if (textLine.equalsIgnoreCase("bye")) {
                System.exit(0);
            }  else {
                String request = textLine;
                if (MagicBooleans.trace_mode)
                    System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
                String response = chatSession.multisentenceRespond(request);
                while (response.contains("&lt;"))
                    response = response.replace("&lt;", "<");
                while (response.contains("&gt;"))
                    response = response.replace("&gt;", ">");
           
                if (response.equals(WRONG_ANSWER)) {
                    System.out.println("Sorry, I didn't understand. Could you repeat that please?");
                } else {
                    System.out.println("Robot : " + response);     
            }
        }
    }
    }

    @SuppressWarnings("rawtypes")
	private static ChatResponse handleCommunication(Chat chatSession) {
    	ChatResponse chatResponse = new ChatResponse();
        System.out.print("Human: ");
        String textLine = IOUtils.readInputTextLine();
        if ((textLine == null) || (textLine.length() < 1))
            textLine = MagicStrings.null_input;
        if (textLine.equalsIgnoreCase("bye")) {
            System.exit(0);
        } else {
            String request = textLine;
            if (MagicBooleans.trace_mode)
                System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
            String response = chatSession.multisentenceRespond(request);
            while (response.contains("&lt;"))
                response = response.replace("&lt;", "<");
            while (response.contains("&gt;"))
                response = response.replace("&gt;", ">");
            if (response.equals(WRONG_ANSWER)) {
                System.out.println("Robot: Sorry, I didn't understand. Could you repeat that please?");
                chatResponse.botResponse = WRONG_ANSWER;
            } else {
            	chatResponse.botResponse = "RIGHT_ANSWER";
                System.out.println("Robot: " + response);
            }
        }
        chatResponse.userResponse = textLine;
        return chatResponse;
    }
    
    private static class ChatResponse {
    	String userResponse;
    	String botResponse;
    }

    private static String extractAge() {
        for (int i = 0, j = 0; i < (userAge + " ").length(); i = j + 1) {
            j = (userAge + " ").indexOf(" ", i);
            if (j == -1) {
                break;
            }
            String word = (userAge + " ").substring(i, j);
           if (!word.equalsIgnoreCase("I") && !word.equalsIgnoreCase("AM")) {
                return word;
            }
        }
        return "";
    }

    private static String extractName() {
        for (int i = 0, j = 0; i < (userName + " ").length(); i = j + 1) {
            j = (userName + " ").indexOf(" ", i);
            if (j == -1) {
                break;
            }
            String word = (userName + " ").substring(i, j);
            if (!word.equalsIgnoreCase("MY") && !word.equalsIgnoreCase("NAME") && !word.equalsIgnoreCase("IS")) {
                return word;
            }
        }
        return "";
    }


    private static String extractOccupation() {
        for (int i = 0, j = 0; i < (userOccupation + " ").length(); i = j + 1) {
            j = (userOccupation + " ").indexOf(" ", i);
            if (j == -1) {
                break;
            }
            String word = (userOccupation + " ").substring(i, j);
            if (!word.equalsIgnoreCase("I") && !word.equalsIgnoreCase("AM") && !word.equalsIgnoreCase("A")) {
                return word;
            }
        }
        return "";
    }

    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        return resourcesPath;
    }

    private static void saveUserToDatabase() {
        try {
            if (!userExists(userName, userAge, userOccupation)) {
                java.io.PrintWriter pw = new PrintWriter(new FileWriter(newTextFile, true));
                pw.write("\nNume: " + userName);
                pw.write("\nVarsta: " + userAge);
                pw.write("\nOcupatie: " + userOccupation + "\n");
                pw.close();
            } else {
               System.out.println("Robot : Welcome back, " + userName + "! ");
           }
        } catch (IOException iox) {
            System.out.println("WTF");
            iox.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
	private static boolean userExists(String nume, String varsta, String ocupatie) throws IOException {
        final Scanner scanner = new Scanner(newTextFile);

        while (scanner.hasNextLine()) {
            boolean acelasiNume = false, aceeasiVarsta = false, aceeasiOcupatie = false;
            int index = 1;
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                if (line.contains("Nume")) {
                    if (line.substring(6, line.length()).equals(nume)) {
                        acelasiNume = true;
                    }
                } else if (line.contains("Varsta")) {
                    if (line.substring(8, line.length()).equals(varsta)) {
                        aceeasiVarsta = true;
                    }
                } else if (line.contains("Ocupatie")) {
                    if (line.substring(10, line.length()).equals(ocupatie)) {
                        aceeasiOcupatie = true;
                    }
                }

                if (index % 4 == 0) {
                    if (aceeasiOcupatie && aceeasiVarsta && acelasiNume) {
                        return true;
                    }
                    break;
                }

                index++;
            }
        }
        return false;
    }
}

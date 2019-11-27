import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class UserHandler extends Thread{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private InputStream infile;
    private Date debounce;
    private List<String> history = new LinkedList<String>();

    private String calc(String input){
        String[] splitedinput = input.split(" ");
        if (input.equals("get_logs")){
            String finalLine = "\n";
            for (String element : history){
                finalLine+=element+"\n";
            }
            return finalLine;
        }
        if (splitedinput.length != 3){
            return "Invalid input";
        }
        if (splitedinput[1].equals("add")){
            try{
                float one = Float.parseFloat(splitedinput[0]);
                float two = Float.parseFloat(splitedinput[2]);
                return Float.toString(one+two);
            } catch (Exception t){
                return "Invalid input";
            }
        } else if (splitedinput[1].equals("mult")){
            try{
                float one = Float.parseFloat(splitedinput[0]);
                float two = Float.parseFloat(splitedinput[2]);
                return Float.toString(one*two);
            } catch (Exception t){
                return "Invalid input";
            }

        }else if (splitedinput[1].equals("subs")){
            try{
                float one = Float.parseFloat(splitedinput[0]);
                float two = Float.parseFloat(splitedinput[2]);
                return Float.toString(one-two);
            } catch (Exception t){
                return "Invalid input";
            }

        }else if (splitedinput[1].equals("divide")){
            try{
                float one = Float.parseFloat(splitedinput[0]);
                float two = Float.parseFloat(splitedinput[2]);
                return Float.toString(one/two);
            } catch (Exception t){
                return "Invalid input";
            }

        } else{
            return "Unsupported operation";
        }
    }

//

    public UserHandler (Socket socket) throws IOException{
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        debounce = new Date();
        start();
    }
    @Override
    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null && (new Date().getTime() - debounce.getTime()) < 60000) {
                if (line.equals("file")){
                    out.println("Enter File name: ->");
                    String filename = in.readLine();
                    byte[] bytes = Files.readAllBytes(Paths.get(filename));
                    String[] fullpath = filename.split("/");
                    System.out.println(fullpath);
                    String userFileName = fullpath[fullpath.length-1];
                    String homedir = "/home/denis/Documents/учеба/java/lab3/src/main/resources/";
                    File homedirFile = new File(homedir);
                    File[] listOfFiles = homedirFile.listFiles();
                    for (File innerFilename:listOfFiles){
                        if (homedir.concat(userFileName).equals(innerFilename.toString())){
                            userFileName = new Date().toString().concat(userFileName);
                        }
                    }
                    try (FileOutputStream fos = new FileOutputStream(homedir.concat(userFileName))) {
                        fos.write(bytes);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    out.println("File uploaded\n");

                }
                String output = calc(line);
                out.println("Your result: " + output);
                if (!line.equals("get_logs")) {
                    history.add(line + " = " + output);
                }
                debounce = new Date();
                }
            out.println("There's no request during long period of time. Good Bye!");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class lab3 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket server = new ServerSocket(8888);
        try {
                System.out.println("Server has started on 127.0.0.1:8888.\r\nWaiting for a connection...");
            while(true) {
                try {
                    System.out.println("Waiting for the client request");
                    Socket socket = server.accept();
                    try {
                        new UserHandler(socket);
                    } catch (IOException e){
                        socket.close();
                    }

                } catch (IOException e){
                    server.close();
                    System.out.println("Error on accept socket!");
                    break;
                }
            }

        } catch (Exception t){
            System.out.println("Exception : " + t);
        }


    }
}
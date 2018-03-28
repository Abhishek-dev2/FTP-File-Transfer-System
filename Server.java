import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        String serverAddress = "172.16.185.123";
        int portForCommands = 1234, portForFiles = 2345;
        ServerSocket socketForCommands = new ServerSocket(portForCommands, 0, InetAddress.getByName(serverAddress));
        ServerSocket socketForFiles = new ServerSocket(portForFiles, 0, InetAddress.getByName(serverAddress));
        System.out.println("Started both the servers, for command and file transfer.");
        while (true) {
            Socket commandClientSocket = socketForCommands.accept();
            Thread t = new ClientThread(commandClientSocket, socketForFiles);
            t.start();
        }
    }
}
class ClientThread extends Thread {
    private Socket commandClientSocket;
    ServerSocket socketForFiles;
    public ClientThread(Socket commandClientSocket, ServerSocket socketForFiles) {
        this.commandClientSocket = commandClientSocket;
        this.socketForFiles = socketForFiles;
    }
    public void run() {
        try {
            BufferedReader commandReader = new BufferedReader(new InputStreamReader(commandClientSocket.getInputStream()));
            OutputStream os = commandClientSocket.getOutputStream();
            String cmd = commandReader.readLine();
            switch (cmd) {
                case "1": // send all file names
                    File[] listOfFiles = (new File("./files")).listFiles();
                    String fileNames = "";
                    for (File i : listOfFiles)
                        fileNames += i.getName() + "*#|*#";
                    os.write(fileNames.getBytes());
                    os.flush();
                    break;
                case "2": // Send file to client
                    String fileNameToSend = commandReader.readLine();
                    File file = new File("./files/" + fileNameToSend);
                    if (file.exists() && file.isFile()) {
                        Socket fileSendingSocket = socketForFiles.accept();
                        sendFile(file, fileSendingSocket);
                    } else {
                        System.out.println("XXX File does not exist XXX");
                        os.write("*#*#*#*Error*#*#*#*\n".getBytes());
                        os.flush();
                    }
                    break;
                case "3": // Receive file from the client
                    String fileNameToReceive = commandReader.readLine();
                    File fileToReceive = new File("./files/" + fileNameToReceive);
                    Socket fileReceivingSocket = socketForFiles.accept();
                    receiveFile(fileToReceive, fileReceivingSocket);
                    break;
                default:
                    System.out.println("XXX Wrong code received XXX");
            }
            os.close();
            commandReader.close();
            commandClientSocket.close();
        } catch (Exception ex) {
            System.out.println("XXX Some Exception occurred XXX");
            ex.printStackTrace();
        }
    }
    private static void receiveFile(File fileToReceive, Socket fileReceivingSocket) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(fileToReceive));
        fileToReceive.createNewFile();
        String line;
        Writer writer = new FileWriter(fileToReceive);
        while (true) {
            line = fileReader.readLine();
            if(line.equals("*#*#*#*EOF*#*#*#*\n"))
                break;
            writer.write(line + "\n");
        }
        writer.flush();
        writer.close();
        fileReader.close();
        fileReceivingSocket.close();
    }
    
    private static void sendFile(File file, Socket fileSendingSocket) throws Exception {
        OutputStream os = fileSendingSocket.getOutputStream();
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null)
            os.write((line + "\n").getBytes());
        os.write("*#*#*#*EOF*#*#*#*\n".getBytes());
        os.flush();
        fileReader.close();
        os.close();
        fileSendingSocket.close();
    }
}
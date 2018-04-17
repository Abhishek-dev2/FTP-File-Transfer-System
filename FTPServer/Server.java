import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        String serverAddress = "172.16.150.122";
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
            BufferedReader commandReader = new BufferedReader(
                    new InputStreamReader(commandClientSocket.getInputStream()));
            OutputStream os = commandClientSocket.getOutputStream();
            String cmd = commandReader.readLine();
            switch (cmd) {
            case "1": // send all file names
                File[] listOfFiles = (new File("./files")).listFiles();
                String fileNames = "";
                for (File i : listOfFiles)
                    fileNames += i.getName() + "#%splitter%#";
                fileNames += "\n";
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
        FileOutputStream fos = new FileOutputStream(fileToReceive);
        int length;
        byte[] recData = new byte[2048];
        while((length = fileReceivingSocket.getInputStream().read(recData)) != -1)
            fos.write(recData, 0, length);
        fileReceivingSocket.close();
    }

    private static void sendFile(File file, Socket fileSendingSocket) throws Exception {
        OutputStream os = fileSendingSocket.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        byte[] sendData = new byte[2048];
        int length;
        while ((length = fis.read(sendData)) != -1)
            os.write(sendData, 0, length);
        // os.write("*#*#*#*EOF*#*#*#*\n".getBytes());
        fis.close();
        os.flush();
        // fileReader.close();
        os.close();
        fileSendingSocket.close();
    }
}

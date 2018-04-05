import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        try {
            Socket getMyFileNamesServer = new Socket(InetAddress.getByName("172.16.185.123"), 1234);
            OutputStream os = getMyFileNamesServer.getOutputStream();
            os.write("2\n".getBytes());
            os.write(("one.txt" + "\n").getBytes());
            os.close();
            getMyFileNamesServer.close();
            Socket getMyFileServer = new Socket(InetAddress.getByName("172.16.185.123"), 2345);
            BufferedReader br = new BufferedReader(new InputStreamReader(getMyFileServer.getInputStream()));
            Writer writer = new FileWriter("one.txt");
            String line;
            while (true) {
                line = br.readLine();
                if (line.equals("*#*#*#*EOF*#*#*#*"))
                    break;
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
            br.close();
            getMyFileServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

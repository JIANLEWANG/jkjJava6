import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ServerApplication {


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8088);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                serve(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void serve(Socket socket) {
        System.out.println("accept");
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            String body = "wuhu,qifei";
            printWriter.println("Content-Length:" + body.getBytes().length);
            printWriter.println();
            printWriter.write(body);
            printWriter.close();
            System.out.println("close socket time:" + System.currentTimeMillis());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

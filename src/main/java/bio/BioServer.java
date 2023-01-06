package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    //定义服务端端口号
    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {

        //建立Socket连接
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
            System.out.println("the time server is start in port:" + PORT);
            Socket socket;
            //等待客户端连接
            while (true) {
                socket = server.accept();

                //传入Hander
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (server != null) {
                System.out.println("the time server close");
                server.close();
            }
        }


    }
}

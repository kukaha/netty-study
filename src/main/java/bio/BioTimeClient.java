package bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BioTimeClient {

    //定义服务端口号
    private static final int PORT = 8888;

    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) {
        Socket socket = null;

        //获取服务端返回的值
        BufferedReader in = null;
        //发送数据给服务端
        PrintWriter out = null;

        try {

            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("向服务端发送的消息：我是客户端");
            String resp = in.readLine();
            System.out.println("接收服务端返回的消息：当前服务器时间是：" + resp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

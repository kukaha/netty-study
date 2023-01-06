package bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable {

    //接收socket
    private final Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * 处理逻辑
     */
    @Override
    public void run() {
        //输入
        BufferedReader in = null;
        //输出
        PrintWriter out = null;

        try {
            //拿取输入流
            //包装设计模式 提高性能
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            //拿取输出流 自动刷新
            out = new PrintWriter(this.socket.getOutputStream(), true);

            //循环读取内容
            String body;
            while ((body = in.readLine()) != null && body.length() != 0) {
                System.out.println("the time server receive msg:" + body);
                //输出
                out.println(new Date());
                //out.flush(); 上面写入了true 自动刷新
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
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
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

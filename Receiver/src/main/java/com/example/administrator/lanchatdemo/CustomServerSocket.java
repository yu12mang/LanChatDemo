package com.example.administrator.lanchatdemo;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class CustomServerSocket extends Thread {
    private static final String TAG = "logan";
    private static int BROADCAST_PORT = 9898;
    private static final String BROADCAST_IP = "224.0.0.0";
    private MulticastSocket multicastSocket;
    String content = "";
    private InetAddress inetAddress;

    public CustomServerSocket()  {
        // 多播配置
        try {
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            multicastSocket.joinGroup(inetAddress);
            Log.i(TAG, "连接成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "连接失败:"+e.getMessage());
        }

        // handler
    }

    @Override
    public void run() {
        while (true) {
            byte buf[] = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length, inetAddress, BROADCAST_PORT);
            try {
                multicastSocket.receive(dp);
                String content = new String(buf, 0, dp.getLength());
                Log.i(TAG, "run: 接受数据:"+content);
//                Message msg = new Message();
//                msg.what = TuringHandler.RECEIVE_COMMOND;
//                msg.obj = content;
//                handler.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i(TAG, "接受失败："+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

    }
}

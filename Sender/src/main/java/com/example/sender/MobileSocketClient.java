package com.example.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

public class MobileSocketClient {
    private static MobileSocketClient mobileSocketClient = null;
    private static String BROADCAST_IP = "224.0.0.0";
    private static int BROADCAST_PORT = 9898;
    private static final String TAG = "logan";
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;

    private MobileSocketClient() {
        try {
            //初始化组播
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            Log.i(TAG, "是否多播地址:" + inetAddress.isMulticastAddress());
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
            multicastSocket.setTimeToLive(10);
            multicastSocket.joinGroup(inetAddress);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static MobileSocketClient getInstance() {
        if (mobileSocketClient == null) {
            mobileSocketClient = new MobileSocketClient();
        }
        return mobileSocketClient;
    }

    //发送数据
    @SuppressLint("StaticFieldLeak")
    public void send(final String content) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... paramVarArgs) {
                byte[] data = paramVarArgs[0].getBytes();
                //构造要发送的数据
                try {
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);

                    multicastSocket.send(dataPacket);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "发送失败";
                }
                return "发送成功";
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Log.i(TAG, "onPostExecute: " + result);
            }
        }.execute(content);

    }

}

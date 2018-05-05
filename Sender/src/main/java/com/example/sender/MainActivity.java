package com.example.sender;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView tvConnectState = null;
    private TextView tvSendState = null;
    private EditText etContent = null;

    private final int CONNECT_SUC = 1;
    private final int CONNECT_FAIL = 2;
    private final int SEND_SUC = 3;
    private final int SEND_FAIL = 4;


    private OutputStream outputStream = null;
    private Socket socket = null;
    private String ip = "192.168.3.204";
    private boolean socketStatus = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_SUC: {
                    tvConnectState.setText("连接成功");
                }
                break;
                case CONNECT_FAIL: {
                    tvConnectState.setText("连接失败");
                }
                break;
                case SEND_SUC: {
                    tvSendState.setText("发送成功");
                }
                break;
                case SEND_FAIL: {
                    tvSendState.setText("发送失败");
                }
                break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


    }

    private void initView() {
        tvConnectState = findViewById(R.id.tv_connect_state);
        tvSendState = findViewById(R.id.tv_send_state);
        etContent = findViewById(R.id.et_content);
    }

    public void connect(View view) {

        if (ip == null) {
            Toast.makeText(MainActivity.this, "please input Server IP", Toast.LENGTH_SHORT).show();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (!socketStatus) {

                    try {
                        socket = new Socket(ip, 8000);
                        if (socket == null) {
                        } else {
                            socketStatus = true;
                        }
                        outputStream = socket.getOutputStream();
                        handler.sendEmptyMessage(CONNECT_SUC);
                    } catch (IOException e) {
                        Log.i("logan", "连接失败" + e.getMessage());
                        handler.sendEmptyMessage(CONNECT_FAIL);
                        e.printStackTrace();
                    }

                }

            }
        };
        thread.start();

    }


    public void send(View view) {

        final String msg = etContent.getText().toString()+"\0";

        if (msg.length() <= 0){
            Toast.makeText(MainActivity.this,"发送的数据不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        outputStream.write(msg.getBytes());
                        handler.sendEmptyMessage(SEND_SUC);
                    } catch (IOException e) {
                        handler.sendEmptyMessage(SEND_FAIL);
                        Log.i("logan", "发送失败： "+e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
        };
        thread.start();
    }


    /*当客户端界面返回时，关闭相应的socket资源*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*关闭相应的资源*/
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

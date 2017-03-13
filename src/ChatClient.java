/**
 * Created by Leo on 2017/3/13.
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args){
        ChatFrame cf = new ChatFrame(100,100,300,400,Color.white);

    }
}

class ChatFrame extends Frame{

    private TextField tfTxt = new TextField(38);
    private TextArea taContent = new TextArea(20,38);
    private Socket ss = null;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    Thread t = new Thread(new Server());

    ChatFrame(int x,int y,int a,int b,Color color){
        super("LeoChat-Client");
        setBackground(color);
        setVisible(true);
        setBounds(x,y,a,b);
        setResizable(false);
        add(tfTxt,BorderLayout.SOUTH);
        add(taContent,BorderLayout.NORTH);
        pack();
        this.addWindowListener(new WindowMonitor());
        tfTxt.addActionListener(new TFlistener());
        connect();
        t.start();
    }

    class WindowMonitor extends WindowAdapter {
        public void windowClosing(WindowEvent e){
            disconnect();
            setVisible(false);
            System.exit(0);
        }
    }



    private class  TFlistener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String str = tfTxt.getText().trim();
            tfTxt.setText("");
            if (dos != null) {
                try {
                    dos.writeUTF(str);
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                taContent.setText("无法连接服务器，请稍后重试" + '\n');
            }

        }
    }

    private void connect(){

        try {
            ss = new Socket("127.0.0.1",8888);
            dos = new DataOutputStream(ss.getOutputStream());
            dis = new DataInputStream(ss.getInputStream());
            System.out.println("Connected");


        }
        catch (ConnectException e) {
            //e.printStackTrace();
            taContent.setText("无法连接服务器，请稍后重试"+'\n');
        }
        catch (IOException e) {
            //e.printStackTrace();
            taContent.setText("无法连接服务器，请稍后重试"+'\n');
        }

    }

    private void disconnect(){
        try {
            if(ss != null) ss.close();
            if(dos != null) dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    class Server implements Runnable{
        private String str;

        @Override
        public void run() {
            try {
                while(true){
                    str = dis.readUTF();
                    System.out.println(str);
                    taContent.setText(taContent.getText() + str + '\n');
                }
            } catch (SocketException e) {
                System.out.println("连接超时");
            } catch (EOFException e) {
                System.out.println("推出了，bye - bye!");
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                System.out.println("无法连接服务器");
            }
        }
    }
}


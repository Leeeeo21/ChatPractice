/**
 * Created by Leo on 2017/3/13.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    boolean start = false;
    ServerSocket ss;

    List<Client> clients = new ArrayList<Client>() ;

    public static void main(String[] args) {
        new ChatServer().Start();

    }

    public void Start(){

        try {ss = new ServerSocket(8888);
        }
        catch (IOException e)
        {e.printStackTrace();}

        start = true;
        try{while (start) {
            Socket s = ss.accept();
            Client c = new Client(s);
            clients.add(c);
            System.out.println(clients.size()+"  个人同时在线");
            System.out.println("a new client login");
            Thread t = new Thread(c);
            t.start();
        }
        }
        catch (SocketException e) {
            try {
                if(ss != null) ss.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(-1);
            e.printStackTrace();
            System.out.println("Socket已关闭，请重启系统");
        }
        catch (IOException e) {
            try {
                if(ss != null) ss.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(-1);
            e.printStackTrace();
            System.out.println("请重启系统");
        }

        /*finally {
            try {
                ss.close();
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }



    class Client implements Runnable{
        private Socket s;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;

        public Client(Socket ss){
            this.s = ss;
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Sent(String str){
            try {
                dos.writeUTF(str);
                //System.out.println(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            //System.out.println("Server closed1");
            String str = null;
            try {
                while (this.s.isBound()){
                    str = dis.readUTF();
                    for (int i = 0; i < clients.size();i ++){
                        Client c = clients.get(i);
                        c.Sent(str);
                    }
                }
            }
            catch (SocketException e){
                System.out.println("一个客户端退出了,我已删除该客户端");
            }
            catch (EOFException e){
                clients.remove(this);
                System.out.println("一个客户端退出了,"+clients.size()+"  个人同时在线");
            }
            catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(dos != null) dos.close();
                    if(dis != null) dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //System.out.println(str);


        }
    }
}



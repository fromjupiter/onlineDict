package dictionary;

import java.util.*;

import commonType.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class dictServer {
	//Internet communication
	static final int portNo = constants.PORT_NO;
	protected class ServerThread extends Thread{
		private Socket clientSocket;
		private BufferedReader sin;
		private PrintWriter sout;
		
		public ServerThread(){
		}
		public ServerThread(Socket s) throws IOException{
			clientSocket=s;
			sin= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			sout= new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(clientSocket.getOutputStream())),true);
			start();
		}
		public void run(){
			//处理客户端的请求
		}
	}
	public static void main(String[] args) throws IOException{
		dictServer myServer= new dictServer();
		ServerSocket s= new ServerSocket(portNo);
		System.out.println("The Server is start: " + s);
		try{
			for(;;){
				//阻塞等待
				Socket socket=s.accept();
				System.out.println("a client joined.");
				myServer.new ServerThread(socket);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			s.close();
		}
	}
	
	
	
	Trie trieTree= new Trie();
	ArrayList<User> activeUsers=new ArrayList<User>();
	
	public dictServer(){
		//init
	}
	
	public Word getWord(String name){
		//查找单词，如果没有的话联网查找释义生成单词，然后返回。
		
		
		Word result=null;
		return result;
	}
	public User logIn(String userName,String passWord){
		User current_user=null;
		//登录
		
		
		activeUsers.add(current_user);
		return current_user;
	}
	public void logOut(User usr){
		//登出
	}
	public void register(String userName,String passWord){
		//注册
	}
	
	public void sendCard(){
		//分享卡片
	}
}

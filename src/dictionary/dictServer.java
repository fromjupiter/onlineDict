package dictionary;

import java.util.*;

import commonType.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;

public class dictServer {
	//Internet communication
	static final int portNo = constants.PORT_NO;
	protected class ServerThread extends Thread{
		private Socket clientSocket;
		private BufferedReader sin;
		private ObjectOutputStream oout;
		public ServerThread(){
		}
		public ServerThread(Socket s) throws IOException{
			clientSocket=s;
			sin= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			oout= new ObjectOutputStream(clientSocket.getOutputStream());
			start();
		}
		public void run(){
			//处理客户端的请求
			//报文格式: start_seq#type#arg[0]#arg[1] ...
			/***
			在线用户:wdzmfkx#10001
				返回user数量，然后返回所有User
			 注册 : wdzmfkx#10011#用户名#密码#英文昵称
			 	返回bool值
			 登录: wdzmfkx#10012#用户名#密码
			 	返回User
			刷新用户状态：wdzmfkx#10013#用户名
				返回User
			登出: wdzmfkx#10013#用户名
				返回bool值	
			查询单词:wdzmfkx#10100#用户名#单词名
				返回Word以及三个bool值（对应是否点过赞）
			发送卡片:wdzmfkx#10200#用户名#接收者#单词名#source#附加的话
				返回Card
			点赞:wdzmfkx#10300#用户名#单词名#来源(1,2,3)
				返回bool值
			取消赞:wdzmfkx#10301#用户名#单词名#来源(1,2,3)
				返回bool值
			 ***/
			System.out.println("a new thread is running..");
			InetAddress clientIp=clientSocket.getInetAddress();
			while(true){
				String cmd=null;
				try{
					cmd=sin.readLine();
					if(cmd==null)
						continue;
				}catch(IOException TimeOut){
					//用户未响应，已经下线
					for(User i:activeUsers){
						if(i.getIpAddr().equals(clientIp))
							logOut(i.getName());
					}
					TimeOut.printStackTrace();
					return;
				}
				String[] args=cmd.split("#");
				
				if(!args[0].equals("wdzmfkx")){
					continue;
				}
				if(args[1].equals("10001")){
					//返回在线用户列表
					assert args.length==2;
					try{
						oout.writeInt(activeUsers.size());
						for(User t:activeUsers){
							oout.writeObject((SimpleUser)t);
						}
					}catch(IOException e){
						e.printStackTrace();
					}
					System.out.println("already sent online users!");
				}else if(args[1].equals("10011")){
					//注册
					assert args.length==5;
					boolean temp=register(args[2],args[3],args[4]);
					try{
						oout.writeBoolean(temp);
					}catch(IOException e){
						e.printStackTrace();
					}
				}else if(args[1].equals("10012")){
					//登录
					assert args.length==4;
					User temp=logIn(args[2],args[3],clientIp.getHostName());
					System.out.println(temp.toString());
					try{
						oout.writeObject(temp);
					}catch(IOException e){
						e.printStackTrace();
					}
				}else if(args[1].endsWith("10013")){
					//刷新用户状态
					assert args.length==3;
					User client=get_active_user(args[2]);
					if(clientIp.equals(client.getIpAddr())){
						//确认请求来源与用户登录时的IP地址一致
						try{
							if(client!=null)
								oout.writeObject(client);
						}catch(IOException e){
							e.printStackTrace();
						}
					}
				}else if(args[1].equals("10014")){
					//登出
					assert args.length==3;
					User client=get_active_user(args[2]);
					try{
						if(clientIp.equals(client.getIpAddr())){
							//确认请求来源与用户登录时的IP地址一致
							try{
								logOut(args[2]);
								oout.writeBoolean(true);
							}catch(IOException e){
								e.printStackTrace();
							}
						}else{
							System.out.println("ILLEGAL LOG OUT:user don't match.");
							oout.writeBoolean(false);
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}else if(args[1].equals("10100")){
					//查询单词
					assert args.length==4;
					Word wd=getWord(args[3]);
					try{
						oout.writeObject(wd);
						oout.writeBoolean(is_liked(args[2],args[3],1));
						oout.writeBoolean(is_liked(args[2],args[3],2));
						oout.writeBoolean(is_liked(args[2],args[3],3));
					}catch(IOException e){
						e.printStackTrace();
					}
				}else if(args[1].equals("10200")){
					//发送卡片
					assert args.length==7;
					boolean temp=send_card(args[2],args[3],args[4],
								Integer.parseInt(args[5]),args[6]);
					try{
						oout.writeBoolean(temp);
					}catch(IOException e){
						e.printStackTrace();
					}
					User receiver=get_active_user(args[3]);
					if(receiver != null){
						//接收方在线,通知接收方
						receiver.receiveCard(args[2], args[3], getWord(args[4]),
								Integer.parseInt(args[5]),args[6]);
					}
				}else if(args[1].equals("10300")){
					//点赞
					assert args.length==5;
					like_word(args[2],args[3],Integer.parseInt(args[4]));
					
				}else if(args[1].endsWith("10301")){
					//取消赞
					assert args.length==5;
					dislike_word(args[2],args[3],Integer.parseInt(args[4]));
				}
				
				try{
					oout.flush();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
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
	
	//database
	private String db_user = "teacher";
	private String db_password = "123456";
	private String database = "dictionary";
	private String server = "localhost:3306";
	Connection con=null;
	String url = "jdbc:mysql://" + server + "/" + database + "?user=" +db_user + "&password=" + db_password; 
	String driver = "com.mysql.jdbc.Driver";
	
	Trie trieTree= new Trie();
	ArrayList<User> activeUsers=new ArrayList<User>();
	
	public dictServer(){
		//init
		try
		{
			Class.forName(driver);
			con = DriverManager.getConnection(url);
		}catch(ClassNotFoundException e1){
			e1.printStackTrace();
		}catch(SQLException e2){
			e2.printStackTrace();
		}
		generateTrieFromDb();
	}
	private void generateTrieFromDb(){
		try{
			Statement stmt=con.createStatement();
			String query="select * from word;";
			ResultSet rs=stmt.executeQuery(query);
			while(rs.next()){
				String wordname=rs.getString(1);
				Word wd=new Word(wordname);
				for(int i=0;i<constants.SOURCE_SITES_NUM;i++){
					String pron=rs.getString(3*i-1);
					String exp=rs.getString(3*i);
					wd.setExp(i, exp);
					wd.setPron(i, pron);
				}
				trieTree.addWord(wd);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public Word getWord(String name){
		//查找单词，如果没有的话联网查找释义生成单词，然后返回。
		Word result=trieTree.find(name);
		if(result == null){
			/*
			 * 查找网络释义
			 */
			try{
				result=searchEngine.search_all(name);
				if(result.getExp(0).equals(""))
					//没有释义，不是单词
					return null;
				
			}catch(Exception e){
				e.printStackTrace();
			}
			trieTree.addWord(result);
			//db
			try{
				Statement stmt=con.createStatement();
				String query="insert into word values('"+name+"','"
						+result.getPron(0)+"','"+result.getExp(0)+"',"+result.getLikeCount(0)+",'"
						+result.getPron(1)+"','"+result.getExp(1)+"',"+result.getLikeCount(1)+",'"
						+result.getPron(2)+"','"+result.getExp(2)+"',"+result.getLikeCount(2)+",'"
						+");";
				stmt.executeUpdate(query);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}
	public User get_active_user(String userName){
		for(User t:activeUsers){
			if(userName.equals(t.getName())){
				return t;
			}
		}
		return null;
	}
	public User logIn(String userName,String passWord,String host){
		User current_user=get_active_user(userName);
		if(current_user != null)
			return null;
		//登录
		try{
			Statement stmt=con.createStatement();
			String query="select * from user where username='"+userName+
					"' AND passWord='"+passWord+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()!=false){
				String username=rs.getString("username");
				String password=rs.getString("password");
				String nickname=rs.getString("nickname");
				current_user=new User(username,password,nickname);
			}
			if(current_user != null){
				current_user.setIp(host);
				query="select * from word_card where receiver='"+userName+"';";
				rs=stmt.executeQuery(query);
				while(rs.next()){
					String sender=rs.getString("sender");
					String wordName=rs.getString("word");
					String script=rs.getString("script");
					int source=rs.getInt("source");
					current_user.receiveCard(sender, userName, getWord(wordName),source,script);
				}
				activeUsers.add(current_user);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return current_user;
	}
	public void logOut(String username){
		//登出
		activeUsers.remove(new User(username));
	}
	public boolean register(String userName,String passWord,String nickName){
		//注册
		try{
			Statement stmt=con.createStatement();
			String query="select * from user where username='"+userName+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()==false){
				query="insert into user(username,password,nickname) values('"+userName+
						"','"+passWord+"','"+nickName+"');";
				stmt.executeUpdate(query);
			}else{
				System.out.println("register FAIL:ALREADY EXIST!");
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean send_card(String sender,String receiver,String wordName,int source,String script){
		//分享卡片
		Word result=trieTree.find(wordName);
		if(result == null){
			//异常，发送卡片说明所查询单词一定在内存或数据库里
			assert false;
		}
		try{
			Statement stmt=con.createStatement();
			String query="select * from user where username='"+receiver+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()==false){
				System.out.println("send card FAIL:SENDING TO ILLEGAL USER!");
				return false;
			}
			
			query="insert into word_card(sender,receiver,word,source,script) values('"
					+sender+"','"+receiver+"','"+wordName+"',"+source+",'"+script+"');";
			stmt.executeUpdate(query);
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	public void like_word(String userName,String wordName,int source){
		//修改内存
		Word result=trieTree.find(wordName);
		if(result == null){
			//异常，点赞说明所查询单词一定在内存或数据库里
			assert false;
		}
		result.like(source);
		//修改db
		try{
			Statement stmt=con.createStatement();
			String query="select uid from user where userName='"+userName+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()!=false){
				int uid=rs.getInt("uid");
				query="insert into like_word(uid,word) values("+uid+",'"+wordName+",'"+source+"');";
				stmt.executeUpdate(query);
			}else{
				System.out.println("like word FAIL:USER DOESN'T EXIST!");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void dislike_word(String userName,String wordName,int source){
		//修改内存
		Word result=trieTree.find(wordName);
		if(result == null){
			//异常，点赞说明所查询单词一定在内存或数据库里
			assert false;
		}
		result.dislike(source);
		//修改db
		try{
			Statement stmt=con.createStatement();
			String query="select uid from user where userName='"+userName+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()!=false){
				int uid=rs.getInt("uid");
				query="delete from like_word where uid="+uid+" AND word='"+wordName
						+"AND source="+source+"';";
				stmt.executeUpdate(query);
			}else{
				System.out.println("dislike word FAIL:USER DOESN'T EXIST!");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public boolean is_liked(String userName,String wordName,int source){
		try{
			Statement stmt=con.createStatement();
			String query="select uid from user where userName='"+userName+"';";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()!=false){
				int uid=rs.getInt("uid");
				query="select * from like_word where uid="+uid
						+" AND word='"+wordName+" AND source="+source+";";
				rs=stmt.executeQuery(query);
				if(rs.next()!=false)
					return true;
				else
					return false;
			}else{
				System.out.println("is_liked FAIL:USER DOESN'T EXIST!");
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		//shouldn't be here
		return false;
	}
}

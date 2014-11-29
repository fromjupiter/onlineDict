package dictionary;

import java.util.*;
import commonType.*;
import java.sql.*;

public class dictServer {
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

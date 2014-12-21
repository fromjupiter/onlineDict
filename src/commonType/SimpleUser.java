package commonType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimpleUser implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	protected String nickName=null;
	protected InetAddress ipAddr=null;
	
	public SimpleUser(){
		
	}
	public SimpleUser(String nickName){
		this.nickName=new String(nickName);
	}
	public String getNickName(){
		return nickName;
	}
	public InetAddress getIpAddr(){
		return ipAddr;
	}
	public void setIp(String host){
		try{
			ipAddr=InetAddress.getByName(host);
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
	}
	public String toString(){
		return nickName+" @ "+ipAddr.toString();
	}
}

package commonType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class User extends SimpleUser implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private String userName=null;
	private String passWord=null;
	private ArrayList<Card> cardList=null;
	
	public User(){
		
	}
	public User(String usrname,String passwd,String nickname){
		super(nickname);
		userName=new String(usrname);
		passWord=new String(passwd);
	}
	public User(String usrname,String passwd){
		userName=new String(usrname);
		passWord=new String(passwd);
	}
	public User(String usrname){
		userName=new String(usrname);
		passWord=new String("");
	}
	public boolean equals(User usr2){
		return this.userName.equals(usr2.userName);
	}

	public String getName(){
		return userName;
	}
	public void receiveCard(Card c){
		cardList.add(c);
	}
	public void receiveCard(String sender,String receiver,Word wd,int source,String script){
		cardList.add(new Card(sender,receiver,wd,source,script));
	}
}


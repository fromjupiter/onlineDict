package commonType;

public class Card implements java.io.Serializable{
	String sender;
	String receiver;
	Word wd;
	int source;
	String script;
	public Card(){
	}
	public Card(String sender,String receiver,Word wd,int source,String script){
		this.sender=sender;
		this.receiver=receiver;
		this.wd=new Word(wd);
		this.source=source;
		this.script=script;
	}
}

package commonType;

import java.util.Arrays;

public class Word {
	private String name;
	private String[] pron=new String[constants.SOURCE_SITES_NUM];//发音
	private String[] exp=new String[constants.SOURCE_SITES_NUM];//释义
	private int[] like_count=new int[constants.SOURCE_SITES_NUM];//点赞数
	public Word(){
		for(int i=0;i<constants.SOURCE_SITES_NUM;i++){
			like_count[i]=0;
		}
	}
	public Word(String name){
		//通过name联网获取释义
		
	}
	public Word(Word cp){
		this.name=new String(cp.name);
		this.pron=Arrays.copyOf(cp.pron, constants.SOURCE_SITES_NUM);
		this.exp=Arrays.copyOf(cp.exp, constants.SOURCE_SITES_NUM);
		this.like_count=Arrays.copyOf(cp.like_count, constants.SOURCE_SITES_NUM);
	}
	public String toString(){
		return getName();
	}
	public String getName(){
		return new String(name);
	}
	public String getPron(int i){
		return new String(pron[i]);
	}
	public String getExp(int i){
		return new String(exp[i]);
	}
	public int getLikeCount(int i){
		return like_count[i];
	}
}

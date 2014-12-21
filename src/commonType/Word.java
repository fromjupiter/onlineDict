package commonType;

import java.util.Arrays;

import dictionary.searchEngine;

public class Word implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
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
		name=new String(name);
		for(int i=0;i<constants.SOURCE_SITES_NUM;i++){
			like_count[i]=0;
		}
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
	public void setName(String s){
		name=new String(s);
	}
	public void setPron(int i,String s){
		pron[i]=new String(s);
	}
	public void setExp(int i,String s){
		exp[i]=new String(s);
	}
	public void like(int source){
		//source start from 1
		like_count[source-1]++;
	}
	public void dislike(int source){
		//source start from 1
		like_count[source-1]--;
	}
	public int getLikeCount(int i){
		return like_count[i];
	}
}

package dictionary;
import java.io.*;

import javax.swing.DefaultListModel;

import commonType.Word;
//字典树参考百度百科“字典树”词条 实现。
class Trie {
	protected TrieNode root=new TrieNode();
	protected final int SIZE=34;//a-z and space .  / ' - , ! ?
	protected TrieNode current=root;//UI list的遍历指针
	protected class TrieNode{
		private int num;//节点字符串出现的次数
		private TrieNode[] son;
		private TrieNode parent;
		private char val;//节点值
		private Word word;
		TrieNode(){
			num=1;
			son= new TrieNode[SIZE];
			parent=null;
			word=null;
		}
	}
	public TrieNode getRoot(){
		return root;
	}
	public void insert(Word wd){
		String str=wd.getName();
		if(str==null || str.length()==0)
			return;
		
		TrieNode node=root;
		char[] letters=str.toCharArray();
		for(int i=0;i<letters.length;i++){
			int pos;
			switch(letters[i]){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(letters[i]>='a' && letters[i]<='z')
					pos=letters[i]-'a';
				else if(letters[i]>='A' && letters[i]<='Z')
					pos=letters[i]-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			
			if(node.son[pos]==null){
				node.son[pos]=new TrieNode();
				node.son[pos].val=letters[i];
				node.son[pos].parent=node;
			}else{
				node.son[pos].num++;
			}
			node=node.son[pos];
		}
		node.word=wd;
	}
	public void shiftUpCurrent(String str){
		//将current上移一层
		if(current!=null && str.length()>0){
			current=current.parent;
		}else{
			//父节点信息丢失，重新查找
			current=find(str);
		}
	}
	public void shiftDownCurrent(char ch){
		//将current下滑一层
		if(current != null){
			int pos;
			switch(ch){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(ch>='a' && ch<='z')
					pos=ch-'a';
				else if(ch>='A' && ch<='Z')
					pos=ch-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			current=current.son[pos];
		}
	}
	public void preTraverse(TrieNode node,DefaultListModel<Word> wordList){
		//遍历node的子节点来生成wordList,最多加入30个元素
		if(node != null && wordList.getSize()<30){
			if(node.word != null){
				wordList.addElement(node.word);
			}
			for(TrieNode child:node.son){
				preTraverse(child,wordList);
			}
		}
	}
	public void traverseByCurrent(String str,DefaultListModel<Word> wordList){
		//通过current生成列表,带纠错
		if(current!=null){
			wordList.clear();
			preTraverse(current,wordList);
		}else{
			//单词查找失败，考虑进行纠错
			Word temp=null;
			if(!wordList.isEmpty())
				temp=wordList.get(0);
			wordList.clear();
			wordList.addElement(temp);
			spellCorrection(str,wordList);
		}
	}
	public void traverseByCurrent(DefaultListModel<Word> wordList){
		//通过current生成列表,不带纠错
		if(current!=null){
			wordList.clear();
			preTraverse(current,wordList);
		}
	}
	public void spellCorrection(String str,DefaultListModel<Word> wordList){
		//纠错处理，生成wordlist的“可能单词”结果
		int[] path=new int[50];//存放从root到temp的路径,逆序存放
		char[] letters= str.toCharArray();
		int pathSize=letters.length;
		for(int i=0;i<letters.length;i++){
			int pos;
			switch(letters[i]){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(letters[i]>='a' && letters[i]<='z')
					pos=letters[i]-'a';
				else if(letters[i]>='A' && letters[i]<='Z')
					pos=letters[i]-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			path[i]=pos;
		}
		//纠错算法的核心是：
		//假设只错了一个字母，在树中改变路径中的一个值寻找可能的单词
		TrieNode temp=root;
		for(int i=0;i<pathSize;i++){
			if(wordList.getSize()<10){
				int lastVal=path[i];
				for(int j=0;j<26;j++){
					path[i]=j;
					temp=root;
					for(int k=0;k<pathSize;k++){
						if(temp!=null)
							temp=temp.son[path[k]];
					}
					if(temp!=null && temp.word!=null)//找到可能的单词
						wordList.addElement(temp.word);
				}
				path[i]=lastVal;
			}
		}
	}
	public TrieNode find(String name){
		//通过name在字典树中查找单词
		if(name == null){
			return null;
		}else if(name.length()==0)
			return root;
		TrieNode node=root;
		char[] letters= name.toCharArray();
		for(int i=0;i<letters.length;i++){
			int pos;
			switch(letters[i]){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(letters[i]>='a' && letters[i]<='z')
					pos=letters[i]-'a';
				else if(letters[i]>='A' && letters[i]<='Z')
					pos=letters[i]-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			
			if(node.son[pos]!=null){
				node=node.son[pos];
			}else{
				return null;
			}
		}
		return node;
	}
	public String getExpByName(String name){
		if(name == null){
			return null;
		}else if(name.length()==0)
			return null;
		TrieNode node=root;
		char[] letters= name.toCharArray();
		for(int i=0;i<letters.length;i++){
			int pos;
			switch(letters[i]){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(letters[i]>='a' && letters[i]<='z')
					pos=letters[i]-'a';
				else if(letters[i]>='A' && letters[i]<='Z')
					pos=letters[i]-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			
			if(node.son[pos]!=null){
				node=node.son[pos];
			}else{
				return null;
			}
		}
		if(node.word == null)
			return null;
		else 
			return node.word.getExp();
	}
	public boolean isExisted(String name){
		if(name == null){
			return false;
		}else if(name.length()==0)
			return false;
		TrieNode node=root;
		char[] letters= name.toCharArray();
		for(int i=0;i<letters.length;i++){
			int pos;
			switch(letters[i]){
			case '?':
				pos=33;break;
			case '!':
				pos=32;break;
			case ',':
				pos=31;break;
			case '-':
				pos=30;break;
			case '\'':
				pos=29;break;
			case '/':
				pos=28;break;
			case '.':
				pos=27;break;
			case ' ':
				pos=26;break;
			default:
				if(letters[i]>='a' && letters[i]<='z')
					pos=letters[i]-'a';
				else if(letters[i]>='A' && letters[i]<='Z')
					pos=letters[i]-'A';
				else //其他未知字符
					pos=26;//与space归在一类
			}
			
			if(node.son[pos]!=null){
				node=node.son[pos];
			}else{
				return false;
			}
		}
		if(node.word == null)
			return false;
		else 
			return true;
	}
	public boolean isExisted(Word wd){
		return isExisted(wd.getName());
	}
	public void generateTrie(String path){
		//通过path的字典文件生成字典树
		try{
			BufferedReader br= new BufferedReader(new FileReader(path));
			br.readLine();//第一行舍去
			String dataLine=br.readLine();
			while(dataLine != null){
				String[] data=dataLine.split("\t");
				//对应字典文件的四个属性
				Word wd=new Word(data[1],data[2],data[3]);
				this.insert(wd);
				dataLine=br.readLine();
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void addWord(Word wd){
		this.insert(wd);
		//加入txt字典文件中
		try{
			FileWriter fw=new FileWriter("added_words.txt",true);
			fw.write("1\t"+wd.getName()+"\t"+wd.getPron()+"\t"+wd.getExp()+"\n");
			fw.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

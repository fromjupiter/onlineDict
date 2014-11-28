package dictionary;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

class UI extends JFrame {
	Trie trieTree= new Trie();
	
	private JTextField jtfInputWord = new JTextField(40);
	private JTextArea jtfInfo = new JTextArea("",26,35);
	private JButton jbClear = new JButton("Add");
	private DefaultListModel<Word> wordList=new DefaultListModel<Word>();
	private JList<Word> jlWordList = new JList<Word>(wordList);
	
	private class WordAddition extends JFrame{
		private JTextField jtfName = new JTextField();
		private JTextField jtfPron = new JTextField();
		private JTextField jtfExp = new JTextField();
		private JButton jbtAdd = new JButton("加入词典");
		
		public WordAddition(){
			JPanel p1= new JPanel(new GridLayout(3,2));
			p1.add(new JLabel("英文:"));
			p1.add(jtfName);
			p1.add(new JLabel("发音:"));
			p1.add(jtfPron);
			p1.add(new JLabel("中文:"));
			p1.add(jtfExp);
			
			JPanel p2 = new JPanel();
			p2.add(jbtAdd);
			this.add(p1,BorderLayout.CENTER);
			this.add(p2,BorderLayout.SOUTH);
			jbtAdd.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//加入新单词
					Word nword=new Word(jtfName.getText(),jtfPron.getText(),jtfExp.getText());
					if(!trieTree.isExisted(nword))
						trieTree.addWord(nword);
				}
			});
		}
	}
	
	public UI(){
		trieTree.generateTrie("dictionary.txt");
		trieTree.generateTrie("added_words.txt");
		trieTree.traverseByCurrent(wordList);
		//输入框
		JPanel jpInput= new JPanel(new BorderLayout());
		//jpInput.setPreferredSize(new Dimension(200,30));
		jpInput.add(new JLabel("Enter the word:"),BorderLayout.WEST);
		jpInput.add(jtfInputWord,BorderLayout.CENTER);
		jpInput.add(jbClear,BorderLayout.EAST);
		jtfInputWord.setFont(new Font("TimesRoman",Font.PLAIN,20));
		
		//左侧显示结果列表
		JPanel jpWordList= new JPanel(new BorderLayout());
		jlWordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlWordList.setForeground(Color.RED);
		jlWordList.setBackground(Color.LIGHT_GRAY);
		jlWordList.setSelectionForeground(Color.PINK);
		jlWordList.setSelectionBackground(Color.BLACK);
		jlWordList.setFixedCellWidth(150);
		jpWordList.add(jlWordList,BorderLayout.CENTER);
		
		//右侧显示选择的单词详细释义
		JPanel jpInfo= new JPanel(new BorderLayout());
		jtfInfo.setEditable(false);
		jtfInfo.setBackground(Color.LIGHT_GRAY);
		jtfInfo.setForeground(Color.BLUE);
		jpInfo.add(jtfInfo,BorderLayout.CENTER);
		
		//整体布局
		setLayout(new BorderLayout());
		add(jpInput,BorderLayout.NORTH);
		add(new JScrollPane(jpWordList),BorderLayout.WEST);
		add(jpInfo,BorderLayout.CENTER);
		
		//监听输入事件
		Document myDoc=jtfInputWord.getDocument();
		myDoc.addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				//jtf文本框属性发生变化
			}
			public void insertUpdate(DocumentEvent e){
				//插入了文本
				String str=jtfInputWord.getText();
				char[] chstr=str.toCharArray();
				int spaceCount=0;
				for(int i=0;i<chstr.length;i++){
					if(Character.isSpaceChar(chstr[i]))
						spaceCount++;
				}
				char lastCh=str.charAt(str.length()-1);
				trieTree.shiftDownCurrent(lastCh);
				if(spaceCount<4){//输入的是单词或词组，寻找精确结果
					trieTree.traverseByCurrent(jtfInputWord.getText(),wordList);
					jlWordList.setSelectedIndex(0);
				}else{
					//输入的是长句，尝试进行翻译
					wordList.clear();
					StringBuffer result= new StringBuffer();
					String[] strWords=str.split(" ");
					//先寻找词组
loop1:				for(int i=0;i<strWords.length;i++){
						int j;
						for(j=i+1;j<strWords.length;j++){
							String phrase=new String(strWords[i]);
							for(int k=i+1;k<=j;k++){
								phrase+=" ";
								phrase+=strWords[k];
							}
							if(trieTree.isExisted(phrase)){
								result.append(trieTree.getExpByName(phrase));
								i=j;
								continue loop1;
							}
						}
						//起点为i找不到词组，所以word[i]以词来翻译
						if(trieTree.isExisted(strWords[i]))
							result.append(trieTree.getExpByName(strWords[i]));
						else
							result.append(strWords[i]);
					}
					jtfInfo.setText(result.toString());
				}
			}
			public void removeUpdate(DocumentEvent e){
				//删除了文本
				trieTree.shiftUpCurrent(jtfInputWord.getText());
				trieTree.traverseByCurrent(jtfInputWord.getText(),wordList);
				jlWordList.setSelectedIndex(0);
			}
		});
		//监听选择改变事件
		jlWordList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				Word selectedWord = (Word)jlWordList.getSelectedValue();
				//改变jtfInfo值
				StringBuffer info=new StringBuffer();
				if(selectedWord != null){
					info.append(selectedWord.getName());
					info.append("\n[");
					info.append(selectedWord.getPron());
					info.append("]\n\n\n");
					info.append(selectedWord.getExp());
				}
				jtfInfo.setText(info.toString());
			}
		});
		//初始化选择框
		jlWordList.setSelectedIndex(0);
		//监听加入单词按钮事件
		jbClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//加入新单词
				WordAddition wa=new WordAddition();
				wa.setTitle("新单词出现！");
				wa.setVisible(true);
				wa.setSize(300, 200);
				wa.setLocationRelativeTo(null);
				wa.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
		});
	}
}

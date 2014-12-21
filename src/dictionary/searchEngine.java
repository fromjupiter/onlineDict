package dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import commonType.*;

public class searchEngine {//baidu youdao biying
	public static Word search_all(String name) throws Exception{
		Word newword = new Word(name);
		search_baidu(newword);
		search_youdao(newword);
		search_biying(newword);
		if(newword.getExp(0)==null)
			return null;
		else
			return newword;
	}
	
	public static  void search_baidu(Word newword) throws Exception {
		StringBuffer buffer = new StringBuffer();
		int maxLength = 1000000;
	//	Scanner input = new Scanner(System.in);
	//	System.out.println("Input a word.");
	//	String word = input.nextLine();
		String word = newword.getName();
		String strURL1 = "http://dict.baidu.com/s?wd=" + word;
		URL url1 = new URL(strURL1);
		HttpURLConnection hConnect = (HttpURLConnection) url1
	                .openConnection();
		  BufferedReader rd = new BufferedReader(new InputStreamReader(
	                hConnect.getInputStream()));
	        int ch;
	        for (int length = 0; (ch = rd.read()) > -1
	                && (maxLength <= 0 || length < maxLength); length++)
	            buffer.append((char) ch);
	        String s = buffer.toString();
	        
	        String regex1 = "<span>(.*?)</b><a href=\"#\"";
		       Pattern p1 = Pattern.compile(regex1);
		       Matcher m1 = p1.matcher(s);
		      while (m1.find())//音标
		       {
		    	   String find = m1.group(); 
		    	   find = find.replaceAll("(</b><a href=\"#\")|(<span>)|(<b lang=\"EN-US\" xml:lang=\"EN-US\">)", ""); 
		    	   newword.setPron(0, find);
		    	   
		       }
		       
	       String regex = "<div><p><strong>(.*)</span></p></div>";
	       Pattern p = Pattern.compile(regex);
	       Matcher m = p.matcher(s);

	       if(m.find()){ //找到该单词
	    	   String find  = m.group(1);
	    	   find = find.replaceAll("(</span></p><p><strong>)", "\n"); 
	    	   find = find.replaceAll("(</strong><span>)", " ");
	    	   //System.out.println(find);  
	    	   newword.setExp(0, find);
	    	   }
	       else
	    	   newword.setExp(0, null);

	        rd.close();
	        hConnect.disconnect();
	}
	
	public static void search_youdao(Word newword) throws Exception {
		StringBuffer buffer = new StringBuffer();
		int maxLength = 1000000;
		//Scanner input = new Scanner(System.in);
		//System.out.println("Input a word.");
	//	String word = input.nextLine();
		String word = newword.getName();
		String strURL1 = "http://dict.youdao.com/search?le=eng&q=" + word + "&keyfrom=dict.top" ;
		URL url1 = new URL(strURL1);
		HttpURLConnection hConnect = (HttpURLConnection) url1
	                .openConnection();
		  BufferedReader rd = new BufferedReader(new InputStreamReader(
	                hConnect.getInputStream()));
	        int ch;
	        for (int length = 0; (ch = rd.read()) > -1
	                && (maxLength <= 0 || length < maxLength); length++)
	            buffer.append((char) ch);
	        String s = buffer.toString();
	        String regex1 = " <span class=\"pronounce\">(.*?)</span>";
		       Pattern p1 =Pattern.compile(regex1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		       Matcher m1 = p1.matcher(s);
	        while(m1.find())
	        {
	        	String find = m1.group(1);
	        	find = find.replaceAll("( )|(\n)|(\t)|(<span class=\"pronounce\">)|(<span class=\"phonetic\">)|(</span>)", "");
	        	//System.out.print(find);
	        	newword.setPron(1, find);
	        }
	       
	       String regex = "<div class=\"trans-container\">(.*?)</ul>";
	       Pattern p =Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	       Matcher m = p.matcher(s);

	       if(m.find()){ //找到该单词
	    	   String find  = m.group(1);
	    	   find = find.replaceAll("(\n)|(\t)|(<ul>)", ""); 
	    	  
	    	  find = find.replaceAll("(        <li>)|(     <li>)", "");
	    	  find = find.replaceAll("(</li>)", "\n");
	    	   //System.out.println(find);
	    	  newword.setExp(1, find);
	    	   }
	       else
	    	   newword.setExp(1, null);

	        rd.close();
	        hConnect.disconnect();
	}
	
	public static void search_biying(Word newword) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int maxLength = 1000000;
		//Scanner input = new Scanner(System.in);
		//System.out.println("Input a word.");
		//String word = input.nextLine();
		String word = newword.getName();
		String strURL1 = "http://cn.bing.com/dict/search?q=" + word + 
				"&go=&qs=n&form=CM&pq=" + word + "&sc=5-3&sp=-1&sk=";
		URL url1 = new URL(strURL1);
		HttpURLConnection hConnect = (HttpURLConnection) url1
	                .openConnection();
		  BufferedReader rd = new BufferedReader(new InputStreamReader(
	                hConnect.getInputStream()));
	        int ch;
	        for (int length = 0; (ch = rd.read()) > -1
	                && (maxLength <= 0 || length < maxLength); length++)
	            buffer.append((char) ch);
	        String s = buffer.toString();
	        
	       String regex1 = "<div class=\"hd_prUS\">(.*?)</div>";
	       Pattern p1 = Pattern.compile(regex1);
	       Matcher m1 = p1.matcher(s);
	       String pron = null;
	       if(m1.find())
	       {
	    	   String find = m1.group(1);
	    	   find = find.replaceAll("(&#160)", "");
	    	   //System.out.print(find);
	    	   pron = find;
	       }
	       
	       String regex3 = "<div class=\"hd_pr\">(.*?)</div>";
	       Pattern p3 = Pattern.compile(regex3);
	       Matcher m3 = p3.matcher(s);
	       if(m3.find())
	       {
	    	   String find = m3.group(1);
	    	   find = find.replaceAll("(&#160)", "");
	    	   //System.out.print(find);
	    	   pron = pron + find;
	       }
	       newword.setPron(2, pron);
	       
	       String regex2 = "<span class=\"pos\">(.*?)</span></span></li></ul>";
	       Pattern p2 =Pattern.compile(regex2);
	       Matcher m2 = p2.matcher(s);

	       if(m2.find()){ //找到该单词
	    	   String find  = m2.group(1);
	    	   find = find.replaceAll("(</span><span class=\"def\"><span>)", ""); 
	    	   find = find.replaceAll("(</span></span></li><li><span class=\"pos\">)", "\n");
	    	   find = find.replaceAll("(</span></span></li><li><span class=\"pos web\">)", "\n");
	    	  // System.out.println(find); 
	    	   newword.setExp(2, find);
	    	   }
	       else
	    	   newword.setExp(2, null);

	        rd.close();
	        hConnect.disconnect();
	    
		
	}
}

package dictionary;
class Word {
	private String name;
	private String pron;//发音
	private String exp;//释义
	public Word(){
		name=null;
		pron=null;
		exp=null;
	}
	public Word(String name,String pron,String exp){
		this.name=new String(name);
		this.pron=new String(pron);
		this.exp=new String(exp);
	}
	public Word(Word cp){
		this.name=new String(cp.name);
		this.pron=new String(cp.pron);
		this.exp=new String(cp.exp);
	}
	public String toString(){
		return getName();
	}
	public String getName(){
		return new String(name);
	}
	public String getPron(){
		return new String(pron);
	}
	public String getExp(){
		return new String(exp);
	}
	public void print(){
		System.out.println(name);
		System.out.println("["+pron+"]");
		System.out.println(exp);
	}
}

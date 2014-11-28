package dictionary;

import javax.swing.JFrame;

public class Dict {
	public static void main(String[] args) {
		UI dicUI=new UI();
		dicUI.setSize(600,500);
		dicUI.setTitle("冯科翔 121220023");
		dicUI.setLocationRelativeTo(null);
		dicUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dicUI.setVisible(true);
	}
}

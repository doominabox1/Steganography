import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class GUI extends JFrame implements ActionListener {
	public static void main(String args[]){
		System.getProperty("java.library.path");
		new GUI();
	}
	private static final long serialVersionUID = 6411499808530678723L;

	BufferedImage embedBufferdImage; 

	public GUI(){
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){e.printStackTrace();}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280,720);

		JTabbedPane tabbedPane = new JTabbedPane();

		EmbedPanel embedPanel = new EmbedPanel();
		tabbedPane.addTab("Embed", null, embedPanel, "Hide files in an image.");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_E);

		JComponent detachPanel = new DetachPanel();
		tabbedPane.addTab("Detach", null, detachPanel, "Extract files from an image.");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_D);

		embedPanel.setPreferredSize(new Dimension(1280,720));
		detachPanel.setPreferredSize(new Dimension(1280,720));

		add(tabbedPane);
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

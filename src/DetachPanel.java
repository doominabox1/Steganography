import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DetachPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 9102145396390949634L;
	JButton selectImageDetachButton;
	JButton executeDetachButton;
	JFileChooser fc;
	File inputDetachImage;
	FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
	JLabel detachImage;
	JLabel imageDetachInfo;
	Image img;
	HeaderInfo hi;

	public DetachPanel(){
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel loadButtonsDetachPanel = new JPanel();
			loadButtonsDetachPanel.setPreferredSize(new Dimension(150, 720));
			loadButtonsDetachPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			loadButtonsDetachPanel.setLayout(new BoxLayout(loadButtonsDetachPanel, BoxLayout.Y_AXIS));

			selectImageDetachButton = new JButton("Select Image");
			executeDetachButton = new JButton("Execute");
			executeDetachButton.setEnabled(false);
			JPanel spacer = new JPanel();
				spacer.setPreferredSize(new Dimension(150, 500));

			selectImageDetachButton.addActionListener(this);
			executeDetachButton.addActionListener(this);


			loadButtonsDetachPanel.add(selectImageDetachButton);
			loadButtonsDetachPanel.add(spacer);
			loadButtonsDetachPanel.add(executeDetachButton);

		JPanel informationsDetachPanel = new JPanel();
			informationsDetachPanel.setPreferredSize(new Dimension(1230, 720));
			informationsDetachPanel.setLayout(new BoxLayout(informationsDetachPanel, BoxLayout.Y_AXIS));
			JPanel imageDetachPanel = new JPanel();
				imageDetachPanel.setPreferredSize(new Dimension(1230, 620));
				detachImage = new JLabel();

				imageDetachPanel.setAlignmentX(0);
				imageDetachPanel.add(detachImage);
			JPanel infoDetachPanel = new JPanel();
				infoDetachPanel.setPreferredSize(new Dimension(1230, 100));
				infoDetachPanel.setLayout(new BoxLayout(infoDetachPanel, BoxLayout.Y_AXIS));
				imageDetachInfo = new JLabel("    Please select an image.");
				JLabel infoSpacer = new JLabel(" ");

				infoDetachPanel.setAlignmentX(0);
				infoDetachPanel.add(imageDetachInfo);
				infoDetachPanel.add(infoSpacer);

			informationsDetachPanel.setAlignmentX(0);
			informationsDetachPanel.add(imageDetachPanel);
			informationsDetachPanel.add(infoDetachPanel);


		add(loadButtonsDetachPanel);
		add(informationsDetachPanel);

		imageDetachPanel.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = -8785350935416957494L;

			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if(droppedFiles.size() == 1){
						selectedImage(droppedFiles.get(0));
					}else{
						imageDetachInfo.setText("    Please drop only one file.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == selectImageDetachButton){
			fc.setFileFilter(imageFilter);
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				selectedImage(fc.getSelectedFile());
			} else {
				return;
			}
		}
		if(e.getSource() == executeDetachButton){
			if(inputDetachImage != null){
				executeDetachButton.setEnabled(false);
				imageDetachInfo.setText("    Detaching file...");
				refreshGUI();
				File outputFile = Steganography.detach(img, hi);
				if(outputFile != null){
					executeDetachButton.setEnabled(true);
					imageDetachInfo.setText("    File Detached.");
					refreshGUI();
					JOptionPane.showMessageDialog(null, "Detached successfully, you can find the output at: " + outputFile.getAbsolutePath());
				}else{
					executeDetachButton.setEnabled(true);
					imageDetachInfo.setText("    Unable to detach.");
					refreshGUI();
					JOptionPane.showMessageDialog(null, "Unable to detach.");
				}
			}
		}
	}

	public void selectedImage(File inputImage){
		inputDetachImage = inputImage;
		try {
			img = ImageIO.read(inputDetachImage);
		} catch (IOException e1) {
			imageDetachInfo.setText("    Error loading image, please try again.");
			inputDetachImage = null;
			executeDetachButton.setEnabled(false);
			return;
		}
		Dimension newDimension = getScaledDimension(new Dimension(img.getWidth(null), img.getHeight(null)), new Dimension(1230, 620));
		ImageIcon image = new ImageIcon(img.getScaledInstance((int)newDimension.getWidth(), (int)newDimension.getHeight(), Image.SCALE_SMOOTH));
		detachImage.setIcon(image);
		refreshGUI();
		hi = Steganography.getHeaderInfo(img);
		if(hi == null){
			imageDetachInfo.setText("    Image header seems to be corrupt, the image may have been compressed or never written to.");
			executeDetachButton.setEnabled(false);
			return;
		}else{
			imageDetachInfo.setText("    This image seems to be valid and contains "
					+ roundToHundreth(Steganography.bitsToMegabytes(hi.getHeaderBits())) + " mb. If it is valid, it will be a '"
					+ hi.getFileExtention() + "' file.");
			Image newImage = Steganography.getDrawnOutline(img, hi.getHeaderBits(), newDimension);
			ImageIcon nII = new ImageIcon(newImage);
			detachImage.setIcon(nII);
			executeDetachButton.setEnabled(true);
			getRootPane().setDefaultButton(executeDetachButton);
			executeDetachButton.requestFocus();
			return;
		}
	}

	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		if (original_width > bound_width) {
			new_width = bound_width;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > bound_height) {
			new_height = bound_height;
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}

	private double roundToHundreth(double i){
		return (double)((int)(i*100))/100;
	}
	private void refreshGUI() {  // Forgive me father for I have sinned
		final JLabel label = new JLabel();
		new Timer(0 , new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((Timer)e.getSource()).stop();
				Window win = SwingUtilities.getWindowAncestor(label);
				win.setVisible(false);
			}
		}){
			private static final long serialVersionUID = 1L;
			{setInitialDelay(0);}}.start();
			JOptionPane.showMessageDialog(null, label);
	}
}

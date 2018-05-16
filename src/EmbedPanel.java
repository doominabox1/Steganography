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
import java.text.DecimalFormat;
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

public class EmbedPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 9102145396390949634L;
	JButton selectFileEmbedButton;
	JButton selectImageEmbedButton;
	JButton executeEmbedButton;
	JFileChooser fc;
	File inputEmbedFile;
	File inputEmbedImage;
	FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
	JLabel embedImage;
	JLabel imageEmbedInfo;
	JLabel fileEmbedInfo;
	JLabel embedInfo;
	long imageCapacity;
	long fileSize;
	Image img;
	DecimalFormat df = new DecimalFormat("#,###,##0.00");

	public EmbedPanel(){
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel loadButtonsEmbedPanel = new JPanel();
			loadButtonsEmbedPanel.setPreferredSize(new Dimension(150, 720));
			loadButtonsEmbedPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			loadButtonsEmbedPanel.setLayout(new BoxLayout(loadButtonsEmbedPanel, BoxLayout.Y_AXIS));

			selectFileEmbedButton = new JButton("Select File");
			selectImageEmbedButton = new JButton("Select Image");
			executeEmbedButton = new JButton("Execute");
			executeEmbedButton.setEnabled(false);
			JPanel spacer = new JPanel();
				spacer.setPreferredSize(new Dimension(150, 500));
				//spacer.setBorder(BorderFactory.createLineBorder(Color.black));

			selectFileEmbedButton.addActionListener(this);
			selectImageEmbedButton.addActionListener(this);
			executeEmbedButton.addActionListener(this);

			loadButtonsEmbedPanel.add(selectImageEmbedButton);
			loadButtonsEmbedPanel.add(selectFileEmbedButton);
			loadButtonsEmbedPanel.add(spacer);
			loadButtonsEmbedPanel.add(executeEmbedButton);

		JPanel informationsEmbedPanel = new JPanel();
			informationsEmbedPanel.setPreferredSize(new Dimension(1230, 720));
			informationsEmbedPanel.setLayout(new BoxLayout(informationsEmbedPanel, BoxLayout.Y_AXIS));
			JPanel imageEmbedPanel = new JPanel();
				imageEmbedPanel.setPreferredSize(new Dimension(1230, 620));
				//imageEmbedPanel.setBorder(BorderFactory.createLineBorder(Color.black));
				embedImage = new JLabel();

				imageEmbedPanel.setAlignmentX(0);
				imageEmbedPanel.add(embedImage);
			JPanel infoEmbedPanel = new JPanel();
				infoEmbedPanel.setPreferredSize(new Dimension(1230, 100));
				//infoEmbedPanel.setBorder(BorderFactory.createLineBorder(Color.black));
				infoEmbedPanel.setLayout(new BoxLayout(infoEmbedPanel, BoxLayout.Y_AXIS));
				imageEmbedInfo = new JLabel("    Please select an image.");
				fileEmbedInfo = new JLabel("    Please select a file.");
				JLabel infoSpacer = new JLabel(" ");
				embedInfo = new JLabel("    Select an image and a file.");

				infoEmbedPanel.setAlignmentX(0);
				infoEmbedPanel.add(imageEmbedInfo);
				infoEmbedPanel.add(fileEmbedInfo);
				infoEmbedPanel.add(infoSpacer);
				infoEmbedPanel.add(embedInfo);

			informationsEmbedPanel.setAlignmentX(0);
			informationsEmbedPanel.add(imageEmbedPanel);
			informationsEmbedPanel.add(infoEmbedPanel);

		add(loadButtonsEmbedPanel);
		add(informationsEmbedPanel);

		imageEmbedPanel.setDropTarget(new DropTarget() {

			private static final long serialVersionUID = -8785350935416957494L;

			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if(droppedFiles.size() == 1){
						selectedImage(droppedFiles.get(0));
					}else{
						imageEmbedInfo.setText("    Please drop only one file.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		loadButtonsEmbedPanel.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = -8785350935416957494L;

			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if(droppedFiles.size() == 1){
						selectedFile(droppedFiles.get(0));
						runUpdate();
					}else{
						imageEmbedInfo.setText("    Please drop only one file.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == selectFileEmbedButton){
			fc.setFileFilter(null);
			int returnVal = fc.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION){
				selectedFile(fc.getSelectedFile());
				runUpdate();
				return;
			} else {
				runUpdate();
				return;
			}
		}
		if(e.getSource() == selectImageEmbedButton){
			fc.setFileFilter(imageFilter);
			int returnVal = fc.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION){
				inputEmbedImage = fc.getSelectedFile();
				selectedImage(inputEmbedImage);
				runUpdate();
				return;
			} else {
				runUpdate();
				return;
			}
		}
		if(e.getSource() == executeEmbedButton){
			if(!(inputEmbedFile == null || inputEmbedImage == null)){
				if(fileSize < imageCapacity){
					executeEmbedButton.setEnabled(false);
					embedInfo.setText("    Embeding file...");
					refreshGUI();
					File outputFile = Steganography.embed(inputEmbedFile, img);
					if(outputFile != null){
						executeEmbedButton.setEnabled(true);
						embedInfo.setText("    File Embeded.");
						refreshGUI();
						JOptionPane.showMessageDialog(null, "    Embeded successfully, you can find the output image at: " + outputFile.getAbsolutePath());
					}else{
						executeEmbedButton.setEnabled(true);
						embedInfo.setText("    Unable to embed.");
						refreshGUI();
						JOptionPane.showMessageDialog(null, "    Unable to embed.");
					}

				}
			}
		}
	}

	public void selectedImage(File inputImage){
		inputEmbedImage = inputImage;
		try {
			img = ImageIO.read(inputImage);
		} catch (IOException e1) {
			imageEmbedInfo.setText("    Error loading image, please try again.");
			inputEmbedImage = null;
			return;
		}
		if(img == null){
			return;
		}
		Dimension newDimension = getScaledDimension(new Dimension(img.getWidth(null), img.getHeight(null)), new Dimension(1230, 620));
		ImageIcon image = new ImageIcon(img.getScaledInstance((int)newDimension.getWidth(), (int)newDimension.getHeight(), Image.SCALE_SMOOTH));
		embedImage.setIcon(image);
		imageCapacity = Steganography.getAvailableBits(img);
		
		if(imageCapacity > 8000000) {
			imageEmbedInfo.setText("    This image can hold " + df.format(Steganography.bitsToMegabytes(imageCapacity)) + " MB.");
		}else {
			imageEmbedInfo.setText("    This image can hold " + df.format(Steganography.bitsToKilobytes(imageCapacity)) + " kB.");
		}
		
		refreshGUI();
		runUpdate();
	}
	public void selectedFile(File inputImage){
		inputEmbedFile = inputImage;
		fileSize = Steganography.getNeededBits(inputImage); //File can only be 268mb - TODO fix :(
		if(fileSize <= 0){
			fileEmbedInfo.setText("    The file is too large, please try a different one.");
			inputEmbedFile = null;
		}else{
			if(fileSize > 8000000) {
				fileEmbedInfo.setText("    This file needs " + df.format(Steganography.bitsToMegabytes(fileSize)) + " MB of space.");
			}else {
				fileEmbedInfo.setText("    This file needs " + df.format(Steganography.bitsToKilobytes(fileSize)) + " kB of space.");
			}
			
		}
	}

	private void runUpdate(){
		if(!(inputEmbedFile == null || inputEmbedImage == null)){
			if(fileSize < imageCapacity){
				embedInfo.setText("    File can fit into image, ready to embed.");
				Dimension newDimension = getScaledDimension(new Dimension(img.getWidth(null), img.getHeight(null)), new Dimension(1230, 620));
				Image newImage = Steganography.getDrawnOutline(img, fileSize, newDimension);
				ImageIcon nII = new ImageIcon(newImage);
				embedImage.setIcon(nII);
				executeEmbedButton.setEnabled(true);
				getRootPane().setDefaultButton(executeEmbedButton);
				executeEmbedButton.requestFocus();
			}else{
				Dimension newDimension = getScaledDimension(new Dimension(img.getWidth(null), img.getHeight(null)), new Dimension(1230, 520));
				ImageIcon image = new ImageIcon(img.getScaledInstance((int)newDimension.getWidth(), (int)newDimension.getHeight(), Image.SCALE_SMOOTH));
				embedImage.setIcon(image);
				embedInfo.setText("    File is too large, please select a smaller one.");
				executeEmbedButton.setEnabled(false);
			}
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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


import javax.swing.SwingConstants;
import javax.swing.JLabel;

import java.awt.Color;

import java.awt.Font;


import java.awt.List;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.SystemColor;


public class GUI {
	public File sourceFile;
	public File destinationFile;
	
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
				
					GUI window = new GUI();
					window.frame.setVisible(true);
					window.frame.setTitle("NCT - Resources Reader Tool");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		sourceFile = null;
		destinationFile = null;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setForeground(Color.RED);
		frame.getContentPane().setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		
		
		final JLabel labelSourceFolder = new JLabel(". . . .");
		labelSourceFolder.setHorizontalAlignment(SwingConstants.LEFT);
		labelSourceFolder.setBounds(121, 11, 323, 16);
		frame.getContentPane().add(labelSourceFolder);
		
		final JLabel labelDestinationFolder = new JLabel(". . . .");
		labelDestinationFolder.setHorizontalAlignment(SwingConstants.LEFT);
		labelDestinationFolder.setBounds(121, 47, 323, 16);
		frame.getContentPane().add(labelDestinationFolder);
		
		final JLabel labelBuild = new JLabel("");
		labelBuild.setForeground(Color.RED);
		labelBuild.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		labelBuild.setBackground(Color.WHITE);
		labelBuild.setHorizontalAlignment(SwingConstants.LEFT);
		labelBuild.setBounds(121, 76, 323, 16);
		frame.getContentPane().add(labelBuild);
		
		final List list = new List();
		list.setForeground(Color.GRAY);
		list.setFont(new Font("Dialog", Font.ITALIC, 12));
		list.setBounds(10, 106, 580, 298);
		
		JButton btnNewButton = new JButton("Open File");
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setBounds(6, 6, 103, 29);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				JFileChooser object = new JFileChooser();
				object.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (object.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					int countFiles = ReadResourceFiles.hasAnyAppriciateFile(object.getSelectedFile());
					if (countFiles > 0){
						sourceFile = object.getSelectedFile();
						labelSourceFolder.setForeground(Color.BLACK);
						labelSourceFolder.setText(sourceFile.getPath());
					}else{
						labelSourceFolder.setForeground(Color.RED);
						labelSourceFolder.setText("Erro: Can't find any .png or .plist files");
					}
				}
			}
		});
		
		
		JButton btnNewButton_1 = new JButton("Destination");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser object = new JFileChooser();
				object.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (object.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					destinationFile = object.getSelectedFile();
					labelDestinationFolder.setText(destinationFile.getPath());
				}
			}
		});
		btnNewButton_1.setBounds(6, 42, 103, 29);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Build");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sourceFile == null){
					labelBuild.setText("Error: No file selected yet !");
				}else if (destinationFile == null){
					labelBuild.setText("Error: No destination folder selected yet !");
				}
				else{
					list.removeAll();
					try {
						ReadResourceFiles.Create(sourceFile,destinationFile.getPath(), list);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					labelBuild.setText("File successfully compiled !");
					
					sourceFile = null;
					destinationFile = null;
					labelDestinationFolder.setText("");
					labelSourceFolder.setText("");
				}
			}
		});
		btnNewButton_2.setBounds(6, 71, 103, 29);
		frame.getContentPane().add(btnNewButton_2);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnNewButton);
		
		
		
		
		frame.getContentPane().add(list);
		
		ImageIcon icon = createImageIcon("NCTlogo.png", "");
		
		JLabel lblLogo = new JLabel("");
		lblLogo.setForeground(new Color(0, 0, 0));
		lblLogo.setIcon(icon);
		lblLogo.setBounds(6, 410, 128, 62);
		frame.getContentPane().add(lblLogo);
		
		JLabel lblNewLabel = new JLabel("Resources Reader Tool");
		lblNewLabel.setFont(new Font("Kohinoor Bangla", Font.BOLD, 14));
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setBounds(110, 428, 179, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Version 1.0");
		lblNewLabel_1.setForeground(SystemColor.controlHighlight);
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		lblNewLabel_1.setBounds(110, 445, 157, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
	}
}

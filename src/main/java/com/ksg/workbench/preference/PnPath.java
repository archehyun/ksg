package com.ksg.workbench.preference;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;

public class PnPath extends JPanel implements PreferencePn,ActionListener{
	private JTextField txfXLSFolder;
	private JTextField txfSaveFolder;
	private Font defaultfont;
	PreferenceDialog preferenceDialog;
	private JButton butSearcFold;
	protected Logger logger = LogManager.getLogger(this.getClass());
	private KSGPropertis propertis = KSGPropertis.getIntance();
	public PnPath(PreferenceDialog preferenceDialog) {
		this.setName("경로지정");
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		Box box = new Box(BoxLayout.Y_AXIS);
		this.preferenceDialog = preferenceDialog; 
		txfXLSFolder = new JTextField(30);
		txfSaveFolder = new JTextField(25);
		butSearcFold = new JButton("찾기");
		defaultfont = KSGModelManager.getInstance().defaultFont;

		JPanel pnXTG = new JPanel();
		pnXTG.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnXTG.add(new JLabel("XTG 저장 폴더 이름: "));
		pnXTG.add(txfSaveFolder);
		JButton butSearcFold = new JButton("찾기");
		butSearcFold.setFont(defaultfont);

		butSearcFold.addActionListener(this);
		pnXTG.add(butSearcFold);
		
		
		JPanel pnXLS = new JPanel();
		pnXLS.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnXLS.add(new JLabel("XLS 파일 폴더 이름: "));
		pnXLS.add(txfXLSFolder);
		butSearcFold.setFont(defaultfont);	

		box.add(createComp(pnXTG));
		box.add(createComp(pnXLS));



		this.add(box);
		txfXLSFolder.setText(propertis.getProperty(KSGPropertis.DATA_LOCATION));
		txfSaveFolder.setText(propertis.getProperty(KSGPropertis.SAVE_LOCATION));

	}
	private Component createComp(Component comp)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnMain.add(comp);
		return pnMain;
	}

	public void saveAction() {
		logger.debug("path saveaction:");
		propertis.setProperty(KSGPropertis.SAVE_LOCATION, txfSaveFolder.getText());
		propertis.setProperty(KSGPropertis.DATA_LOCATION, txfXLSFolder.getText());
	}
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(propertis.getProperty(KSGPropertis.SAVE_LOCATION));

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = fileChooser.getSelectedFile();
			if(selectedFile.isDirectory())
			{
				txfSaveFolder.setText(selectedFile.getAbsolutePath());	
			}
			else if(selectedFile.isFile())
			{
				txfSaveFolder.setText(selectedFile.getParent());
			}
		}
		
	}


}

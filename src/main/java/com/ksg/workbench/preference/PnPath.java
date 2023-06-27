package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.view.comp.panel.KSGPanel;

public class PnPath extends PnOption{
	private JTextField txfXLSFolder;
	private JTextField txfSaveFolder;
	private Font defaultfont;	
	private JButton butSearcFold;
	
	private KSGPropertis propertis = KSGPropertis.getIntance();
	
	public PnPath(PreferenceDialog preferenceDialog) {
		super(preferenceDialog);
		
		this.setName("경로지정");
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.addComponentListener(this);

		this.add(buildCenter());
		
		

	}
	
	private KSGPanel buildCenter()
	{
		
		Box pnBox = new Box(BoxLayout.Y_AXIS);
		 
		txfXLSFolder = new JTextField(30);
		
		txfSaveFolder = new JTextField(25);
		
		butSearcFold = new JButton("찾기");
		
		defaultfont = KSGModelManager.getInstance().defaultFont;

		KSGPanel pnXTG = new KSGPanel();
		
		pnXTG.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnXTG.add(new JLabel("XTG 저장 폴더 이름: "));
		
		pnXTG.add(txfSaveFolder);
		
		JButton butSearcFold = new JButton("찾기");
		
		butSearcFold.setFont(defaultfont);

		butSearcFold.addActionListener(this);
		
		pnXTG.add(butSearcFold);
		
		
		KSGPanel pnXLS = new KSGPanel();
		
		pnXLS.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnXLS.add(new JLabel("XLS 파일 폴더 이름: "));
		
		pnXLS.add(txfXLSFolder);
		
		butSearcFold.setFont(defaultfont);	

		pnBox.add(createComp(pnXTG));
		
		pnBox.add(createComp(pnXLS));
		
		KSGPanel pnMain=new KSGPanel(new BorderLayout());
		
		pnMain.add(pnBox);
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,15, 5,5));
		return pnMain;

		
	}
	private Component createComp(Component comp)
	{
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnMain.add(comp);
		return pnMain;
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
		txfXLSFolder.setText(propertis.getProperty(KSGPropertis.DATA_LOCATION));
		txfSaveFolder.setText(propertis.getProperty(KSGPropertis.SAVE_LOCATION));
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

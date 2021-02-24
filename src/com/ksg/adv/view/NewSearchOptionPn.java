package com.ksg.adv.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.view.comp.SheetModel;
import com.ksg.adv.view.dialog.ViewXLSFileDialog;
import com.ksg.commands.SearchSheetNameCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.view.comp.FileInfo;

/**
 * @author 박창현
 *
 */
public class NewSearchOptionPn extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private KSGPropertis 	propertis = KSGPropertis.getIntance();

	class SimpleFileFilter extends FileFilter {

		String description;

		String[] extensions;

		public SimpleFileFilter(String ext) {
			this(new String[] { ext }, null);
		}

		public SimpleFileFilter(String[] exts, String descr) {
			// Clone and lowercase the extensions
			extensions = new String[exts.length];
			for (int i = exts.length - 1; i >= 0; i--) {
				extensions[i] = exts[i].toLowerCase();
			}
			// Make sure we have a valid (if simplistic) description
			description = (descr == null ? exts[0] + " files" : descr);
		}

		public boolean accept(File f) {
			// We always allow directories, regardless of their extension
			if (f.isDirectory()) {
				return true;
			}

			// Ok, it's a regular file, so check the extension
			String name = f.getName().toLowerCase();
			for (int i = extensions.length - 1; i >= 0; i--) {
				if (name.endsWith(extensions[i])) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return description;
		}
	}
	private JTable _tblSearchList;
	
	private JTable			_tblSheetNameList2;


	private JComboBox cbxSearchType;


	private JComboBox cbxSelectedInput;


	private ButtonGroup bgKeyword;


	private JRadioButton butVoyageOpt;


	private JRadioButton butVesselOpt;
	
	private String 			selectXLSFilePath;


	private JList fileLi;
	private static final String SEARCH_TYPE_COMPANY = "선사";
	private static final String SEARCH_TYPE_PAGE = "페이지";
	public NewSearchOptionPn()
	{
		this.setLayout(new BorderLayout());
		Box pnMain = Box.createVerticalBox();
		_tblSearchList = new JTable();
		cbxSearchType = new JComboBox();
		cbxSearchType.addItem(SEARCH_TYPE_COMPANY);
		cbxSearchType.addItem(SEARCH_TYPE_PAGE);
		cbxSearchType.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				/*selectLay2.show(pnSubSearch, e.getItem().toString());
				searchType= e.getItem().toString();
*/
			}
		});
		JLabel lblSearch= new JLabel();
		lblSearch.setText("검색 형식 : ");
		
		
		JPanel pnP1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnP1.add(lblSearch);
		pnP1.add(cbxSearchType);
		
		JPanel pnSelectType = new JPanel();
		pnSelectType.setLayout(new FlowLayout(FlowLayout.LEFT));
		cbxSelectedInput = new JComboBox();
		cbxSelectedInput.addItem("File");
		cbxSelectedInput.addItem("Text");
		cbxSelectedInput.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
			/*	selectLay.show(pnSubSelect, e.getItem().toString());
				selectedInput = e.getItem().toString();

				manager.selectedInput=selectedInput ;*/
			}
		});
		JLabel  lbl = new JLabel("입력 형식 : ");
//		lbl.setFont(defaultFont);

		pnSelectType.add(lbl);
		pnSelectType.add(cbxSelectedInput);
		
		
		JPanel pnImportBut = new JPanel(new GridLayout(0,1));

		JButton butImportFile = new JButton("\n불러오기(V)",new ImageIcon("images/importxls.gif"));
		butImportFile.setMnemonic(KeyEvent.VK_V);

		butImportFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

			}});
		JButton butSheetSelect = new JButton("Sheet 선택");
		butSheetSelect.setFont(KSGModelManager.getInstance().defaultFont);
		butSheetSelect.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

			/*	SheetSelectDialog dialog = new SheetSelectDialog(_tblSheetNameList);
				dialog.createAndUpdateUI();*/

			}});
		pnImportBut.add(butImportFile);
		pnImportBut.add(butSheetSelect);
		
		JPanel pnKeyType = new JPanel(new GridLayout(0,1));

		JPanel pnType = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		bgKeyword = new ButtonGroup();
		
		butVesselOpt = new JRadioButton("Vessel",true);
		butVoyageOpt = new JRadioButton("Voyage");
		
		bgKeyword.add(butVesselOpt);
		bgKeyword.add(butVoyageOpt);
		
		pnKeyType.add(new JLabel("Key word 형식"));
		pnKeyType.add(butVesselOpt);
		pnKeyType.add(butVoyageOpt);

		pnType.add(pnKeyType);
		pnType.add(pnImportBut);
		

		
		JPanel pnSelectInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnSelectInfo.add(buildFileListPn());
		pnSelectInfo.add(pnType);
		
		
		pnMain.add(pnP1);
		pnMain.add(new JScrollPane(_tblSearchList));
		pnMain.add(pnSelectType);
		
		pnMain.add(pnSelectInfo);
		
		
		this.add(pnMain);
		
		
		
	}
	private JPanel buildFileListPn() {
		JPanel pnSubControlInfo1 = new JPanel();
		pnSubControlInfo1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel lblFileName = new JLabel("파일 명 : ");
		lblFileName.setIcon(new ImageIcon("images/xlslogo.png"));
		pnSubControlInfo1.add(lblFileName);



		JButton butFile = new JButton("추가(A)");
		butFile.setMnemonic(KeyEvent.VK_A);
		butFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				fileAddAction(fileLi,_tblSheetNameList2);
			}

		});

		JPanel pnButList = new JPanel();
		pnButList.setPreferredSize(new Dimension(275,25));
		pnButList.setLayout(new GridLayout(1,0));

		pnButList.add(butFile);
		JButton butDel = new JButton("삭제(D)");
		butDel.setMnemonic(KeyEvent.VK_D);
		butDel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				fileDelAction(fileLi,_tblSheetNameList2);

			}});
		pnButList.add(butDel);
		JButton butUp = new JButton("위로");
		butUp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileUPAction(fileLi);

			}

		});
		pnButList.add(butUp);

		JButton butDown = new JButton("아래로");
		butDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				fileDownAction(fileLi);

			}
		});
//		butDown.setFont(defaultFont);
		pnButList.add(butDown);

		JPanel pnFile = new JPanel();
		pnFile.setLayout(new BorderLayout());
		fileLi = new JList();
		fileLi.setComponentPopupMenu(createXLSListPopup());
//		fileLi.setFont(defaultFont);

		fileLi.setModel(new DefaultListModel());
		JScrollPane scrollPane = new JScrollPane(fileLi);
		scrollPane.setPreferredSize(new Dimension(150,50));
		pnFile.add(scrollPane);

		pnFile.add(pnButList,BorderLayout.SOUTH);

		pnSubControlInfo1.add(pnFile);
		return pnSubControlInfo1;
	}
	private void fileDelAction(JList fileLi,JTable table) {
		Object s[]=fileLi.getSelectedValues();

		if(s==null||s.length<1)
			return;

		DefaultListModel model = (DefaultListModel) fileLi.getModel();
		for(int i=0;i<s.length;i++)		
			model.removeElement(s[i]);

		updateSheetNameList(fileLi,table);
	}

	private void fileDownAction(JList fileLi) {
		DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();
		int selectedIndex=fileLi.getSelectedIndex();
		if(selectedIndex>filemodel.getSize()-2)
			return;

		Object tempobj=filemodel.getElementAt(selectedIndex+1);
		Object obj = filemodel.getElementAt(selectedIndex);
		filemodel.setElementAt(obj, selectedIndex+1);
		filemodel.setElementAt(tempobj, selectedIndex);
		fileLi.setSelectedIndex( selectedIndex+1);
		updateSheetNameList(fileLi, _tblSheetNameList2);
	}

	private void fileUPAction(JList fileLi) {
		DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();
		int selectedIndex=fileLi.getSelectedIndex();
		if(selectedIndex<1)
			return;

		Object tempobj=filemodel.getElementAt(selectedIndex-1);
		Object obj = filemodel.getElementAt(selectedIndex);
		filemodel.setElementAt(obj, selectedIndex-1);
		filemodel.setElementAt(tempobj, selectedIndex);
		fileLi.setSelectedIndex( selectedIndex-1);
		updateSheetNameList(fileLi, _tblSheetNameList2);
	}
	private void updateSheetNameList(JList fileLi, JTable _tblSheetNameList) {


		DefaultListModel filemodel = (DefaultListModel) fileLi.getModel();
		SearchSheetNameCommand comm = new SearchSheetNameCommand(filemodel);
		comm.execute();

		List temp = comm.sheetNameList;


		if(temp.size()>=0)
		{
			Object data[][] =new Object[temp.size()][];
			for(int j=0;j<temp.size();j++)
			{
				SheetInfo info=(SheetInfo) temp.get(j);
				data[j]=new Object[]{info.filePath,info.file,info.sheetName,Boolean.FALSE};
			}
			TableModel sheelModel = new SheetModel(data);
			_tblSheetNameList.setModel(sheelModel);
		}
	}
	private JPopupMenu createXLSListPopup() {
		JPopupMenu menu =  new JPopupMenu();
		JMenuItem viewMenu = new JMenuItem("보기");
		viewMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				FileInfo info = (FileInfo) fileLi.getSelectedValue();

				if(info==null)
					return;
				ViewXLSFileDialog dialog = new ViewXLSFileDialog(info);
				dialog.createAndUpdateUI();



			}});

		menu.add(viewMenu);
		return menu;
	}
	private void fileAddAction(JList fileLi,JTable table) {
		JFileChooser fileChooser = new JFileChooser(propertis.getProperty("dataLocation"));
		fileChooser.setMultiSelectionEnabled(true);
		String[] pics = new String[] { ".xls"};

		fileChooser.addChoosableFileFilter(new SimpleFileFilter(pics,"Excel(*.xls)"));


		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File[] selectedFiles = fileChooser.getSelectedFiles();

			//_txfXLSFile.setText(selectedFile.getName());

			for(int i=0;i<selectedFiles.length;i++)
			{
				DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();

				for(int j=0;j<filemodel.size();j++)
				{
					FileInfo t = (FileInfo) filemodel.get(j);
					if(t.file.equals(selectedFiles[i].getName()))
					{
						JOptionPane.showMessageDialog(null, "동일 항목이 존재합니다.");
						return;
					}
				}
				FileInfo fileInfo= new FileInfo();
				fileInfo.file = selectedFiles[i].getName();
				fileInfo.filePath = selectedFiles[i].getAbsolutePath();
				KSGPropertis pp=KSGPropertis.getIntance();
				// 위치 저장
				pp.setProperty(KSGPropertis.DATA_LOCATION, selectedFiles[i].getParent());
				pp.store();


				filemodel.addElement(fileInfo);

				selectXLSFilePath = selectedFiles[i].getAbsolutePath();


				updateSheetNameList(fileLi,table);
			}
		}
	}


	
	
	

}


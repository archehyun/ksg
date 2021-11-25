package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.commands.SearchSheetNameCommand;
import com.ksg.commands.ViewXLSFileCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.FileInfo;
import com.ksg.view.comp.dialog.KSGDialog;

public class ViewXLSFileDialog extends KSGDialog {
	/**
	 * 
	 */
	DAOManager manager = DAOManager.getInstance();
	private static final long serialVersionUID = 1L;
	FileInfo info;
	private String[] vesselKeyWord;
	private String[] bothKeyWord;
	public ViewXLSFileDialog(FileInfo info) {
		this.info =info;
		try {
			baseService=manager.createBaseService();
			vesselKeyList = baseService.getKeywordList("VESSEL");
			voyageKeyList = baseService.getKeywordList("VOYAGE");
			bothKeyList = baseService.getKeywordList("BOTH");
			vesselKeyWord = new String[vesselKeyList.size()];
			bothKeyWord = new String[bothKeyList.size()];

			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private JTable table;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private List vesselKeyList;
	private List voyageKeyList;
	private List bothKeyList;
	public void createAndUpdateUI() {
		this.setTitle("¿¢¼¿ º¸±â");

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		

		JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox box =(JComboBox) e.getSource();

				Object info= box.getSelectedItem();

				if(info==null)
					return;
				if(info instanceof SheetInfo){

					ViewXLSFileCommand command = new ViewXLSFileCommand((SheetInfo) info,table);
					command.execute();
					if(command.model==null)
						return;

					if(table==null)
						return;

					table.setModel(command.model);
					
					
					TableColumnModel colmodel =table.getColumnModel();
					
					
					
					
					for(int i=0;i<colmodel.getColumnCount();i++)
					{

						TableColumn namecol = colmodel.getColumn(i);

						DefaultTableCellRenderer renderer = new VesselRenderer();
						namecol.setCellRenderer(renderer);
					}
					table.updateUI();
					logger.debug(table.getModel().getRowCount());
				}
				
			}});
		comboBox.addItem("¼±ÅÃ");
	


		SearchSheetNameCommand command = new SearchSheetNameCommand(info);
		command.execute();
		List li=command.sheetNameList;
		for(int i=0;i<li.size();i++)
		{
			SheetInfo info = (SheetInfo) li.get(i);

			comboBox.addItem(info);
		}
		pnMain.add(new JLabel("¿¢¼¿ ½¬Æ® ¸í"));
		pnMain.add(comboBox);


		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		this.getContentPane().add(pnMain,BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(table));
		this.getContentPane().add(buildButtom(),BorderLayout.SOUTH);
		
		int w=KSGModelManager.getInstance().frame.getWidth();
		int h= KSGModelManager.getInstance().frame.getHeight();

		this.setSize(w-60,h-60);
		ViewUtil.center(this, false);
		this.setVisible(true);


	}
	private Component buildButtom() 
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JButton butCancel = new JButton("´Ý±â");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				ViewXLSFileDialog.this.setVisible(false);
				ViewXLSFileDialog.this.dispose();
			}});
		pnMain.add(butCancel);
		
		JPanel pnLeft = new JPanel();
		JCheckBox cbxScroll = new JCheckBox("Scroll",true);
		cbxScroll.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				JCheckBox boc = (JCheckBox) e.getSource();
				if(boc.isSelected())
				{
					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					
				}else
				{
					table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				}
				
			}});
		pnLeft.add(cbxScroll);
		
		JPanel pnRight =new JPanel();
		pnRight.add(butCancel);
		pnMain.add(pnRight, BorderLayout.EAST);
		pnMain.add(pnLeft, BorderLayout.WEST);
		
		
		return pnMain;
	}
	class VesselRenderer extends DefaultTableCellRenderer
	{
		public void setValue(Object value)
		{
			
			if(value==null)
				return;
			setText(value.toString());
			
			for(int z=0;z<vesselKeyWord.length;z++)
			{
				if(value.toString().trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
				{
					setForeground(Color.blue);
					setFont(new Font("aria",Font.BOLD,12));
					
					return;
				}
			}
			for(int z=0;z<bothKeyWord.length;z++)
			{
				if(value.toString().trim().equals(bothKeyWord[z].trim()))
				{
					setForeground(Color.blue);
					setFont(new Font("aria",Font.BOLD,12));
					return;
				}
			}
			setForeground(table.getForeground());
			
		}


	}

}


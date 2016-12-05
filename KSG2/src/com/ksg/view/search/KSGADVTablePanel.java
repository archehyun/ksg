/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.view.search;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.commands.InsertADVCommand;
import com.ksg.commands.KSGCommand;
import com.ksg.commands.SearchADVCommand;
import com.ksg.dao.DAOManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.model.KSGModelManager;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.comp.KSGPanel;
import com.ksg.view.search.dialog.ManagePortDialog;
import com.ksg.view.search.dialog.ManageVesselDialog;
import com.ksg.view.search.dialog.SearchAndInsertVesselDialog;
import com.ksg.view.search.dialog.SearchVesselDialog;
import com.ksg.view.util.KSGDateUtil;

/**
 * 
 * @관련Command SearchADVCommand
 * @author hrchoi
 *
 */
public class KSGADVTablePanel extends KSGPanel implements ActionListener,KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTable				_tblADVTable;

	private DAOManager daoManager;
	public boolean motherVessel=false;
	private ShippersTable selectedTable;

	private SearchUI base;
	

	private JTextField txfImportDate;

	public JTable _tblVesselTable;
	
	private DefaultTableModel vesselModel;

	private ManageVesselDialog vesseldialog;
	public KSGADVTablePanel(SearchUI base) {
		this.base=base;
		
		daoManager =DAOManager.getInstance();
		
		_advService= daoManager.createADVService();
		
		_baseSearvice = daoManager.createBaseService();
		
		_tableService = daoManager.createTableService();
		this.createAndUpdateUI();
	}
	public String getStringValue(Object value)
	{
		if(value==null||value.equals("null"))		
			return "";
		else			
			return String.valueOf(value);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if(command.equals("항구 관리"))
		{
			KSGDialog dialog = new ManagePortDialog(KSGModelManager.getInstance().selectedTable_id,base);
			dialog.createAndUpdateUI();
			int result = dialog.OPTION;
			if(result == ManagePortDialog.UPDATE_OPTION)
			{
				SearchADVCommand advCommand = new SearchADVCommand(this);
				advCommand.execute();
				this.updateUI();
			}
		}
		if(command.equals("선박 관리"))
		{
			vesseldialog = new ManageVesselDialog(KSGModelManager.getInstance().selectedTable_id,base,vesselModel);
			vesseldialog.createAndUpdateUI();
		}
		else if(command.equals("전체 삭제"))
		{
			{
				DefaultTableModel model=(DefaultTableModel) _tblADVTable.getModel();
				int row = model.getRowCount();
				int col = model.getColumnCount();
				for(int i=0;i<row;i++)
				{
					for(int j=0;j<col;j++)
					model.setValueAt("", i, j);
				}
				_tblADVTable.setModel(model);
				_tblADVTable.updateUI();

			}
		}
	}

	public void applyADVTable()
	{
		DefaultTableModel dmodel=(DefaultTableModel) _tblADVTable.getModel();

		Element rootElement = new Element("input");
		rootElement.setAttribute("type","xls");
		Element tableInfos = new Element("table");
		tableInfos.setAttribute("id",this.selectedTable.getTable_id());		
		rootElement.addContent(tableInfos);

		for(int i=0;i<dmodel.getRowCount();i++)
		{
			String vesselName =String.valueOf(dmodel.getValueAt(i, 0));

			if(vesselName==null||vesselName.equals("null")||vesselName.equals(""))
				continue;

			Element vesselInfo =new Element("vessel");
			vesselInfo.setAttribute("name", vesselName);
			try{
			vesselInfo.setAttribute("full-name",(String) vesselModel.getValueAt(i, 0));
			}catch(IllegalDataException e)
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "전체 선박명이 등록되지 않은 선박명 약어가 있습니다.");
				return;
			}

			vesselInfo.setAttribute("voyage", this.getStringValue(String.valueOf(dmodel.getValueAt(i, 1))));


			// TS 인경우
			if(selectedTable.getGubun()!=null&&selectedTable.getGubun().equals("TS"))
			{	
				vesselInfo.setAttribute("ts_name", this.getStringValue(dmodel.getValueAt(i, 2)));
				vesselInfo.setAttribute("ts_voyage",this.getStringValue(dmodel.getValueAt(i, 3)));
			}
			// TS 인경우:4, 아닌 경우 2

			for(int j = selectedTable.getGubun()!=null&&selectedTable.getGubun().equals("TS")?4:2;j<dmodel.getColumnCount();j++)
			{
				Element inputInfo =new Element("input_date");
				inputInfo.setAttribute("index", String.valueOf(j));				

				inputInfo.setAttribute("date", this.getStringValue(dmodel.getValueAt(i, j)));
				vesselInfo.addContent(inputInfo);				
			}
			rootElement.addContent(vesselInfo);			
		}

		Document document = new Document(rootElement);
		Format format = Format.getPrettyFormat();

		format.setEncoding("EUC-KR");
		format.setIndent("\n");
		format.setIndent("\t");

		XMLOutputter outputter = new XMLOutputter(format);

		String data=outputter.outputString(document);
		logger.debug("adv data:\n"+data);
		ADVData dd=null;

		if(KSGModelManager.getInstance().selectedADVData!=null)
		{
			dd =KSGModelManager.getInstance().selectedADVData;	
			dd.setData(data);
		}else
		{
			dd =new ADVData();
			dd.setTable_id(this.selectedTable.getTable_id());
			dd.setCompany_abbr(this.selectedTable.getCompany_abbr());
			logger.debug("company_abbr:"+this.selectedTable.getCompany_abbr());
			dd.setData(data);
		}
		try
		{
			
			dd.setDate_isusse(KSGDateUtil.toDate2(txfImportDate.getText()));
			logger.debug("input date : "+KSGDateUtil.toDate2(txfImportDate.getText())+",company_abbr:"+dd.getCompany_abbr());
		} catch (ParseException e1) 
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
		}
		KSGCommand insert = new InsertADVCommand(dd);
		insert.execute();
		JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "광고 정보를 저장 했습니다.");
		try {
			base.showTableList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// 선박 명 자동 생성
	public void autoVesselWrite3(JTable table,int selectedVesselrow) {
		this.autoVesselWrite3(table, selectedVesselrow,0);
	}

	public void autoVesselWrite3(JTable table,int selectedVesselrow, int col) {

		if(selectedVesselrow==-1)
			return;		


		final Object value = table.getValueAt(selectedVesselrow, col);

		logger.debug("enter value:"+value+","+table.isEditing());
		try {

			if(value==null)
				return;
			if(String.valueOf(value).length()<=0)
				return;
			Vessel  op = new Vessel();
			op.setVessel_name(String.valueOf(value));
			
			List li=_baseSearvice.getVesselAbbrInfoByPatten(String.valueOf(value)+"%");
			if(li.size()==1)
			{
				Vessel vessel = (Vessel) li.get(0);
				String obj = vessel.getVessel_abbr().toUpperCase();
				
				setValue(table,obj, selectedVesselrow, col)	;
				vesselModel.setRowCount(vesselModel.getRowCount()+1);
				vesselModel.setValueAt(vessel.getVessel_name(), selectedVesselrow, 0);
				vesselModel.setValueAt(vessel.getVessel_abbr(), selectedVesselrow, 1);
				return;
				

			}else if(li.size()>1)
			{
				SearchVesselDialog searchVesselDialog = new SearchVesselDialog(li);
				searchVesselDialog.createAndUpdateUI();


				if(searchVesselDialog.result!=null)
				{
					setValue(table,searchVesselDialog.resultAbbr, selectedVesselrow, col);
					
					vesselModel.setRowCount(vesselModel.getRowCount()+1);
					vesselModel.setValueAt(searchVesselDialog.result, selectedVesselrow, 0);
					vesselModel.setValueAt(searchVesselDialog.resultAbbr, selectedVesselrow, 1);
					return;
					
				}else
				{
					setValue(table,null, selectedVesselrow, col);
					return;
				}

			}else
			{
				int result=JOptionPane.showConfirmDialog(null, "해당 선박명이 없습니다. 추가 하시겠습니까?", "선박명 추가", JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION)
				{
					SearchAndInsertVesselDialog dialog = new SearchAndInsertVesselDialog(this,table,selectedVesselrow,col,value.toString(),vesselModel );
					dialog .createAndUpdateUI();
					
				}else
				{
					setValue(table, null, selectedVesselrow, col);
					logger.debug("select no option:"+selectedVesselrow);
					return;
				}
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
			e1.printStackTrace();
		}
	}

	public void setValue(JTable table, String obj, int selectedVesselrow,int col) {
		DefaultTableModel model=(DefaultTableModel) table.getModel();
		TableModelListener s[]=model.getTableModelListeners();

		for(int i=0;i<s.length;i++)
		{
			model.removeTableModelListener(s[i]);
		}
		if(obj==null)
		{
			table.setValueAt(obj, selectedVesselrow, col);	
		}
		else
		{
			table.setValueAt(obj.toUpperCase(), selectedVesselrow, col);
		}
		
		table.changeSelection(selectedVesselrow, col, false, false);
		model.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e) {
				if(e.getColumn()==0)
				{
					KSGADVTablePanel.this.autoVesselWrite3(_tblADVTable, e.getFirstRow());
				}
				if(KSGADVTablePanel.this.getSelectedTable().getGubun()!=null&&KSGADVTablePanel.this.getSelectedTable().getGubun().equals("TS")&&e.getColumn()==2)
				{
					KSGADVTablePanel.this.autoVesselWrite3(_tblADVTable, e.getFirstRow(),2);
				}					
			}});
	}

	/* (non-Javadoc)
	 * @see com.ksg.view.comp.KSGPanel#createAndUpdateUI()
	 */
	public void createAndUpdateUI() {
		this.setName("KSGADVTablePanel");
		_tblADVTable = new JTable();
		Font defaultFont=_tblADVTable.getFont();
		
		_tblADVTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tblADVTable.addKeyListener(this);

		_tblADVTable.setName("advTable");
		_tblADVTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);


		this.setLayout(new BorderLayout());
		JScrollPane jScrollPane = new JScrollPane(_tblADVTable);


		JPanel pnCenter = new JPanel();
		pnCenter.setLayout(new BorderLayout());
		pnCenter.add(jScrollPane);


		JPanel pnADVControl = new JPanel();
		pnADVControl.setLayout(new BorderLayout());

		JButton butPortList = new JButton("항구 관리(P)");
		butPortList.setActionCommand("항구 관리");
		butPortList.setMnemonic(KeyEvent.VK_P);
		butPortList.addActionListener(this);
		JButton butVesselList = new JButton("선박 관리");
		butVesselList.setActionCommand("선박 관리");
		butVesselList.addActionListener(this);
		
		JButton butDel = new JButton("전체 삭제(D)");
		butDel.setActionCommand("전체 삭제");
		butDel.setMnemonic(KeyEvent.VK_D);
		butDel.addActionListener(this);
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 50, 150, 75);
		slider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent item) {
				JSlider op = (JSlider) item.getSource();
				int value=op.getValue();
				KSGViewParameter.TABLE_COLUM_WIDTH= value;
				SearchADVCommand searchADVCommand = new SearchADVCommand(selectedTable,KSGADVTablePanel.this);
				searchADVCommand.execute();
				
				
			}});
		slider.setMinorTickSpacing(15);
	    slider.setMajorTickSpacing(30);
	    slider.setPaintTicks(true);
	    slider.setPaintLabels(true);

	    // We'll just use the standard numeric labels for now...
	    slider.setLabelTable(slider.createStandardLabels(15));


		JPanel pn1 = new JPanel();
		pn1.add(butPortList);
		pn1.add(butVesselList);
		pn1.add(butDel);
		pn1.add(slider);
		
		
		pnADVControl.add(pn1,BorderLayout.WEST);

		JPanel pn2 = new JPanel();
		JLabel lblDate = new JLabel(" 입력날짜 : ");
		lblDate.setFont(defaultFont);

		txfImportDate = new JTextField(8);
		txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
		JCheckBox cbxImportDate = new JCheckBox("월요일",true);
		cbxImportDate.setFont(defaultFont);
		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
				}
			}});

		JButton butSave = new JButton("저장(S)");
		
		
		butSave.setMnemonic(KeyEvent.VK_S);
		butSave.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				try
				{
				applyADVTable();
				
								
				}catch(Exception ee)
				{
					ee.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, ee.getMessage());
				}

			}});
		
		JButton butCancel = new JButton("취소(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try 
				{
					base.showTableList();
				} catch (SQLException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		pn2.add(lblDate);
		pn2.add(txfImportDate);
		pn2.add(cbxImportDate);
		pn2.add(butSave);
		pn2.add(butCancel);

		pnADVControl.add(pn2,BorderLayout.EAST);
		pnCenter.add(pnADVControl,BorderLayout.SOUTH);

		this.add(pnCenter,BorderLayout.CENTER);
		this.add(pnADVControl,BorderLayout.SOUTH);
	}
	public ShippersTable getSelectedTable() {
		return selectedTable;
	}
	

	public void keyPressed(KeyEvent e) {				
		
	}

	public void keyReleased(KeyEvent e) {
		JTable table = (JTable) e.getSource();


		if(table.equals(_tblADVTable))
		{
			int row=table.getSelectedRow();
			if(row==-1)
				return;

			int col = table.getSelectedColumn();
			if(col==-1)
				return;
			
			
			if(e.getKeyCode()==KeyEvent.VK_TAB)
			{
				int cl=table.getSelectedColumn();
				int ro = table.getSelectedRow();
				int colCount=table.getColumnCount();
				if(cl==colCount-1)
				{
					table.changeSelection(ro+1, 0, false, false);
				}
			}

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				row=row-1;
				if(row==-1)
					row=0;
			}else
			{
				col=col-1;
				if(col==-1)
					col=0;
			}
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void setSelectedTable(ShippersTable st) {
		selectedTable =st;

	}
	public void update(KSGModelManager manager){}



	public void searchADVTable() {
		base.searchADVTable();

	}
	
	public void setVesseleModel(DefaultTableModel vesselmodel2) {
		this.vesselModel = vesselmodel2;
	}

}

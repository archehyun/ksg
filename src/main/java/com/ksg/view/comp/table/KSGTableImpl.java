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
package com.ksg.view.comp.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.VesselService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.admin.KSGViewParameter;
import com.ksg.workbench.adv.comp.ADVTableData;
import com.ksg.workbench.adv.xls.XLSTableInfo;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * @author 박창현
 *
 */
public class KSGTableImpl extends KSGTable implements KeyListener{
	
	/**
	 * @author 박창현
	 *
	 */
	class CellRenderer extends DefaultTableCellRenderer 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CellRenderer() {
			setHorizontalAlignment(JLabel.CENTER);
		}
		public void setValue(Object value)
		{
			if(value instanceof ADVTableData)
			{
				ADVTableData lbl =(ADVTableData) value;
				setText(lbl.toString());


			}else
			{
				super.setValue(value);

			}
		}
	}
	/**
	 * @author 박창현
	 *
	 */
	class MyTableCellEditor  extends DefaultCellEditor {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JTextField editor;
		
		public MyTableCellEditor() {
			super(new JTextField());
			// TODO Auto-generated constructor stub
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
				int row, int column) {
			editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);
			if (value != null)
			{
				editor.setText(value.toString());
				editor.selectAll();
				
			}
			if (column == 0) {
				editor.setHorizontalAlignment(SwingConstants.CENTER);
				editor.setFont(new Font("Serif", Font.BOLD, 14));
			} else {
				editor.setHorizontalAlignment(SwingConstants.RIGHT);
				editor.setFont(new Font("Serif", Font.ITALIC, 12));
			}
			return editor;
		}
		public Object getCellEditorValue() {
			if(editor==null)
				return null;
			ADVTableData data= new ADVTableData(editor.getText());
			return data;
		}
	}
	/**
	 * @author 박창현
	 *
	 */
	class VesselRenderer extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public VesselRenderer() {
		}
		public void setValue(Object value)
		{
			if(value instanceof ADVTableData)
			{
				ADVTableData lbl = (ADVTableData) value;

				if(!lbl.isVessel)			
				{
					setForeground(Color.red);
				}else
				{
					setForeground(Color.black);
				}
				setText(lbl.toString());

			}else
			{
				super.setValue(value);

			}
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static void updateColum(int type,JTable table) {

		TableColumnModel colmodel =table.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{

			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(SwingConstants.CENTER);
			namecol.setCellRenderer(renderer);
		}
		TableColumn co=colmodel.getColumn(0);
		co.setMinWidth(300);
	}

	private BaseService baseService;
	
	private VesselServiceV2 vesselService;
	
	
	DAOManager manager;
	
	private ADVService service;
	
	private int table_type;	

	private int tableIndex;

	private String tableName;
	
	private String updatedData;

	public KSGTableImpl(int type) {
		super();

		manager = DAOManager.getInstance();
		service= manager.createADVService();
		
		vesselService = new VesselServiceImpl();
		baseService = manager.createBaseService();
		this.table_type =type;
		this.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		this.addKeyListener(this);
	}
	private void autoVesselWrite(final JTable table, int KEYCODE) {
		logger.debug("auto create vessel");

		final int row1=table.getSelectedRow();

		if(row1==-1)
			return;

		final int col2 = table.getSelectedColumn();
		if(col2>0&&KEYCODE==KeyEvent.VK_ENTER)
			return;
		if(col2-1>0&&KEYCODE==KeyEvent.VK_TAB)
			return;

		final Object value = table.getColumnModel().getColumn(0).getCellEditor().getCellEditorValue();
		try {

			if(value==null)
				return;
			if(String.valueOf(value).length()<=0)
				return;

			logger.debug("search vessel :"+value);
			
			Vessel  op = new Vessel();
			op.setVessel_name(String.valueOf(value));
			Vessel oriVessel=baseService.getVesselInfo(op);
			if(oriVessel!=null)
			{
				table.setValueAt(oriVessel.getVessel_name().toUpperCase(), row1, 0);
				return;
			}
			logger.debug("search vessel : "+value);
			List li=baseService.getVesselInfoByPatten(String.valueOf(value)+"%");
			if(li.size()==1)
			{
				Vessel vessel = (Vessel) li.get(0);

				table.setValueAt(vessel.getVessel_name().toUpperCase(), row1, 0);
			}else if(li.size()>1)
			{
				SelectedVesselDialog dialog = new SelectedVesselDialog(table, row1, col2, li);
				dialog.createAndUpdateUI();

			}else
			{

				int result=JOptionPane.showConfirmDialog(null, value+" 해당 선박명이 없습니다. 추가 하시겠습니까?", "선박명 추가", JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION)
				{
					final JDialog di = new JDialog(KSGModelManager.getInstance().frame);
					di.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e) {table.setValueAt(null, row1, 0);}
					});

					di.setTitle("선박명 추가");
					di.setModal(true);
					JPanel pnMain = new JPanel();

					final JTextField txf = new JTextField(20);
					JCheckBox cbx = new JCheckBox("수정");
					cbx.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) {
							JCheckBox box=(JCheckBox) e.getSource();
							txf.setEditable(box.isSelected());

						}});

					JLabel lbl = new JLabel("선박명: ");
					pnMain.add(lbl);
					pnMain.add(txf);
					txf.setText(String.valueOf(value));



					JPanel pnControl = new JPanel();
					JButton butOK = new JButton("확인");
					butOK.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) {
							Vessel vessel = new Vessel();
							vessel.setVessel_name(txf.getText());
							try {
								vesselService.insert(vessel);
								JOptionPane.showMessageDialog(null, "선박명: "+vessel.getVessel_name()+"이(가) 추가 되었습니다.");
								di.setVisible(false);
								di.dispose();
								table.setValueAt(vessel.getVessel_name().toUpperCase(), row1, 0);
							} catch (SQLException e1) 
							{
								if(e1.getErrorCode()==2627)
								{
									JOptionPane.showMessageDialog(null, "동일한 선박명이 존재합니다.");
									return;
								}else
								{
									JOptionPane.showMessageDialog(null, e1.getMessage());
									logger.debug(e1.getMessage());
									e1.printStackTrace();	
								}

							}


						}});
					JButton butCancel = new JButton("취소");
					butCancel.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) {
							table.setValueAt(null, row1, 0);
							di.setVisible(false);
							di.dispose();

						}});


					pnControl.add(butOK);
					pnControl.add(butCancel);


					di.getContentPane().add(pnMain);

					di.getContentPane().add(pnControl,BorderLayout.SOUTH);
					di.setSize(350, 130);
					ViewUtil.center(di, false);
					di.setResizable(false);
					di.setVisible(true);


				}else
				{
					table.setValueAt(null, row1, 0);
				}
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
			e1.printStackTrace();
		}
	}

	public String getCompName() {
		return tableName;
	}
	public int getTableIndex() {
		return tableIndex;
	}


	public void keyPressed(KeyEvent e){}


	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode()==KeyEvent.VK_ENTER||e.getKeyCode()==KeyEvent.VK_TAB)
		{
			autoVesselWrite(this,e.getKeyCode());
		}
	}
	public void keyTyped(KeyEvent e) {}


	public void setTableIndex(int tableIndex) 
	{
		this.tableIndex = tableIndex;
	}

	public void setUpdatedData(String text) {
		this.updatedData =text;
		this.updateTable(updatedData);
	}


	public void update(KSGModelManager manager) 
	{
		switch (table_type) {
		case KSGTable.TABLE_TYPE_ADV:
			updateADVInfo(manager);
			break;
		case KSGTable.TABLE_TYPE_ERROR:
			break;

		}
		this.updateUI();

	}
	private void updateADVInfo(KSGModelManager manager) {
		logger.debug(this.getCompName()+":update");
		if(updatedData==null)
		{
			try{
				Vector advDataList =manager.getTempXLSTableInfoList();
				if(advDataList!=null||advDataList.size()!=0){
					Vector columes = new Vector();
					Object dd[][] = new Object[advDataList.size()][1];

					for(int i=0;i<advDataList.size();i++)
					{	
						dd[i][0]=advDataList.get(i);
					}

					XLSTableInfo info = (XLSTableInfo) advDataList.get(this.tableIndex);
					//updateTable(info.getTableStringInfo());

				}
			}catch (Exception e) {
				JOptionPane.showMessageDialog(null, "KSG_Table update error:"+e.getMessage());
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}else
		{
			updateTable(updatedData);
		}
	}


	private void updateTable(String data)
	{
		logger.debug("update table data:"+data);
		
		
		/*String dateList[][]=service.createADVTableModel(data);
		
		if(dateList.length<=0)
			return;
		KSGADVTableData models = new KSGADVTableData();
		models.addColum(dateList[0]);

		for(int i=1;i<dateList.length;i++)
		{
			models.addData(dateList[i]);
		}
		this.setModel(models);


		TableColumnModel colmodel = getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);
			DefaultTableCellRenderer renderer=null;
			if(i>0)
			{
				namecol.setWidth(50);
				renderer = new CellRenderer();
			}
			if(i==0)
			{
				renderer =new VesselRenderer();

			}
			namecol.setCellRenderer(renderer);
			MyTableCellEditor cellEditor = new MyTableCellEditor();
			namecol.setCellEditor(cellEditor);

		}*/
	}
	class SelectedVesselDialog extends KSGDialog
	{
		JTable table;
		int row1,col2;
		List li;
		public SelectedVesselDialog(JTable table, int row,int col2,List li) {
			this.table =table;
			this.row1 = row;
			this.li=li;
			this.col2 =col2;
		}

		public void createAndUpdateUI() {
			setTitle("선박 선택");
			JPanel pnMain= new JPanel();
			getContentPane().add(pnMain);
			final JTable jTable = new JTable();
			jTable.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()>1)
					{
						int row=jTable.getSelectedRow();
						int col =jTable.getSelectedColumn();
						if(row==-1)
						{
							return;
						}
						Object obj = jTable.getValueAt(row, col);
						table.setValueAt(obj, row1, 0);
						setVisible(false);
						dispose();
					}

				}
			});
			jTable.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						int row=jTable.getSelectedRow();
						int col =jTable.getSelectedColumn();
						if(row==-1)
						{
							return;
						}
						Object obj = jTable.getValueAt(row, col);
						table.setValueAt(obj, row1, col2);
						setVisible(false);
						dispose();
					}
				}

				public void keyReleased(KeyEvent e) {}

				public void keyTyped(KeyEvent e) {}
			});

			DefaultTableModel model = new DefaultTableModel(){
				public boolean isCellEditable(int row, int column)
				{
					return false;
				}
			};
			model.addColumn("선박 명");

			for(int i=0;i<li.size();i++)
			{
				Vessel v=(Vessel) li.get(i);
				model.addRow(new Object[]{v.getVessel_name()});
			}
			jTable.setModel(model);

			getContentPane().add(new JScrollPane(jTable));
			JPanel pnContorl = new JPanel();
			JButton butOk = new JButton("확인");
			butOk.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int row=jTable.getSelectedRow();
					int col =jTable.getSelectedColumn();
					if(row==-1)
					{
						JOptionPane.showMessageDialog(null, "선택된 선박이 없습니다.");
						return;
					}
					Object obj = jTable.getValueAt(row, col);
					table.setValueAt(obj, row1, 0);
					setVisible(false);
					dispose();
				}
			});
			JButton butCancel = new JButton("취소");
			butCancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					table.setValueAt(null, row1, 0);
					setVisible(false);
					dispose();
				}
			});
			pnContorl.add(butOk);
			pnContorl.add(butCancel);

			getContentPane().add(pnContorl,BorderLayout.SOUTH);

			setSize(new Dimension(300,300));
			ViewUtil.center(this, false);
			setVisible(true);

		}

	}
	@Override
	public void retrive() throws SQLException {
		// TODO Auto-generated method stub
		
	}

}

package com.ksg.adv.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.ksg.adv.view.ADVListPanel;
import com.ksg.adv.view.xls.XLSTableInfo;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * @author pch
 *
 */
public class AdjestADVListDialog extends KSGDialog{

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private BaseService service = new BaseServiceImpl();
	class EditTableModel extends DefaultTableModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EditTableModel(String[] columnames, int size) {
			super(columnames, size);
		}


		public boolean isCellEditable(int row, int column) {
			switch (column) {
			case 0:

				return false;
			case 1:

				return false;

			default:
				return true;
			}
		}
	}
	private Vector<ShippersTable> tableInfoList;
	private JTable tblAdv;
	private KSGModelManager 		manager = KSGModelManager.getInstance();
	ADVListPanel base;
	public String shipper;
	public AdjestADVListDialog(ADVListPanel base,Vector<ShippersTable> tableInfoList) {
		this.base=base;
		this.tableInfoList=tableInfoList;

		logger.debug(this.tableInfoList);
		tableService = new TableServiceImpl();
	}

	JTable tblTable;
	private Vector advDataList;
	public void createAndUpdateUI() {
		advDataList = manager.getXLSTableInfoList();
		this.setTitle("테이블 위치 조정");
		tblTable =  new JTable();
		tblAdv = new JTable();
		JPanel pnMain = new JPanel();
		GridLayout gridLayout = new GridLayout(1,0);
		gridLayout.setHgap(10);
		pnMain.setLayout(gridLayout);
		List li = new LinkedList();

		String columnames[]={"Tabel ID","제목","페이지","순번"};		

		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column) {
				switch (column) {
				case 0:

					return false;
				case 1:

					return false;

				default:
					return true;
				}
			}
		};
		for(int i=0;i<columnames.length;i++)
		{
			model.addColumn(columnames[i]);
		}


		for(int i=0;i<tableInfoList.size();i++)
		{
			ShippersTable table=(ShippersTable) tableInfoList.get(i);
			try {
				ShippersTable shippersTable =  new ShippersTable();
				shippersTable.setPage(table.getPage());
				List<ShippersTable> li2 = tableService.getTableListByPage(shippersTable);
				int col=0;
				for(int j=0;j<li2.size();j++)
				{
					ShippersTable tableInfo=(ShippersTable) li2.get(j);

					model.addRow(new Object[]{	tableInfo.getTable_id(),
							tableInfo.getTitle(),
							tableInfo.getPage(),
							tableInfo.getTable_index()
					});
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}


			//index++;
		}
		tblTable.setModel(model);
		TableColumnModel colModel=tblTable.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(80);
		colModel.getColumn(2).setMaxWidth(55);
		colModel.getColumn(3).setMaxWidth(55);

		JPanel pnTblInfo = new JPanel();
		pnTblInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTblInfo.add(new JLabel("테이블 정보"));
		JPanel pnTbl = new JPanel();

		pnTbl.setLayout(new BorderLayout());
		pnTbl.add(pnTblInfo,BorderLayout.NORTH);
		
		pnTbl.add(new JScrollPane(tblTable));
		
		pnTbl.add(pnTblInfo,BorderLayout.NORTH);
		
		JPanel pnIndex =new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton pnButton = new JButton("테이블 인덱스 저장");
		pnButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				DefaultTableModel model= (DefaultTableModel) tblAdv.getModel();
				StringBuffer index =new StringBuffer();
				for(int i=0;i<model.getRowCount();i++)
				{
					index.append(model.getValueAt(i, 0));
					if(i<model.getRowCount())
						index.append(",");
				}
				Code code_info = new Code();
				code_info.setCode_name(shipper);
				try {
					Code searchCode =service.getCodeInfo(code_info);
					if(searchCode==null)
					{
						Code code = new Code();
						code.setCode_field(index.toString());
						code.setCode_name(shipper);
						code.setCode_type("table_index");
						code.setCode_name_kor("-");
						service.insertCode(code);
					}else
					{
						Code code = new Code();
						code.setCode_field(index.toString());
						code.setCode_name(shipper);
						code.setCode_type("table_index");
						service.updateCode(code);
					}
				}catch (Exception ee) {
					ee.printStackTrace();
				}	
				
			}});
		pnIndex.add(pnButton);
		
		pnTbl.add(pnIndex,BorderLayout.SOUTH);
		
		pnMain.add(pnTbl);
		
		
		DefaultTableModel dataModel = new DefaultTableModel();

		dataModel.addColumn("순번");
		dataModel.addColumn("제목");
		dataModel.addColumn("ID");
		if(advDataList!=null)
		{
			for(int i=0;i<advDataList.size();i++)
			{
				XLSTableInfo info = (XLSTableInfo) advDataList.get(i);
				info.setTable_index(i+1);

			}

			Code code_info = new Code();
			code_info.setCode_name(shipper);
			try {
				Code searchCode =service.getCodeInfo(code_info);

				// 정렬 부분 추가
				StringTokenizer stringTokenizer = new StringTokenizer(searchCode.getCode_field(),",");

				if(stringTokenizer.countTokens()==advDataList.size())
				{
					while(stringTokenizer.hasMoreTokens())
					{
						String index = stringTokenizer.nextToken();
						for(int i=0;i<advDataList.size();i++)
						{
							XLSTableInfo info = (XLSTableInfo) advDataList.get(i);

							if(Integer.valueOf(index)==info.getTable_index())
							{
								dataModel.addRow(new Object[]{info.getTable_index(),info.getXLSTitle(),info});
							}
						}
					}

				}else
				{
					logger.error("size가 일치 하지 않습니다.");
					for(int i=0;i<advDataList.size();i++)
					{
						XLSTableInfo info = (XLSTableInfo) advDataList.get(i);
						dataModel.addRow(new Object[]{i+1,info.getXLSTitle(),info});
					}	
				}
			} catch (SQLException e1) 
			{
				
				e1.printStackTrace();
				
			}catch (NullPointerException e1) 
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage()+":"+shipper);
			}



		}
		tblAdv.setModel(dataModel);
		TableColumnModel colModel2=tblAdv.getColumnModel();
		colModel2.getColumn(0).setMaxWidth(55);
		colModel2.getColumn(2).setMaxWidth(0);
		

		ListSelectionModel smodel = tblTable.getSelectionModel();
		tblAdv.setSelectionModel(smodel);

		JPanel pnADV = new JPanel();
		pnADV.setLayout(new BorderLayout());

		JPanel pnADVInfo = new JPanel();
		pnADVInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnADVInfo.add(new JLabel("XLS 위치 정보"));
		pnADV.add(pnADVInfo,BorderLayout.NORTH);
		pnADV.add(new JScrollPane(tblAdv));
		JPanel pnADVControl = new JPanel();
		pnADVControl.setLayout(new BorderLayout());
		JButton butUp = new JButton("위로");
		butUp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int row=tblAdv.getSelectedRow();
				if(row>0)
				{
					DefaultTableModel defaultTableModel = (DefaultTableModel) tblAdv.getModel();

					Object tempObj[]= new Object[defaultTableModel.getColumnCount()];
					for(int i=0;i<tempObj.length;i++)
					{
						tempObj[i]= defaultTableModel.getValueAt(row-1, i);
					}
					Object newObj[]= new Object[defaultTableModel.getColumnCount()];
					for(int i=0;i<tempObj.length;i++)
					{
						newObj[i]= defaultTableModel.getValueAt(row, i);
					}

					for(int i=0;i<tempObj.length;i++)
					{
						defaultTableModel.setValueAt(tempObj[i], row, i);
					}

					for(int i=0;i<newObj.length;i++)
					{
						defaultTableModel.setValueAt(newObj[i], row-1, i);
					}

					tblAdv.changeSelection(row-1, tblAdv.getSelectedColumn(), false, false);
				}

			}
		});

		JButton butDown = new JButton("아래로");
		butDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int row=tblAdv.getSelectedRow();
				DefaultTableModel defaultTableModel = (DefaultTableModel) tblAdv.getModel();
				if(row<defaultTableModel.getRowCount())
				{
					Object tempObj[]= new Object[defaultTableModel.getColumnCount()];
					for(int i=0;i<tempObj.length;i++)
					{
						tempObj[i]= defaultTableModel.getValueAt(row+1, i);
					}

					Object newObj[]= new Object[defaultTableModel.getColumnCount()];
					for(int i=0;i<tempObj.length;i++)
					{
						newObj[i]= defaultTableModel.getValueAt(row, i);
					}

					for(int i=0;i<tempObj.length;i++)
					{
						defaultTableModel.setValueAt(tempObj[i], row, i);
					}

					for(int i=0;i<newObj.length;i++)
					{
						defaultTableModel.setValueAt(newObj[i], row+1, i);
					}

					tblAdv.changeSelection(row+1, tblAdv.getSelectedColumn(), false, false);
				}
			}
		});

		JPanel pnMove = new JPanel();
		pnMove.add(butUp);
		pnMove.add(butDown);
		pnADVControl.add(pnMove,BorderLayout.EAST);
		JPanel pnRowCount = new JPanel();
		pnRowCount.add(new JLabel("타이틀 위치 : "));
		JComboBox cbxTitleLocation = new JComboBox();
		cbxTitleLocation.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				JComboBox box = (JComboBox) e.getSource();

				int index=(Integer)box.getSelectedItem();

				DefaultTableModel dataModel = (DefaultTableModel) tblAdv.getModel();
				for(int i=0;i<dataModel.getRowCount();i++)
				{
					XLSTableInfo info = (XLSTableInfo) dataModel.getValueAt(i, 2);

					dataModel.setValueAt(info.getXLSTitle(-index), i, 1);
				}
				

			}});
		for(int i=1;i<10;i++)
		{
			cbxTitleLocation.addItem(-i);
		}
		pnRowCount.add(cbxTitleLocation);
		pnADVControl.add(pnRowCount,BorderLayout.WEST);
		pnADV.add(pnADVControl,BorderLayout.SOUTH);
		pnMain.add(pnADV);

		JPanel pnContorl = new JPanel();
		JButton butOk = new JButton("다시불러오기");
		butOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				DefaultTableModel advmodel=(DefaultTableModel) tblAdv.getModel();
				manager.initTempXLSTableInfoList();
				for(int i=0;i<advmodel.getRowCount();i++)
				{
					int index=(Integer) advmodel.getValueAt(i, 0);
					XLSTableInfo info = (XLSTableInfo) manager.getXLSTableInfoList().get(index-1);
					DefaultTableModel model2=(DefaultTableModel) tblTable.getModel();
					try {
						ShippersTable table=tableService.getTableById((String) model2.getValueAt(i, 0));
						info.setTableInfo(table);
						manager.getTempXLSTableInfoList().add(manager.getXLSTableInfoList().get(index-1));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				base.updateTableListPN();
			}
		});
		pnContorl.add(butOk);
		JButton butCancel = new JButton("닫기");
		butCancel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		pnContorl.add(butCancel);
		pnContorl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(pnMain);
		getContentPane().add(pnContorl,BorderLayout.SOUTH);

		JPanel pnLeft = new JPanel();
		pnLeft.setPreferredSize(new Dimension(15,0));
		JPanel pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(15,0));

		getContentPane().add(pnRight,BorderLayout.EAST);
		getContentPane().add(pnLeft,BorderLayout.WEST);
		int w = KSGModelManager.getInstance().frame.getWidth()/4*3;
		int h = KSGModelManager.getInstance().frame.getHeight()/4*3;
		setSize(new Dimension(w,h));
		ViewUtil.center(this, false);
	}
	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

}

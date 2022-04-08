package com.ksg.workbench.base.comp;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapException;
import com.ksg.commands.base.SearchBaseInfoCommand;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**코드 정보관리 화면
 * 
 * @author 박창현
 *
 */
public abstract class PnCode extends JPanel implements ActionListener
{
	public static final String CODE_TYPE_INBOUND_PORT = "Inbound 출발 항구";
	
	public static final String CODE_TYPE_IN_PORT = "IN항구";
	
	public static final String CODE_TYPE_CON_TYPE = "컨테이너 타입";
	
	private static final int _ROW_SIZE = 25;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	CardLayout cardLayout2 = new CardLayout();
	
	private JLabel lblTable;

	private Box pnMain;
	
	private String selectedTable;
	
	private int table_type;
	
	private JTable tblConType;
	
	private JTable tableInCode;
	
	private JTable tableCurrent;
	
	private BaseService service = new BaseServiceImpl();
	
	private JTable tableInboundPortList;
	
	private JTable tableExPortList;
	
	protected Logger logger = Logger.getLogger(getClass());

	BaseInfoUI base;

	private PnCodeConType pnCodeConType;
	
	private PnCodeInboundPortOrder pnCodeInboundPort;
	
	private PnCodeInboundPort pnCodeInboundInCode;

	public PnCode(BaseInfoUI base) {
		this.base=base;
		
		createAndUpdateUI();
	}
	
	private void createAndUpdateUI() {

		this.setLayout(new BorderLayout());

		pnMain = Box.createVerticalBox();

		Box subMain1 = Box.createHorizontalBox();

		pnCodeConType 	  = new PnCodeConType(base);

		pnCodeInboundPort = new PnCodeInboundPortOrder(base);

		pnCodeInboundInCode = new PnCodeInboundPort(base);

		subMain1.add(pnCodeConType);

		subMain1.add(Box.createHorizontalStrut(15));

		subMain1.add(pnCodeInboundInCode);

		subMain1.add(Box.createGlue());

		Box subMain2 = Box.createHorizontalBox();

		subMain2.add(pnCodeInboundPort);

		subMain2.add(Box.createGlue());

		pnMain.add(subMain1);

		pnMain.add(subMain2);
		
		pnCodeConType.updateTable();

		pnCodeInboundInCode.updateTable();

		pnCodeInboundPort.updateTable();


		JPanel pnTitle = createTiltlePn();
		this.add(pnMain,BorderLayout.CENTER);
		this.add(KSGDialog.createMargin(),BorderLayout.EAST);
		this.add(KSGDialog.createMargin(),BorderLayout.WEST);		
		this.add(pnTitle,BorderLayout.NORTH);
	}	

	public void show(String name)
	{
		selectedTable=name;
		lblTable.setText(name);
		cardLayout2.show(pnMain, name);
		try {
			updateCodeTable2();
		} catch (SqlMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private JPanel createTiltlePn() {
		JPanel pnTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pn1 = new JPanel(new FlowLayout(FlowLayout.LEFT));


		pn1.setPreferredSize(new Dimension(300,45));
		pn1.setBackground(new Color(171,126,111));
		lblTable = new JLabel("코드 정보");
		lblTable.setFont(new Font("돋음",Font.BOLD,16));

		JPanel pn1_1 = new JPanel(new BorderLayout());
		pn1_1.add(lblTable);

		Dimension d=pn1.getPreferredSize();
		pn1_1.setPreferredSize(new Dimension(d.width-5,d.height-5));
		pn1.add(pn1_1);
		JPanel pn2 =  new JPanel();
		JButton button = new JButton("신규");
		button.setSize(new Dimension(45,35));
		pn2.add(button);
		JButton butDelCode = new JButton("삭제");
		butDelCode.setActionCommand("코드 삭제");
		butDelCode.addActionListener(this);
		butDelCode.setSize(new Dimension(45,35));
		pn2.add(butDelCode);

		pnTitle.add(pn1);
		//pnTitle.add(pn2);


		return pnTitle;
	}


	private void updateCodeTable2() throws SQLException,SqlMapException{
		if(selectedTable==null)
			return;



		Code code_info = new Code();
		code_info.setCode_name_kor(selectedTable);

		List<Code> li=service.getSubCodeInfo(code_info);
		String[] colums2 = {"","Name","한글","Field"};

		DefaultTableModel model = new KSGTableModel(colums2,li.size());

		for(int i=0;i<li.size();i++)
		{
			Code code = li.get(i);
			model.setValueAt("", i, 0);
			model.setValueAt(code.getCode_name(), i, 1);
			model.setValueAt(code.getCode_name_kor(), i, 2);
			model.setValueAt(code.getCode_field(), i, 3);
		}



		if(selectedTable.equals("errorCode"))
		{
			table_type = SearchBaseInfoCommand.CODE_ERROR;
			lblTable.setText("에러 정보");
		}else if(selectedTable.equals("table"))
		{
			table_type = SearchBaseInfoCommand.CODE_TABLE;
			lblTable.setText("테이블 필드명");
		}
		else if(selectedTable.equals(CODE_TYPE_CON_TYPE))
		{
			lblTable.setText(CODE_TYPE_CON_TYPE);

			tableCurrent = tblConType;
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_CON_TYPE;
			tblConType.setModel(model);

			TableColumnModel colmodel = tblConType.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setPreferredWidth(200);	
				}else
				{
					namecol.setPreferredWidth(400);
				}
			}
		}
		else if(selectedTable.equals("항구 예외 항목"))
		{
			lblTable.setText("항구 예외 항목");
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_EXCEPTION_PORT;
			tableCurrent = tableExPortList;
			tableExPortList.setModel(model);
			tableExPortList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnModel colmodel = tableExPortList.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setPreferredWidth(200);	
				}else
				{
					namecol.setPreferredWidth(400);
				}
			}
		}
		else if(selectedTable.equals(CODE_TYPE_INBOUND_PORT))
		{
			lblTable.setText(CODE_TYPE_INBOUND_PORT);
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_START_PORT;

			tableCurrent = tableInboundPortList;
			tableInboundPortList.setModel(model);
			TableColumnModel colmodel = tableInboundPortList.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setPreferredWidth(200);	
				}else
				{
					namecol.setPreferredWidth(400);
				}
			}
		}
		else if(selectedTable.equals("DB 테이블"))
		{
			lblTable.setText("DB 테이블");
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_DB_TABLE;
		}
		else if(selectedTable.equals(CODE_TYPE_IN_PORT))
		{
			lblTable.setText(CODE_TYPE_IN_PORT);
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_PORT_LIST;
			tableInCode.setModel(model);
			tableInCode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnModel colmodel = tableInCode.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);

				if(i==0)
				{
					namecol.setMaxWidth(15);
					namecol.setMinWidth(15);
				}
				else if(i==1)
				{
					namecol.setPreferredWidth(70);	
				}else
				{
					namecol.setPreferredWidth(120);
				}
			}
		}
		else if(selectedTable.equals("테이블 INDEX"))
		{
			lblTable.setText(CODE_TYPE_IN_PORT);
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_PORT_LIST;
		}
		tableCurrent.setRowHeight(_ROW_SIZE);


	}
	public void updateCodeTable(String selectedTable) throws SQLException,SqlMapException{
		if(selectedTable==null)
			return;



		Code code_info = new Code();
		code_info.setCode_name_kor(selectedTable);		
		List<Code> li=service.getSubCodeInfo(code_info);
		String[] colums2 = {"","Name","한글","Field"};

		DefaultTableModel model = new KSGTableModel(colums2,li.size());

		for(int i=0;i<li.size();i++)
		{
			Code code = li.get(i);
			model.setValueAt("", i, 0);
			model.setValueAt(code.getCode_name(), i, 1);
			model.setValueAt(code.getCode_name_kor(), i, 2);
			model.setValueAt(code.getCode_field(), i, 3);
		}

		if(selectedTable.equals("errorCode"))
		{
			table_type = SearchBaseInfoCommand.CODE_ERROR;
			//			lblTable.setText("에러 정보");
		}else if(selectedTable.equals("table"))
		{
			table_type = SearchBaseInfoCommand.CODE_TABLE;
			//			lblTable.setText("테이블 필드명");
		}
		else if(selectedTable.equals(CODE_TYPE_CON_TYPE))
		{
			//			lblTable.setText("컨테이너 타입");

			tableCurrent = tblConType;
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_CON_TYPE;
			tblConType.setModel(model);
			//			tableConType.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnModel colmodel = tblConType.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setMaxWidth(20);
					namecol.setMinWidth(20);
				}
				else if(i==1)
				{
					namecol.setPreferredWidth(70);	
				}else
				{
					namecol.setPreferredWidth(120);
				}
			}
		}
		else if(selectedTable.equals("항구 예외 항목"))
		{
			lblTable.setText("항구 예외 항목");
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_EXCEPTION_PORT;
			tableCurrent = tableExPortList;
			tableExPortList.setModel(model);
			tableExPortList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnModel colmodel = tableExPortList.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setMaxWidth(20);
					namecol.setMinWidth(20);
				}
				else if(i==1)
				{
					namecol.setPreferredWidth(70);	
				}else
				{
					namecol.setPreferredWidth(120);
				}
			}
		}
		else if(selectedTable.equals(CODE_TYPE_INBOUND_PORT))
		{
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_START_PORT;

			tableCurrent = tableInboundPortList;
			tableInboundPortList.setModel(model);
			tableInCode.setRowHeight(25);
			TableColumnModel colmodel = tableInboundPortList.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);
				if(i==0)
				{
					namecol.setMaxWidth(20);
					namecol.setMinWidth(20);
				}
				else if(i==1)
				{
					namecol.setPreferredWidth(70);	
				}else
				{
					namecol.setPreferredWidth(120);
				}
			}
		}
		else if(selectedTable.equals("DB 테이블"))
		{
			lblTable.setText("DB 테이블");
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_DB_TABLE;
		}
		else if(selectedTable.equals(CODE_TYPE_IN_PORT))
		{
			//			lblTable.setText("IN항구");
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_PORT_LIST;
			tableCurrent = tableInCode;
			tableInCode.setModel(model);
			//			tableInCode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableInCode.setRowHeight(25);
			TableColumnModel colmodel = tableInCode.getColumnModel();
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				namecol.setCellRenderer(renderer);

				if(i==0)
				{
					namecol.setMaxWidth(20);
					namecol.setMinWidth(20);
				}
				else if(i==1)
				{
					namecol.setPreferredWidth(70);	
				}else
				{
					namecol.setPreferredWidth(120);
				}
			}
		}
		else if(selectedTable.equals("테이블 INDEX"))
		{
			lblTable.setText(CODE_TYPE_IN_PORT);
			table_type = SearchBaseInfoCommand.CODE_SUBTABLE_IN_PORT_LIST;
		}
		tableCurrent.setRowHeight(_ROW_SIZE);

	}

	public void actionPerformed(ActionEvent e) 
	{

		int row=tableCurrent.getSelectedRow();
		if(row==-1)
			return;

		Code code_info = new Code();
		code_info.setCode_field((String) tableCurrent.getValueAt(row, 3));

		int result=JOptionPane.showConfirmDialog(this,tableCurrent.getValueAt(row, 3)+"를 삭제 하시겠습니까?",selectedTable,JOptionPane.YES_NO_OPTION);
		if(result==JOptionPane.YES_OPTION)
		{

			try {
				int count=service.deleteCode(code_info);
				if(count>0)
					updateCodeTable2();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else
		{
			return;
		}
	}
}

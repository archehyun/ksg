package com.ksg.workbench.base.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.dialog.InsertInPortCodeInfodialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * @설명 인바운드 항구 코드 화면
 * @author 박창현
 *
 */

@Deprecated
public class PnCodeInboundPort extends PnBase implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTable tblCode;
	
	private BaseService service = new BaseServiceImpl();
	
	public PnCodeInboundPort(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		tblCode = new JTable();
		
		tblCode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JPanel pnTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblTitle = new JLabel("항구 코드");
		
		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butAdd = new JButton("신규 코드");	
		
		JButton butDel = new JButton("삭제");
		
		butAdd.addActionListener(this);
		
		butDel.addActionListener(this);

		pnControl.add(butAdd);
		
		pnControl.add(butDel);

		add(pnControl,BorderLayout.SOUTH);
		
		pnTitle.add(lblTitle);
		
		add(pnTitle,BorderLayout.NORTH);
		
		add(new JScrollPane(tblCode));
	}
	public void updateTable() 
	{
		try{
		Code code_info = new Code();
		code_info.setCode_name_kor(PnCode.CODE_TYPE_IN_PORT);		
		List<Code> li=service.getSubCodeInfo(code_info);
		String[] colums2 = {"","코드","영문","한글"};

		DefaultTableModel model = new KSGTableModel(colums2,li.size());

		for(int i=0;i<li.size();i++)
		{
			Code code = li.get(i);
			model.setValueAt("", i, 0);
			model.setValueAt(code.getCode_name(), i, 1);
			model.setValueAt(code.getCode_field(), i, 2);
			
			model.setValueAt(code.getCode_name_kor(), i, 3);
			
		}
		tblCode.setModel(model);
		tblCode.setRowHeight(25);
		TableColumnModel colmodel = tblCode.getColumnModel();
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
				namecol.setMaxWidth(50);
			}else
			{
				namecol.setPreferredWidth(120);
			}
		}
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("신규 코드"))
		{
			InsertInPortCodeInfodialog codeInfodialog = new InsertInPortCodeInfodialog();
			codeInfodialog.createAndUpdateUI();
			if(codeInfodialog.result==KSGDialog.SUCCESS)
			{
					updateTable();
				
			}
		}
		else if(command.equals("삭제"))
		{
			int row=tblCode.getSelectedRow();
			if(row==-1)
				return;

			Code code_info = new Code();
			code_info.setCode_name((String) tblCode.getValueAt(row, 1));
			code_info.setCode_field((String) tblCode.getValueAt(row, 2));
			

			int result=JOptionPane.showConfirmDialog(KSGModelManager.getInstance().frame,tblCode.getValueAt(row, 3)+"를 삭제 하시겠습니까?","국내 항구",JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_OPTION)
			{

				try {
					int count=service.deleteCode(code_info);
					if(count>0)
					updateTable();
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
	@Override
	public void updateTable(String query) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fnSearch() {
		// TODO Auto-generated method stub
		
	}

}

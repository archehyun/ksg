package com.ksg.base.view.comp;

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

import com.ksg.base.view.BaseInfoUI;
import com.ksg.base.view.dialog.InsertInboundCodeInfodialog;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTableModel;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;

public class PnCodeInboundPortOrder extends PnBase implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTable tblCode;
	
	private BaseService service = new BaseServiceImpl();
	
	public PnCodeInboundPortOrder(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);

		tblCode = new JTable();
		
		tblCode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JPanel pnTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblTitle = new JLabel("인바운드 출발 항구");
		
		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butAddInboundPort = new JButton("신규 코드");
		
		butAddInboundPort.addActionListener(this);

		JButton butUpdate = new JButton("수정");
		
		butUpdate.setEnabled(false);
		
		butUpdate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) 
			{

				int row=tblCode.getSelectedRow();
				if(row==-1)
				{
					JOptionPane.showConfirmDialog(KSGModelManager.getInstance().frame, "선택된 코드가 없습니다.");
					return;
				}
			}});
		JButton butDel = new JButton("삭제");
		butDel.addActionListener(this);

		pnControl.add(butAddInboundPort);
		//pnControl.add(butUpdate);
		pnControl.add(butDel);


		add(new JScrollPane(tblCode));
		add(pnControl,BorderLayout.SOUTH);
		pnTitle.add(lblTitle);
		add(pnTitle,BorderLayout.NORTH);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("신규 코드"))
		{
			InsertInboundCodeInfodialog codeInfodialog = new InsertInboundCodeInfodialog();
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
			String value = (String) tblCode.getValueAt(row, 2);
			int result=JOptionPane.showConfirmDialog(KSGModelManager.getInstance().frame, value+"를 삭제 하시겠습니까?","코드 삭제",JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_OPTION)
			{

				Code param= new Code();
				param.setCode_field(String.valueOf(tblCode.getValueAt(row,1)));
				param.setCode_name(String.valueOf(tblCode.getValueAt(row, 2)));

				try 
				{
					int count=service.deleteCode(param);
					if(count>0)
					{
						updateTable();
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, " 삭제 되었습니다.");
					}
				}
				catch (SQLException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

	}
	public void updateTable()
	{
		try{
			Code code_info = new Code();
			code_info.setCode_name_kor(PnCode.CODE_TYPE_INBOUND_PORT);		
			List<Code> li=service.getSubCodeInfo(code_info);
			String[] colums2 = {"","순서","영문","한글"};

			DefaultTableModel model = new KSGTableModel(colums2,li.size());

			for(int i=0;i<li.size();i++)
			{
				Code code = li.get(i);
				model.setValueAt("", i, 0);
				model.setValueAt(code.getCode_field(), i, 1);
				model.setValueAt(code.getCode_name(), i, 2);
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

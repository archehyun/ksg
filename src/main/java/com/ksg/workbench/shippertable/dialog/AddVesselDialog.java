package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.shippertable.comp.KSGADVTablePanel;

public class AddVesselDialog extends KSGDialog {

	DAOManager daoManager;
	private static final long serialVersionUID = 1L;
	private int row1;
	private int col1;
	private JTable table;
	private Object value;
	private KSGADVTablePanel main;
	private JCheckBox box;
	private KSGComboBox cbxType;
	DefaultTableModel vesselModel;
	
	VesselServiceV2 vesselService;
	public AddVesselDialog(KSGADVTablePanel main,JTable table,int row,Object value) {
		this(main,table,row,0,value);

	}
	public AddVesselDialog(KSGADVTablePanel main,JTable table,int row,int col,Object value) {
		super();
		this.main = main;
		this.row1=row;
		this.table =table;
		this.value=value;
		this.col1=col;
		daoManager =DAOManager.getInstance();
//		baseService = daoManager.createBaseService();
		this.addComponentListener(this);
		vesselService = new VesselServiceImpl();
		

	}
	

	public AddVesselDialog(KSGADVTablePanel ksgadvTablePanel, JTable table,
			int selectedVesselrow, int col, Object value,
			DefaultTableModel vesselModel) {
		this.main = ksgadvTablePanel;
		this.row1=selectedVesselrow;
		this.table =table;
		this.value=value;
		this.col1=col;
		daoManager =DAOManager.getInstance();
//		baseService = daoManager.createBaseService();
		this.vesselModel = vesselModel;
		
	}
	public void createAndUpdateUI() {
		final JDialog di = new JDialog(KSGModelManager.getInstance().frame);
		di.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {table.setValueAt(null, row1, col1);}
		});
		cbxType = new KSGComboBox("conType");
//		Code code = new Code();
//		code.setCode_name_kor("컨테이너 타입");
//		try {
//			List li=	baseService.getSubCodeInfo(code);
//			
//			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
//			
//			Iterator iter = li.iterator();
//			
//			while(iter.hasNext())
//			{
//				Code code2=(Code) iter.next();
//				
//				ConType conType = new ConType();
//				conType.setTypeField(code2.getCode_field());
//				conType.setTypeName(code2.getCode_name());
//				boxModel.addElement(conType);
//				
//			}
//			
//			
//			cbxType.setModel(boxModel);
//
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		di.setTitle("선박명 추가");
		di.setModal(true);
		
		Box pnBox = Box.createVerticalBox();
		JPanel pnMain = new JPanel();

		final JTextField txf = new JTextField(15);
		JCheckBox cbx = new JCheckBox("수정");
		cbx.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox box=(JCheckBox) e.getSource();
				txf.setEditable(box.isSelected());

			}});
		box = new JCheckBox("사용안함");
		JLabel lbl = new JLabel("선박명: ");
		pnMain.add(lbl);
		pnMain.add(txf);
		pnMain.add(box);
		
		
		
		pnBox.add(pnMain);
		pnBox.add(cbxType);
		
		String temp= String.valueOf(value);
		
		
		String result="";
		String tempList[]=temp.split(" ");
		
			for(int j=0;j<tempList.length;j++)
			{
				String fword = tempList[j].substring(0, 1);
				
				fword = fword.toUpperCase();
				
				String bword = tempList[j].substring(1,tempList[j].length());
				bword = bword.toLowerCase();
				
				result+=(fword+bword);
				System.out.println(result);
				if(j<tempList.length-1)
					result+=" ";
			}
		txf.setText(result);



		JPanel pnControl = new JPanel();
		JButton butOK = new JButton("확인");
		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				Vessel vessel = new Vessel();
				vessel.setVessel_name(txf.getText());
				vessel.setVessel_abbr(txf.getText());
				vessel.setVessel_use(box.isSelected()?1:0);
				
				ConType con =(ConType) cbxType.getSelectedItem();
				vessel.setVessel_type(con.getTypeField());
				
				try {
					vesselService.insertVessel(vessel);
					
					
					JOptionPane.showMessageDialog(null, "선박명: "+vessel.getVessel_name()+"이(가) 추가 되었습니다.");
					di.setVisible(false);
					di.dispose();
					vesselModel.setRowCount(vesselModel.getRowCount()+1);
					vesselModel.setValueAt(txf.getText(), row1, 0);
					vesselModel.setValueAt(txf.getText(), row1, 1);

					main.setValue( vessel.getVessel_name().toUpperCase(), row1, 0);
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
				table.setValueAt(null, row1, col1);
				di.setVisible(false);
				di.dispose();

			}});

		pnControl.add(butOK);
		pnControl.add(butCancel);


		di.getContentPane().add(pnBox);

		di.getContentPane().add(pnControl,BorderLayout.SOUTH);

		ViewUtil.center(di, true);
		di.setResizable(false);
		di.setVisible(true);
	}
	class ConType
	{
		private String typeName;
		private String typeField;
		public String getTypeName() {
			return typeName;
		}
		public String toString()
		{
			return typeField+" : "+typeName;
		}
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		public String getTypeField() {
			return typeField;
		}
		public void setTypeField(String typeField) {
			this.typeField = typeField;
		}
		
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		cbxType.initComp();
	}

}

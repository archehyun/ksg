package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;
import com.ksg.service.BaseService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.shippertable.comp.AdvertiseTable;

public class AddVesselDialog extends KSGDialog {

	private DAOManager daoManager;
	private static final long serialVersionUID = 1L;

	private Object vesselName;
	private JCheckBox box;
	private JComboBox cbxType;
	private BaseService baseService;
	private VesselService vesselService;
	public AddVesselDialog(Object vesselName) {
		this.vesselName=vesselName;
		daoManager =DAOManager.getInstance();
		baseService = daoManager.createBaseService();
		vesselService = new VesselServiceImpl();
		this.addComponentListener(this);

	}
	private AdvertiseTable advTable;
	private DefaultTableModel  vesselModel;
	private int col;
	private int row;
	private JTextField txfMMSI;
	private JCheckBox cbxMMSICheck;
	private JTextField txfInput;

	public AddVesselDialog(AdvertiseTable advTable, int row,
			int col, String vesselName, DefaultTableModel vesselModel) {
		
		this(vesselName);
		this.advTable = advTable;
		this.row = row;
		this.col = col;
		this.vesselModel= vesselModel;		
		
	}


	public void createAndUpdateUI() {

		this.setTitle("선박명 추가");
		
		this.setModal(true);

		this.getContentPane().add(buildCenter());

		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this, true);
		
		this.setResizable(false);
		
		this.setVisible(true);
	}
	public KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout()); 

		Box pnBox = Box.createVerticalBox();
		KSGPanel pnInfoControl = new KSGPanel();
		cbxType = new JComboBox();
		txfInput = new JTextField(15);
		JCheckBox cbx = new JCheckBox("수정");
		cbx.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox box=(JCheckBox) e.getSource();
				txfInput.setEditable(box.isSelected());

			}});
		box = new JCheckBox("사용안함");
		JLabel lbl = new JLabel("선박명: ");
		pnInfoControl.add(lbl);
		pnInfoControl.add(txfInput);
		pnInfoControl.add(box);

		KSGPanel pnMMSI = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		txfMMSI = new JTextField(9);
		txfMMSI.setEnabled(false);
		cbxMMSICheck = new JCheckBox("없음",true);

		cbxMMSICheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				txfMMSI.setEnabled(!cbxMMSICheck.isSelected());

				if(cbxMMSICheck.isSelected())
				{
					txfMMSI.setText("");
				}
			}
		});

		pnMMSI.add(new JLabel("MMSI 코드:"));
		pnMMSI.add(txfMMSI);
		pnMMSI.add(cbxMMSICheck);



		pnBox.add(pnInfoControl);
		pnBox.add(pnMMSI);
		pnBox.add(cbxType);
		
		pnMain.add(pnBox);
		
		return pnMain;
	}
	
	public KSGPanel buildControl()
	{
		
		KSGPanel pnControl = new KSGPanel();
		JButton butOK = new JButton("확인");
		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				Vessel vessel = new Vessel();
				vessel.setVessel_name(txfInput.getText());
				vessel.setVessel_abbr(txfInput.getText());
				vessel.setVessel_use(box.isSelected()?1:0);

				if(!cbxMMSICheck.isSelected())
				{
					int mmsi_size=txfMMSI.getText().length();
					if(mmsi_size<9)					
					{
						JOptionPane.showMessageDialog(AddVesselDialog.this, "MMSI는 9자리입니다.");
						return;
					}
					vessel.setVessel_mmsi(txfMMSI.getText());
				}


				ConType con =(ConType) cbxType.getSelectedItem();
				vessel.setVessel_type(con.getTypeField());

				try {
					vesselService.insert(vessel);

					if(advTable!=null)
					{
						vesselModel.setRowCount(vesselModel.getRowCount()+1);
						vesselModel.setValueAt(txfInput.getText(), row, 0);
						vesselModel.setValueAt(txfInput.getText(), row, 1);
					}

					if(advTable!=null)
						advTable.setValue( vessel.getVessel_abbr().toUpperCase(), row, 0);
					JOptionPane.showMessageDialog(null, "선박명: "+vessel.getVessel_name()+"이(가) 추가 되었습니다.");
					AddVesselDialog.this.setVisible(false);
					AddVesselDialog.this.dispose();

				} catch (SQLException e1) 
				{
					if(e1.getErrorCode()==8152)
					{
						JOptionPane.showMessageDialog(AddVesselDialog.this, "9자리까지 입력 할수 있습니다.");	
					}
					else
					{
						JOptionPane.showMessageDialog(null, e1.getMessage());
						logger.debug(e1.getMessage());
						e1.printStackTrace();	
					}
				}catch(Exception e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(AddVesselDialog.this, e1.getMessage());
				}
			}});
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				AddVesselDialog.this.setVisible(false);
				AddVesselDialog.this.dispose();

			}});

		pnControl.add(butOK);
		pnControl.add(butCancel);
		return pnControl;
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
		Code code = new Code();
		code.setCode_name_kor("컨테이너 타입");
		try {
			List li=	baseService.getSubCodeInfo(code);

			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

			Iterator iter = li.iterator();

			while(iter.hasNext())
			{
				Code code2=(Code) iter.next();

				ConType conType = new ConType();
				conType.setTypeField(code2.getCode_field());
				conType.setTypeName(code2.getCode_name());
				boxModel.addElement(conType);

			}


			cbxType.setModel(boxModel);
			
			
			String temp= String.valueOf(vesselName);


			String result="";
			String tempList[]=temp.split(" ");

			for(int j=0;j<tempList.length;j++)
			{
				String fword = tempList[j].substring(0, 1);

				fword = fword.toUpperCase();

				String bword = tempList[j].substring(1,tempList[j].length());
				bword = bword.toLowerCase();

				result+=(fword+bword);
				if(j<tempList.length-1)
					result+=" ";
			}
			txfInput.setText(result);


		} catch (SQLException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}
		
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


}

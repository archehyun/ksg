package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.dialog.PortSearchDialog;

public class ADDADVDialog extends KSGDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JList portLi;
	
	private String company_abbr;
	
	private int page;
	
	private int t_index;
	
	private String table_id;
	
	Font defaultfont;
	
	private JTextField txfPort;
	
	private BaseServiceImpl baseService;

	public ADDADVDialog(String table_id,int page, int t_index,String company_abbr) {
		super();
		advservice = new ADVServiceImpl();
		tableService= new TableServiceImpl();
		baseService = new BaseServiceImpl(); 
		this.table_id=table_id;
		this.page=page;
		this.t_index=t_index;
		this.company_abbr=company_abbr;
		defaultfont = KSGModelManager.getInstance().defaultFont;
	}

	public void createAndUpdateUI() {
		this.setTitle("광고정보생성");
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new GridLayout(1,0));
		this.setModal(true);
		JPanel pnPort =new JPanel();
		pnPort.setLayout(new BorderLayout());
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("항구정보");
		createTitledBorder.setTitleFont(defaultfont);

		pnPort.setBorder(createTitledBorder);

		txfPort = new JTextField(20);
		txfPort.addKeyListener(new KeyAdapter(){

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField f=(JTextField) e.getSource();
					String portInfo = f.getText();

					try {
						PortInfo port=baseService.getPortInfo(portInfo);
						if(port==null)
						{
							JOptionPane.showMessageDialog(null, "존재하지 않는 항구명입니다.");
							return;
						}
					} catch (SQLException ee) {
						// TODO Auto-generated catch block
						ee.printStackTrace();
					}

					DefaultListModel model= (DefaultListModel) portLi.getModel();
					model.addElement(portInfo);
					f.setText("");	
				}
			}
		});
		JPanel pnPortInput =new JPanel();
		JLabel lblPortName = new JLabel("항구명");
		lblPortName.setFont(defaultfont);
		pnPortInput.add(lblPortName);
		pnPortInput.add(txfPort);
		JButton butPortSearch = new JButton("검색");
		butPortSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String portname=txfPort.getText();
				PortSearchDialog dialog = new PortSearchDialog(ADDADVDialog.this);
				dialog.setPortName(portname);
				dialog.createAndUpdateUI();

				DefaultListModel model= (DefaultListModel) portLi.getModel();
				model.addElement(dialog.portName);
				txfPort.setText("");

			}
		});
		pnPortInput.add(txfPort);
		pnPortInput.add(butPortSearch);
		pnPort.add(pnPortInput,BorderLayout.NORTH);
		portLi = new JList();
		DefaultListModel portModel = new DefaultListModel();

		try {
			List li=tableService.getParentPortList(table_id);
			for(int i=0;i<li.size();i++)
			{
				TablePort po = (TablePort) li.get(i);
				portModel.addElement(po.getPort_name());
			}
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		portLi.setModel(portModel);	

		pnPort.add(new JScrollPane(portLi));

		JPanel pnPortSub = new JPanel();
		JButton butADD = new JButton("추가");
		butADD.addActionListener(this);
		butADD.setFont(defaultfont);

		pnPortSub.add(butADD);
		JButton butDel = new JButton("삭제");
		butDel.addActionListener(this);
		butDel.setFont(defaultfont);
		pnPortSub.add(butDel);
		JButton butUp = new JButton("위로");
		butUp.addActionListener(this);
		butUp.setFont(defaultfont);
		pnPortSub.add(butUp);
		JButton butDown = new JButton("아래로");
		butDown.addActionListener(this);
		butDown.setFont(defaultfont);
		pnPortSub.add(butDown);
		pnPort.add(pnPortSub,BorderLayout.SOUTH);
		pnMain.add(pnPort);


		JPanel pnControl = new JPanel();
		pnControl.setLayout(new  BorderLayout());
		final JTextField txfDate2 = new JTextField(6);
		JPanel pnRight = new JPanel();
		JButton butOK = new JButton("확인");
		butOK.setFont(defaultfont);

		butOK.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				String date = txfDate2.getText();
				if(date.length()<=0||date==null)
				{
					JOptionPane.showMessageDialog(null, "날짜를 입력하세요");
					return;
				}
				try {
					ShippersTable talbeInfo=tableService.getTableById(table_id);
					if(talbeInfo.getGubun().equals("TS"))
					{
						DefaultListModel portModel=(DefaultListModel) portLi.getModel();
						String ports[]= new String[portModel.size()];

						for(int i=0;i<ports.length;i++)
						{
							ports[i]=(String) portModel.get(i);
						}

						String data= new String();
						data+="Feeder VESSEL\tFeeder Voyage\tMother Vessel\tMother Voyage\t";
						for(int i=0;i<ports.length;i++)
						{
							data+=ports[i]+"\t";
						}
						data+="\n\n";

						ADVData adv = new ADVData();
						adv.setData(data);
						adv.setCompany_abbr(company_abbr);
						adv.setPage(page);
						adv.setT_index(t_index);
						adv.setTable_id(table_id);

						try 
						{
							adv.setDate_isusse(KSGDateUtil.toDate2(txfDate2.getText()));
							advservice.insertADVData(adv);
							tableService.updateTableDate(adv);

						} catch (SQLException e1) 
						{
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
						}
						catch (ParseException e1) 
						{
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "("+txfDate2.getText()+")입력 형식이 틀렸습니다.");
						}
						setVisible(false);
						dispose();
					}else
					{

						DefaultListModel portModel=(DefaultListModel) portLi.getModel();

						for(int i=0;i<portModel.getSize();i++)
						{
							String port_name=(String) portModel.get(i);
							TablePort port = new TablePort();
							port.setParent_port(port_name);
							port.setPort_type(TablePort.TYPE_PARENT);
							port.setPort_name(port_name);
							port.setPort_index(i+1);
							port.setTable_id(table_id);
							if(tableService.getTablePort(port)!=null)
							{
								try{
									tableService.updateTablePort(port);
								}catch(SQLException e1)
								{
									e1.printStackTrace();
								}
							}
							else
							{
								try{
									tableService.insertPortList(port);
								}catch(SQLException ee)
								{
									ee.printStackTrace();
								}
							}

						}
						String ports[]= new String[portModel.size()];


						for(int i=0;i<ports.length;i++)
						{
							ports[i]=(String) portModel.get(i);

						}

						String data= new String();
						data+="VESSEL\tVOY\t";
						for(int i=0;i<ports.length;i++)
						{
							data+=ports[i]+"\t";
						}
						data+="\n\n";

						ADVData adv = new ADVData();
						adv.setData(data);
						adv.setCompany_abbr(company_abbr);
						adv.setPage(page);
						adv.setT_index(t_index);
						adv.setTable_id(table_id);


						try {
							adv.setDate_isusse(KSGDateUtil.toDate2(txfDate2.getText()));
							advservice.insertADVData(adv);
							tableService.updateTableDate(adv);

						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						catch (ParseException e1) {
							JOptionPane.showMessageDialog(null, "("+txfDate2.getText()+")입력 형식이 틀렸습니다.");
						}
						setVisible(false);
						dispose();
					}

				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			}
		});
		pnRight.add(butOK);
		JButton butCancel = new JButton("취소");
		butCancel.setFont(defaultfont);
		butCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();						
			}
		});
		pnRight.add(butCancel);
		JPanel pn =new JPanel();
		pn.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblDate = new JLabel("입력날짜 : ");
		lblDate.setFont(defaultfont);
		pn.add(lblDate);

		JCheckBox cbxMonday= new JCheckBox("월요일");
		cbxMonday.setFont(defaultfont);
		cbxMonday.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfDate2.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
				}
			}
		});

		pn.add(txfDate2);
		pn.add(cbxMonday);
		this.getContentPane().add(pn,BorderLayout.NORTH);
		pnControl.add(pnRight,BorderLayout.EAST);
		this.getContentPane().add(pnControl,BorderLayout.SOUTH);
		this.getContentPane().add(pnMain,BorderLayout.CENTER);
		
		this.setSize(600,350);
		ViewUtil.center(this, true);
		this.setVisible(true);

	}


	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equals("삭제"))
		{
			int index=portLi.getSelectedIndex();
			if(index!=-1)
			{
				DefaultListModel model=(DefaultListModel) portLi.getModel();
				TablePort tablePort = new TablePort();
				tablePort.setTable_id(table_id);
				tablePort.setPort_name(String.valueOf(model.remove(index)));
				tablePort.setPort_type(TablePort.TYPE_PARENT);
				try {
					tableService.deleteTablePort(tablePort);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}else if(command.equals("위로"))
		{
			int index=portLi.getSelectedIndex();
			if(index==-1||index==0)
				return;

			DefaultListModel li=(DefaultListModel) portLi.getModel();

			Object selectedvalue=li.get(index);
			Object tempvalue=li.get(index-1);
			li.set(index, tempvalue);
			li.set(index-1, selectedvalue);
			portLi.setSelectedIndex(index-1);

		}else if(command.equals("아래로"))
		{
			int index=portLi.getSelectedIndex();
			if(index==-1||index==portLi.getModel().getSize()-1)
				return;

			DefaultListModel li=(DefaultListModel) portLi.getModel();
			Object selectedvalue=li.get(index);
			Object tempvalue=li.get(index+1);
			li.set(index, tempvalue);
			li.set(index+1, selectedvalue);
			portLi.setSelectedIndex(index+1);
		}
		else if(command.equals("추가"))
		{
			String portInfo = txfPort.getText();
			try {
				PortInfo port=baseService.getPortInfo(portInfo);
				if(port==null)
				{
					JOptionPane.showMessageDialog(null, "존재하지 않는 항구명입니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			DefaultListModel model= (DefaultListModel) portLi.getModel();
			model.addElement(portInfo);
			txfPort.setText("");
		}


	}

}

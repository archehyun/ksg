package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dtp.api.control.PortController;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.service.PortService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.label.BoldLabel;
import com.ksg.workbench.common.comp.panel.KSGPageTablePanel;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.master.dialog.UpdatePortInfoDialog;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : PnPort.java

 * @Date : 2021. 2. 25. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :항구 정보 관리 화면

 */
@Slf4j
public class PnPort extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox cbxPortArea,cbxAreaCode,cbxField;

	private JTextField txfSearch;

	private PortService portService = new PortServiceImpl();

	private AreaServiceImpl areaService = new AreaServiceImpl();

	private KSGPageTablePanel tableH;

	private KSGAbstractTable tableD;

	private JLabel lblPortName;

	private JLabel lblArea;

	private JLabel lblAreaCode;

	private JLabel lblPationality;

	public PnPort(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);

		this.addComponentListener(this);

		this.setController(new PortController());

		this.add(buildCenter());

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	}
	/**
	 * @return
	 */
	private JComponent buildSearchPanel() {

		KSGPanel pnSearch = new KSGPanel();

		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JLabel lbl = new JLabel("필드명 : ");

		cbxField = new JComboBox();

		cbxField.addItem("항구명");

		cbxField.addItem("나라");

		txfSearch = new JTextField(15);
		txfSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});


		JButton butUpSearch = new JButton("검색");

		butUpSearch.addActionListener(this);

		cbxField.setPreferredSize(new Dimension(150,23));

		JLabel lblArea = new JLabel("지역:");

		JLabel lblAreaCode = new JLabel("지역코드:");

		cbxPortArea = new JComboBox();

		cbxAreaCode = new JComboBox();

		cbxPortArea.setPreferredSize(new Dimension(300,25));

		cbxAreaCode.setPreferredSize(new Dimension(80,25));


		pnSearch.add(lblArea);

		pnSearch.add(cbxPortArea);

		pnSearch.add(lblAreaCode);

		pnSearch.add(cbxAreaCode);

		pnSearch.add(lbl);

		pnSearch.add(cbxField);

		pnSearch.add(txfSearch);

		pnSearch.add(butUpSearch);

		Box pnSearchAndCount = Box.createVerticalBox();

		pnSearchAndCount.add(pnSearch);

		KSGPanel pnMain= new KSGPanel(new BorderLayout());

		pnMain.add(buildLine(),BorderLayout.SOUTH);

		pnMain.add(pnSearchAndCount,BorderLayout.EAST);

		pnMain.add(buildTitleIcon("항구 정보"),BorderLayout.WEST);

		return pnMain;
	}


	private KSGPanel buildButton()
	{
		KSGPanel pnButtom = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		KSGPanel pnButtomRight = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		JButton butNewAbbr = new JButton("추가");

		butNewAbbr.setActionCommand("약어 등록");

		JButton butDelAbbr = new JButton("삭제");

		butDelAbbr.setActionCommand("약어 삭제");

		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		

		butNewAbbr.addActionListener(this);
		
		butDelAbbr.addActionListener(this);

		pnButtom.add(pnButtomRight);
		return pnButtom;
	}

	private KSGPanel addComp(String name, JComponent comp)
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		
		layout.setHgap(5);		
		
		KSGPanel pnMain = new KSGPanel(layout);

		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JLabel lblName = new JLabel(name);

		Font font = lblName.getFont();
		
		Font newFont = new Font(font.getFontName(),Font.BOLD,font.getSize());

		lblName.setFont(newFont);

		Dimension siz = lblName.getPreferredSize();
		
		lblName.setPreferredSize(new Dimension(75, (int) siz.getHeight()));

		pnMain.add(lblName);
		
		pnMain.add(comp);
		
		return pnMain;


	}
	private KSGPanel createPortDetail()
	{		
		lblPortName = new JLabel();
		lblArea = new JLabel();
		lblAreaCode = new JLabel();
		lblPationality = new JLabel();

		lblPortName.setBackground(Color.WHITE);

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));		

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("항구상세정보"),BorderLayout.WEST);


		KSGPanel pnControl = new KSGPanel(new FlowLayout());
		JButton butNewAbbr = new JButton("추가");
		butNewAbbr.setActionCommand("약어 등록");
		JButton butDelAbbr = new JButton("삭제");
		butDelAbbr.setActionCommand("약어 삭제");

		pnControl.add(butNewAbbr);
		pnControl.add(butDelAbbr);

		butNewAbbr.addActionListener(this);
		butDelAbbr.addActionListener(this);

		pnTitle.add(pnControl,BorderLayout.EAST);

		KSGPanel pnSubMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnPortInfo = new KSGPanel(new GridLayout(4,1,2,2));

		pnPortInfo.add(addComp("항구명",lblPortName));
		pnPortInfo.add(addComp("나라",lblPationality));
		pnPortInfo.add(addComp("지역",lblArea));
		pnPortInfo.add(addComp("지역코드",lblAreaCode));

		tableD = new KSGAbstractTable();

		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns = new KSGTableColumn();
		dcolumns.columnField = "port_abbr";
		dcolumns.columnName = "항구명 약어";

		tableD.addColumn(dcolumns);
		tableD.initComp();

		pnSubMain.add(pnPortInfo,BorderLayout.NORTH);
		pnSubMain.add(new JScrollPane(tableD));

		pnMain.add(pnTitle,BorderLayout.NORTH);
		pnMain.add(pnSubMain);
		tableD.getParent().setBackground(Color.white);

		return pnMain;
	}


	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGPageTablePanel("항구목록");

		tableH.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if(!e.getValueIsAdjusting())
				{
					String portName=(String) tableH.getValueAt(e.getFirstIndex(), 0);

					String pationality = (String) tableH.getValueAt(e.getFirstIndex(), 1);

					String area = (String) tableH.getValueAt(e.getFirstIndex(), 2);

					String areaCode = (String) tableH.getValueAt(e.getFirstIndex(), 3);

					lblPortName.setText(portName);
					lblPationality.setText(pationality);
					lblArea.setText(area);
					lblAreaCode.setText(areaCode);

					HashMap<String, Object> commandMap = new HashMap<String, Object>();

					commandMap.put("port_name", portName);

					try {
						List li=portService.selectPortAbbrList(commandMap);
						tableD.setResultData(li);
					} catch (Exception e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(PnPort.this, e1.getMessage());
					}

				}
			}
		});



		KSGPanel pnMainCenter = new KSGPanel(new BorderLayout(5,5));

		pnMainCenter.add(tableH);

		pnMainCenter.add(createPortDetail(),BorderLayout.EAST);	

		KSGTableColumn columns[] = new KSGTableColumn[5];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "port_name";
		columns[0].columnName = "항구명";
		columns[0].size = 300;
		columns[0].ALIGNMENT = SwingConstants.LEFT;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "port_nationality";
		columns[1].columnName = "나라";
		columns[1].size = 300;
		columns[1].ALIGNMENT = SwingConstants.LEFT;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "port_area";
		columns[2].columnName = "지역";
		columns[2].size = 300;
		columns[2].ALIGNMENT = SwingConstants.LEFT;

		columns[3] = new KSGTableColumn();
		columns[3].columnField = "area_code";
		columns[3].columnName = "지역코드";
		columns[3].size = 100;

		columns[4] = new KSGTableColumn();
		columns[4].columnField = "abbr_count";
		columns[4].columnName = "상세수";
		columns[4].size = 50;

		tableH.setColumnName(columns);

		tableH.initComp();

		tableH.setPageCountIndex(6);

		tableH.addPageActionListener(new PageAction(tableH, portService));

		tableH.setShowControl(true);

		tableH.addMouseListener(new TableSelectListner());

		tableH.addContorlListener(this);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(pnMainCenter);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		return pnMain;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("검색"))
		{
			this.fnSearch();
		}
		else if(command.equals(KSGTablePanel.INSERT))
		{
			KSGDialog dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.INSERT);
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				this.fnSearch();
			}
		}
		else if(command.equals("약어 등록"))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;
			String port_name=(String) tableH.getValueAt(row, 0);

			KSGDialog dialog = new InsertPortAbbrInfoDialog(getBaseInfoUI(),port_name);
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				try {
					int hrow = tableH.getSelectedRow();

					HashMap<String, Object> param2 = new HashMap<String, Object>();
					param2.put("port_name", port_name);
					tableD.setResultData(portService.selectPortAbbrList(param2));
				}catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
		else if(command.equals(KSGTablePanel.DELETE))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			String data = (String) tableH.getValueAt(row, 0);
			int result=JOptionPane.showConfirmDialog(PnPort.this, data+"를 삭제 하시겠습니까?", "항구 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{						
				try {
					System.out.println("delete ");
					CommandMap param = new CommandMap();

					param.put("port_name", data);

					portService.delete(param);

					this.fnSearch();

				}
				catch (ResourceNotFoundException e1) {
					e1.printStackTrace();
				}
				catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(PnPort.this, e1.getMessage());
				}
			}
			else
			{
				System.out.println("no select");
			}
		}
		else if(command.equals("약어 삭제"))
		{
			int row=tableD.getSelectedRow();
			if(row<0)
				return;
			String data = (String) tableD.getValueAt(row, 0);
			int result=JOptionPane.showConfirmDialog(this, data+"를 삭제 하시겠습니까?", "항구 약어 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					CommandMap param = new CommandMap();

					param.put("port_abbr", data);

					int count=(int) portService.deleteDetail(param);
					if(count>0)
					{
						int hrow = tableH.getSelectedRow();

						String port_name =(String) tableH.getValueAt(hrow,0);

						HashMap<String, Object> param2 = new HashMap<String, Object>();
						param2.put("port_name", port_name);
						tableD.setResultData(portService.selectPortAbbrList(param2));

					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}
	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;

		String portName;

		String pationality;

		String area;

		String areaCode;

		public void mouseClicked(MouseEvent e) 
		{	
			try
			{
				JTable es = (JTable) e.getSource();

				int row=es.getSelectedRow();
				if(row<0)
					return;

				if(e.getClickCount()>0)
				{
					CommandMap param = (CommandMap) tableH.getValueAt(row);

					lblPortName.setText((String) param.get("port_name"));

					lblPationality.setText((String) param.get("port_nationality"));

					lblArea.setText((String) param.get("port_area"));

					lblAreaCode.setText((String) param.get("area_code"));

					HashMap<String, Object> commandMap = new HashMap<String, Object>();

					commandMap.put("port_name", param.get("port_name"));
					
					

					List li=portService.selectPortAbbrList(commandMap);

										
					PnPort.this.callApi("selectPortDetailList", param);
				}


				if(e.getClickCount()>1)
				{				
					HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

					dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.UPDATE,param);

					dialog.createAndUpdateUI();

					if(dialog.result==KSGDialog.SUCCESS)
					{
						fnSearch();
					}
				}

			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(PnPort.this, ee.getMessage());
			}

		}

	}


	@Override
	public void fnSearch() {


		CommandMap param = new CommandMap();

		if(cbxAreaCode.getSelectedIndex()>0)
		{
			param.put("area_code", cbxAreaCode.getSelectedItem());
		}

		if(cbxPortArea.getSelectedIndex()>0)
		{
			param.put("port_area", cbxPortArea.getSelectedItem());
		}
		String field = (String) cbxField.getSelectedItem();


		String searchParam = txfSearch.getText();

		if(!"".equals(searchParam))
		{
			if(field.equals("항구명"))
			{
				param.put("port_name", searchParam);

			}else if(field.equals("나라"))
			{
				param.put("port_nationality", searchParam);
			}	
		}			


		try {


			log.info("param:"+param);


			callApi("selectPort", param);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void componentShown(ComponentEvent e) {

		try {

			initComboBox(cbxPortArea, areaService.getAreaListGroupByAreaName());

			initComboBox(cbxAreaCode, areaService.getAreaListGroupByAreaCode());


		} catch (Exception ee) {

			JOptionPane.showMessageDialog(PnPort.this, ee.getMessage());
			ee.printStackTrace();
		}



		if(isShowData) fnSearch();

	}
	private void initComboBox(JComboBox combox, List list)	
	{
		combox.removeAllItems();

		combox.addItem("선택");

		list.stream().forEach(item -> combox.addItem(item));


	}
	@Override
	public void updateView() {
		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
			
			String serviceId=(String) result.get("serviceId");
			List data = (List )result.get("data");
			
			if("selectPort".equals(serviceId))
			{
				
				
				tableH.setResultData(data);

				if(data.size()==0)tableH.changeSelection(0,0,false,false);

				if(data.size()==0)
				{
					lblArea.setText("");
					lblAreaCode.setText("");
					lblPationality.setText("");
					lblPortName.setText("");
					tableD.clearReslult();
				}
				else
				{
					tableH.changeSelection(0,0,false,false);
				}
			}
			else if("selectPortDetailList".equals(serviceId))
			{
				tableD.setResultData(data);

			}

		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}

	}


}

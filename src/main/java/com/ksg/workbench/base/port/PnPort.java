package com.ksg.workbench.base.port;

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
import java.util.Iterator;
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

import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.port.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.base.port.dialog.UpdatePortInfoDialog;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

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

	private JLabel lblTotal,lblTable;

	private JComboBox cbxPortArea,cbxAreaCode,cbxField;

	private JTextField txfSearch;
	
	private PortServiceImpl portService = new PortServiceImpl();
	
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
					//searchData();
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
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
		columns[4].columnField = "ABBR_COUNT";
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
			int result=JOptionPane.showConfirmDialog(null, data+"를 삭제 하시겠습니까?", "항구 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{						
				try {
					
					HashMap<String, Object> param = new HashMap<String, Object>();
					
					param.put("port_name", data);
					
					int count=portService.delete(param);
					
					if(count>0)
					{						
						this.fnSearch();
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
					HashMap<String, Object> param = new HashMap<String, Object>();
					
					param.put("port_abbr", data);
					
					int count=portService.deletePortAbbr(param);
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
			
			
			JTable es = (JTable) e.getSource();
			
			int row=es.getSelectedRow();
			if(row<0)
				return;
			
			if(e.getClickCount()>0)
			{
				
				HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);
				
				lblPortName.setText((String) param.get("port_name"));
				
				lblPationality.setText((String) param.get("port_nationality"));
				
				lblArea.setText((String) param.get("port_area"));
				
				lblAreaCode.setText((String) param.get("area_code"));
				
				HashMap<String, Object> commandMap = new HashMap<String, Object>();
				
				commandMap.put("port_name", param.get("port_name"));
				
				try {
					List li=portService.selectPortAbbrList(commandMap);
					tableD.setResultData(li);
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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

	}
	

	@Override
	public void fnSearch() {
		
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
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
		
			

			int page_size = tableH.getPageSize();
			
			param.put("PAGE_SIZE", page_size);
			
			param.put("PAGE_NO", 1);
			
			
			log.info("param:"+param);
			
			HashMap<String, Object> result = (HashMap<String, Object>) portService.selectListByPage(param);
			
			result.put("PAGE_NO", 1);
			
			tableH.setResultData(result);
			
			List master = (List) result.get("master");

			if(master.size()==0)
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
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
		try {
			cbxPortArea.addItem("선택");
			cbxAreaCode.addItem("선택");
			List listArea = areaService.getAreaListGroupByAreaName();
			List listAreaCode = areaService.getAreaListGroupByAreaCode();
			Iterator areaIter = listArea.iterator();
			while(areaIter.hasNext())
			{
				String area = (String) areaIter.next();
				cbxPortArea.addItem(area);
			}

			Iterator areaCodeIter = listAreaCode.iterator();
			while(areaCodeIter.hasNext())
			{
				String code = (String) areaCodeIter.next();
				cbxAreaCode.addItem(code);
			}

		} catch (SQLException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}

		
		
		if(isShowData) fnSearch();
		
	}

	


}

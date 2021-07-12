package com.ksg.shippertable.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.shippertable.service.impl.ShipperTableService;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;

@SuppressWarnings("serial")
public class ShipperTableView extends KSGPanel implements ComponentListener, ActionListener{
	
	private KSGTablePanel tableH;
	
	private String tiltle;
	
	private String path;
	
	HashMap<String, Object> param = new HashMap<String, Object>();
	
	ShipperTableService shipperTableService;

	private JComboBox cbxField;

	private JComboBox cbxGubun;

	private JTextField txfSearch;

	private JTextField txfInputDate;
	public ShipperTableView() {
		
		super();
		
		this.setLayout(new BorderLayout());
		
		shipperTableService = new ShipperTableService();
				
		this.addComponentListener(this);
		
		this.path ="광고정보";

		this.tiltle ="광고정보고";

		this.add(createNavigate(),BorderLayout.NORTH);

		this.add(buildCenter());
		
	}
	private Component buildCenter() {
		
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGTablePanel("항구목록");
		
		tableH.addColumn(new KSGTableColumn("page","페이지"));
		tableH.addColumn(new KSGTableColumn("table_id","테이블ID"));
		tableH.addColumn(new KSGTableColumn("gubun","구분"));
		tableH.addColumn(new KSGTableColumn("title","제목"));
		tableH.addColumn(new KSGTableColumn("agent","에이전트"));
		
		tableH.initComp();
		
		pnMain.add(tableH);
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		
		
		return pnMain;
	}
	protected KSGPanel createNavigate()
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		BoldLabel lblTitle = new BoldLabel(path+">>"+tiltle);
		
		pnMain.add(lblTitle);
		
		return pnMain;
		
	}
	
	
	/**
	 * @return
	 */
	private JPanel buildSearchPanel() {
		
		
		KSGPanel pnMain= new KSGPanel(new BorderLayout());
		
		
		KSGPanel pnSearch = new KSGPanel(new FlowLayout(FlowLayout.LEFT	));
		
		
		cbxGubun = new JComboBox();
		cbxGubun.addItem("전체");
		cbxGubun.addItem("Normal");
		cbxGubun.addItem("TS");
		cbxGubun.addItem("Console");
		cbxGubun.addItem("Inland");
		
		cbxField = new JComboBox();		
		cbxField.addItem("페이지");
		cbxField.addItem("테이블ID");
		cbxField.addItem("선사명");
		cbxField.addItem("에이전트");
		cbxField.addItem("인덱스");
		
		txfSearch = new JTextField(15);
		
		txfInputDate = new JTextField(15);

		txfSearch.addKeyListener(new KeyAdapter() {


			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});
		
		JLabel label = new JLabel("개 항목");
		
		JButton butUpSearch = new JButton("검색");
		
		butUpSearch.addActionListener(this);
		
		
		pnSearch.add(new JLabel("테이블 구분"));
		
		pnSearch.add(cbxGubun);
		
		pnSearch.add(new JLabel("항목"));
		
		pnSearch.add(cbxField);
		
		pnSearch.add(txfSearch);
		
		pnSearch.add(new JLabel("입력일자"));
		pnSearch.add(txfInputDate);
		
		
		
		KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		pnControl.add(butUpSearch);
		
		
		
		pnMain.add(pnSearch,BorderLayout.WEST);
		pnMain.add(pnControl,BorderLayout.EAST);
		return pnMain;
	}
	
	
	public void fnSearch() {

		try {

			setParam();

			HashMap<String, Object> result = (HashMap<String, Object>) shipperTableService.selectShipperTableList(param);

			tableH.setResultData(result);

			List master = (List) result.get("master");

			if(master.size()==0)
			{
				/*
				 * lblArea.setText(""); lblAreaCode.setText(""); lblPationality.setText("");
				 * lblPortName.setText(""); tableD.clearReslult();
				 */
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
	private void setParam() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		this.fnSearch();
		
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}

package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.view.comp.table.KSGTablePanel;

/**

  * @FileName : KSGPageTablePanel.java

  * @Project : KSG2

  * @Date : 2022. 3. 7. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGPageTablePanel extends KSGTablePanel{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTotalPage;
	private JComboBox<String> cbxPageCount;
	private JButton butFirst;
	private JButton butFw;
	private JTextField txfPage;
	private JButton butBw;
	private JButton butEnd;
	private int pageSize;
	private int totalPage;
	public int getTotalPage() {
		return totalPage;
	}
	
	private int page;
	private int endPage;
	
	
	
	public KSGPageTablePanel(String title) {
		super(title);
		
		this.add(createPagingPanel(),BorderLayout.SOUTH);
		
		
		
		
	}


	private JPanel createPagingPanel() {
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lbl = new JLabel("page:");
		lblTotalPage = new JLabel();
		lblTotalPage.setPreferredSize(new Dimension(25, 25));

		cbxPageCount = new JComboBox<String>();
		cbxPageCount.addItem("20");
		cbxPageCount.addItem("50");
		cbxPageCount.addItem("100");
		cbxPageCount.addItem("500");
		cbxPageCount.addItem("1000");
		cbxPageCount.addItem("2000");

		butFirst = new JButton("<<");
		butFirst.setActionCommand("First");
		butFw = new JButton("<");
		butFw.setActionCommand("Forword");

		txfPage = new JTextField(2);

		txfPage.setText("1");

		butBw = new JButton(">");
		butBw.setActionCommand("Next");

		butEnd = new JButton(">>");
		butEnd.setActionCommand("Last");

		pnMain.add(butFirst);
		pnMain.add(butFw);
		pnMain.add(lbl);
		pnMain.add(txfPage);
		pnMain.add(lblTotalPage);
		pnMain.add(butBw);
		pnMain.add(butEnd);
		pnMain.add(cbxPageCount);
		//pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return pnMain;
	}

	@Override
	public void setResultData(HashMap<String, Object> resultMap) {
		
		

		super.setResultData(resultMap);

		pageSize = Integer.parseInt(cbxPageCount.getSelectedItem().toString());

		totalPage = total / pageSize;

		if (total % pageSize > 0) {
		    totalPage++;
		}

		lblTotalPage.setText(String.valueOf(totalPage));

		page = (Integer) resultMap.get("PAGE_NO");

		if (totalPage < page) {
			page = totalPage;
		}

		txfPage.setText(String.valueOf(page));

		int startPage = ((page - 1) / pageSize ) * pageSize  + 1;
		endPage = startPage + pageSize  - 1;


		if (endPage > totalPage) {
			endPage = totalPage;
		}
	}
	
	
	public void addPageActionListener(ActionListener l)	
	{
		this.butEnd.addActionListener(l);
		this.butFirst.addActionListener(l);
		this.butBw.addActionListener(l);
		this.butFw.addActionListener(l);
	}
	public void addActionListener(ActionListener l) {
		this.butEnd.addActionListener(l);
		this.butFirst.addActionListener(l);
		this.butBw.addActionListener(l);
		this.butFw.addActionListener(l);
	}

	public int getPageSize() {
		return Integer.parseInt(cbxPageCount.getSelectedItem().toString());
	}
	public int getPage() {
		return page;
	}


}

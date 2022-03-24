package com.ksg.workbench.common.comp;

import java.awt.BorderLayout;
import java.awt.Color;
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

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :

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
	
	
	
	/**
	 * 
	 * ������ ���̺� ������
	 * 
	 * @param title
	 */
	public KSGPageTablePanel(String title) {
		super(title);
		
		this.add(createPagingPanel(),BorderLayout.SOUTH);
		
		//TO-DO TEST
		
		
		
		
	}
	
	public void setPageCountIndex(int index)
	{
		cbxPageCount.setSelectedIndex(index);
	}


	/**
	 * 
	 * 
	 * @return
	 */
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
		cbxPageCount.addItem("5000");

		butFirst = createButton("<<", "First");		
		
		butFw = createButton("<", "Forword");

		txfPage = new JTextField(2);
		txfPage.setEditable(false);

		txfPage.setText("1");

		butBw = createButton(">", "Next"); 
		
		
		butBw .setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		butEnd = createButton(">>", "Last");

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
	
	private JButton createButton(String title, String command)
	{
		
		JButton but = new JButton(title);
		
		but.setActionCommand(command);
		return but;
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

		//TO-TO ���� �߻� ���� �� ����
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

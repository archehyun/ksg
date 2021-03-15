package com.ksg.common.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class KSGTablePanel extends KSGPanel{
	
	private int total;

	private KSGTable table;

	private String title;

	private JLabel lblTotalCount;

	public void setTitle(String title) {
		this.title = title;
	}

	public KSGTablePanel() {
		super();
		this.setLayout(new BorderLayout(5,5));

		table = new KSGTable();

		this.add(new JScrollPane(table));

		table.getParent().setBackground(Color.white);
	}

	public KSGTablePanel(String title) {

		this();
		this.title = title;
		this.add(createTitle(), BorderLayout.NORTH);
		
	}
	
	public void setAutoResizeMode(int mode)
	{
		table.setAutoResizeMode(mode);
	}
	
	 public ListSelectionModel getSelectionModel() {
	        return table.getSelectionModel();
	    }
	
	public KSGTablePanel(String title,boolean paging) {

		this(title);
	}

	public JComponent createTitle() {
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		BoldLabel lblTitle = new BoldLabel(title + " ÃÑ");
		
		lblTotalCount = new JLabel("0");
		lblTotalCount.setForeground(Color.red);
		
		
		pnTitle.add(lblTitle);
		pnTitle.add(lblTotalCount);
		pnTitle.add(new JLabel("°Ç"));

		return pnTitle;
	}

	public void setColumnName(KSGTableColumn columnNames[]) {
		table.setColumnName(columnNames);
	}

	public void setResultData(List resultData) {

		table.setResultData(resultData);
	}

	public int getSelectedRow() {
		// TODO Auto-generated method stub
		return table.getSelectedRow();
	}

	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return table.getValueAt(row, col);
	}

	public void initComp() {
		table.initComp();

	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		
		super.addMouseListener(l);

		table.addMouseListener(l);
	}
	
	public synchronized void addKeyListener(KeyListener l)
	{
		super.addKeyListener(l);
		table.addKeyListener(l);
	}

	public void addColumn(KSGTableColumn ksgTableColumn) {
		table.addColumn(ksgTableColumn);

	}

	public Object getValueAt(int rowIndex, String columnField) {
		// TODO Auto-generated method stub
		return table.getValueAt(rowIndex, columnField);
	}

	public Object getValueAt(int rowIndex) {
		// TODO Auto-generated method stub
		return table.getValueAt(rowIndex);
	}

	public void setResultData(HashMap<String, Object> resultMap) {
		
		List master = (List) resultMap.get("master");

		table.setResultData(master);

		total = (Integer) resultMap.get("total");
		
		
		if(lblTotalCount!=null)
		lblTotalCount.setText(String.valueOf(master.size())+"/"+total);
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		table.changeSelection(rowIndex, columnIndex, toggle, extend);
		
	}
	
	public void  clearResult()
	{
		table.clearReslult();
	}

}

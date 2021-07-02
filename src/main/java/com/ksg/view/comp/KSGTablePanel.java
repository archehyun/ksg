package com.ksg.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.ksg.common.model.KSGModelManager;

@SuppressWarnings("serial")
public class KSGTablePanel extends KSGPanel{
	
	
	public static final String INSERT="insert";
	public static final String DELETE="delete";
	public static final String UPDATE="update";
	
	
	private int total;

	private KSGAbstractTable table;

	private String title;

	private JLabel lblTotalCount;
	
	private boolean showControl=false;

	private JButton butInsert;

	private JButton butDelete;

	private JButton butUpdate;
	private KSGPanel pnControl;

	public void setShowControl(boolean showControl) {
		this.showControl = showControl;
		pnControl.setVisible(showControl);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public KSGTablePanel() {
		super();
		
		this.setLayout(new BorderLayout(5,5));

		table = new KSGAbstractTable();

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
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		BoldLabel lblTitle = new BoldLabel(title + " 총");
		
		butInsert = new JButton("추가");
		butDelete = new JButton("삭제");
		butUpdate = new JButton("수정");
		
		butInsert.setActionCommand(INSERT);
		butDelete.setActionCommand(DELETE);
		butUpdate.setActionCommand(UPDATE);
		
		lblTotalCount = new JLabel("0");
		lblTotalCount.setForeground(Color.red);
		
		
		pnControl.add(butInsert);
		pnControl.add(butUpdate);
		pnControl.add(butDelete);
		
		pnTitle.add(lblTitle);
		pnTitle.add(lblTotalCount);
		pnTitle.add(new JLabel("건"));
		
		
		pnMain.add(pnTitle,BorderLayout.LINE_START);
		pnMain.add(pnControl,BorderLayout.LINE_END);
		pnControl.setVisible(showControl);

		return pnMain;
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
	
	public void addContorlListener(ActionListener l)
	{
		butDelete.addActionListener(l);
		butInsert.addActionListener(l);
		butUpdate.addActionListener(l);
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

	@Override
	public void update(KSGModelManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAndUpdateUI() {
		// TODO Auto-generated method stub
		
	}

}

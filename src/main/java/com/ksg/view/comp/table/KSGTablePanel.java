package com.ksg.view.comp.table;

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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.groupable.GroupableTableHeader;
import com.ksg.view.comp.table.model.TableModel;

import mycomp.comp.MyTable;




/**

 * @FileName : KSGTablePanel.java

 * @Project : KSG2

 * @Date : 2021. 12. 29. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
@SuppressWarnings("serial")
public class KSGTablePanel extends KSGPanel{ 
	public static final String INSERT="insert";

	public static final String DELETE="delete";

	public static final String UPDATE="update";


	protected int total;

	private MyTable table;

	private String title;

	private JLabel lblTotalCount;

	private boolean showControl=false;

	private JButton butInsert;

	private JButton butDelete;	

	private KSGPanel pnControl;
	
	public MyTable getTable()
	{
		return table;
	}

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
//		table = new KSGAbstractTable();
		table = new KSGAbstractTable()
		{
			protected JTableHeader createDefaultTableHeader() {
	              return new GroupableTableHeader(columnModel);
	          }
		};

		table.setGridColor(Color.lightGray);

		this.add(new JScrollPane(table));

		table.getParent().setBackground(Color.white);
	}
	
	public void setOdd(boolean isOdd)
	{
		table.setOdd(isOdd);
	}

	public KSGTablePanel(TableModel model) {

		super();

		this.setLayout(new BorderLayout(5,5));

		table = new KSGAbstractTable(model);

		table.setGridColor(Color.lightGray);

		this.add(new JScrollPane(table));

		table.getParent().setBackground(Color.white);

	}

	public KSGTablePanel(String title) {

		this();
		this.title = title;
		this.add(createTitle(), BorderLayout.NORTH);

	}

	public KSGTablePanel(String title, TableModel model) {

		this(model);
		this.title = title;
		this.add(createTitle(), BorderLayout.NORTH);

	}

	public void setSelectionMode(int SINGLE_SELECTION)
	{
		table.setSelectionMode(SINGLE_SELECTION);
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

		butInsert = new KSGGradientButton("추가");

		butDelete = new KSGGradientButton("삭제");

		//butUpdate = new JButton("수정");


		//butInsert.setBackground(Color.BLUE);
		//butInsert.setForeground(Color.white);

		butInsert.setActionCommand(INSERT);
		butDelete.setActionCommand(DELETE);
		//butUpdate.setActionCommand(UPDATE);

		lblTotalCount = new JLabel("0");
		lblTotalCount.setForeground(Color.red);


		pnControl.add(butInsert);
		//pnControl.add(butUpdate);
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
		lblTotalCount.setText(String.valueOf(resultData.size()));
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	public Object getValueAt(int row, int col) {
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
		//butUpdate.addActionListener(l);
	}

	public void addColumn(KSGTableColumn ksgTableColumn) {
		table.addColumn(ksgTableColumn);

	}

	public Object getValueAt(int rowIndex, String columnField) {
		return table.getValueAt(rowIndex, columnField);
	}

	public Object getValueAt(int rowIndex) {
		return table.getValueAt(rowIndex);
	}

	public void setResultData(HashMap<String, Object> resultMap) {

		List master = (List) resultMap.get("master");

		table.setResultData(master);

		try {

			total = (Integer) resultMap.get("total");
		}catch(Exception e)
		{
			total=0;
		}


		if(lblTotalCount!=null)

			lblTotalCount.setText(String.valueOf(master.size())+"/"+total);
	}
	
	public void setTotalCount(String total)
	{
		lblTotalCount.setText(total);
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		table.changeSelection(rowIndex, columnIndex, toggle, extend);

	}
	public void setComponentPopupMenu(JPopupMenu popup) 
	{
		super.setComponentPopupMenu(popup);
		table.setComponentPopupMenu(popup);
	}


	public void  clearResult()
	{
		table.clearReslult();
	}


	@Override
	public void createAndUpdateUI() {

	}

	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}




}

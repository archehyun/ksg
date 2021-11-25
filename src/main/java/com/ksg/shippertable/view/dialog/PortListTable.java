package com.ksg.shippertable.view.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.TablePort;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.shippertable.view.dialog.ManagePortDialog.TablePortTransable;

/**
 * @설명 항구 정보 조정
 * @author 박창현
 *
 */
public class PortListTable extends JTable implements DropTargetListener,  DragGestureListener, DragSourceListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int selectedcolIndex;

	private static final int PORT_INDEX_COLUM = 0;

	private static final int PORT_NAME_COLUM = 1;

	protected Logger 			logger = Logger.getLogger(getClass());

	protected TableService tableService;

	private TablePortTransable portTransable;

	public int selectedPortIndex;

	private String table_id;

	private DragSource dragSource; // A central DnD object

	private DropTarget dtg;	

	protected BaseService baseService;

	PortListTableModel portTableModel;

	private int portMaxIndex;

	public int selectedindex;

	public PortListTable(String table_id) {

		this.table_id = table_id;

		portTransable = new TablePortTransable();

		tableService = new TableServiceImpl();

		baseService  = new BaseServiceImpl();

		dtg = new DropTarget(this, this);
		
		this.setRowHeight(25);

		setName(TablePort.TYPE_PARENT);

		initTable();	

	}

	/**
	 * @throws SQLException 
	 * 
	 */
	private void initTable()
	{
		portTableModel = new PortListTableModel();

		setModel(portTableModel);

		TableColumnModel colModel=getColumnModel();

		TableColumn col=colModel.getColumn(PORT_INDEX_COLUM);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		col.setMaxWidth(50);

		col.setCellRenderer(renderer);

		col.setCellEditor(new DefaultCellEditor(new JTextField()));
		col.getCellEditor().addCellEditorListener(new MyCellEditerListner());
		try {
			retrive();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			updateUI();
		}
	}
	/**
	 * 
	 * 조회
	 * 
	 * @throws SQLException
	 */
	public void retrive() throws SQLException {


		portTableModel.setPortli(tableService.getParentPortList(this.table_id));

		portMaxIndex=tableService.getMaxPortIndex(this.table_id);

		updateUI();

	}

	/**
	 * @param onePortInfo	
	 * @param twoPortInfo
	 * @throws SQLException
	 */
	public void chagePortIndex(TablePort onePortInfo, TablePort twoPortInfo) throws SQLException
	{
		int oneIndex =onePortInfo.getPort_index();
		int twoIndex =twoPortInfo.getPort_index();

		onePortInfo.setPort_index(twoIndex);
		twoPortInfo.setPort_index(oneIndex);

		initTable();

	}

	/**
	 * @return
	 */
	public int getPortListSize()
	{
		return portTableModel.getPortli().size();
	}
	/**
	 * @param index
	 * @return
	 */
	public TablePort getTablePort(int index)
	{
		if(getPortListSize()>index)
		{
			return portTableModel.getPortli().get(index);
		}else			
		{
			throw new ArrayIndexOutOfBoundsException();	
		}		
	}
	/**
	 * 항구 정보 저장
	 * 
	 * 
	 * @throws SQLException
	 */
	public void save() throws SQLException
	{

		TablePort port = new TablePort();

		port.setTable_id(this.table_id);

		tableService.deleteTablePort(port);

		Iterator<TablePort> iter = portTableModel.getPortli().iterator();		

		while(iter.hasNext())
		{
			TablePort newport= iter.next();
			tableService.insertPortList(newport);
		}
	}

	/**
	 * @param port_name
	 * @throws SQLException
	 * @throws PortNullException
	 */
	public void insertPort(String port_name) throws SQLException, PortNullException
	{	
		int max=tableService.getMaxPortIndex(this.table_id);

		if(port_name.length()<=0||port_name==null||port_name.equals(""))
		{
			JOptionPane.showMessageDialog(null, "항구명을 입력하세요");
			return;
		}
		PortInfo info =baseService.getPortInfoByPortName(port_name);
		if(info==null)
		{
			Code code_info = new Code();
			code_info.setCode_name(port_name);
			baseService = new BaseServiceImpl();
			Code templi=	baseService.getCodeInfo(code_info);
			if(templi==null)
			{
				throw new PortNullException(port_name);
			}
		}
		TablePort port = new TablePort();
		port.setPort_type(TablePort.TYPE_PARENT);
		port.setPort_name(port_name);
		port.setParent_port(port_name);
		port.setTable_id(this.table_id);
		port.setPort_index(max+1);
		tableService.insertPortList(port);


	}

	/**20190219 신규 추가
	 * 바로 저장하지 않고 저장 및 닫기 누르면 반영하는 방식
	 * 
	 * 
	 * 20210122 대소문자 구분 추가(SQL)
	 * 
	 * @param portName
	 * @throws SQLException
	 */
	public void insertPortName(String portName) throws SQLException, PortNullException
	{	
		logger.info("insert port:"+portName);
		if(portName.length()<=0||portName==null||portName.equals(""))
		{
			JOptionPane.showMessageDialog(null, "항구명을 입력하세요");
			return;
		}
		
		
		PortInfo info =baseService.getPortInfoByPortName(portName);
		
		if(info==null)
		{
			
			logger.info("search excption:"+portName);
			Code code_info = new Code();
			code_info.setCode_name(portName);
			baseService = new BaseServiceImpl();
			Code templi=	baseService.getCodeInfo(code_info);
			if(templi==null)
			{
				throw new PortNullException(portName);
			}
		}
		
		
		portMaxIndex+=1;

		TablePort port = new TablePort();
		port.setPort_type(TablePort.TYPE_PARENT);
		port.setPort_name(portName);
		port.setParent_port(portName);
		port.setTable_id(this.table_id);
		port.setPort_index(portMaxIndex);
		portTableModel.getPortli().add(port);

		updateUI();
		
		logger.info("end insert port:"+portName);
	}

	/**
	 * 삭제
	 */
	public void delete(boolean isDeleteSubPort) throws SQLException{

		int row=getSelectedRow();
		if(row<-1)
			return;


		String port_name= (String) getValueAt(row, PORT_NAME_COLUM);
		if(port_name==null)
			return;

		TablePort port = new TablePort();

		port.setPort_name(port_name);

		port.setTable_id(table_id);

		Object temp_port_index = getValueAt(row, PORT_INDEX_COLUM);
		if(temp_port_index instanceof Integer)
		{
			port.setPort_index((Integer) temp_port_index);

		}else if(temp_port_index instanceof String)
		{
			port.setPort_index(Integer.valueOf( (String)temp_port_index));
		}
		if(getName().equals(TablePort.TYPE_PARENT))
		{
			port.setPort_type(TablePort.TYPE_PARENT);

			if(isDeleteSubPort)
			{						
				port.setPort_name(null);
			}
		}
		else
		{
			port.setPort_type(TablePort.TYPE_CHAILD);
		}

		tableService.deleteTablePort(port);

		changeSelection(row==0?0:row-1, PORT_NAME_COLUM, false, false);
	}

	/**
	 * <pre>
	 * 1. 개요 : 항구 정보 삭제
	 * 2. 처리 내용:
	 *    - 항구 선택(row=n)
	 *    - delete    
	 * </pre>
	 * @Method Name: deletePort
	 * @date: 2019. 08. 18
	 * @author 박창현
	 * @history:
	 * ---------------------------------------
	 * 변경일                    작성자         변경내용
	 * ---------------------------------------
	 * 2019. 08. 18  박창현         최조 작성
	 * 
	 * 2019. 09. 15  박창현         항구 삭제 후 삭제된 항구보다 큰 인덱스를 가진 항구의 인덱스를 -1 함
	 * 
	 * @param selectedPort 선택된 항국 정보
	 *
	 * 
	 */
	public void deletePort(TablePort selectedPort)
	{

		int selectedPortIndex = selectedPort.getPort_index();
		portTableModel.getPortli().remove(selectedPort);
		List<TablePort> li =portTableModel.getPortli();


		// 같은 항구 익덱스가 있는지 체크
		boolean isSamePortIndexExit=false;
		for(TablePort item:li)
		{
			if(item.getPort_index()==selectedPortIndex)
			{
				isSamePortIndexExit =true;
			}
		}
		//같은 항구 인덱스가 존재하지 않으면 실시
		if(!isSamePortIndexExit)
		{
			for(TablePort item:li)
			{
				if(item.getPort_index()>selectedPortIndex)
				{
					item.setPort_index(item.getPort_index()-1);
				}
			}
		}

		updateUI();
	}
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {

		System.out.println("endter");
	}
	@Override
	public void dragExit(DropTargetEvent dte) {
		System.out.println("exit");
	}

	TablePort changePort;

	public void dragOver(DropTargetDragEvent dtde) {

		int row=this.rowAtPoint(dtde.getLocation());

		if(portTableModel.getPortli().size()>row)
		{
			TablePort  portItem=portTableModel.getPortli().get(row);
			changePort = portItem;
		}
		else
		{
			changePort = null;
			return;

		}

		this.changeSelection(row, 0, false, false);


	}
	@Override
	public void drop(DropTargetDropEvent dtde) {

		if (dtde.isDataFlavorSupported(TablePortTransable.scribbleDataFlavor)
				|| dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		} else {
			dtde.rejectDrop();

			return;
		}

		Transferable t = dtde.getTransferable(); // Holds the dropped data
		// First, try to get the data directly as a scribble object
		TablePortTransable  droppedScribble;
		try {
			droppedScribble = (TablePortTransable) t
					.getTransferData(TablePortTransable.scribbleDataFlavor);


			if(changePort==null)
				return;


			chagePortIndex(droppedScribble.getTablePort(), changePort);
		} catch (Exception ex) { // unsupported flavor, IO exception, etc
			ex.printStackTrace();
			// If that doesn't work, try to get it as a String and parse it
			try {
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				//  droppedScribble = Scribble.parse(s);
			} catch (Exception ex2) {
				System.out.println("error drop");
				// If we still couldn't get the data, tell the system we failed
				dtde.dropComplete(false);
				return;
			}
		}  

	}
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {


	}
	@Override
	public void dragGestureRecognized(DragGestureEvent e) {

		MouseEvent inputEvent = (MouseEvent) e.getTriggerEvent();
		int x = inputEvent.getX();
		int y = inputEvent.getY();

		Cursor cursor;
		switch (e.getDragAction()) {
		case DnDConstants.ACTION_COPY:
			cursor = DragSource.DefaultCopyDrop;
			break;
		case DnDConstants.ACTION_MOVE:
			cursor = DragSource.DefaultMoveDrop;
			break;
		default:
			return; 
		}
		if (dragSource.isDragImageSupported()) {
			Image dragImage = this.createImage(100,
					25);
			Graphics2D g = (Graphics2D) dragImage.getGraphics();
			Rectangle scribbleBox = portTransable.getBounds();
			g.setColor(new Color(0, 0, 0, 0)); // transparent background
			g.fillRect(0, 0, 200, 25);
			g.setColor(Color.black);				
			g.drawString(portTransable.getTablePort().getPort_name(), 0, 15);
			g.translate(-scribbleBox.x, -scribbleBox.y);

			//g.draw(portTransable);

			Point hotspot = new Point(-scribbleBox.x, -scribbleBox.y);

			// Now start dragging, using the image.
			e.startDrag(cursor, dragImage, hotspot, portTransable, this);

		}
		else
		{
			e.startDrag(cursor, portTransable,this);
		}
		return;


	}
	@Override
	public void dragDropEnd(DragSourceDropEvent e) {
		if (!e.getDropSuccess())
			return;
		int action = e.getDropAction();
		if (action == DnDConstants.ACTION_MOVE) {
			/*scribbles.remove(beingDragged);
	      beingDragged = null;
	      repaint();*/
			//테이블 정보 업데이트
			System.out.println("테이블 정보 업데이트");


		}
	}
	@Override
	public void dragEnter(DragSourceDragEvent arg0) {}
	@Override
	public void dragExit(DragSourceEvent arg0) {}
	@Override
	public void dragOver(DragSourceDragEvent arg0) {}

	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {}

	public TablePort getSelectedPort() {
		int selectedRow = getSelectedRow();

		TablePort port=null;
		if(selectedRow<portTableModel.getPortli().size())
		{
			port = portTableModel.getPortli().get(selectedRow);
		}

		return port;

	}
	/**
	 * @author 박창현
	 *
	 */
	class PortListTableModel extends AbstractTableModel
	{

		boolean isEdit=false;

		public boolean isEdit() {
			return isEdit;
		}

		public void setEdit(boolean isEdit) {
			this.isEdit = isEdit;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"순서", "항구명"};

		public PortListTableModel() {
			portli = new LinkedList<TablePort>();

		}

		private List<TablePort> portli;

		private void moveRow(int start, int end)
		{
			TablePort temp = portli.get(end);
			TablePort startport = portli.get(start);
			portli.set(end, startport);
			portli.set(start,temp);
		}

		public void setPortli(List<TablePort> portli) {

			this.portli = portli;

		}
		public List<TablePort> getPortli() {
			return portli;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			//all cells false


			if(isEdit()&&getSelectedRow()==row&&getSelectedColumn()==PORT_INDEX_COLUM)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		public int getPortSize()
		{
			return portli.size();
		}
		public TablePort getPort(int index) {
			return portli.get(index);
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return portli.size();
		}


		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}


		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TablePort portItem = portli.get(rowIndex);

			Object value=null;
			switch (columnIndex) {
			case PORT_INDEX_COLUM:
				value = portItem.getPort_index();
				break;
			case PORT_NAME_COLUM:
				value = portItem.getPort_name();
				break;
			default:
				break;
			}
			return value;
		}
	}

	/**
	 * <pre>
	 * 1. 개요 : 항구 인덱스 변경
	 * 2. 처리 내용:
	 *    - 항구 선택(row=n)
	 *    - up 버튼 선택	
	 *    - 항구 위로 이동(row=n-1)
	 *    - 인덱스 변경
	 * </pre>
	 * @Method Name: movePort
	 * @date: 2019. 08. 17
	 * @author 박창현
	 * @history:
	 * ---------------------------------------
	 * 변경일                    작성자         변경내용
	 * ---------------------------------------
	 * 2018. 08. 17  박창현         최조 작성
	 * @param start 선택된 항구
	 * @param end 이동할 위치
	 * 
	 * 버튼을 클릭하면 항구 인덱스는 그대로 있고 항구의 위치만 변경
	 * 저장버튼을 누르면 변경된 정보가 DB에 저장됨
	 * 
	 */	
	public void movePort(int start, int end) {

		TablePort startPort=portTableModel.getPort(start);
		
		TablePort endPort=portTableModel.getPort(end);

		int startIndex = startPort.getPort_index();
		int endIndex = endPort.getPort_index();

		startPort.setPort_index(endIndex);
		endPort.setPort_index(startIndex);

		portTableModel.moveRow(start, end);


	}

	private static void rotate(Vector v, int a, int b, int shift) {
		int size = b - a;
		int r = size - shift;
		int g = gcd(size, r);
		for(int i = 0; i < g; i++) {
			int to = i;
			Object tmp = v.elementAt(a + to);
			for(int from = (to + r) % size; from != i; from = (to + r) % size) {
				v.setElementAt(v.elementAt(a + from), a + to);
				to = from;
			}
			v.setElementAt(tmp, a + to);
		}
	}
	private static int gcd(int i, int j) {
		return (j == 0) ? i : gcd(j, i%j);
	}

	public void setEdit(boolean b) {
		portTableModel.setEdit(b);		
	}

	@Override // Always selectAll()
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		final Component editor = getEditorComponent();
		if (editor == null || !(editor instanceof JTextComponent)) {
			return result;
		}
		if (e instanceof MouseEvent) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					((JTextComponent) editor).selectAll();
				}
			});
		} else {
			((JTextComponent) editor).selectAll();
		}
		return result;
	}
	/**
	 * @author 박창현
	 *
	 */
	class MyCellEditerListner implements CellEditorListener
	{

		@Override
		public void editingStopped(ChangeEvent e) {
			int row = PortListTable.this.getSelectedRow();
			int column =PortListTable.this.getSelectedColumn();
			JTextField ff =(JTextField) PortListTable.this.getEditorComponent();


			TableCellEditor editor = (TableCellEditor) e.getSource();

			try {
				int portIndex = Integer.parseInt((String) editor.getCellEditorValue());
				TablePort port=portTableModel.getPort(row);


				port.setPort_index(portIndex);

			}catch(Exception ee)
			{
				JOptionPane.showMessageDialog(PortListTable.this, "숫자만 입력하십시요");
				return;
			}
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			// TODO Auto-generated method stub

		}
	}

	class MouseAction extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e) {

			int row = PortListTable.this.getSelectedRow();
			if(row<0||portTableModel.getPortli().size()<row-1)
				return;

			try {

				TablePort portInfo = portTableModel.getPortli().get(row);

				PortListTable.this.portTransable.setTablePort(portInfo);

				portTransable.moveto(e.getX(), e.getY());

			}catch(Exception ee)
			{
				//JOptionPane.showConfirmDialog(null, ee.getMessage());
			}
		}		
	}

}

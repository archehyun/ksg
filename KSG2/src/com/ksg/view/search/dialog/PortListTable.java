package com.ksg.view.search.dialog;

import java.awt.Color;
import java.awt.Cursor;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.TablePort;
import com.ksg.schedule.build.PortNullException;
import com.ksg.view.search.dialog.ManagePortDialog.TablePortTransable;

public class PortListTable extends JTable implements DropTargetListener,  DragGestureListener, MouseListener, DragSourceListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int selectedcolIndex;

	private static final int PORT_NAME_COLUM = 1;

	private static final int PORT_INDEX_COLUM = 0;

	protected TableService tableService;

	TablePortTransable portTransable;

	public int selectedPortIndex;

	public int selectedindex;	

	private String table_id;

	private DragSource dragSource; // A central DnD object

	private DropTarget dtg;

	private List<TablePort> portli;

	protected BaseService baseService;

	public PortListTable(String table_id) {

		portTransable = new TablePortTransable();

		this.table_id = table_id;
		
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		baseService  = manager.createBaseService();

		/*		addMouseListener(new MyMouseAdapter());

		addKeyListener(new MyKeyAdapter());	*/		

		this.addMouseListener(this);

		dragSource = DragSource.getDefaultDragSource();

		dragSource.createDefaultDragGestureRecognizer(this, // What component
				DnDConstants.ACTION_COPY_OR_MOVE, // What drag types?
				this);// the listener

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dtg = new DropTarget(this, this);

		this.setDropTarget(dtg);

		setName(TablePort.TYPE_PARENT);



	}
	/**
	 * @throws SQLException
	 */
	public void retrive() throws SQLException {

		portli=tableService.getParentPortList(this.table_id);

		DefaultTableModel model = new DefaultTableModel();

		model.addColumn("순서");

		model.addColumn("항구명");

		if(portli.size()<10)
		{
			model.setRowCount(portli.size()+10);
		}else
		{
			model.setRowCount(portli.size()+5);
		}

		for(int i=0;i<portli.size();i++)
		{
			TablePort port = portli.get(i);
			model.setValueAt(port.getPort_name(), i, PORT_NAME_COLUM);
			model.setValueAt(port.getPort_index(), i, PORT_INDEX_COLUM);
		}

		setModel(model);

		TableColumnModel colModel=getColumnModel();

		TableColumn col=colModel.getColumn(PORT_INDEX_COLUM);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		col.setMaxWidth(50);

		col.setCellRenderer(renderer);

		changeSelection(selectedPortIndex, selectedcolIndex, false, false);
	}

	private void chagePortIndex(TablePort onePortInfo, TablePort twoPortInfo) throws SQLException
	{
		int oneIndex =onePortInfo.getPort_index();
		int twoIndex =twoPortInfo.getPort_index();

		onePortInfo.setPort_index(twoIndex);
		twoPortInfo.setPort_index(oneIndex);
		tableService.updateTablePort(onePortInfo);
		tableService.updateTablePort(twoPortInfo);

		retrive();
	}


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
				//JOptionPane.showMessageDialog(ManagePortDialog.this, "("+port_name+") 존재하지 않는 항구입니다.");

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

		//logger.debug("delete port:"+table_id+","+port_name);


	}
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {

		System.out.println("endter");
	}
	@Override
	public void dragExit(DropTargetEvent dte) {
		System.out.println("exit");
	}

	TablePort chagePort;
	public void dragOver(DropTargetDragEvent dtde) {

		int row=this.rowAtPoint(dtde.getLocation());

		if(portli.size()>row)
		{
			TablePort  portItem=portli.get(row);
			chagePort = portItem;
		}
		else
		{
			chagePort = null;
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


			if(chagePort==null)
				return;


			chagePortIndex(droppedScribble.getTablePort(), chagePort);
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
			return; // We only support move and copys
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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mousePressed(MouseEvent e) {

		int row = this.getSelectedRow();
		if(row<0||portli.size()<row-1)
			return;

		TablePort portInfo = portli.get(row);

		this.portTransable.setTablePort(portInfo);

		portTransable.moveto(e.getX(), e.getY());

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

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
	public void dragEnter(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void dragExit(DragSourceEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void dragOver(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {
		// TODO Auto-generated method stub

	}

}

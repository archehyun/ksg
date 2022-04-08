package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.ksg.common.util.ViewUtil;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.service.impl.ShipperTableServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**

 * @FileName : ManageTablePortPop.java

 * @Date : 2021. 3. 9. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� : �ױ���������
 * 1. �ױ� ������ �߰�, ���� �Ѵ�.

 */
@SuppressWarnings("serial")
public class ManageTablePortPop extends KSGDialog implements ActionListener{

	private static final String ACTION_UP = "UP";
	private static final String ACTION_DOWN = "DOWN";
	JButton butSave;
	JButton butDelete;
	JButton butSearch;
	JButton butUpdate;
	JButton butInsert;
	JButton butUp;
	JButton butDown;

	private JTextField txfSelectedPortName;

	private JTextField txfIndex;	

	public int RESLULT=2;

	public static int OK=1;

	public static int CANCEL=2;

	private HashMap<String, Object> result;

	ShipperTableServiceImpl shipperTableService;

	public static final String ACTION_SAVE="����";

	public static final String ACTION_DELETE="����";

	public static final String ACTION_CANCEL="���";

	public static final String ACTION_INSERT="�߰�"; 

	public static final String ACTION_UPDATE="����";


	private KSGTablePanel tableH;

	private String tableId;

	private PortServiceImpl portService;

	private List<HashMap<String, Object>> master;

	private PortIndexChangeAction indexChangeAction = new PortIndexChangeAction();

	private PortSelectionEventHandler portSelectionEventHandler = new PortSelectionEventHandler();	

	private JButton butCancel;

	public ManageTablePortPop(String tableId) {
		super();

		this.setModal(true);

		this.setTitle("�ױ���������");

		shipperTableService = new ShipperTableServiceImpl();

		portService = new PortServiceImpl();

		this.tableId = tableId;	

		this.getContentPane().add(createCenter());

		this.getContentPane().add(createControl(),BorderLayout.SOUTH);

		this.getContentPane().add(createTitle(),BorderLayout.NORTH);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				HashMap<String, Object> param = new HashMap<String, Object>();
				

				param.put("port_type", "P");

				param.put("table_id", ManageTablePortPop.this.tableId);

				result =(HashMap<String, Object>) shipperTableService.selectPortList(param);

				master = (List<HashMap<String, Object>>) result.get("master");

				tableH.setResultData(result);

				tableH.changeSelection(0, 0, false, false);			

			}

			@Override
			public void componentResized(ComponentEvent e) {}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});

	}

	/**
	 *  �˾��� ǥ���Ѵ�.
	 */
	public void showPop()
	{
		this.pack();

		ViewUtil.center(this);

		this.setVisible(true);

	}

	private KSGPanel createCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		tableH = new KSGTablePanel();

		KSGTableColumn ksgTableColumn = new KSGTableColumn("port_index", "����");

		ksgTableColumn.size=50;
		ksgTableColumn.minSize=50;
		ksgTableColumn.maxSize=50;

		tableH.addColumn(ksgTableColumn);	

		tableH.addColumn(new KSGTableColumn("port_name", "�ױ���"));

		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		tableH.initComp();		

		tableH.addMouseListener(portSelectionEventHandler);		

		pnMain.add(createCenterNorth(),BorderLayout.NORTH);		
		pnMain.add(tableH);
		pnMain.add(createCenterEast(),BorderLayout.EAST);

		return pnMain;
	}

	/** ��ư ��� ǥ�� �г� ����
	 * @return
	 */
	private KSGPanel createCenterEast()
	{
		KSGPanel pnMain = new KSGPanel();
		butUp = new JButton("��");		
		butDown = new JButton("��");		
		butInsert = new JButton("�߰�");
		butDelete = new JButton("����");	
		butUpdate = new JButton("����");	


		butUp.setActionCommand(ACTION_UP);		
		butDown.setActionCommand(ACTION_DOWN);


		butUp.addActionListener(indexChangeAction);		
		butDown.addActionListener(indexChangeAction);

		butInsert.addActionListener(this);
		butDelete.addActionListener(this);
		butUpdate.addActionListener(this);

		GroupLayout layout = new GroupLayout (pnMain);
		pnMain.setLayout (layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(butUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(butDown, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
								.addComponent(butInsert, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)	                    
								.addComponent(butDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
								.addComponent(butUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
								)	                
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)

						.addContainerGap())
				);

		layout.setVerticalGroup (
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(butUp, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(10, 10, 10)
										.addComponent(butDown, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(10, 10, 10)
										.addComponent(butInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(10, 10, 10)
										.addComponent(butDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(10, 10, 10)
										.addComponent(butUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)

										)		                    

								)
						)
				);

		return pnMain;
	}

	private KSGPanel createCenterNorth()
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		txfIndex = new JTextField(2);

		txfSelectedPortName = new JTextField(10);

		txfIndex.setEnabled(false);

		return pnMain;
	}

	private KSGPanel createTitle()
	{
		KSGPanel pnMain = new KSGPanel();
		return pnMain;
	}
	private KSGPanel createControl()
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		butSave = new JButton("���� �� �ݱ�");
		butCancel = new JButton("���");


		butSave.setActionCommand(ACTION_SAVE);
		butSave.addActionListener(this);
		butCancel.addActionListener(this);

		pnMain.add(butSave);
		pnMain.add(butCancel);

		return pnMain;
	}

	public int getMaxIndex()
	{
		int max=0;

		for(HashMap<String, Object> item:master)
		{
			int index1 = (Integer) item.get("port_index");
			max=index1>max?index1:max;
		}
		return max;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(ACTION_SAVE))
		{

			// check ports

			// save ports info
			
			try {

			HashMap<String, Object> commandMap = new HashMap<String, Object>();

			commandMap.put("table_id", tableId);

			commandMap.put("master", master);
			

			shipperTableService.saveShipperPort(commandMap);

			RESLULT=OK;

			}catch(Exception ee)
			{
				ee.printStackTrace();
				
				JOptionPane.showMessageDialog(ManageTablePortPop.this, ee.getMessage());
			}finally {
				this.setVisible(false);

				this.dispose();
			}
		}

		else if(command.equals(ACTION_INSERT))
		{	
			SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master);

			searchPortDialog.createAndUpdateUI();

			String port_name = searchPortDialog.result;

			boolean isSamePort = searchPortDialog.isSamePort;

			if(port_name==null||"".equals(port_name))
				return;			

			HashMap<String, Object> newPort = new HashMap<String, Object>();


			// ���� �ε���, �ٸ� �ױ��� �߰�
			if(isSamePort)
			{	
				int row =tableH.getSelectedRow();

				HashMap<String, Object> seletedPort = (HashMap<String, Object>) master.get(row);

				int port_index = (Integer) seletedPort.get("port_index");

				newPort.put("port_index",port_index);

			}
			else
			{
				int max=getMaxIndex();

				newPort.put("port_index",max+1);
			}

			newPort.put("port_name",port_name);

			newPort.put("table_id",tableId);

			newPort.put("port_type","P");

			newPort.put("parent_port",port_name);			

			master.add(newPort);

			arragePort();

			tableH.setResultData(master);
		}
		else if(command.equals(ACTION_DELETE))
		{			
			int row=tableH.getSelectedRow();

			if(row<0)
				return;

			HashMap<String, Object> seleted = master.get(row);

			int seletedIndex = (Integer) seleted.get("port_index");
			// ���� �ε��� ���� Ȯ��

			master.remove(master.get(row));

			// 1 ���� �ε��� ���翩�� Ȯ��

			boolean isExistSameIndex=false;

			for(HashMap<String, Object> item :master)
			{
				int port_index = (Integer) item.get("port_index");				

				if(seletedIndex==port_index)
				{
					isExistSameIndex=true;
				}
			}

			if(!isExistSameIndex) {
				for(HashMap<String, Object> item :master)
				{
					int port_index = (Integer) item.get("port_index");				

					item.put("port_index",seletedIndex<port_index?--port_index:port_index);
				}
			}

			// �ε��� ������Ʈ

			tableH.setResultData(master);

			tableH.changeSelection(row, 0, false, false);

		}
		else if(command.equals(ACTION_CANCEL))
		{
			this.setVisible(false);
			this.dispose();
		}		
		else if(command.equals(ACTION_UPDATE))
		{
			int row=tableH.getSelectedRow();

			if(row<0)
				return;

			SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master, false);
			
			searchPortDialog.createAndUpdateUI();
			

			String port_name = searchPortDialog.result;

			if(port_name==null||"".equals(port_name))
				return;	

			HashMap<String, Object> portInfos =(HashMap<String, Object>)master.get(row);
			portInfos.put("port_name", port_name);
			tableH.setResultData(master);


		}
	}

	public void arragePort()
	{
		Collections.sort(master, new Comparator<HashMap<String, Object>>() { 
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
				// TODO Auto-generated method stub

				int index1 =(Integer) o1.get("port_index");
				int index2 =(Integer) o2.get("port_index");					

				return index1>index2?1:-1;
			}
		});
	}

	/**

	 * @FileName : ManageTablePortPop.java

	 * @Date : 2021. 3. 12. 

	 * @�ۼ��� : ��â��

	 * @�����̷� :

	 * @���α׷� ���� : �ױ� �ε��� ����

	 */
	class PortIndexChangeAction implements ActionListener
	{	

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List<HashMap<String, Object>>[] getPortGroup(int port_index, int updown)
		{
			List lists[] = new LinkedList[2];
			lists[0] = new LinkedList<HashMap<String, Object>>();
			lists[1] = new LinkedList<HashMap<String, Object>>();


			// �ε��� �� �ױ� �׷� ����
			for(HashMap<String, Object> item:master)
			{
				int index=(Integer) item.get("port_index");

				if(index==port_index)
				{
					lists[0].add(item);
				}

				if(index ==port_index+updown)
				{
					lists[1].add(item);
				}					
			}


			return lists;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String command = e.getActionCommand();

				int row=tableH.getSelectedRow();

				if(row<0)
					return;
				HashMap<String, Object> seletedPort = (HashMap<String, Object>) master.get(row);				

				int port_index=(Integer) seletedPort.get("port_index");

				int newRow = 0;

				boolean swap=false;

				if(command.equals(ACTION_UP))
				{	
					List upGroup = new LinkedList<HashMap<String, Object>>(); // ���� �ε��� �ױ� �׷�				

					if(port_index==1) // ���õ� �ױ� �ε��� 1�̸� return
						return;


					// �ε��� �� �ױ� �׷� ���� : �����ױ�(0), �����ױ�(-1)
					List<HashMap<String, Object>> portGroup[] = getPortGroup(port_index, -1);

					// �ε��� ���� : �����ױ�(0), �����ױ�(1)
					portIndexSwap(portGroup[0], portGroup[1], port_index-1, port_index);

					newRow = row-portGroup[1].size();

					swap = true;

				}
				else if(row<master.size()-1&&command.equals(ACTION_DOWN))
				{
					int max=getMaxIndex();


					if(port_index==max) // ���õ� �ױ� �ε��� max�̸� return
						return;


					// �ε��� �� �ױ� �׷� ���� : �����ױ�(0), �Ʒ����ױ�(1)
					List<HashMap<String, Object>> portGroup[] = getPortGroup(port_index, 1);

					// �ε��� ���� : �����ױ�(0), �����ױ�(1)
					portIndexSwap(portGroup[0], portGroup[1], port_index+1, port_index);

					newRow = row+portGroup[1].size();

					swap = true;
				}

				if(swap) {
					// �ε��� ���� ���� ���� ����
					arragePort();

					tableH.setResultData(master);

					tableH.changeSelection(newRow, 0, false, false);
				}
			}catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(ManageTablePortPop.this, ee.getMessage());
			}

		}
		private void portIndexSwap(List<HashMap<String, Object>> portGroup, List<HashMap<String, Object>> upGroup, int port_index1, int port_index2) {
			for(HashMap<String, Object> item :portGroup)
			{
				item.put("port_index", port_index1);
			}

			for(HashMap<String, Object> item :upGroup)
			{

				item.put("port_index", port_index2);
			}

		}

	}
	/**

	 * @FileName : ManageTablePortPop.java

	 * @Date : 2021. 3. 9. 

	 * @�ۼ��� : ��â��

	 * @�����̷� :

	 * @���α׷� ���� :
	 * 
	 * �ױ��� �Է½� �ڵ��ϼ�
	 * 1���� ��� -> �ڵ��ϼ�
	 * 1�� �̻��� ���-> �˾� ǥ��

	 */
	class PortNameKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyChar()==KeyEvent.VK_ENTER)
			{
				JTextField txf = (JTextField) e.getSource();
				String val = txf.getText();

				if(!"".equals(val))
				{
					HashMap<String, Object> param = new HashMap<String, Object>();

					param.put("port_name", val);
					try {

						HashMap<String, Object> result=(HashMap<String, Object>) portService.selectList(param);

						List master = (List) result.get("master");

						// �ױ����� 1�� �˻� �� ���
						if(master.size()==1)
						{
							String port_name = (String) ((HashMap<String, Object>) master.get(0)).get("port_name");

							txf.setText(port_name);
						}

						// �ױ����� ������ �˻� �� ���
						else if(master.size()>1)
						{
							SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master);
							searchPortDialog.createAndUpdateUI();

							if(searchPortDialog.result!=null)
							{
								txf.setText(searchPortDialog.result);

							}else
							{
								txf.setText("");
							}
						}else 
						{
							JOptionPane.showMessageDialog(null, "�ش� �ױ������� �����ϴ�.");
							txf.setText("");
						}


					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}

	}

	class PortSelectionEventHandler implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = tableH.getSelectedRow();
			if(row>-1)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) tableH.getValueAt(row);				

				int port_index = (Integer) item.get("port_index");
				String port_name = (String) item.get("port_name");

				txfIndex.setText(String.valueOf(port_index));
				txfSelectedPortName.setText(port_name); 
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
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

	}

	@Override
	public void createAndUpdateUI() {
		// TODO Auto-generated method stub
		
	}

}



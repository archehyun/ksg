package com.ksg.shippertable.view.dialog;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.ksg.base.service.PortService;
import com.ksg.common.comp.KSGTableColumn;
import com.ksg.common.comp.KSGTablePanel;
import com.ksg.common.util.ViewUtil;
import com.ksg.shippertable.service.impl.ShipperTableService;

/**

 * @FileName : ManageTablePortPop.java

 * @Date : 2021. 3. 9. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 항구정보관리

 */
@SuppressWarnings("serial")
public class ManageTablePortPop extends JDialog implements ActionListener{

	private static final String ACTION_UP = "UP";
	private static final String ACTION_DOWN = "DOWN";
	JButton butSave;
	JButton butDelete;
	JButton butSearch;
	JButton butUpdate;
	JButton butInsert;
	JButton butUp;
	JButton butDown;
	
	JTextField txfSelectedPortName;
	
	JTextField txfIndex;
	
	JTextField txfPortName;
	
	public int RESLULT=2;
	
	public static int OK=1;
	
	public static int CANCEL=2;

	HashMap<String, Object> result;

	ShipperTableService shipperTableService;

	private static final String ACTION_SAVE="저장";

	private static final String ACTION_DELETE="삭제";
	
	private static final String ACTION_CANCEL="취소";

	public final String ACTION_INSERT="추가"; 

	KSGTablePanel tableH;

	private String tableId;

	PortService portService;

	List<HashMap<String, Object>> master;
	
	PortIndexChangeAction indexChangeAction = new PortIndexChangeAction();
	
	PortSelectionEventHandler portSelectionEventHandler = new PortSelectionEventHandler();
	
	private JLabel lblSearch;
	
	private JButton butCancel;


	public ManageTablePortPop(String tableId) {

		this.setModal(true);
		
		this.setTitle("항구정보관리");
		
		shipperTableService = new ShipperTableService();

		portService = new PortService();

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

	public void showPop()
	{
		this.pack();

		ViewUtil.center(this);

		this.setVisible(true);

	}

	private JPanel createCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout(5,5));
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		tableH = new KSGTablePanel();
		
		KSGTableColumn ksgTableColumn = new KSGTableColumn("port_index", "순서");
		
		ksgTableColumn.size=50;
		ksgTableColumn.minSize=50;
		ksgTableColumn.maxSize=50;
		
		tableH.addColumn(ksgTableColumn);	
		
		tableH.addColumn(new KSGTableColumn("port_name", "항구명"));
		
		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		
		
		tableH.initComp();		
		
		tableH.addMouseListener(portSelectionEventHandler);		
		
		pnMain.add(createCenterNorth(),BorderLayout.NORTH);		
		pnMain.add(tableH);
		pnMain.add(createCenterEast(),BorderLayout.EAST);

		return pnMain;
	}
	
	private JPanel createCenterEast()
	{
		JPanel pnMain = new JPanel();
		butUp = new JButton("▲");		
		butDown = new JButton("▼");		
		butInsert = new JButton("추가");
		butDelete = new JButton("삭제");		
		
		butUp.setActionCommand(ACTION_UP);		
		butDown.setActionCommand(ACTION_DOWN);
		
		butInsert.addActionListener(this);
		butDelete.addActionListener(this);
		butUp.addActionListener(indexChangeAction);		
		butDown.addActionListener(indexChangeAction);
		
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
		                        
		                     )		                    
		                    
		                    )
		                )
		            );
		
		return pnMain;
	}

	private JPanel createCenterNorth()
	{
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.LEFT));

		txfIndex = new JTextField(2);
		
		txfSelectedPortName = new JTextField(10);
		
		butUpdate = new JButton("수정");
		
		txfIndex.setEnabled(false);

		butUpdate.addActionListener(this);		
		
		pnMain.add(new JLabel("순서"));		

		pnMain.add(txfIndex);
		
		pnMain.add(txfSelectedPortName);
		
		pnMain.add(butUpdate);	
		

		return pnMain;
	}

	/*private JPanel createCenterSouth()
	{
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		txfPortName = new JTextField(10);		
		
		ImageIcon img = new ImageIcon("images/search1.png");
		
		//추출된 Image의 크기를 조절하여 새로운 Image객체 생성
		Image changedImg = img.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);

		//새로운 Image로 ImageIcon객체를 생성

		ImageIcon Icon = new ImageIcon(changedImg);

		lblSearch = new JLabel(Icon);

		txfPortName.addKeyListener(new PortNameKeyAdapter());
		
		txfPortName.setEditable(false);		

		lblSearch.setBorder(BorderFactory.createLineBorder(Color.gray));

		lblSearch.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				lblSearch.setBorder(BorderFactory.createLineBorder(Color.gray));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				lblSearch.setBorder(BorderFactory.createLoweredBevelBorder());

			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblSearch.setBorder(BorderFactory.createLineBorder(Color.gray));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblSearch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master);
				searchPortDialog.createAndUpdateUI();

				if(searchPortDialog.result!=null)
				{
					txfPortName.setText(searchPortDialog.result);

				}else
				{
					txfPortName.setText("");
				}

			}
		});		


		return pnMain;
	}*/
	private JPanel createTitle()
	{
		JPanel pnMain = new JPanel();
		return pnMain;
	}
	private JPanel createControl()
	{
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		butSave = new JButton("저장 및 닫기");
		butCancel = new JButton("취소");
		

		butSave.setActionCommand(ACTION_SAVE);
		butSave.addActionListener(this);
		butCancel.addActionListener(this);
			
		pnMain.add(butSave);
		pnMain.add(butCancel);

		return pnMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(ACTION_SAVE))
		{
			
			// check ports
			
			// save ports info
			
			HashMap<String, Object> commandMap = new HashMap<String, Object>();
			
			commandMap.put("table_id", tableId);
			commandMap.put("master", master);
			
			shipperTableService.saveShipperPort(commandMap);
			
			RESLULT=OK;
			
			this.setVisible(false);
			
			this.dispose();
			

		}

		else if(command.equals(ACTION_INSERT))
		{
			
			SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master);
			searchPortDialog.createAndUpdateUI();

			
			String port_name = searchPortDialog.result;
			
			if(port_name==null||"".equals(port_name))
				return;			
			
			HashMap<String, Object> newPort = new HashMap<String, Object>();
			
			int max=0;
			
			for(int i=0;i<master.size();i++)
			{
				HashMap<String, Object> item1 = master.get(i);
				int index1 = (Integer) item1.get("port_index");
				max=index1>max?index1:max;
					
			}

			newPort.put("port_index",max+1);

			newPort.put("port_name",port_name);
			
			newPort.put("table_id",tableId);
			
			newPort.put("port_type","P");
			
			newPort.put("parent_port",port_name);			

			master.add(newPort);

			tableH.setResultData(master);
			
			txfPortName.setText("");

		}
		else if(command.equals(ACTION_DELETE))
		{			
			int row=tableH.getSelectedRow();
			
			if(row<0)
				return;
			
			for(int i=row+1;i<master.size();i++)
			{
				HashMap<String, Object> item1 = (HashMap<String, Object>) master.get(i);
				int port_index = (Integer) item1.get("port_index");
				item1.put("port_index",--port_index);
			}
			master.remove(master.get(row));
			
			tableH.setResultData(master);
			
			tableH.changeSelection(row, 0, false, false);			

		}
		else if(command.equals(ACTION_CANCEL))
		{
			this.setVisible(false);
			this.dispose();
		}

	}
	
	/**
	
	  * @FileName : ManageTablePortPop.java
	
	  * @Date : 2021. 3. 12. 
	
	  * @작성자 : 박창현
	
	  * @변경이력 :
	
	  * @프로그램 설명 : 항구 인덱스 관리
	
	  */
	class PortIndexChangeAction implements ActionListener
	{	
		private void portIndexSwap(int first, int second) {
			
			//swap index
			HashMap<String, Object> item1 = (HashMap<String, Object>) tableH.getValueAt(first);
			HashMap<String, Object> item2 = (HashMap<String, Object>) tableH.getValueAt(second);

			int index1 = (Integer) item1.get("port_index");
			int index2 = (Integer) item2.get("port_index");

			item1.put("port_index", index2);
			item2.put("port_index", index1);
			
		}
		
		private int maxPortIndex()
		{	
			
			int max=0;
			
			for(int i=0;i<master.size();i++)
			{
				HashMap<String, Object> item1 = master.get(i);
				int index1 = (Integer) item1.get("port_index");
				max=index1>max?index1:max;
					
			}
			return max;
		}
		
		

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
			String command = e.getActionCommand();
			
			int row=tableH.getSelectedRow();
			
			if(row<0)
				return;
			
			int newRow = 0;
			
			boolean swap=false;
			
			if(row>0&&command.equals(ACTION_UP))
			{
				newRow=row-1;				
				
				portIndexSwap(row, newRow);
				
				swap = true;
								
			}
			else if(row<master.size()-1&&command.equals(ACTION_DOWN))
			{
				newRow=row+1;
				
				portIndexSwap(row, newRow);
				swap = true;
			}
			
			if(swap) {
			// 인덱스 기준 오름 차순 정렬
			Collections.sort(master, new Comparator<HashMap<String, Object>>() { 
				@Override
				public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
					// TODO Auto-generated method stub
					
					int index1 =(Integer) o1.get("port_index");
					int index2 =(Integer) o2.get("port_index");					
					
					return index1>index2?1:-1;
				}
			});
			
			tableH.setResultData(master);
			tableH.changeSelection(newRow, 0, false, false);
			}
			}catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(ManageTablePortPop.this, ee.getMessage());
			}
			
		}
		
	}
	/**

	 * @FileName : ManageTablePortPop.java

	 * @Date : 2021. 3. 9. 

	 * @작성자 : 박창현

	 * @변경이력 :

	 * @프로그램 설명 :
	 * 
	 * 항구명 입력시 자동완성
	 * 1개일 경우 -> 자동완성
	 * 1개 이상일 경우-> 팝업 표시

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

						HashMap<String, Object> result=(HashMap<String, Object>) portService.selectPortList(param);

						List master = (List) result.get("master");


						// 항구명이 1개 검색 된 경우
						if(master.size()==1)
						{
							String port_name = (String) ((HashMap<String, Object>) master.get(0)).get("port_name");

							txf.setText(port_name);
						}

						// 항구명이 여러개 검색 된 경우
						else if(master.size()>1)
						{
							System.out.println("size:"+master.size());
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
							JOptionPane.showMessageDialog(null, "해당 항구정보이 없습니다.");
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

}




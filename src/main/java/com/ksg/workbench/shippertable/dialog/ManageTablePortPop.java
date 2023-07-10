package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dtp.api.control.ShipperTableController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.dialog.SearchPortDialog;
import com.ksg.workbench.master.dialog.BaseInfoDialog;

/**

 * @FileName : ManageTablePortPop.java

 * @Date : 2021. 3. 9. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 항구정보관리
 * 1. 항구 정보를 추가, 삭제 한다.

 */
@SuppressWarnings("serial")
public class ManageTablePortPop extends BaseInfoDialog implements ActionListener{

	private static final String ACTION_UP = "UP";

	private static final String ACTION_DOWN = "DOWN";

	private KSGGradientButton butDelete;
	private KSGGradientButton butUpdate;
	private KSGGradientButton butInsert;
	private KSGGradientButton butUp;
	private KSGGradientButton butDown;

	private JTextField txfSelectedPortName;

	private JTextField txfIndex;	

	public int RESLULT=2;

	public static int OK=1;

	public static int CANCEL=2;

	private HashMap<String, Object> result;

	private KSGTablePanel tableH;

	private String table_id;

	private List<HashMap<String, Object>> master;

	private PortIndexChangeAction indexChangeAction = new PortIndexChangeAction();

	private PortSelectionEventHandler portSelectionEventHandler = new PortSelectionEventHandler();	

	private JButton butCancel;

	public ManageTablePortPop(String table_id) {
		
		super();

		this.setModal(true);

		this.setTitle("항구정보관리");

		this.addComponentListener(this);

		this.setController(new ShipperTableController());

		this.table_id = table_id;	
	}

	private KSGPanel createCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

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

	/** 버튼 목록 표시 패널 생성
	 * @return
	 */
	private KSGPanel createCenterEast()
	{
		KSGPanel pnMain = new KSGPanel();

		butUp 		= new KSGGradientButton("▲");

		butDown 	= new KSGGradientButton("▼");

		butInsert 	= new KSGGradientButton("추가");

		butDelete 	= new KSGGradientButton("삭제");

		butUpdate 	= new KSGGradientButton("수정");	
		
		butUp.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butDown.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butInsert.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butDelete.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butUpdate.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

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
			CommandMap commandMap = new CommandMap();

			commandMap.put("table_id", table_id);

			commandMap.put("tablePortList", master);

			callApi("managePortDialog.saveTablePort", commandMap);
		}

		else if(command.equals(ACTION_INSERT))
		{	
			SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master);

			searchPortDialog.createAndUpdateUI();

			String port_name = searchPortDialog.result;
			
			boolean isSamePort = searchPortDialog.isSamePort;

			if(port_name==null||"".equals(port_name)) return;

			HashMap<String, Object> newPort = new HashMap<String, Object>();


			// 동일 인덱스, 다른 항구명 추가
			if(isSamePort)
			{	
				int row = tableH.getSelectedRow();

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

			newPort.put("table_id",table_id);

			newPort.put("port_type","P");

			newPort.put("parent_port",port_name);			

			master.add(newPort);

			arragePort();

			tableH.setResultData(master);
		}
		else if(command.equals(ACTION_DELETE))
		{			
			int row=tableH.getSelectedRow();

			if(row<0) return;

			HashMap<String, Object> seleted = master.get(row);

			int seletedIndex = (Integer) seleted.get("port_index");
			// 동일 인덱스 여부 확인

			master.remove(master.get(row));

			// 1 동일 인덱스 존재여부 확인

			boolean isExistSameIndex=false;

			for(HashMap<String, Object> item :master)
			{
				int port_index = (Integer) item.get("port_index");				

				if(seletedIndex==port_index) isExistSameIndex=true;
			}

			if(!isExistSameIndex) {
				for(HashMap<String, Object> item :master)
				{
					int port_index = (Integer) item.get("port_index");				

					item.put("port_index",seletedIndex<port_index?--port_index:port_index);
				}
			}

			// 인덱스 업데이트

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
			int row = tableH.getSelectedRow();

			if(row<0) return;

			SearchPortDialog searchPortDialog = new SearchPortDialog(ManageTablePortPop.this,master, false);

			searchPortDialog.createAndUpdateUI();

			String port_name = searchPortDialog.result;

			if(port_name==null||"".equals(port_name)) return;	

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

	 * @작성자 : 박창현

	 * @변경이력 :

	 * @프로그램 설명 : 항구 인덱스 관리

	 */
	class PortIndexChangeAction implements ActionListener
	{	

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List<HashMap<String, Object>>[] getPortGroup(int port_index, int updown)
		{
			List lists[] = new LinkedList[2];

			lists[0] = new LinkedList<HashMap<String, Object>>();

			lists[1] = new LinkedList<HashMap<String, Object>>();

			// 인덱스 별 항구 그룹 선택
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
				String command 	= e.getActionCommand();

				int row 		= tableH.getSelectedRow();

				if(row<0) return;

				HashMap<String, Object> seletedPort = (HashMap<String, Object>) master.get(row);				

				int port_index=(Integer) seletedPort.get("port_index");

				int newRow = 0;

				boolean swap=false;

				if(command.equals(ACTION_UP))
				{	
					List upGroup = new LinkedList<HashMap<String, Object>>(); // 상위 인덱스 항구 그룹				

					// 선택된 항구 인덱스 1이면 return
					if(port_index==1) return;

					// 인덱스 별 항구 그룹 선택 : 선택항구(0), 위쪽항구(-1)
					List<HashMap<String, Object>> portGroup[] = getPortGroup(port_index, -1);

					// 인덱스 스왑 : 선택항구(0), 위쪽항구(1)
					portIndexSwap(portGroup[0], portGroup[1], port_index-1, port_index);

					newRow = row-portGroup[1].size();

					swap = true;

				}
				else if(row<master.size()-1&&command.equals(ACTION_DOWN))
				{
					int max=getMaxIndex();

					// 선택된 항구 인덱스 max이면 return
					if(port_index==max)  return;

					// 인덱스 별 항구 그룹 선택 : 선택항구(0), 아래쪽항구(1)
					List<HashMap<String, Object>> portGroup[] = getPortGroup(port_index, 1);

					// 인덱스 스왑 : 선택항구(0), 위쪽항구(1)
					portIndexSwap(portGroup[0], portGroup[1], port_index+1, port_index);

					newRow = row+portGroup[1].size();

					swap = true;
				}

				if(swap) {
					// 인덱스 기준 오름 차순 정렬
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

	class PortSelectionEventHandler extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = tableH.getSelectedRow();

			if(row<0) return;

			HashMap<String, Object> item 	= (HashMap<String, Object>) tableH.getValueAt(row);				

			int port_index 					= (Integer) item.get("port_index");

			String port_name 				= (String) item.get("port_name");

			txfIndex.setText(String.valueOf(port_index));

			txfSelectedPortName.setText(port_name); 
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {

		CommandMap param = new CommandMap();

		param.put("table_id", this.table_id);
		
		param.put("tablePortList", master);

		callApi("managePortDialog.init", param);
	}
	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("managePortDialog.init".equals(serviceId)) {

			List data=(List) result.get("data");
			
			this.master = data;

			tableH.setResultData(data);

			tableH.changeSelection(0, 0, false, false);			
		}
		
		else if("managePortDialog.saveTablePort".equals(serviceId)) 
		{
			RESLULT=OK;
			
			this.setVisible(false);

			this.dispose();
		}
	}

	@Override
	public void createAndUpdateUI() {

		this.getContentPane().add(createCenter());

		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		this.getContentPane().add(createTitle(),BorderLayout.NORTH);

		ViewUtil.center(this, true);

		this.setVisible(true);
	}
}
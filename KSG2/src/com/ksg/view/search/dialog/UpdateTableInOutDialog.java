/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.view.search.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.ksg.dao.DAOManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.model.KSGModelManager;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class UpdateTableInOutDialog extends KSGDialog implements ActionListener{

	private Font defaultFont = new Font("돋음",0,10);
	private static final String OUT_PORT_LEFT = "OutPortLeft";
	private static final String OUT_PORT_RIGHT = "OutPortRight";
	private static final String IN_PORT_LEFT = "InPortLeft";
	private static final String IN_PORT_RIGHT = "InPortRight";
	private static final String OUT_TO_PORT_LEFT = "OutToPortLeft";
	private static final String OUT_TO_PORT_RIGHT = "OutToPortRight";
	private static final String IN_TO_PORT_LEFT = "InToPortLeft";
	private static final String IN_TO_PORT_RIGHT = "InToPortRight";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ShippersTable shippersTable;
	JList totalPort = new JList();
	JList subPort  =new JList();
	JList outPort = new JList();
	JList inPort = new JList();
	JList outToPort = new JList();
	JList inToPort = new JList();
	
	public String inPortIndex=null;
	public String inToPortIndex=null;
	public String outPortIndex=null;
	public String outToPortIndex=null;


	HashMap<Integer, ListData> inPortMap = new HashMap<Integer, ListData>();
	HashMap<Integer, ListData> inToPortMap = new HashMap<Integer, ListData>();
	HashMap<Integer, ListData> outPortMap = new HashMap<Integer, ListData>();
	HashMap<Integer, ListData> outToPortMap = new HashMap<Integer, ListData>();
	HashMap<Integer, ListData> portMap = new HashMap<Integer, ListData>();

	public UpdateTableInOutDialog(ShippersTable table) {
		shippersTable = table;
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		advservice  = manager.createADVService();
	}
	public void createAndUpdateUI() {

		try{
			ADVData advData=advservice.getADVData(shippersTable.getTable_id(), shippersTable.getDate_isusse());
			if(advData==null)
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "광고정보가 없습니다.");
				return;
			}
			ShippersTable tableInfo = tableService.getTableById(shippersTable.getTable_id());
			String[]portList=advData.getPortArray();
			for(int i=0;i<portList.length;i++)
			{

				ListData data = new ListData(portList[i],i+1);
				portMap.put(i+1, data);
			}

			initMap(tableInfo.getOut_port(),outPortMap,portMap);
			initMap(tableInfo.getOut_to_port(),outToPortMap,portMap);
			initMap(tableInfo.getIn_port(),inPortMap,portMap);
			initMap(tableInfo.getIn_to_port(),outPortMap,portMap);

			initList(portMap,totalPort);
			initList(outPortMap,outPort);
			initList(outToPortMap,outToPort);
			initList(inPortMap,inPort);
			initList(inToPortMap,inToPort);


			this.setTitle("In/Out 항구 등록");
			this.setModal(true);


			JPanel pnMain = new JPanel();
			GridLayout gridLayout = new GridLayout(1,0);
			gridLayout.setHgap(5);
			pnMain.setLayout(gridLayout);
			

			JTabbedPane pnMainRight = new JTabbedPane();
			pnMainRight.setFont(defaultFont);

			JTabbedPane pnMainLeft = new JTabbedPane();
			pnMainLeft.setFont(defaultFont);
			
			
			Box portLists = new Box(BoxLayout.Y_AXIS);
			
			
			JLabel lblPortList = new JLabel("항구목록",JLabel.LEFT);
			lblPortList.setFont(defaultFont);
			JScrollPane spTotal = new JScrollPane(totalPort);
			JScrollPane spSub = new JScrollPane(subPort);
			
			portLists.add(spTotal);
			JLabel lbl = new JLabel("등록항구목록",JLabel.LEFT);
			lbl.setFont(defaultFont);
			portLists.add(lbl);
			portLists.add(spSub);
			
			pnMainLeft.add(portLists,"항구목록");
			
//			pnTotalNorth.add(lblPortList,BorderLayout.NORTH);
			pnMain.add(pnMainLeft);

			// OutPort
			JPanel pnOutPort = createInOut("Outbound 국내항",OUT_PORT_RIGHT,OUT_PORT_LEFT,outPort);

			// OutToPort		
			JPanel pnOutToPort = createInOut("Outbound 외국항",OUT_TO_PORT_RIGHT,OUT_TO_PORT_LEFT,outToPort);

			//InPort		
			JPanel pnInPort = createInOut("Inbound 국내항",IN_PORT_RIGHT,IN_PORT_LEFT,inPort);

			//InToPort
			JPanel pnInToPort = createInOut("Inbound 외국항",IN_TO_PORT_RIGHT,IN_TO_PORT_LEFT,inToPort);
			
			JPanel pnOut = new JPanel();
			pnOut.setLayout(new GridLayout(0,1));
			
			pnOut.add(pnOutPort);
			pnOut.add(pnOutToPort);
			
			JPanel pnIn = new JPanel();
			pnIn.setLayout(new  GridLayout(0,1));
			pnIn.add(pnInPort);
			pnIn.add(pnInToPort);
			
			
			pnMainRight.addTab("Outbound", pnOut);
			pnMainRight.addTab("Inbound",pnIn);


			pnMain.add(pnMainRight);


			JPanel pnControl = new JPanel();
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton butOK = new JButton("확인");
			butOK.setFont(defaultFont);
			butOK.addActionListener(this);
			pnControl.add(butOK);
			JButton butCancel = new JButton("취소");
			butCancel.setFont(defaultFont);
			
			butCancel.addActionListener(this);

			pnControl.add(butCancel);

			this.getContentPane().add(pnMain,BorderLayout.CENTER);
			this.getContentPane().add(pnControl,BorderLayout.SOUTH);
			this.getContentPane().add(buildTitle(),BorderLayout.NORTH);
			this.getContentPane().add(createLine(),BorderLayout.WEST);
			this.getContentPane().add(createLine(),BorderLayout.EAST);
			this.setSize(500,500);
			ViewUtil.center(this, false);
			this.setVisible(true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Component buildTitle() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle = new JLabel("In/Out 항구를 등록합니다.");
		pnMain.add(lblTitle);
		pnMain.setBackground(Color.white);
		return pnMain;
	}
	private Component createLine() {
		JPanel pnMain = new JPanel();
		pnMain.setPreferredSize(new Dimension(15,0));

		return pnMain;
	}
	public void initMap(String ports, HashMap<Integer, ListData> outPortList2, HashMap<Integer, ListData> portMap2)
	{
		StringTokenizer stInport = new StringTokenizer(ports,"#");
		while(stInport.hasMoreTokens())
		{
			try{
				int index =Integer.parseInt(stInport.nextToken());
				if(portMap2.containsKey(index))
				{
					outPortList2.put(index, portMap2.remove(index));
				}
			}catch (NumberFormatException e) {
				// TODO: handle exception
			}
		}


	}
	private void initList(HashMap<Integer , ListData> map,JList list) {
//		StringTokenizer stInport = new StringTokenizer(inPorts,"#");

		Iterator iter = map.keySet().iterator();

		DefaultListModel inPortListModel = new DefaultListModel();
		while (iter.hasNext()) 
		{
			Integer key = (Integer) iter.next();

			inPortListModel.addElement(map.get(key));
		}
		list.setModel(inPortListModel);
	}
	
	/**
	 * @param oneList
	 * @param twoList
	 * @param oneMap
	 * @param twoMap
	 */
	private void addList(JList oneList,JList twoList, HashMap<Integer, ListData> oneMap,HashMap<Integer, ListData> twoMap)
	{
		int index = oneList.getSelectedIndex();
		if(index!=-1)
		{
			DefaultListModel model = (DefaultListModel) oneList.getModel();
			DefaultListModel model2 = (DefaultListModel) twoList.getModel();
			ListData data =(ListData) model.remove(index);					
			model2.addElement(data);
			
			/**
			 * 정렬 기능이 추가 되어야 함
			 * 
			 */
			if(oneMap.containsKey(data.index))
			{
				twoMap.put(data.index, oneMap.remove(data.index));
			}
		}
	}
	
	private JPanel createInOut(String label, String right, String left,JComponent comp)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		Box pnBox = new Box(BoxLayout.Y_AXIS);
		
		pnBox.add(addButton("▶", right));
		pnBox.add(addButton("◀", left));
		JPanel pnControl = new JPanel();
		pnControl.setLayout(new BorderLayout());
		JLabel lblTitle = new JLabel(label);
		lblTitle.setFont(defaultFont);
		comp.setFont(defaultFont);
		
		pnControl.add(pnBox,BorderLayout.CENTER);
		
		pnMain.add(lblTitle,BorderLayout.NORTH);
		pnMain.add(new JScrollPane(comp),BorderLayout.CENTER);
		pnMain.add(pnControl,BorderLayout.WEST);
		
		
		return pnMain;
		
	}
	
	public JButton addButton(String label, String command)
	{
		JButton but =new JButton(label);
		but.setActionCommand(command);
		but.addActionListener(this);
		but.setFont(new Font("돋움",Font.BOLD,8));
		return but;
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			this.inPortIndex=getIndexInfo(inPortMap);
			this.outPortIndex=getIndexInfo(outPortMap);
			this.inToPortIndex=getIndexInfo(inToPortMap);
			this.outToPortIndex=getIndexInfo(outToPortMap);
			
			

			this.setVisible(false);
			this.dispose();
			
		}
		else if(command.equals(OUT_PORT_LEFT))
		{
			addList(outPort, totalPort, outPortMap,  portMap);
		}
		else if(command.equals(OUT_PORT_RIGHT))
		{

			addList(totalPort, outPort, portMap, outPortMap);
		}

		else if(command.equals(IN_PORT_LEFT))
		{
			addList(inPort, totalPort, inPortMap,  portMap);

		}
		else if(command.equals(IN_PORT_RIGHT))
		{
			addList(totalPort, inPort, portMap, inPortMap);
		}

		else if(command.equals(OUT_TO_PORT_LEFT))
		{
			addList(outToPort, totalPort, outToPortMap,  portMap);
		}
		else if(command.equals(OUT_TO_PORT_RIGHT))
		{

			addList(totalPort, outToPort, portMap, outToPortMap);
		}

		else if(command.equals(IN_TO_PORT_LEFT))
		{
			addList(inToPort, totalPort, inToPortMap,  portMap);
		}
		else if(command.equals(IN_TO_PORT_RIGHT))
		{

			addList(totalPort, inToPort, portMap, inToPortMap);
		}

		else if(command.equals("취소"))
		{		
			this.setVisible(false);
			this.dispose();
		}

	}
	private String getIndexInfo(HashMap<Integer, ListData> inPortMap) {
		Set st = inPortMap.keySet();
		Iterator itr = st.iterator();
		StringBuffer buffer = new StringBuffer();
		while (itr.hasNext())
		{
			ListData data=inPortMap.get(itr.next());
			buffer.append(data.index);
			if(itr.hasNext())
				buffer.append("#");	
		}
		return buffer.toString();
	}
	class ListData
	{

		String portName;
		int index;
		public ListData(String portName, int index) {
			this.portName=portName;
			this.index=index;
		}
		public String toString()
		{
			return index+":"+portName;
		}
	}

}

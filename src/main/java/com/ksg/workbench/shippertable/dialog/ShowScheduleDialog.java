package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Autowired;

import com.dtp.api.schedule.create.CreateSchedule;
import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.tree.CustomTree;
import com.ksg.workbench.master.dialog.BaseInfoDialog;

public class ShowScheduleDialog extends BaseInfoDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ShippersTable selectedTable;
	
	private List<ScheduleData> scheduleList;
	
	ShipperTableService shipperTableService;

	public ShowScheduleDialog() {

		shipperTableService = new ShipperTableServiceImpl();

		this.addComponentListener(this);
	}

	public ShowScheduleDialog(String table_id) {

		this();

		try {
			this.selectedTable = shipperTableService.selectShipperTableAllById(table_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ShowScheduleDialog(ShippersTable selectedTable) {
		this();

		this.selectedTable = selectedTable;

	}

	@Override
	public void createAndUpdateUI() {

		setModal(true);

		this.setTitle("스케줄 정보");

		getContentPane().add(buildCenter());

		getContentPane().add(buildControl(),BorderLayout.SOUTH);

		this.butOK.setVisible(false);

		this.butCancel.setText("닫기");

		this.setSize(1200,600);

		ViewUtil.center(this, false);

		setVisible(true);

	}

	private Component buildCenter() {

		pnTabMain = new JTabbedPane();


		return pnTabMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("닫기"))
		{
			close();
		}
	}

	private KSGPanel updateOutbound(List<ScheduleData> inbound, String name, String inOutType)	
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		// tree
		CustomTree outboundTree = new CustomTree();
		outboundTree.setPreferredSize(new Dimension(200, 100));
		
		// table
		KSGTablePanel tableOutbound = new KSGTablePanel(name+" 목록");

		tableOutbound.addColumn(new KSGTableColumn("inOutType", "I/O", 50));		
		tableOutbound.addColumn(new KSGTableColumn("vessel", "선박명", 150));
		tableOutbound.addColumn(new KSGTableColumn("company_abbr", "선사", 150));
		tableOutbound.addColumn(new KSGTableColumn("voyage_num", "항차",100));
		tableOutbound.addColumn(new KSGTableColumn("fromPort", "출발항", 150));
		tableOutbound.addColumn(new KSGTableColumn("fromDate", "출발일", 85));
		tableOutbound.addColumn(new KSGTableColumn("port", "도착항",150));
		tableOutbound.addColumn(new KSGTableColumn("toDate", "도착일", 85));
		tableOutbound.initComp();
		
		pnMain.add(tableOutbound);

		pnMain.add(outboundTree,BorderLayout.WEST);
		
		// data init		
		
		if(inbound == null) return pnMain;
		
		List<String>  inboundTreeData = inbound.stream()
												
												.map(ScheduleData::getVessel)
												.distinct()
												.collect(Collectors.toList());
		
		DefaultMutableTreeNode inboundRoot = new DefaultMutableTreeNode("전체");

		inboundTreeData.forEach(item -> inboundRoot.add(new DefaultMutableTreeNode(item)));

		DefaultTreeModel rootModel = new DefaultTreeModel(inboundRoot);

		outboundTree.setModel(rootModel);

		List<CommandMap> inboundList=convert(inbound);

		tableOutbound.setResultData(inboundList);
		
		outboundTree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {

				TreePath path=e.getNewLeadSelectionPath();

				if(path!=null)
				{
					int pathCount = path.getPathCount();
					

					switch (pathCount) {

					case 2:

						String pa = path.getLastPathComponent().toString();
						
						List selectedList = ShowScheduleDialog.this.scheduleList.stream()
																				.filter(schedule -> schedule.getInOutType().equals(inOutType))
																				.filter(schedule -> schedule.getVessel().equals(pa))
																				.collect(Collectors.toList());
						tableOutbound.setResultData(convert(selectedList));
						
						break;
					case 1:
						List selectedList1 = ShowScheduleDialog.this.scheduleList.stream()
																	.filter(schedule -> schedule.getInOutType().equals(inOutType))						
																	.collect(Collectors.toList());
						tableOutbound.setResultData(convert(selectedList1));
						break;

					default:
						break;
					}
				}

			}});
		return pnMain;
	}

	ObjectMapper objectMapper = new ObjectMapper();

	private JTabbedPane pnTabMain;
	@Autowired
	@Override
	public void componentShown(ComponentEvent e) {
		try {
			CreateSchedule scheduleData = new CreateSchedule();

			scheduleData.setShipperTable(selectedTable);			

			ScheduleManager.getInstance().initMasterData(); 

			scheduleList=scheduleData.getInboundScheduleList();

			Map<String, List<ScheduleData>> companymap =  scheduleList.stream().collect(
					Collectors.groupingBy(ScheduleData::getInOutType)); // 선사


			List<ScheduleData> inbound =companymap.get("I");
			
			List<ScheduleData> outbound =companymap.get("O");
			
			if(inbound!=null)
			pnTabMain.addTab("Inbound", updateOutbound(inbound, "Inbound","I"));
			
			if(outbound!=null)
			pnTabMain.addTab("Outbound", updateOutbound(outbound, "Outbound","O"));



		} catch (Exception e1) {
			e1.printStackTrace();

			JOptionPane.showMessageDialog(this, e1.getMessage());
		}

	}
	private List convert(List<ScheduleData> list)
	{
		if(list == null) return new ArrayList();

		return ((List<CommandMap>) list.stream()
				.map(o -> objectMapper.convertValue(o, CommandMap.class))
				.collect(Collectors.toList()));

	}

}

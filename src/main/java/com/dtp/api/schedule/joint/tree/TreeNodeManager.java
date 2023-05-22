package com.dtp.api.schedule.joint.tree;

import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Vessel;


/**==============================================
 * 

 * @FileName : TreeNodeManager.java

 * @Project : KSG2

 * @Date : 2022. 12. 8. 

 * @�ۼ��� : pch

 * @�����̷� :

 * @���α׷� ���� : ȭ�鿡�� ǥ���ϴ� ������ ��� ���� Ŭ����
 * 
 *
  ==============================================*/
public class TreeNodeManager {


	private ObjectMapper objectMapper;
	
	private RouteNodeManager routeNodeManager 		= new RouteNodeManager();
	
	private AbstractNodeManager outboundNodeManager = new OutboundNodeManager();
	
	private InboundNodeManager inboundNodeManager 	= new InboundNodeManager();
	


	public TreeNodeManager()
	{
		objectMapper = new ObjectMapper();
	}

	/**
	 * 
	 * ������ ����Ʈ�� �������� outbound Ʈ�� ��� ����
	 * ������ -> ����� -> �����ٱ׷� -> ������ or ������ -> ����� -> ������   
	 * 
	 * @param result
	 * @return
	 */
	public DefaultMutableTreeNode getOutboundTreeNode(CommandMap areaList) {


		return outboundNodeManager.getTreeNode(areaList);
	}


	/**=======================================================================**/
	/** inbounds tree node**/

	/**
	 * ����
	 * ----�����(�ܱ���)
	 * --------����
	 * ------------������(������)
	 * 
	 * 
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getInboundTreeNode(CommandMap areaList) {

		return inboundNodeManager.getTreeNode(areaList);
	}

	/**
	 * ���� - ����
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getRouteTreeNode(CommandMap areaList) {		

		return routeNodeManager.getTreeNode(areaList);
	}
	


}

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

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 화면에서 표시하느 스케줄 노드 생성 클래스
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
	 * 스케줄 리스트를 기준으로 outbound 트리 노드 생성
	 * 도착항 -> 출발항 -> 스케줄그룹 -> 스케줄 or 도착항 -> 출발항 -> 스케줄   
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
	 * 지역
	 * ----출발항(외국항)
	 * --------선박
	 * ------------도착항(국내항)
	 * 
	 * 
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getInboundTreeNode(CommandMap areaList) {

		return inboundNodeManager.getTreeNode(areaList);
	}

	/**
	 * 지역 - 선박
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getRouteTreeNode(CommandMap areaList) {		

		return routeNodeManager.getTreeNode(areaList);
	}
	


}

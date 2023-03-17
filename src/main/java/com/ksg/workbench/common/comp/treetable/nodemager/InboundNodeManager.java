package com.ksg.workbench.common.comp.treetable.nodemager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.InboundGroupTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;

public class InboundNodeManager extends AbstractNodeManager{
	
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
	public DefaultMutableTreeNode getTreeNode(CommandMap areaList) {

		//inbound port 약어 목록 조회
		
		//=====================================
		//트리 노드 생성
		//=====================================

		DefaultMutableTreeNode root = new AreaTreeNode("AREA");

		for(String strArea: areaList.keySet())		
		{	

			DefaultMutableTreeNode area = new AreaTreeNode(strArea);

			//출발항
			CommandMap fromPortItems =  (CommandMap) areaList.get(strArea);

			// 출발항 정렬
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{	
				CommandMap vesselitems =  (CommandMap) fromPortItems.get(fromPortKey);
				//tree 노드 생성
				DefaultMutableTreeNode fromPort = new PortTreeNode((String)fromPortKey);

				// 선박 목록

				//makeSortedKey

				String[][] sortedKey = getInboundScheduleKey(vesselitems.keySet().toArray());


				for(String arr[]: sortedKey)
				{
					String str = (String) arr[0];

					String vesselName =str.substring(0,str.indexOf("$$"));

					// 스케줄 목록
					//TODO 스케줄 INBOUND 공동배선 적용 룰 검토
					ArrayList<CommandMap> scheduleList =  (ArrayList<CommandMap>) vesselitems.get(str);


					fromPort.add(new InboundGroupTreeNode(vesselName,scheduleList ));	
				}

				area.add(fromPort);

			}
			root.add(area);
		}


		return root;

	}
	

	/**
	 * 
	 * @param vesselAndDatekey
	 * @return inbound sorted keys
	 */
	private String[][] getInboundScheduleKey(Object[] vesselAndDatekey) {

		String[][] sortedKey = new String[vesselAndDatekey.length][2];

		for(int i =0;i<vesselAndDatekey.length;i++)
		{
			String str = (String) vesselAndDatekey[i];

			sortedKey[i][0] =str; //vesselAndDateF

			sortedKey[i][1] =str.substring(str.indexOf("$$")+2, str.length()); // dateF

		}

		// key 출발일 기준 정렬
		Arrays.sort(sortedKey, new Comparator<String[]>() { 
			@Override 
			public int compare(String[] o1, String[] o2) {

				String fromDateOne = o1[1];
				String fromDateTwo = o2[1];;

				return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

			}
		});
		return sortedKey;
	}

}

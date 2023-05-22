package com.dtp.api.schedule.joint.tree;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;

public abstract class AbstractNodeManager {
	
	protected ObjectMapper objectMapper;
	protected HashMap<String, PortInfo> portMap;
	protected HashMap<String, Vessel> vesselMap;
	
	public AbstractNodeManager()
	{
		objectMapper = new ObjectMapper();
	}
	
	public abstract DefaultMutableTreeNode getTreeNode(CommandMap areaList);

}

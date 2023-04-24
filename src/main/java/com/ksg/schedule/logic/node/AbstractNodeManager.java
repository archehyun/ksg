package com.ksg.schedule.logic.node;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;

public abstract class AbstractNodeManager {
	
	protected ObjectMapper objectMapper;
	
	public AbstractNodeManager()
	{
		objectMapper = new ObjectMapper();
	}
	
	public abstract DefaultMutableTreeNode getTreeNode(CommandMap areaList);

}

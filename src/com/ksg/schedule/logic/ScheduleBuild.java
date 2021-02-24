package com.ksg.schedule.logic;

import com.ksg.commands.LongTask;

/**
/**
 * 
 * ������ ����
 * <pre>
 * com.ksg.schedule.logic.build
 *     |_CreateNormalSchdeduleCommandNew.java     
 * </pre>
 * 
 * @date: 
 * @version: 1.0
 * @author: ��â��
 * @explanation ���� ������ ����
 * 
 *   	
 * 
 */
 
public interface ScheduleBuild extends LongTask{
	public int execute() throws Exception;
	public final int SUCCESS=1;
	public final int FAILURE=2;

}

package com.ksg.schedule.logic;

import com.ksg.commands.LongTask;

/**
/**
 * 
 * 스케줄 생성
 * <pre>
 * com.ksg.schedule.logic.build
 *     |_CreateNormalSchdeduleCommandNew.java     
 * </pre>
 * 
 * @date: 
 * @version: 1.0
 * @author: 박창현
 * @explanation 선박 스케줄 생성
 * 
 *   	
 * 
 */
 
public interface ScheduleBuild extends LongTask{
	public int execute() throws Exception;
	public final int SUCCESS=1;
	public final int FAILURE=2;

}

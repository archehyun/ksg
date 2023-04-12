package com.ksg.schedule.logic;

import com.ksg.commands.ScheduleExecute;

/**
 * 스케줄 조인트 인터페이스
 * 
 * 공동 배선 생성
 * 
 * 1. console
 * 2. Inbound
 * 3. Inland
 * 4. Outbound
 * 5. Route
 * @author 박창현
 *
 */
public interface SchedulePrint extends ScheduleExecute{
	public void initTag();
	
}


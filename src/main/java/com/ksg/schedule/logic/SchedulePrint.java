package com.ksg.schedule.logic;

import com.ksg.commands.ScheduleExecute;

/**
 * ������ ����Ʈ �������̽�
 * 
 * ���� �輱 ����
 * 
 * 1. console
 * 2. Inbound
 * 3. Inland
 * 4. Outbound
 * 5. Route
 * @author ��â��
 *
 */
public interface SchedulePrint extends ScheduleExecute{
	public void initTag();
	
}


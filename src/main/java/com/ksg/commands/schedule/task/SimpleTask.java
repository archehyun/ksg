package com.ksg.commands.schedule.task;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

public abstract class SimpleTask implements ScheduleTask{
	protected int lengthOfTask;
	protected boolean done = false;
	protected boolean canceled = false;
	protected String statMessage;
	protected int current = 0;
	protected int process=0;
	protected BaseService baseService;
	protected ScheduleSubService scheduleService;
	protected HashMap<String, String> portMap;
	protected String fileName;
	protected ScheduleBuildMessageDialog di;
	protected Logger logger = LogManager.getLogger(this.getClass());
	public SimpleTask() {
		baseService 	= DAOManager.getInstance().createBaseService();
		scheduleService	= new ScheduleServiceImpl();
		current = 0;
		done = false;
		canceled = false;
		statMessage = null;
		try {
			// fromPort 초기화
			Code param = new Code();
			param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);


			List<Code> li = baseService.getCodeInfoList(param);
			logger.debug("국내 출발항 초기화:\n"+li);

			portMap = new HashMap<String, String>();

			for(int i=0;i<li.size();i++)
			{
				Code info = li.get(i);
				portMap.put(info.getCode_name(), info.getCode_name_kor());

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public abstract void makeSchedule()throws SQLException, JDOMException, IOException, ParseException;
	public int getLengthOfTask() {
		return lengthOfTask;
	}

	/**
	 */
	public int getCurrent() {
		return current;
	}

	public void stop() {
		canceled = true;
		statMessage = null;
	}

	/**
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Returns the most recent status message, or null
	 * if there is no current status message.
	 */
	public String getMessage() {
		return statMessage;
	}
	/**@설명 keyr값을 기준으로 정렬정렬 알고리즘: 버블 소트
	 * @param intArray
	 */
	protected void bubbleSort(String[] intArray) {
		int out, in;
		for (out = intArray.length - 1; out > 0; out--) {
			for (in = 0; in < out; in++) {
				
				if (intArray[in].compareTo(intArray[in + 1])>0) {
					swap(intArray, in, in + 1);
				}
			}
		}
	}


	/** 버블 소트 수행시 값 교체 메소드
	 * @param intArray
	 * @param one
	 * @param two
	 */
	protected void swap(String[] intArray, int one, int two) {
		String temp = intArray[one];
		intArray[one] = intArray[two];
		intArray[two] = temp;
	}
	protected abstract void initTag();

}

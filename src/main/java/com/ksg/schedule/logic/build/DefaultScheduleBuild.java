package com.ksg.schedule.logic.build;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.service.ADVService;
import com.ksg.service.ScheduleService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ��â��
 *
 */
@Slf4j
public abstract class DefaultScheduleBuild implements ScheduleBuild{
	
	protected ShippersTable searchOption;
	
	private TableService 		tableService;
	protected ADVService 		advService;
	protected ScheduleSubService 	scheduleService;
	protected List scheduleList;
	protected int lengthOfTask, current;
	protected Date selectedDate;
	private Vector portDataArray;
	protected String[] vesselArray;
	protected ADVData advData;
	protected String[][] arrayDatas;
	protected String[][] vslDatas;
	public DefaultScheduleBuild(ShippersTable searchOption) throws SQLException
	{
		tableService = new TableServiceImpl();
		
		scheduleService = new ScheduleServiceImpl();
		
		advService = new ADVServiceImpl();
		
		this.searchOption = searchOption;
		
		scheduleList =tableService.getScheduleTableListByDate(searchOption);
		
		lengthOfTask=scheduleList.size();
		
		log.info("������ ó���� ���̺� �� : "+lengthOfTask);
	}
	/**
	 * 
	 * ������ �߰�
	 * @param scheduledata
	 */
	protected void insertSchedule(ScheduleData scheduledata)
	{
		try
		{
			scheduleService.insertScheduleData(scheduledata);// DB �� ����
		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				break;
			case 2627:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;
			default:
				log.error("error code:"+e.getErrorCode()+"error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
			e.printStackTrace();
			break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//errorlist.add(createError(e.getMessage(), scheduledata.getTable_id(), ""));

		}
	}
	/**
	 * �ιٿ��, �ƿ��ٿ�� �ױ� �ε��� �и�
	 * @param indexField
	 * 
	 * @return portArry
	 */
	protected int[] makePortArraySub(String indexField) {
		if(indexField==null||indexField.equals("")||indexField.equals(" "))
			return null;

		
		indexField=indexField.trim();

		// #�� �������� �ױ� �ε����� ����
		StringTokenizer st = new StringTokenizer(indexField,"#");

		Vector<Integer> indexList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			try
			{
				indexList.add(Integer.parseInt(st.nextToken().trim()));
			}
			catch (NumberFormatException e) 
			{
				/*logger.error("number foramt error:"+indexField+",id:"+table.getTable_id());
				//errorlist.add(createError("�ε��� ����", table.getTable_id(), field));
*/
				continue;
			}
		}

		int array[] =new int[indexList.size()];
		
		for(int i=0;i<indexList.size();i++)
		{
			array[i]=indexList.get(i);
		}		

		return array;

	}
	
	/**
	 * �ױ� ���� ��ȸ
	 * 
	 * @param arry
	 * @param index
	 * @return
	 */
	protected TablePort getPort(Vector arry,int index)
	{
		TablePort port1 = new TablePort();

		if(arry.size()==1)
		{
			port1 = (TablePort) arry.get(0);
		}
		else if(arry.size()>1)
		{
			for(int i=0;i<arry.size();i++)
			{
				TablePort port=(TablePort) arry.get(i);
				if(port.getPort_index()==index)
				{
					port1.setPort_name(port.getPort_name());
					port1.addSubPort(port);
				}
			}
		}

		return port1;

	}
	
	/**
	 * @param table
	 * @return
	 * @throws SQLException 
	 */
	protected Vector getPortList(ShippersTable table) throws SQLException 
	{
		portDataArray  = new Vector();

			TablePort tablePort = new TablePort();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(TablePort.TYPE_PARENT);

			List li=tableService.getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				TablePort port = (TablePort) li.get(i);

				portDataArray.add(port);
			}
			log.debug("portarray:"+table.getTable_id()+","+portDataArray);
		
		return portDataArray;
	}
	
	/**
	 * 
	 * ��ȿ�� üũ
	 * 1. ��¥
	 * 2. �Էµ� �������� ����
	 * 3. ���� ��� ����
	 * 
	 * @param tableData
	 * @param selectedDate
	 * @return
	 */
	protected boolean isValidation(ShippersTable tableData) 
	{
		
		try {
		// ��¥ ���� ���� DB ������ ��ü
		if(KSGDateUtil.daysDiffABS(KSGDateUtil.toDate2(tableData.getDate_isusse()), KSGDateUtil.toDate2(selectedDate))!=0)
			return false;

		advData = (ADVData) advService.getADVData(tableData.getTable_id());
		// �Էµ� ���� ������ ������ ���
		if(advData==null||advData.getData()==null)
		{
			return false;
		}

		return true;
		}catch( Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public int getLengthOfTask() {
		// TODO Auto-generated method stub
		return lengthOfTask;
	}
	public int getCurrent() {
		// TODO Auto-generated method stub
		return current;
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}

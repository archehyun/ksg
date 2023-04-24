package com.ksg.schedule.logic.print.outbound;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;

/**
 * @deprecated
 * @author archehyun
 *
 */
public class OutboundSchedulePrintV1 extends AbstractOutboundSchedulePrint{

	XTGManager xtgmanager = new XTGManager();
	
	private String fromPort[];
	private int fromPortCount;


	DAOManager manager =DAOManager.getInstance();

	private List portli;


	public OutboundSchedulePrintV1() throws Exception {
		super();

		portli = scheduleService.getOutboundPortList();

		lengthOfTask = portli.size();

		message = "Outbound 생성중...";

		logger.info("outbound 스케줄 생성 및 초기화:"+lengthOfTask);
	}


	public void init() {

		logger.info("태그정보 초기화");

		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

		TAG_VERSION4="<dps:Body Text=<BasedOn:정규><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";


		TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";
		try {
			
			// 국내 출발항 목록
			
			// 부산 - 광양 - 인천 - 평택 - 울산
			
			Code param = new Code();
			
			param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);
			List<Code> li = baseService.getCodeInfoList(param);

			fromPort = new String[li.size()];
			for(int i=0;i<li.size();i++)
			{
				Code info = li.get(i);
				fromPort[i] =info.getCode_name();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	/**
	 * 도착항구 정렬
	 * @param outboundFromPortList
	 * @return
	 */
	private List<String> arrangeFromPort(List outboundFromPortList) 
	{	
		logger.debug("from-port arrange");

		List<String> tempFromPort = new LinkedList<String>();
		for(int i=0;i<fromPort.length;i++)
		{
			tempFromPort.add(fromPort[i]);
		}

		for(int i=0;i<fromPort.length;i++)
		{
			if(!outboundFromPortList.contains(fromPort[i]))
			{
				tempFromPort.remove(fromPort[i]);
			}
		}
		return tempFromPort;
	}

	/**
	 * 출발항구 정렬
	 * @param outboundFromPortList
	 * @return
	 */
	private List arrangeToPort(List outboundFromPortList) 
	{	
		logger.debug("to-port arrange");

		List tempFromPort = new LinkedList();
		for(int i=0;i<outboundFromPortList.size();i++)
		{
			tempFromPort.add(outboundFromPortList.get(i));			
		}

		for(int i=0;i<fromPort.length;i++)
		{
			if(outboundFromPortList.contains(fromPort[i]))
			{
				tempFromPort.remove(fromPort[i]);
			}
		}
		return tempFromPort;
	}


	private void createXMLFile(Document document) throws JDOMException,
	IOException {
		try {
			logger.info("create outbound xml file");
			Format format = Format.getPrettyFormat();

			format.setEncoding("EUC-KR");
			format.setIndent("\r\n");
			format.setIndent("\t");

			XMLOutputter outputter = new XMLOutputter(format);

			FileWriter writer = new FileWriter("outbound_source.xml");
			outputter.output(document, writer);
			writer.close();

			Element root = document.getRootElement();
			String data="";
			List<MyElement> rows = root.getChildren(XML_INFO.XML_TAG_SCHEDULE_ROW);
			logger.debug("schedule-row size:"+rows.size());
			for (int i = 0; i < rows.size(); i++) 
			{
				Element row = rows.get(i);
				data+=row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NAME)+" , "+row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NATIONALITY)+" ";
				List<MyElement> columns = row.getChildren(XML_INFO.XML_TAG_FROM_PORT);

				for (int j = 0; j < columns.size(); j++) 
				{
					Element column = columns.get(j);
					List<MyElement> vessellist=column.getChildren(XML_INFO.XML_TAG_VESSEL);
					sortElementByDateF(vessellist);
					for (int vesselcount = 0; vesselcount < vessellist.size(); vesselcount++) 
					{
						Element vessel = vessellist.get(vesselcount);
						String use = vessel.getAttributeValue(XML_INFO.XML_TAG_USE);
						if(Boolean.valueOf(use))
						{
							String dateF =vessel.getAttributeValue(XML_INFO.XML_TAG_DATE_F);

							String vesselname =vessel.getAttributeValue(XML_INFO.XML_TAG_NAME);

							String company =vessel.getAttributeValue(XML_INFO.XML_TAG_COMPANY);

							String agent =vessel.getAttributeValue(XML_INFO.XML_TAG_AGENT);

							String dateT =vessel.getAttributeValue(XML_INFO.XML_TAG_DATE_T);

							if(company.equals(agent))
							{
								data+=dateF+","+vesselname+" ("+company+")"+dateT+"\r\n ";
							}else
							{
								data+=dateF+","+vesselname+" ("+company+")"+dateT+"\r\n ";
							}
						}
					}

				}
			}

			makeXTG(document);


		} catch (java.io.IOException e) {
			e.printStackTrace();
		} 

	}

	// 자바 힙 메모리 오류 원소스, 2014-8-5
	private void makeXTG2(Document document) throws IOException {
		logger.debug("print file");
		Element root = document.getRootElement();
		StringBuffer xtgStringBuffer = new StringBuffer();
		StringBuffer outport_buffer = new StringBuffer();

		xtgStringBuffer.append(	TAG_VERSION0+"\r\n"
				+TAG_VERSION2+"\r\n"
				+TAG_VERSION3+"\r\n"
				+TAG_VERSION4+"\r\n"
				+TAG_VERSION5);

		List<MyElement> schedule_list = root.getChildren(XML_INFO.XML_TAG_SCHEDULE_ROW);
		for(int i=0;i<schedule_list.size();i++)
		{
			Element schedule_row = schedule_list.get(i);

			xtgStringBuffer.append(
					buildXTG2(i, schedule_row));

			outport_buffer.append(schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NAME)+" \r\n");


			List<MyElement> columns = schedule_row.getChildren(XML_INFO.XML_TAG_FROM_PORT);
			for (int j = 0; j < columns.size(); j++) {
				Element column = columns.get(j);

				String name = column.getAttribute(XML_INFO.XML_TAG_NAME).getValue();

				//*****				xtgStringBuffer.append((j==0?" \r\n \r\n<cs:><ct:Bold><cs:7.000000>- ":" \r\n<cs:><ct:Bold><cs:7.000000>- ")+name+" -<ct:>\r\n");
				//*****2				xtgStringBuffer.append((j==0?"<cf:Helvetica LT Std> \r\n \r\n<cs:><ct:Bold><cs:7.000000>- ":"<cf:Helvetica LT Std> \r\n<cs:><ct:Bold><cs:7.000000>- ")+name+" -<ct:>\r\n<cf:>");
				xtgStringBuffer.append(buildXTG3(j, name));
				List<MyElement> vessellist=column.getChildren(XML_INFO.XML_TAG_VESSEL);

				sortElementByDateF(vessellist);


				for (int vesselcount = 0; vesselcount < vessellist.size(); vesselcount++) 
				{
					Element vessel = vessellist.get(vesselcount);
					String use = vessel.getAttributeValue(XML_INFO.XML_TAG_USE);
					String major_company 		= vessel.getAttributeValue(XML_INFO.XML_TAG_MAJOR_COMPANY);
					if(Boolean.valueOf(use))
					{	
						logger.info("vessel use1:"+Boolean.valueOf(use));
						String dateF =vessel.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
						String vesselname =vessel.getAttributeValue(XML_INFO.XML_TAG_NAME);

						String company =vessel.getAttributeValue(XML_INFO.XML_TAG_COMPANY);

						String agent =vessel.getAttributeValue(XML_INFO.XML_TAG_AGENT);

						String dateT =vessel.getAttributeValue("dateT");

						String vessel_type =vessel.getAttributeValue(XML_INFO.XML_TAG_VESSEL_TYPE);
						try{

							xtgStringBuffer.append(buildXTG4(dateF, vesselname, company, agent, dateT,
									vessel_type));


						}
						catch(Exception e)
						{
							continue;
						}

					}
					else
					{
						//logger.info("vessel use2:"+Boolean.valueOf(use));
						Element commonlist=vessel.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
						if(commonlist==null)
						{
							continue;
						}

						String dateF 		= commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F).substring(5);

						String vesselname 	= commonlist.getAttributeValue(XML_INFO.XML_TAG_NAME);

						String company 		= commonlist.getAttributeValue(XML_INFO.XML_TAG_COMPANY);


						String vessel_type  = vessel.getAttributeValue(XML_INFO.XML_TAG_VESSEL_TYPE);
						String dateT ="";

						try
						{
							dateT =commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T).substring(5);
						}catch(Exception e)
						{
							dateT =commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T);
						}

						Vector<String> companyList = new Vector<String>();
						companyList.add(company);


						List<MyElement> vesselList=commonlist.getChildren(XML_INFO.XML_TAG_COMMON_VESSEL);


						//정렬
						for(int vesselIndex=0;vesselIndex<vesselList.size();vesselIndex++)
						{
							Element vesselItem = vesselList.get(vesselIndex);
							boolean flag=true;
							for(int c=0;c<companyList.size();c++)
							{
								// 동일 선사명이 있는지 확인
								if(companyList.get(c).equals(vesselItem.getAttributeValue(XML_INFO.XML_TAG_COMPANY)))
								{
									flag=false;
								}
							}
							if(flag)
							{
								companyList.add(vesselItem.getAttributeValue(XML_INFO.XML_TAG_COMPANY));
							}
						}


						// 대표 선사 확인
						try{
							if(major_company!=null)
							{
								logger.debug("major_compamy:"+major_company);
								company=arrangedCompanyList(major_company,companyList);
							}else
							{
								company=arrangedCompanyList(companyList);
							}


							//*****							xtgStringBuffer.append("<cs:><cs:6.000000>"+dateF+"\t"+BOLD_TAG_F+vesselname+BOLD_TAG_B+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n");
							//*****2							xtgStringBuffer.append("<cs:><cf:Helvetica LT Std><ct:Roman><cs:6.000000>"+dateF+"<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n");
							xtgStringBuffer.append(buildXTG5(dateF, vesselname, company, vessel_type,
									dateT));



						}catch(Exception e)
						{
							e.printStackTrace();
							continue;
						}


					}

					Element ts_vessel=vessel.getChild(XML_INFO.XML_TAG_TS_VESSEL);
					String vesselname =vessel.getAttributeValue(XML_INFO.XML_TAG_NAME);
					try {
						if(ts_vessel!=null)
						{	
							String ts_vesselname=ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_VESSELNAME);
							Vessel op = new Vessel();
							op.setVessel_name(ts_vesselname);


							Vessel info = scheduleManager.searchVessel(ts_vesselname);
							//*****							xtgStringBuffer.append("<cs:><cs:6.000000>\t<ct:Narrow Bold>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n");
							//*****2							xtgStringBuffer.append("<cs:><cs:6.000000>\t<ct:77 Bold Condensed>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n");
							xtgStringBuffer.append(buildXTG6(ts_vessel, vesselname, info));
						}
					} catch (ResourceNotFoundException e) 
					{
						e.printStackTrace();
					}
					
					
				}
			}
		}
		xtgmanager.createXTGFile(xtgStringBuffer.toString(),"outbound_print.txt");
		xtgmanager.createXTGFile(outport_buffer.toString(),"outbound_port_print.txt");
		logger.info("outbound print end");
	}
	private void makeXTG(Document document) throws IOException {
		logger.debug("print file");
		Element root = document.getRootElement();
		StringBuffer xtgStringBuffer = new StringBuffer();
		StringBuffer outport_buffer = new StringBuffer();

		// 태그 정보를 저장하기 위한 리스트
		Vector<String> xtgList = new Vector<String>();



		// 1차 태그
		/*		xtgStringBuffer.append(	TAG_VERSION0+"\r\n"+
								TAG_VERSION2+"\r\n"+
								TAG_VERSION3+"\r\n"+
								TAG_VERSION4+"\r\n"+
								TAG_VERSION5);*/		

		xtgList.add(buildVersionXTG());



		List<MyElement> schedule_list = root.getChildren(XML_INFO.XML_TAG_SCHEDULE_ROW);
		for(int i=0;i<schedule_list.size();i++)
		{
			Element schedule_row = schedule_list.get(i);

			// 2차 태그
			/*			xtgStringBuffer.append(
					(i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

					+schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NAME)

			+" , "+schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NATIONALITY)+" ");*/

			xtgList.add(buildXTG2(i, schedule_row));


			outport_buffer.append(schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NAME)+" \r\n");


			List<MyElement> columns = schedule_row.getChildren(XML_INFO.XML_TAG_FROM_PORT);
			for (int j = 0; j < columns.size(); j++) {
				Element column = columns.get(j);

				String name = column.getAttribute(XML_INFO.XML_TAG_NAME).getValue();


				/*xtgStringBuffer.append((j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+name+" -\r\n");*/
				xtgList.add(buildXTG3(j, name));
				List<MyElement> vessellist=column.getChildren(XML_INFO.XML_TAG_VESSEL);

				sortElementByDateF(vessellist);


				for (int vesselcount = 0; vesselcount < vessellist.size(); vesselcount++) 
				{
					Element vessel = vessellist.get(vesselcount);
					String use = vessel.getAttributeValue(XML_INFO.XML_TAG_USE);
					String major_company 		= vessel.getAttributeValue(XML_INFO.XML_TAG_MAJOR_COMPANY);
					if(Boolean.valueOf(use))
					{	

						// 사용
						logger.info("vessel use1:"+Boolean.valueOf(use));
						String dateF =vessel.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
						String vesselname 	= vessel.getAttributeValue(XML_INFO.XML_TAG_NAME);

						String company 		= vessel.getAttributeValue(XML_INFO.XML_TAG_COMPANY);

						String agent 		= vessel.getAttributeValue(XML_INFO.XML_TAG_AGENT);

						String dateT 		= vessel.getAttributeValue("dateT");

						String vessel_type 	= vessel.getAttributeValue(XML_INFO.XML_TAG_VESSEL_TYPE);
						try{

							/*							xtgStringBuffer.append("◆◆◆◆◆"+KSGDateUtil.format2(KSGDateUtil.toDate4(dateF))
									+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B
										+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")
										+"("+(company.equals(agent)?company:company+"/"+agent)+")\t"
										+KSGDateUtil.format2(KSGDateUtil.toDate4(dateT))+"\r\n");*/
							xtgList.add(buildXTG4(dateF, vesselname, company, agent, dateT,vessel_type));


						}
						catch(Exception e)
						{

							logger.error(e.getMessage());
							continue;
						}

					}
					else
					{
						//logger.info("vessel use2:"+Boolean.valueOf(use));
						Element commonlist=vessel.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
						if(commonlist==null)
						{
							continue;
						}

						String dateF 		= commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F).substring(5);

						String vesselname 	= commonlist.getAttributeValue(XML_INFO.XML_TAG_NAME);

						String company 		= commonlist.getAttributeValue(XML_INFO.XML_TAG_COMPANY);


						String vessel_type  = vessel.getAttributeValue(XML_INFO.XML_TAG_VESSEL_TYPE);
						String dateT ="";

						try
						{
							dateT =commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T).substring(5);
						}catch(Exception e)
						{

							logger.error(e.getMessage());
							dateT =commonlist.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T);
						}

						Vector<String> companyList = new Vector<String>();
						companyList.add(company);


						List<MyElement> vesselList=commonlist.getChildren(XML_INFO.XML_TAG_COMMON_VESSEL);


						//정렬
						for(int vesselIndex=0;vesselIndex<vesselList.size();vesselIndex++)
						{
							Element vesselItem = vesselList.get(vesselIndex);
							boolean flag=true;
							for(int c=0;c<companyList.size();c++)
							{
								// 동일 선사명이 있는지 확인
								if(companyList.get(c).equals(vesselItem.getAttributeValue(XML_INFO.XML_TAG_COMPANY)))
								{
									flag=false;
								}
							}
							if(flag)
							{
								companyList.add(vesselItem.getAttributeValue(XML_INFO.XML_TAG_COMPANY));
							}
						}


						// 대표 선사 확인
						try{
							if(major_company!=null)
							{
								logger.info("major_compamy:"+major_company);
								company=arrangedCompanyList(major_company,companyList);
							}else
							{
								company=arrangedCompanyList(companyList);
							}


							//*****							xtgStringBuffer.append("<cs:><cs:6.000000>"+dateF+"\t"+BOLD_TAG_F+vesselname+BOLD_TAG_B+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n");
							//*****2							xtgStringBuffer.append("<cs:><cf:Helvetica LT Std><ct:Roman><cs:6.000000>"+dateF+"<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n");
							/*xtgStringBuffer.append("<ct:><cs:><ct:Roman><cs:6.000000>"+dateF+"<ct:>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n");*/
							xtgList.add(buildXTG5(dateF, vesselname, company, vessel_type,dateT));



						}catch(Exception e)
						{
							e.printStackTrace();
							continue;
						}


					}

					Element ts_vessel=vessel.getChild(XML_INFO.XML_TAG_TS_VESSEL);
					String vesselname =vessel.getAttributeValue(XML_INFO.XML_TAG_NAME);
					try {
						if(ts_vessel!=null)
						{	
							String ts_vesselname=ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_VESSELNAME);
							Vessel op = new Vessel();
							op.setVessel_name(ts_vesselname);
							Vessel info = scheduleManager.searchVessel(ts_vesselname);
							//*****							xtgStringBuffer.append("<cs:><cs:6.000000>\t<ct:Narrow Bold>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n");
							//*****2							xtgStringBuffer.append("<cs:><cs:6.000000>\t<ct:77 Bold Condensed>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n");
							/*xtgStringBuffer.append("<cs:><cs:6.000000>\t<ct:Bold Condensed>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n");*/
							xtgList.add(buildXTG6(ts_vessel, vesselname, info));
						}
					} catch (ResourceNotFoundException e) 
					{
						e.printStackTrace();
					}
					
				}
			}
		}
		xtgmanager.createXTGFile_NEW(xtgList,"outbound_print.txt");
		xtgmanager.createXTGFile(outport_buffer.toString(),"outbound_port_print.txt");
		logger.info("outbound print end");
	}

	private String buildXTG6(Element ts_vessel, String vesselname, Vessel info) {
		return "<cs:><cs:6.000000>\t<ct:Bold Condensed>-TS  "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_PORT)+"-<ct:>   "+ts_vessel.getAttributeValue(XML_INFO.XML_TAG_TS_DATE)+"   "+BOLD_TAG_F+(vesselname.equals(info.getVessel_name())?"":info.getVessel_name())+BOLD_TAG_B+"\r\n";
	}

	private String buildXTG5(String dateF, String vesselname, String company,
			String vessel_type, String dateT) {
		return "<ct:><cs:><ct:Roman><cs:6.000000>"+dateF+"<ct:>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n";
	}

	private String buildXTG4(String dateF, String vesselname, String company,
			String agent, String dateT, String vessel_type)
					throws ParseException {
		return "◆◆◆◆◆"+KSGDateUtil.format2(KSGDateUtil.toDate4(dateF))
				+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B
				+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")
				+"("+(company.equals(agent)?company:company+"/"+agent)+")\t"
				+KSGDateUtil.format2(KSGDateUtil.toDate4(dateT))+"\r\n";
	}

	private String buildXTG3(int j, String name) {
		return (j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+name+" -\r\n";
	}

	private String buildXTG2(int i, Element schedule_row) {
		return (i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

				+schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NAME)

				+" , "+schedule_row.getChild(XML_INFO.XML_TAG_TO_PORT).getAttributeValue(XML_INFO.XML_TAG_NATIONALITY)+" ";
	}

	private String buildVersionXTG() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}	
	private String arrangedCompanyList(String major_company,
			Vector<String> companyList) {

		Vector<String> new_company = new Vector<String>();
		for(int i=0;i<companyList.size();i++)
		{
			if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
			{
				new_company.add(companyList.get(i));
			}
		}

		return major_company+","+this.arrangedCompanyList(new_company);
	}

	private String arrangedCompanyList(Vector<String> companyList) {
		String[] array2=new String[companyList.size()];		

		companyList.toArray(array2);
		//알파벳 정렬
		Arrays.sort(array2, new StringCompare());

		String companyStringList ="";
		for(int i=0;i<array2.length;i++)
		{
			companyStringList+=array2[i];
			if(i!=array2.length-1)
				companyStringList+=",";
		}

		return companyStringList;
	}

	private void sortElementByDateF(List<MyElement> vessellist) {
		MyElement d[]= new MyElement[vessellist.size()];
		for(int i=0;i<vessellist.size();i++)
			d[i]=vessellist.get(i);
		Arrays.sort(d);
		vessellist.clear();
		for(int i=0;i<d.length;i++)
			vessellist.add(d[i]);
	}

	/**
	 * @param fromPort
	 * @return
	 */
	private Element makeCommomShipping(MyElement fromPort)
	{
		int contentsize = fromPort.getContentSize();
		logger.debug("makeCommon:"+contentsize);
		for(int i=0;i<contentsize;i++)
		{
			Content c=fromPort.getContent(i);

			if(c instanceof MyElement)
			{
				MyElement oneData = (MyElement)c;

				// ts - schedule 이 있는 지
				Element element =oneData.getChild(XML_INFO.XML_TAG_TS_VESSEL);
				if(element!=null)
					continue;

				// 스케줄이 사용 됬는 지 확인
				if(!Boolean.valueOf(oneData.getAttributeValue(XML_INFO.XML_TAG_USE)))
					continue;


				String vesselName=oneData.getAttributeValue(XML_INFO.XML_TAG_NAME);
				String dateF1 = oneData.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
				String dateT1 = oneData.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
				String company1 = oneData.getAttributeValue(XML_INFO.XML_TAG_COMPANY);
				String voyage = oneData.getAttributeValue(XML_INFO.XML_TAG_VOYAGE);
				String agent1 = oneData.getAttributeValue(XML_INFO.XML_TAG_AGENT);
				String table_id1 = oneData.getAttributeValue(XML_INFO.XML_TAG_TABLE_ID);

				try {
					//대표 선사 검색

					Vessel searchedVessel =  scheduleManager.searchVessel(vesselName);
					if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
					{
						oneData.setAttribute(XML_INFO.XML_TAG_MAJOR_COMPANY,searchedVessel.getVessel_company());
						logger.info("set major-company:"+searchedVessel.getVessel_company());
					}
				} catch (ResourceNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				

				for(int j=0;j<contentsize;j++)
				{
					Content sub=fromPort.getContent(j);

					if(sub instanceof MyElement)
					{
						MyElement subData = (MyElement)sub;
						if(!Boolean.valueOf(subData.getAttributeValue(XML_INFO.XML_TAG_USE)))
							continue;
						// ts - schedule 이 있는 지
						Element sube =subData.getChild(XML_INFO.XML_TAG_TS_VESSEL);
						if(sube!=null)
							continue;

						String vesselname2=subData.getAttributeValue(XML_INFO.XML_TAG_NAME);
						String dateF2 = subData.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
						String dateT2 = subData.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
						String company2 = subData.getAttributeValue(XML_INFO.XML_TAG_COMPANY);
						String voyage2 = subData.getAttributeValue(XML_INFO.XML_TAG_VOYAGE);
						String agent2 = subData.getAttributeValue(XML_INFO.XML_TAG_AGENT);
						String table_id2 = oneData.getAttributeValue(XML_INFO.XML_TAG_TABLE_ID);

						// 선박 명이 다르다면 통과
						if(!vesselName.equals(vesselname2))
							continue;



						if(company1.equals(company2))
						{
							// 같은 Voyage 일때
							if(ScheduleBuildUtil.getNumericVoyage(voyage)==ScheduleBuildUtil.getNumericVoyage(voyage2)) 
							{
								// 동일 스케줄일때는 skip
								if(dateF1.equals(dateF2)&&dateT1.equals(dateT2)&&!table_id1.equals(table_id2))
									continue;

								MyElement common_sub=(MyElement) oneData.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
								/**
								 *추가 
								 */
								String dateFF="";
								if(common_sub!=null)
								{
									dateFF = common_sub.getAttributeValue(XML_INFO.XML_TAG_BASE_DATE_F);
								}else
								{
									dateFF = dateF1;
								}
								// 출발일 차이가 3일 이내
								if(KSGDateUtil.isThreeDayUnder(dateFF,dateF2))
								{
									MyElement common_vessel = new MyElement(XML_INFO.XML_TAG_COMMON_VESSEL);
									common_vessel.setAttribute(XML_INFO.XML_TAG_NAME,vesselname2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_COMPANY,(company2.equals(agent2)?company2:company2+"/"+agent2));
									common_vessel.setAttribute(XML_INFO.XML_TAG_VOYAGE,voyage2);


									if(common_sub==null)
									{
										common_sub = new MyElement(XML_INFO.XML_TAG_COMMON_SHIPPING);
										common_sub.setAttribute(XML_INFO.XML_TAG_NAME,vesselName);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF1);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT1);
										common_sub.setAttribute(XML_INFO.XML_TAG_COMPANY,(company1.equals(agent1)?company1:company1+"/"+agent1));

										oneData.addContent(common_sub);
									}else
									{
										dateT1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T);
										dateF1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);
									}

									common_sub.setAttribute(XML_INFO.XML_TAG_BASE_DATE_F,KSGDateUtil.rowerDate(dateF1, dateFF));
									common_sub.addContent(common_vessel);
									oneData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									subData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));
									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_T,KSGDateUtil.rowerDate(dateT1, dateT2));

									// 정렬 하기 위한 날짜 추가
									oneData.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));
								}

							}
						}
						else // 다른  선사 일때
						{
							if(ScheduleBuildUtil.getNumericVoyage(voyage)==ScheduleBuildUtil.getNumericVoyage(voyage2)) // 같은 Voyage 일때
							{
								// 출발일 차이가 3일 이내
								MyElement common_sub=(MyElement) oneData.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
								/**
								 *추가 
								 */
								String dateFF="";
								if(common_sub!=null)
								{
									dateFF = common_sub.getAttributeValue(XML_INFO.XML_TAG_BASE_DATE_F);
								}else
								{
									dateFF = dateF1;
								}


								if(KSGDateUtil.isThreeDayUnder(dateFF,dateF2))
								{
									MyElement common_vessel = new MyElement(XML_INFO.XML_TAG_COMMON_VESSEL);
									common_vessel.setAttribute(XML_INFO.XML_TAG_NAME,vesselname2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_COMPANY,(company2.equals(agent2)?company2:company2+"/"+agent2));
									common_vessel.setAttribute(XML_INFO.XML_TAG_VOYAGE,voyage2);
									if(common_sub==null)
									{
										common_sub = new MyElement(XML_INFO.XML_TAG_COMMON_SHIPPING);
										common_sub.setAttribute(XML_INFO.XML_TAG_NAME,vesselName);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF1);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT1);
										common_sub.setAttribute(XML_INFO.XML_TAG_COMPANY,(company1.equals(agent1)?company1:company1+"/"+agent1));

										oneData.addContent(common_sub);
									}
									else
									{
										dateT1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T);
										dateF1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);
									}
									common_sub.setAttribute(XML_INFO.XML_TAG_BASE_DATE_F,KSGDateUtil.rowerDate(dateF1, dateFF));
									common_sub.addContent(common_vessel);
									oneData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									subData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));
									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_T,KSGDateUtil.rowerDate(dateT1, dateT2));
									oneData.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));

								}

							}else //다른 Voyage 일때
							{
								// 출발일이 같을 때
								if(KSGDateUtil.isSame(dateF1,dateF2))
								{
									MyElement common_vessel = new MyElement(XML_INFO.XML_TAG_COMMON_VESSEL);
									common_vessel.setAttribute(XML_INFO.XML_TAG_NAME,vesselname2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT2);
									common_vessel.setAttribute(XML_INFO.XML_TAG_COMPANY,(company2.equals(agent2)?company2:company2+"/"+agent2));
									common_vessel.setAttribute(XML_INFO.XML_TAG_VOYAGE,voyage2);
									MyElement common_sub=(MyElement) oneData.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
									if(common_sub==null)
									{
										common_sub = new MyElement(XML_INFO.XML_TAG_COMMON_SHIPPING);
										common_sub.setAttribute(XML_INFO.XML_TAG_NAME,vesselName);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF1);
										common_sub.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT1);
										common_sub.setAttribute(XML_INFO.XML_TAG_COMPANY,(company1.equals(agent1)?company1:company1+"/"+agent1));
										oneData.addContent(common_sub);
									}
									else
									{
										dateT1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_T);
										dateF1 = common_sub.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);
									}
									common_sub.setAttribute(XML_INFO.XML_TAG_BASE_DATE_F,KSGDateUtil.rowerDate(dateF1, dateF2));
									common_sub.addContent(common_vessel);
									oneData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									subData.setAttribute(XML_INFO.XML_TAG_USE,"false");
									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));
									String selectDateT =KSGDateUtil.rowerDate(dateT1, dateT2);

									common_sub.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_T,selectDateT);
									oneData.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,KSGDateUtil.biggerDate(dateF2,dateF1 ));

								}
							}

						}
					}
				}

				// 날짜 체크

				String checkDateT=oneData.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
				
				String checkDateF=oneData.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);

				// 공동배선 일때 앞에 날짜가 뒤에 날짜 보다 클때
				try {
					if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(checkDateF), KSGDateUtil.toDate4(checkDateT))<=0&&
							oneData.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING)!=null)
					{

						Element common=oneData.getChild(XML_INFO.XML_TAG_COMMON_SHIPPING);
						List<MyElement> common_vessel = common.getChildren(XML_INFO.XML_TAG_COMMON_VESSEL);
						for(int b=0; b<common_vessel.size();b++)
						{
							MyElement sub=common_vessel.get(b);

							if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(checkDateF), KSGDateUtil.toDate4(sub.getAttributeValue("dateT")))>0)
							{
								common.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_T, sub.getAttributeValue(XML_INFO.XML_TAG_DATE_T));
								break;
							}
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	class MyElement extends Element implements Comparable<Element>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public MyElement(String string) {
			super(string);
		}
		public String toString()
		{
			return "["+this.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F)+","+ this.getAttributeValue(XML_INFO.XML_TAG_NAME)+"]";
		}
		public int compareTo(Element o) {
			Element table1 = o;
			String dateF1 =table1.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);
			String dateF2 =this.getAttributeValue(XML_INFO.XML_TAG_COMMON_DATE_F);
			String dateT1 =table1.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
			String dateT2 =this.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
			int result=0;
			Date oneDateF = null;
			Date twoDateF = null;
			Date oneDateT = null;
			Date twoDateT = null;
			try 
			{
				oneDateF = KSGDateUtil.toDate4(dateF1);
				twoDateF = KSGDateUtil.toDate4(dateF2);
				oneDateT = KSGDateUtil.toDate4(dateT1);
				twoDateT = KSGDateUtil.toDate4(dateT2);
				result = KSGDateUtil.daysDiff( oneDateF,twoDateF);
				if(result ==0)
				{
					return KSGDateUtil.daysDiff( oneDateT,twoDateT);
				}else
				{
					return result;	
				}
			}
			catch (ParseException e) 
			{
				logger.error("sort error=>dateF1:"+dateF1+",dateF2:"+dateF2);
				return -1;
			}catch (Exception e)
			{
				logger.error("sort error=>"+e.getMessage());
				return -1;
			}
		}
	}


	@Override
	public int execute() {


		logger.info("make OutboundFile start");
		try{
			long startTime = System.currentTimeMillis();
			Element rootElement = new Element("root");
			Document document = new Document(rootElement);

			// 아웃바운드 도착항 조회

			for(int i=0;i<portli.size();i++)
			{
				logger.debug(" to port : "+portli.get(i));	
			}
			portli=arrangeToPort(portli);

			Iterator portiter = portli.iterator();
			MyElement schedule_row;
			fromPortCount = portli.size();

			//	lengthOfTask=fromPortCount;


			PortInfo portInfo=null;
			while(portiter.hasNext())
			{
				String port = (String) portiter.next();

				try{
					portInfo=scheduleManager.searchPort(port);
				}catch(PortNullException e)
				{
					logger.error(e.getMessage());
					continue;
				}
				

				//xml 객체 생성================
				schedule_row = new MyElement(XML_INFO.XML_TAG_SCHEDULE_ROW);

				rootElement.addContent(schedule_row);
				MyElement toPort =new MyElement(XML_INFO.XML_TAG_TO_PORT);
				toPort.setAttribute(XML_INFO.XML_TAG_NAME,port);
				logger.debug("port name:"+port);
				if(portInfo!=null)
				{	
					toPort.setAttribute(XML_INFO.XML_TAG_NATIONALITY,portInfo.getPort_nationality());
				}
				schedule_row.addContent(toPort);			
				//============================


				List<String> fromPortList = arrangeFromPort( scheduleService.getOutboundFromPortList(port));
				logger.debug("fromPort size:"+fromPortList.size());
				Iterator<String> fromPortiter = fromPortList.iterator();
				while(fromPortiter.hasNext())
				{
					String fromPort = fromPortiter.next();
					//출발항 , 도착항 기준으로 스케줄 조회
					List<ScheduleData> schedueList_1 = scheduleService.getOutboundScheduleList(port, fromPort);				
					logger.debug("scheduel size:"+schedueList_1.size());

					// 일자 별로 정렬
					//sortByDateF(schedueList_1);

					if(schedueList_1.size()>0)
					{
						MyElement fromPort2 =new MyElement(XML_INFO.XML_TAG_FROM_PORT);

						fromPort2.setAttribute(XML_INFO.XML_TAG_NAME,fromPort);
						schedule_row.addContent(fromPort2);

						Iterator<ScheduleData> sIterator = schedueList_1.iterator();
						while(sIterator.hasNext())
						{
							ScheduleData oneData = sIterator.next();
							if(oneData.getTS().equals("TS"))
							{
								fromPort2.setAttribute(XML_INFO.XML_TAG_TS,"true");
							}	

							String dateF=oneData.getDateF();
							String dateT=oneData.getDateT();

							String company_abbr=oneData.getCompany_abbr();
							String agent = oneData.getAgent();

							MyElement vessel =new MyElement(XML_INFO.XML_TAG_VESSEL);
							vessel.setAttribute(XML_INFO.XML_TAG_NAME,oneData.getVessel());



							vessel.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF);
							vessel.setAttribute(XML_INFO.XML_TAG_COMMON_DATE_F,dateF);
							vessel.setAttribute(XML_INFO.XML_TAG_COMPANY,company_abbr);
							vessel.setAttribute(XML_INFO.XML_TAG_AGENT,agent);
							vessel.setAttribute(XML_INFO.XML_TAG_DATE_T,dateT);
							vessel.setAttribute(XML_INFO.XML_TAG_USE,"true");
							vessel.setAttribute(XML_INFO.XML_TAG_VOYAGE,oneData.getVoyage_num());
							vessel.setAttribute(XML_INFO.XML_TAG_VESSEL_TYPE,oneData.getVessel_type()!=null?oneData.getVessel_type():"");
							vessel.setAttribute(XML_INFO.XML_TAG_TABLE_ID,oneData.getTable_id());
							String gubun =oneData.getGubun();
							if(gubun.equals("TS"))
							{	
								MyElement tsVessel = new MyElement(XML_INFO.XML_TAG_TS_VESSEL);
								vessel.addContent(tsVessel);
								tsVessel.setAttribute(XML_INFO.XML_TAG_TS_PORT,oneData.getTS());
								tsVessel.setAttribute(XML_INFO.XML_TAG_TS_VESSELNAME,oneData.getTs_vessel());
								tsVessel.setAttribute(XML_INFO.XML_TAG_TS_DATE,oneData.getTs_date());
							}

							fromPort2.addContent(vessel);
						}

						logger.debug("common check fromPort:"+fromPort2.getAttributeValue(XML_INFO.XML_TAG_NAME));
						makeCommomShipping(fromPort2);
					}
				}

				/*process++;
				 * 
				 */
				current++;
			}
			createXMLFile(document);
			logger.info("outbound build time:"+(System.currentTimeMillis()-startTime));


			return ScheduleBuild.SUCCESS;
		}catch(Exception e)
		{
			e.printStackTrace();
			return ScheduleBuild.FAILURE;
		}
	}
}

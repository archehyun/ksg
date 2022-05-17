package com.ksg.workbench.adv.xls;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * ���� �� Ÿ�Կ� ���� ���� ��ȯ�Ͽ� ��ȯ �ϴ� Ŭ����
 * 
 * @author ��â��
 *
 */
public class XLSStringUtil {
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
	
	private static FormulaEvaluator evaluator;

	/**
	 * @param cell
	 * @return
	 */
	public static String getVesselData(HSSFCell cell)
	{
		
		String vesselResult="";
		if(cell==null)
		{
			return "-";
		}
		switch (cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_STRING:
			
			vesselResult=cell.getRichStringCellValue().toString();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			evaluator.evaluateFormulaCell(cell);
			vesselResult=String.valueOf(evaluator.evaluateFormulaCell(cell));
			break;
			default:
				vesselResult="(error)";
		}
		vesselResult = vesselResult.replace("\n"," ").trim();
		
		return vesselResult;
	}

	public static String getVoyageData(HSSFCell cell,boolean getformual)
	{
		if(cell==null)
			return "-";

		DataFormatter df = new DataFormatter();
		evaluator=cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		String voyageResult="";
		switch (cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_NUMERIC:

		{
			// voyage ��Ÿ�� ����� 100 ������ ��쿡�� ��Ÿ�� ������ �ȵ�
			// ��� ��쿡 ���� ����... ���Ŀ� ���氡�ɼ� ����
			try
			{
				
				String fo =cell.getCellStyle().getDataFormatString();
				Format formatter = new DecimalFormat(fo.replace("\"", "\'"));
				if(fo.equals("General"))// �Ϲ�
				{
					voyageResult= String.valueOf(new Double(cell.getNumericCellValue()).intValue());
					System.out.println("voy general type Ȯ��:"+voyageResult);
				}
				else if(fo.equals("@"))//����
				{
					// ���� ���� �߻�
					//
					
					//2014.7.17 ���� ���
					//voyageResult= formatter.format(cell.getNumericCellValue());
					//2014.7.17 ���� ���
					voyageResult= formatter.format(String.valueOf(Double.valueOf(cell.getNumericCellValue())));
				}
				else
				{
					// ���� ���� �߻�
					//
					//2014.7.17 ���� ���
					voyageResult= formatter.format(cell.getNumericCellValue());
					//2014.7.17 ���� ���
					
				}



			}catch(Exception e){
				voyageResult= String.valueOf(evaluator.evaluateInCell(cell));
			}
			break;
		}

		case HSSFCell.CELL_TYPE_BOOLEAN:
			voyageResult= cell.getBooleanCellValue()+"";
			break;

		case HSSFCell.CELL_TYPE_STRING:

			StringTokenizer st = new StringTokenizer(cell.getStringCellValue(),"/");
			try{
				if(st.countTokens()==2)
				{
					voyageResult= getDateType(st);// �������� ���� /
				}else
				{
					StringTokenizer st2 = new StringTokenizer(cell.getStringCellValue(),".");
					if(st2.countTokens()==2)
					{
						voyageResult= getDateType(st2);// 
					}else
					{
						voyageResult= cell.getRichStringCellValue().toString();
					}
				}
			}catch(Exception e)
			{
				voyageResult= cell.getRichStringCellValue().toString();
			}
			break;


		case HSSFCell.CELL_TYPE_FORMULA:
			//
			Cell cells =evaluator.evaluateInCell(cell);

			try{
				if(cells.getCellType()!=HSSFCell.CELL_TYPE_ERROR)
				{
					voyageResult= String.valueOf(evaluator.evaluateInCell(cell));

				}else
				{
					voyageResult= "error";
				}

			}catch(IllegalStateException e)
			{	
				//logger.error("error cell:"+cell+","+cell.getCellType()+","+cell.getColumnIndex()+","+cell.getRowIndex());

				e.printStackTrace();
				
				voyageResult= String.valueOf(cell.getNumericCellValue());
				
			}catch(RuntimeException ee)
			{
				//logger.error(cell+","+cell.getCellType()+","+cell.getColumnIndex()+","+cell.getRowIndex());

				ee.printStackTrace();
				voyageResult= "error";
			}
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			voyageResult= Byte.toString(cell.getErrorCellValue());
			break;
		default:
			voyageResult= "-";
			break;
		

		}

		
		
		// ���๮�ڸ� �������� �����ϰ� ������ ������ ����
		
		
		voyageResult = voyageResult.replace("\n"," ").trim();	
		
		return voyageResult;
	}

	public static String getStringData(HSSFCell cell,boolean getformual) {
		if(cell==null)
			return "-";
		evaluator=cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

		DataFormatter df = new DataFormatter();
		String result="";

		switch (cell.getCellType()) 
		{

		case HSSFCell.CELL_TYPE_NUMERIC:

			double d = cell.getNumericCellValue();

			if(!getformual)
			{
				result=df.formatCellValue(cell);
			}
			if(HSSFDateUtil.isValidExcelDate(d))
			{
				result= sdf.format(cell.getDateCellValue());
			}
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			result="-";
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:


			result=cell.getBooleanCellValue()+"";
			break;

		case HSSFCell.CELL_TYPE_STRING:


			byte[] a = cell.getStringCellValue().getBytes();

			if(a.length==1)
			{
				result="-";
			}

			StringTokenizer st = new StringTokenizer(cell.getStringCellValue(),"/");
			try{
				if(st.countTokens()==2)
				{
					try{
						result = getDateType(st);//
					}catch(Exception e)
					{
						result = st.nextToken();
					}
				}else
				{
					StringTokenizer st2 = new StringTokenizer(cell.getStringCellValue(),".");
					if(st2.countTokens()==2)
					{
						try{
							result = getDateType(st2);//
						}catch(Exception e)
						{
							result = st2.nextToken();
						}

					}else
					{
						result = cell.getRichStringCellValue().toString();
					}
					break;

				}
			}catch(Exception e)
			{
				//e.printStackTrace();
				return cell.getRichStringCellValue().toString();
			}



		case HSSFCell.CELL_TYPE_FORMULA:
			try{

				if(getformual)
				{
					String value=sdf.format(cell.getDateCellValue());

					//  TODO ���Ŀ� �ѹ��� Ȯ���� �غ��� ��.
					if(value.length()<=0||value.equals(""))
					{
						result="-1";
						break;


					}else
					{
						result =value;
						break;
					}
				}

				double ds = cell.getNumericCellValue();


				if(HSSFDateUtil.isValidExcelDate(ds))
				{
					result = sdf.format(cell.getDateCellValue());
				}else
				{
					result = String.valueOf(evaluator.evaluateInCell(cell));
				}

			}catch(IllegalStateException e)
			{

				result=cell.getRichStringCellValue().toString();
				//e.printStackTrace();

			}
			break;
		default:
			result="-";

		}
		

		if(result.length()<=0||result.equals(""))
		{
			return "-+"+cell.getCellType();
		}else
		{
			// Ȯ�� �κ�
			//System.out.println("Ȯ�λ���:"+result);
			result = result.replace("\n"," ").trim();
			return result;
		}

	}
	/**
	 * @param st
	 * @return
	 * @throws NumberFormatException
	 */
	private static String getDateType(StringTokenizer st) throws NumberFormatException{
		int month = Integer.parseInt(st.nextToken().trim());

		int day = Integer.parseInt(st.nextToken().trim());

		return month+"/"+day;
	}
	/**
	 * @param cell
	 * @return
	 */
	public static String getColumString(HSSFCell cell)
	{
		if(cell==null)
			return "";

		switch (cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_STRING:

			return cell.getRichStringCellValue().toString();
		default:

			break;
		}
		return "";
	}

	/**
	 * @param cell
	 * @param b
	 * @return
	 */
	public static String getPortName(HSSFCell cell, boolean b)
	{
		// ���� �߻� ���ɼ� ����
		String result = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:

			byte[] a = cell.getStringCellValue().getBytes();

			if(a.length==1)
			{
				result="-";
			}else
			{
				result= cell.getStringCellValue();
			}

			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			result=String.valueOf(evaluator.evaluateInCell(cell));
		default:
			break;
		}

		if(result==null)
			result="-";

		
		result  = result.replace("\n"," ").trim();
		return result;
	}
	/**
	 * @param cell
	 * @return
	 */
	public String getVesselName(HSSFCell cell)
	{
		// ���� �߻� ���ɼ� ����
		String result = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:

			byte[] a = cell.getStringCellValue().getBytes();

			if(a.length==1)
			{
				result="-";
			}else
			{
				result= cell.getStringCellValue();
			}

			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			result=String.valueOf(evaluator.evaluateInCell(cell));
		default:
			break;
		}

		if(result==null)
			result="-";
		
		return result;
	}
}

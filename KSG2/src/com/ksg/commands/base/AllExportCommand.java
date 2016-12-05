package com.ksg.commands.base;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class AllExportCommand extends ExportCommand{

	private static final String ADV = "adv";
	private static final String TABLE = "table";
	private static final String PORT_ABBR = "portAbbr";
	private static final String PORT = "port";
	private static final String COMPANY = "company";
	private static final String VESSEL = "vessel";
	private static final String AREA = "area";
	ArrayList<String> tableList;
	public AllExportCommand(String fileName) {
		super(fileName);	
		tableList = new ArrayList<String>();
		wb = new HSSFWorkbook();
	}
	
	public void addWriteTable(String tableName)
	{
		this.tableList.add(tableName);
	}

	@Override
	public int execute() 
	{
		Iterator<String> iter = tableList.iterator();
		try {
			while(iter.hasNext())
			{
				String table = iter.next();
				ExportCommand command=null;
				if(table.equals(COMPANY))
				{
					command =new CompanyExportCommand(COMPANY,wb);
				}
				else if(table.equals(VESSEL))
				{
					command =new VesselInfoExportCommand(VESSEL,wb);
				}
				else if(table.equals(PORT))
				{
					command =new PortInfoExportCommand(PORT,wb);
				}
				else if(table.equals(PORT_ABBR))
				{
					command =new PortAbbrInfoExportCommand(PORT_ABBR,wb);
				}
				else if(table.equals(TABLE))
				{
					command =new TableInfoExportCommand(TABLE,wb);
				}
				else if(table.equals(ADV))
				{
					command =new ADVInfoExportCommand(ADV,wb);
				}
				else if(table.equals(AREA))
				{
					command =new ADVInfoExportCommand(ADV,wb);
				}
				command.execute();
			}

			fileWrite(wb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return 0;
	}
	public static void main(String[] args) {
		AllExportCommand allExportCommand = new AllExportCommand("all_test");
		allExportCommand.addWriteTable(AllExportCommand.VESSEL);
		allExportCommand.addWriteTable(AllExportCommand.COMPANY);
		allExportCommand.addWriteTable(AllExportCommand.PORT);
		allExportCommand.addWriteTable(AllExportCommand.PORT_ABBR);
		allExportCommand.addWriteTable(AllExportCommand.TABLE);
		allExportCommand.addWriteTable(AllExportCommand.ADV);
		
		allExportCommand.execute();
	}

}

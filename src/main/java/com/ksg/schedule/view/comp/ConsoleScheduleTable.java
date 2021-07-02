package com.ksg.schedule.view.comp;

import java.sql.SQLException;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.ksg.domain.ScheduleData;
import com.ksg.view.comp.table.KSGTableCellRenderer;

public class ConsoleScheduleTable extends KSGScheduleTable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ConsoleScheduleTable() {
		super("console");
		initTable(colums);
	}
	
	
	private String colums[] = {"","I/O","테이블 ID","선사명","에이전트","선박명","출력날짜","Voyage번호","출발항","DateF","DateT","도착항","구분","TS Port","지역코드","Console CFS","Console Page","C Time","D Time"};
	protected TableColumnModel updateTableRender() {
		
		TableColumnModel fvcolmodel =getColumnModel();
		for(int i=0;i<fvcolmodel.getColumnCount();i++)
		{
			TableColumn namecol =fvcolmodel.getColumn(i);
			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();

			switch (i) {
			case 0:
				renderer.setHorizontalAlignment(SwingConstants.CENTER);

				namecol.setCellRenderer(renderer);
				namecol.setMaxWidth(20);
				namecol.setMinWidth(20);
			case 1: // I/O
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setPreferredWidth(45);
				break;
			case 2: // 테이블 아이디
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);

				break;				
			case 3: // 선사명
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setPreferredWidth(125);
				break;
			case 4: // 에이전트
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setPreferredWidth(125);
				break;

			case 5: // 선박명
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(120);
			case 6: // 출력날짜
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);	

			case 7: // Voyage 번호
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);				
				break;


			case 8: // 	출발항
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(120);
				break;
			case 9: // 출발일
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(75);
				namecol.setMaxWidth(75);
				break;				

			case 10: // 도착일
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(75);
				namecol.setMaxWidth(75);

			case 11: // 도착항
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(120);

				break;
			case 12: // 구분
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(60);
				break;
			case 13: // TS 포트
				renderer.setHorizontalAlignment(SwingConstants.CENTER);

				namecol.setCellRenderer(renderer);
				namecol.setMinWidth(150);
				break;				
			case 14:
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);
				break;	
			case 17: // TS 포트
				renderer.setHorizontalAlignment(SwingConstants.CENTER);

				namecol.setCellRenderer(renderer);

				break;		

			default:
				break;
			}
			namecol.setHeaderRenderer(new IconHeaderRenderer());

		}
		return fvcolmodel;
	}

	public int updateTable(ScheduleData op) throws SQLException {
		
		
		//op.setOrderby("gubun");
		List li = scheduleService.getScheduleList(op);
		
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		
		for(int i=0;i<colums.length;i++)
		{
			defaultTableModel.addColumn(colums[i]);
		}

		int j=0;

		for(int i=0;i<li.size();i++)
		{
			ScheduleData data = (ScheduleData) li.get(i);
			defaultTableModel.addRow(new Object[]{"",
					data.getInOutType(),
					data.getTable_id(),													
					data.getCompany_abbr(),
					data.getAgent(),
					data.getVessel(),
					data.getDate_issue(),
					data.getVoyage_num(),

					data.getFromPort(),
					data.getDateF(),
					data.getDateT(),
					data.getPort(),
					data.getGubun(),
					data.getPort(),

					data.getArea_code(),
					
					data.getConsole_cfs(),
					data.getConsole_page(),
					data.getC_time(),
					data.getD_time()
			});

		}
		
		RowSorter<TableModel> sorter
		= new TableRowSorter<TableModel>(defaultTableModel);
		
		setModel(defaultTableModel);
		updateTableRender();
		return li.size();
		
	}

}

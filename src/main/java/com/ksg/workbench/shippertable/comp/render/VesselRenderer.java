package com.ksg.workbench.shippertable.comp.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.admin.KSGViewParameter;
import com.ksg.workbench.shippertable.comp.AdvertiseTable;

@SuppressWarnings("serial")
public class VesselRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private boolean is=true;

	private Font defaultFont;

//	private VesselServiceV2 vesselService;
	
	AdvertiseTable advertiseTable;

	public VesselRenderer(AdvertiseTable advertiseTable) {
		
		defaultFont = new Font(this.getFont().getName(),this.getFont().getStyle(), KSGViewParameter.TABLE_CELL_SIZE);
		
//		vesselService = new VesselServiceImpl();
		
		this.advertiseTable = advertiseTable;
	}

	public boolean isCellEditable(int row, int column)
	{
		return (column ==0);
	}
	public void setHorizontalAlignment(int i)
	{
		super.setHorizontalAlignment(i);
	}
	public void setVisible(boolean is)
	{
		super.setVisible(is);
		this.is=is;
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		Component renderer=super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		((JLabel) renderer).setOpaque(true);

		Color foreground, background;

		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				setForeground(Color.BLUE);
				setBackground(header.getBackground());
				setFont(header.getFont());

			}
		}

		if(isSelected)
		{
			foreground = Color.white;
			background=new Color(51,153,255);				
		}
		else
		{
			foreground = Color.black;
			background=Color.WHITE;	
		}

			// 오류 발생시 글자색 변경

			if(value!=null&&String.valueOf(value).isEmpty())
			{	
				String vessel_abbr =String.valueOf(table.getValueAt(row,0));

				//TODO SQL 제거
				
				
				//선박약어 기준
				
				if(!vessel_abbr.isEmpty())
				{
				Vessel result = advertiseTable.selectVesselDetail(vessel_abbr);
				if(result==null) foreground = Color.RED;
				}
			}

			renderer.setBackground(background);
			renderer.setForeground(foreground);

		
		this.setFont(defaultFont);

		return renderer;
	}
}
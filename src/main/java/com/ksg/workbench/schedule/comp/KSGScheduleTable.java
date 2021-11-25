package com.ksg.workbench.schedule.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ScheduleData;
import com.ksg.service.ScheduleService;
import com.ksg.service.ScheduleServiceImpl;

public abstract class KSGScheduleTable extends JTable{

	
	protected DefaultTableModel defaultTableModel;
	
	protected ScheduleService scheduleService;
	
	protected static final int TALBE_ROW_HEIGHT = 40;
	
	public class IconHeaderRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(new Color(61,86,113));

					setBackground(new Color(214,226,242));
					
					Font f = header.getFont();
					
					Font font = new Font(f.getFontName(),Font.BOLD,f.getSize());
					
					setFont(font);
				}
			}

			setText((value == null) ? "" : value.toString());

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			setHorizontalAlignment(JLabel.CENTER);
			this.setPreferredSize(new Dimension(this.getSize().width,30));


			return this;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public KSGScheduleTable(String name) {
		logger.info(name+"테이블 생성");
		scheduleService = new ScheduleServiceImpl();
	}
	protected JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem itemSearchTable = new JMenuItem("테이블검색");
		itemSearchTable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				int row=getSelectedRow();

				changeSelection(row-1, 0, false, false);
				//showAdvView(row);

			}});
		popupMenu.add(itemSearchTable);

		JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem("스크롤 표시",true);
		boxMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				JCheckBoxMenuItem m =(JCheckBoxMenuItem) arg0.getSource();
				if(m.isSelected())
				{
					setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}else
				{
					setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				}
			}
		});
		popupMenu.add(boxMenuItem);

		return popupMenu;
	}
	/**
	 * @설명 테이블 초기화
	 */
	protected void initTable(String[] colums)
	{	
		setComponentPopupMenu(createPopupMenu());
		setFont(KSGModelManager.getInstance().defaultFont);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowHeight(TALBE_ROW_HEIGHT);
		addKeyListener(new KeyAdapter(){

			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyChar() ==KeyEvent.VK_ENTER)
				{
					int row=getSelectedRow();
					changeSelection(row-1, 0, false, false);
				}
			}});
		
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		for(int i=0;i<colums.length;i++)
		{
			defaultTableModel.addColumn(colums[i]);
		}		
		setColumnModel(updateTableRender());
		setModel(defaultTableModel);
	}

	protected abstract TableColumnModel updateTableRender();
	public abstract int updateTable(ScheduleData op) throws SQLException;
	
}

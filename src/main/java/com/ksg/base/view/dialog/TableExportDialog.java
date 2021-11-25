package com.ksg.base.view.dialog;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.ksg.domain.ShippersTable;
import com.ksg.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;

public class TableExportDialog extends JDialog{
	private TableService tableService = new TableServiceImpl();
	
	private ArrayList<ShippersTable> tableList;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TableExportDialog() {

	}
	public void createAndUpdateUI()
	{
		JPanel pnMain = new JPanel(new BorderLayout());
		JTable tblList = new JTable();
		pnMain.add(new JScrollPane(tblList));
		getContentPane().add(pnMain);
		
		pack();
		setVisible(true);	
	}

}

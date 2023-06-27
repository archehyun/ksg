package com.ksg.workbench.base.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import com.ksg.commands.LongTask;
import com.ksg.common.dao.DAOImplManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.dialog.KSGDialog;

public class ExportDialog extends KSGDialog{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int ONE_SECOND = 100;

	private JProgressBar bar;

	private JLabel lblMessage,lblCurrentMessage;

	private JButton butClose;

	private ArrayList<String> errorList;

	protected BaseDAOManager baseService;

	private JTable tblErrorList;

	private LongTask task;

	
	private SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");

	private String message = "엑셀 파일에서 가져오는 중...";

	DefaultTableModel defaultTableModel = new DefaultTableModel();
	File xlsfile;

	private Timer timer;
	public ExportDialog(File selectedFile) {

		super();
		
		xlsfile = selectedFile;
		
		baseService = DAOImplManager.getInstance().createBaseDAOImpl();

	}


	public void createAndUpdateUI() {

		JPanel pnMain = new JPanel();

		pnMain.setLayout(new BorderLayout());

		this.setTitle("선박 정보 가져오기");

		JPanel pnProcess = new JPanel();

		tblErrorList = new JTable();

		DefaultTableModel defaultTableModel = new DefaultTableModel();

		defaultTableModel.addColumn("");

		tblErrorList.setModel(defaultTableModel);

		bar = new JProgressBar();

		lblCurrentMessage = new JLabel(message);

		pnProcess.add(lblCurrentMessage);

		pnProcess.add(bar);

		JPanel pnMessage = new JPanel(new FlowLayout(FlowLayout.LEFT));

		lblMessage = new JLabel();

		JPanel pnSouth=new JPanel(new FlowLayout(FlowLayout.RIGHT));

		butClose = new JButton("완료");

		butClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setVisible(false);

				dispose();

			}
		});

		pnSouth.add(butClose);

		butClose.setEnabled(false);

		pnMessage.add(lblMessage);		

		pnMain.add(pnProcess,BorderLayout.NORTH);

		pnMain.add(new JScrollPane(tblErrorList));

		pnMain.add(pnSouth,BorderLayout.SOUTH);

		pnMain.setOpaque(true); 

		this.setContentPane(pnMain);

		this.setSize(400, 150);

		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);

		this.setVisible(true);
		
		
		timer = new Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				bar.setMaximum(task.getLengthOfTask());
				
				bar.setValue(task.getCurrent());
				
				lblMessage.setText(task.getMessage());
				
				lblCurrentMessage.setText(task.getMessage());
				if (task.isDone()) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					setCursor(null); //turn off the wait cursor
					bar.setValue(bar.getMinimum());
				}
			}
		});
		
	}

	public void setMessage(String message) {

		this.message=message;

	}

	public void addErrorMessage(String errro) {

		errorList.add(errro);

		defaultTableModel = new DefaultTableModel();

		defaultTableModel.addColumn("설명");		

		for(int i=0;i<errorList.size();i++)
		{
			defaultTableModel.addRow(new Object[]{errorList.get(i)});
		}

		tblErrorList.setModel(defaultTableModel);

	}

	
	/**선박 사용 항목  가져오기
	 * @param vesselUseCell
	 * @return
	 */
	private int getVesselUse(Cell vesselUseCell)
	{
		int vesselUse;
		try{
			switch (vesselUseCell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				vesselUse= Integer.valueOf(vesselUseCell.getStringCellValue());

				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				vesselUse =(int) vesselUseCell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				vesselUse =Vessel.USE;
			default:
				vesselUse =Vessel.USE;
				break;
			}
		}catch(Exception e)
		{
			return Vessel.USE;
		}

		return vesselUse;
	}
	public void end()
	{
		butClose.setEnabled(true);
	}
	public void setTask(LongTask task) {
		this.task = task;
		bar.setMaximum(task.getLengthOfTask());
		bar.setValue(0);
		bar.setStringPainted(true);
		
		timer.start();
		
	}

}

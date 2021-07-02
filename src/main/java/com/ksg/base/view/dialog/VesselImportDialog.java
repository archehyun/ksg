package com.ksg.base.view.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.LongTask;
import com.ksg.commands.base.VesselInfoImportCommand;
import com.ksg.common.dao.DAOImplManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * @author 박창현
 *
 */
public class VesselImportDialog extends KSGDialog{

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
	public VesselImportDialog(File selectedFile) {

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
		
		
		
		
		
		Thread thread = new Thread(){
			public void run()
			{
				importAction();		
			}
		};
		thread.start();
		
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
	
	private void importAction()
	{
		try {
			VesselInfoImportCommand command = new VesselInfoImportCommand(xlsfile);			
			this.setTask(command);
			int result=command.execute();
			if(result== KSGCommand.RESULT_SUCCESS)
			{
				end();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void importAction2()
	{
		Vessel insertParameter=null;
		try{
			POIFSFileSystem fs= new POIFSFileSystem(new FileInputStream(xlsfile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			Sheet sheet = wb.getSheetAt(0);
			
			
			lblCurrentMessage.setText(sheet.getLastRowNum()+"개 선박정보 가져오는중");
			bar.setMaximum(sheet.getLastRowNum());
			bar.setValue(1);
			bar.setStringPainted(true);

			
			
			for(int i=1;i<=sheet.getLastRowNum();i++)
			{
				HSSFRow row =(HSSFRow) sheet.getRow(i);
				Cell cell0 =row.getCell(0, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_name
				Cell cell1 =row.getCell(1, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_abbr
				Cell cell2 =row.getCell(2, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_type
				Cell cell3 =row.getCell(3, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_use
				Cell cell4 =row.getCell(4, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_company
				Cell cell5 =row.getCell(5, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_mmsi
				Cell cell6 =row.getCell(6, HSSFRow.RETURN_BLANK_AS_NULL);//input_date
				insertParameter = new Vessel();

				insertParameter.setVessel_name(cell0.getStringCellValue());
				insertParameter.setVessel_abbr(cell1.getStringCellValue());
				insertParameter.setVessel_type(cell2.getStringCellValue());
				insertParameter.setVessel_use(this.getVesselUse(cell3));					
				insertParameter.setVessel_company(cell4.getStringCellValue());
				insertParameter.setVessel_mmsi(cell5.getStringCellValue());
				insertParameter.setInput_date(cell6.getStringCellValue().equals("")?null:format.parse(cell6.getStringCellValue()));

				logger.info("xls insert:"+insertParameter.toInfoString());
				baseService.insertVessel(insertParameter);
				bar.setValue(i);
			}
			end();
		}
		catch (SQLException e1) 
		{
			e1.printStackTrace();

			// 동일한 항목이 있을 경우
			if(e1.getErrorCode()==2627)
			{
				try 
				{
					baseService.update(insertParameter);
				} catch (SQLException e2) 
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			else
			{
				logger.error(e1.getErrorCode()+":"+e1.getMessage()+" : "+insertParameter.toInfoString());

				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


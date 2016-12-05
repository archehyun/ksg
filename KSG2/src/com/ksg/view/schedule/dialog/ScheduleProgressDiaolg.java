package com.ksg.view.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;

import com.ksg.commands.LongTask;
import com.ksg.model.KSGModelManager;

public class ScheduleProgressDiaolg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int ONE_SECOND = 100;
	private LongTask task;
	private JTable tblErrorList;
	private JProgressBar progressBar;
	private Timer timer;
	public ScheduleProgressDiaolg() {
		super(KSGModelManager.getInstance().frame);
	}
	public ScheduleProgressDiaolg(LongTask task) {
		this();
		this.task = task;
		
	}
	public void createAndUpdateUI()
	{
		progressBar = new JProgressBar(0,100);
		tblErrorList = new JTable();
		this.setTitle("스케줄 생성");
		JPanel pnNorth = new JPanel(new BorderLayout());
		pnNorth.add(progressBar);
		pnNorth.add(new JLabel("진행중.."),BorderLayout.WEST);
		
		
		timer = new Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				progressBar.setValue(task.getCurrent());
				//lblMessage.setText(task.getMessage());
				if (task.isDone()) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					setCursor(null); //turn off the wait cursor
					progressBar.setValue(progressBar.getMinimum());
				}
			}
		});
		timer.start();
		
		this.getContentPane().add(pnNorth,BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(tblErrorList));
		this.pack();
		this.setVisible(true);
	}
	public static void main(String[] args) {
		ScheduleProgressDiaolg createOptionDialog = new ScheduleProgressDiaolg();
		createOptionDialog.createAndUpdateUI();
		
		
	}

}

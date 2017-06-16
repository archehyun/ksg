package com.ksg.view.adv.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.ksg.model.KSGModelManager;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class ProcessDialog extends KSGDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	public ProcessDialog() {



	}
	public void createAndUpdateUI() {

		KSGModelManager.getInstance().processBar = ProcessDialog.this;
		
		progressBar = new JProgressBar();
		
		JPanel pnMain = new JPanel(new BorderLayout());
		
		progressBar.setStringPainted(true);
		
		Border border = BorderFactory.createTitledBorder("Reading...");
		
		progressBar.setBorder(border);
		
		pnMain.add(progressBar,BorderLayout.CENTER);
		
		getContentPane().add(pnMain, BorderLayout.CENTER);
		
		setSize(400, 100);
		
		ViewUtil.center(ProcessDialog.this);
		
		KSGModelManager.getInstance().isWorkMoniter=true;
		
		progressBar.setIndeterminate(true);
		
		this.setTitle("�������� ����");
		Timer timer  = new Timer(1000, this);
		timer.start();
		this.setVisible(true);	
		logger.info("process start");

	}

	public static void main(String[] args) {
		ProcessDialog dialog = new ProcessDialog();
		dialog.createAndUpdateUI();

	}
	public void setValues(int i)
	{
		progressBar.setValue(i);
		progressBar.updateUI();
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		SwingUtilities.invokeLater(new Update());


	}
	class Update implements Runnable {
		public void run() {
			
			progressBar.setString(	KSGModelManager.getInstance().workProcessText);
			
			ProcessDialog.this.repaint();
		
		}
	}
	public void setMAX(int tableCount) {
		this.progressBar.setMaximum(tableCount);
		
	}


}


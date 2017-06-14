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
		Timer timer  = new Timer(1, this);
		timer.start();
		this.setVisible(true);
		logger.debug("process start");


	}
	public void createAndUpdateUI() {


	}

	public static void main(String[] args) {
		ProcessDialog dialog = new ProcessDialog();
		dialog.createAndUpdateUI();

	}
	public void setValues(int i)
	{
		progressBar.setValue(i);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		SwingUtilities.invokeLater(new Update());


	}
	class Update implements Runnable {
		public void run() {
			
			logger.debug("update progross");

			/*pbar.setProgress(counter);
	      pbar.setNote("Operation is " + counter + "% complete");
	      counter += 2;*/
		}
	}


}

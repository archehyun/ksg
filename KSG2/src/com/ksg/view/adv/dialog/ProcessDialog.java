package com.ksg.view.adv.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import com.ksg.model.KSGModelManager;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class ProcessDialog extends KSGDialog{

	private JProgressBar progressBar;
	public ProcessDialog() {
		KSGModelManager.getInstance().processBar = this;
		progressBar = new JProgressBar();
		JPanel pnMain = new JPanel(new BorderLayout());
	    progressBar.setStringPainted(true);
	    Border border = BorderFactory.createTitledBorder("Reading...");
	    progressBar.setBorder(border);
	    pnMain.add(progressBar,BorderLayout.CENTER);
	    this.getContentPane().add(pnMain, BorderLayout.CENTER);
	    this.setSize(400, 100);
	    ViewUtil.center(this);
	    KSGModelManager.getInstance().isWorkMoniter=true;
	}
	public void createAndUpdateUI() {

	    Thread t = new Thread(new Runnable() {
	      public void run() {
	    	  ProcessDialog.this.setVisible(true);
	    	  
	      }
	    });
	    t.start();

	}
	
	public static void main(String[] args) {
		ProcessDialog dialog = new ProcessDialog();
		dialog.createAndUpdateUI();
		
	}
	public void setValues(int i)
	{
		progressBar.setValue(i);
	}



}

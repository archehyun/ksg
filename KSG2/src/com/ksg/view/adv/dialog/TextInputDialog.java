package com.ksg.view.adv.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class TextInputDialog extends KSGDialog {

	private JTextArea area;
	public TextInputDialog() {
		
	}
	public void createAndUpdateUI() {
		setTitle("텍스트 입력");
		area = new JTextArea();
		JPanel pnControl = new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();

			}
		});
		pnControl.add(butOK);
		pnControl.add(butCancel);
		setModal(true);

		this.getContentPane().add(new JScrollPane(area));
		this.getContentPane().add(pnControl,BorderLayout.SOUTH);

		setSize(500,400);
		ViewUtil.center(this, false);
		this.setVisible(true);

	}

}

package com.ksg.workbench.admin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.view.comp.textfield.LookAheadTextField;
import com.ksg.view.comp.textfield.StringArrayLookAhead;

public class ToolBar extends JToolBar implements ActionListener
{
	private static final String MONDAY = "월요일";
	
	private LookAheadTextField txfImportDate;
	
	KSGMainFrame mainFrame;
	
	ArrayList<Component> butList = new ArrayList<>(); 
	
	public ToolBar(KSGMainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		setPreferredSize(new Dimension(-1,45));
		setBorderPainted(true);
	}
	
	public void setButtonActionEvent(ActionListener listener)
	{
		
	}
	
	public void addKeyListener(KeyListener listener)
	{
		txfImportDate.addKeyListener(listener);
	}
	
	public void initComponent()
	{	
		txfImportDate = new LookAheadTextField("광고입력수 조회",10,new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date()))));
		txfImportDate.setMaximumSize(new Dimension(100,25));
		
		txfImportDate.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {

				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String date=txfImportDate.getText();

					if(!KSGDateUtil.isDashFomatt(date))
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1)이 틀렸습니다. "+date);

						requestFocus();

						return;
					}

					try {
						String searchDate = KSGDateUtil.toDate3(date).toString();
						
						CommandMap param = new CommandMap();
						
						param.put("searchDate", searchDate);
						
						mainFrame.callApi("searchTableCount", param);
					} 
					catch (DateFormattException e2) {
						JOptionPane.showMessageDialog(null, "error:"+e2.getMessage());
						e2.printStackTrace();
					}
				}
			}
		});

		

		JCheckBox cbxImportDate = new JCheckBox(MONDAY,false);
		cbxImportDate.setFont(KSGModelManager.getInstance().defaultFont);
		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				JCheckBox bo =(JCheckBox) e.getSource();				
				if(bo.isSelected()) txfImportDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
			}
		});
		
		butList.forEach(comp -> add(comp));
		
		addSeparator();	
		
		add(txfImportDate);
	}
	
	public void createToolBarButton(String butName, String img, String action)
	{
		JButton butSearch = new JButton(butName,new ImageIcon(img));

		butSearch.setPreferredSize(new Dimension(20,20));
		
		butSearch.setActionCommand(action);

		butSearch.setBorderPainted(false);

		butSearch.setVerticalAlignment(0);
		
		butSearch.addActionListener(this);
		
		butList.add(butSearch);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.showPanel(e.getActionCommand());
		
	}

}

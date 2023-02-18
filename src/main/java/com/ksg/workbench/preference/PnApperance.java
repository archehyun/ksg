package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.workbench.common.comp.panel.KSGPanel;

/**

  * @FileName : PnApperance.java

  * @Project : KSG2

  * @Date : 2022. 3. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PnApperance extends PnOption {	
	
	KSGViewUtil propeties = KSGViewUtil.getInstance();
	private int HEADER_HEIGHT;
	private int ROW_HEIGHT;
	private Color GRID_COLOR;
	private int FONT_SIZE;
	
	public PnApperance(PreferenceDialog preferenceDialog) {
		super(preferenceDialog);
		
		this.setLayout(new BorderLayout());
		this.setName("Apperance");
		
		this.add(buildCenter());
		
	}
	

	private Component buildCenter() {
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		JTree tree = new JTree();
		
        HEADER_HEIGHT=Integer.parseInt(propeties.getProperty("table.header.height"));

		ROW_HEIGHT=Integer.parseInt(propeties.getProperty("table.row.height"));
		
		GRID_COLOR = getColor(propeties.getProperty("table.girdcolor"));
		
		FONT_SIZE = Integer.parseInt(propeties.getProperty("table.font.size"));
		
		KSGPanel pnLight = new KSGPanel();
		pnLight.add(new JButton("EDIT"));
		
		pnMain.add(new JScrollPane(tree));
		pnMain.add(pnLight,BorderLayout.EAST);
		
		return pnMain;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveAction() {
		// TODO Auto-generated method stub
		
	}

	
	private Color getColor(String param)
	{
		String index[] = param.split(",");
		return new Color(Integer.parseInt(index[0].trim()),Integer.parseInt(index[1].trim()), Integer.parseInt(index[2].trim()));
	}

}

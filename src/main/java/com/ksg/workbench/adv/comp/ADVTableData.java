package com.ksg.workbench.adv.comp;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class ADVTableData extends JTextField{

	public ADVTableData(String data) {
		this.setText(data);
		this.setFont(KSGModelManager.getInstance().defaultFont);
		this.setBorder(BorderFactory.createEmptyBorder());
	}
	
	// is Vessel
	public boolean isVessel;
	
	public ADVTableData(String data,boolean isVessel) {
		this(data);
		this.isVessel=isVessel;
	}
	public String toString()
	{
		return this.getText();
	}
}

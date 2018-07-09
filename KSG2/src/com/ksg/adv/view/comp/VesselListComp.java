package com.ksg.adv.view.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.ksg.adv.view.dialog.SearchVesselDialog;
import com.ksg.common.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Vessel;

public class VesselListComp extends JList{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	class ComplexCellRenderer implements ListCellRenderer
	{
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Font theFont = null;
			Color theForeground = null;
			Icon theIcon = null;
			String theText = null;

			JLabel renderer = (JLabel) defaultRenderer
			.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (value instanceof VesselInfo) {
				VesselInfo values = (VesselInfo) value;
				theForeground = list.getForeground();
				theIcon = values.getIcon();
				theText = values.vesselName;

				if(!values.isExist())
				{
					theForeground =Color.red;
				}
				if(values.isMulti())
				{
					theForeground =Color.blue;
				}
			} else {
				theFont = list.getFont();
				theForeground = list.getForeground();
				theText = "";
			}

			if (!isSelected) {
				renderer.setForeground(theForeground);
			}
			if (theIcon != null) {
				renderer.setIcon(theIcon);
			}
			renderer.setText(theText);
			renderer.setFont(theFont);
			return renderer;
		}
	}


	private BaseService baseService;
	//	private JTextArea txaADV;
	KSGXLSImportPanel base;
	protected Logger logger = Logger.getLogger(getClass());
	private int vesselSize;
	public VesselListComp(KSGXLSImportPanel base) {

		this.base = base;
		//		this.txaADV=txaADV;
		this.setFixedCellHeight(15);
		this.setCellRenderer(new ComplexCellRenderer());	
		this.setComponentPopupMenu(createVesselPopup());
		DAOManager manager = DAOManager.getInstance();
		baseService = manager.createBaseService();
	}
	private JPopupMenu createVesselPopup() 
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("°Ë»ö");
		menuItem.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) 
			{
				final int index =VesselListComp.this.getSelectedIndex();
				if(index==-1)
					return;

				VesselInfo info=(VesselInfo) VesselListComp.this.getSelectedValue();

				SearchVesselDialog dialog = new SearchVesselDialog(info.vesselName);
				dialog.createAndUpdateUI();
				if(dialog.OPTION==SearchVesselDialog.OK_OPTION)
				{
					DefaultListModel model=(DefaultListModel) VesselListComp.this.getModel();
					model.setElementAt(dialog.info, index);

					base.updateVesseFulllName(index,dialog.info.vesselName);
				}
			}});



		menu.add(menuItem);
		return menu;
	}
	Element vesselElement;
	public void setModel(Element vesselElement) {
		DefaultListModel vesselListModel = new DefaultListModel();
		this.vesselElement=vesselElement;

		List vessel_list=vesselElement.getChildren("vessel");
		this.vesselSize=vessel_list.size();
		try {
			for(int i=0;i< vessel_list.size();i++)
			{

				Element port_info = (Element) vessel_list.get(i);
				String vesselName=port_info.getAttributeValue("name");

				Vessel op = new Vessel();
				op.setVessel_abbr(vesselName);
				List v =baseService.getVesselList(op);

				VesselInfo info = new VesselInfo();
				if(v.size()==0)
				{

					info.setExist(false);
					info.setMulti(false);
					info.vesselName=vesselName;	


				}else if(v.size()==1)
				{
					Vessel  item = (Vessel) v.get(0);
					info.setMulti(false);
					info.setExist(true);
					info.vesselName=item.getVessel_name();
					base.updateVesseFulllName(i, item.getVessel_name());
				}
				else if(v.size()>1)
				{
					Vessel  item = (Vessel) v.get(0);
					info.setMulti(true);
					info.setExist(true);
					info.vesselName=item.getVessel_name();
					base.updateVesseFulllName(i, item.getVessel_name());
				}
				vesselListModel.addElement(info);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("error");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setModel(vesselListModel);
	}
	public Vector<VesselInfo> getNullVesselList() {
		Vector<VesselInfo> list = new Vector<VesselInfo>();
		int size=this.getModel().getSize();
		try {
			for(int i=0;i<size;i++)
			{
				VesselInfo litinfo=(VesselInfo) this.getModel().getElementAt(i);
				String vesselName =litinfo.vesselName;
				Vessel op = new Vessel();
				op.setVessel_name(vesselName);
				Vessel v1 =baseService.getVesselAbbrInfo(vesselName);
				VesselInfo info = new VesselInfo();

				if(v1==null)
				{
					logger.error("null vessel:"+vesselName);
					info.setExist(false);
					info.vesselName=vesselName;			
					list.add(info);

				}			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*List<Element> vessel_list=vesselElement.getChildren("vessel");

			for(int i=0;i< vessel_list.size();i++)
			{

				Element port_info =  vessel_list.get(i);
				String vesselName =port_info.getAttributeValue("name");

				Vessel v =baseService.getVesselInfo(vesselName);
				VesselInfo info = new VesselInfo();
				if(v==null)
				{

					Vessel v1 =baseService.getVesselAbbrInfo(vesselName);

					if(v1==null)
					{
						info.setExist(false);
						info.vesselName=port_info.getAttributeValue("name");				
						list.add(info);
					}
				}			
			}*/

		return list;
	}
	/*public Vector<VesselInfo> getNullVesselList2() {
		Vector<VesselInfo> list = new Vector<VesselInfo>();
		List<Element> vessel_list=vesselElement.getChildren("vessel");
		try {
			for(int i=0;i< vessel_list.size();i++)
			{

				Element port_info =  vessel_list.get(i);
				String vesselName =port_info.getAttributeValue("name");
				Vessel op = new Vessel();
				op.setVessel_name(vesselName);
				Vessel v =baseService.getVesselInfo(op);
				VesselInfo info = new VesselInfo();
				if(v==null)
				{
					Vessel v1 =baseService.getVesselAbbrInfo(vesselName);

					if(v1==null)
					{
						info.setExist(false);
						info.vesselName=port_info.getAttributeValue("name");				
						list.add(info);
					}
				}			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}*/
	public int getVesselSize() {
		return vesselSize;
	}


}

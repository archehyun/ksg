package com.ksg.workbench.adv.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import org.jdom.Element;

import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.adv.KSGXLSImportPanel;
import com.ksg.workbench.adv.dialog.SearchVesselDialog;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ���� ��� ǥ�� ����Ʈ
 * @author ��â��
 *
 */
@Slf4j
public class VesselListComp extends JList{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**

	 * @FileName : VesselListComp.java

	 * @Project : KSG2

	 * @Date : 2022. 5. 30. 

	 * @�ۼ��� : pch

	 * @�����̷� :

	 * @���α׷� ���� : ���ڸ� �� ǥ��

	 */

	public final Color VESSEL_MULTI = Color.blue;
	public final Color VESSEL_NOT_EXIT = Color.red;
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
					theForeground =VESSEL_NOT_EXIT;
				}
				if(values.isMulti())
				{
					theForeground =VESSEL_MULTI;
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

	private KSGXLSImportPanel base;



	private int vesselSize;

	private VesselServiceV2 vesselService;
	
	public VesselListComp(KSGXLSImportPanel base) {

		this.base = base;
		//		this.txaADV=txaADV;
		this.setFixedCellHeight(20);
		this.setCellRenderer(new ComplexCellRenderer());
		this.setComponentPopupMenu(createVesselPopup());
		this.setToolTipText("Red : ���� ����, Blue : ���� ��� �ټ�����");
		//baseService = manager.createBaseService();

		vesselService = new VesselServiceImpl();
	}
	private JPopupMenu createVesselPopup() 
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("�˻�");
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

		log.info("vessel list init start");

		DefaultListModel vesselListModel = new DefaultListModel();

		this.vesselElement=vesselElement;

		List vessel_list=vesselElement.getChildren("vessel");

		this.vesselSize=vessel_list.size();

		try {

			// ���ڸ�, ���ڸ� ��� ����

			// ���ڸ� ������� �� ��� ���ڸ����� ��ü


			for(int i=0;i< vessel_list.size();i++)
			{

				Element port_info = (Element) vessel_list.get(i);

				String vesselName=port_info.getAttributeValue("name");

				HashMap<String, Object> param = new HashMap<String, Object>();

				param.put("vessel_name", vesselName);

				Vessel itemDetail=vesselService.selectDetail(vesselName);

				VesselInfo info = new VesselInfo();
				//���ڸ��� ���� ���� ���� ���
				// ����� ���
				// �� ���� ���

				// �� ������ �������� �ʴ� ����
				if(itemDetail== null)
				{
					info.setExist(false);
					info.setMulti(false);
					info.vesselName=vesselName;
				}
				else
				{
					//���ڸ��� �ִ��� ��ȸ
					Vessel itemHead=vesselService.select(vesselName);
					// ���ڸ��� ������ ���ڸ� ���
					if(itemHead==null)
					{
						info.vesselName=itemDetail.getVessel_name();
						
					}
					// ������ ��������� ���ڸ� ���
					else
					{
						info.vesselName=itemHead.getVessel_name();
					}

					param.put("vessel_name", info.vesselName);
					HashMap<String, Object> result = vesselService.selectDetailList(param);

					List detailList=(List) result.get("master");

					if(detailList.size()>1)
					{
						info.setMulti(true);
					}

					base.updateVesseFulllName(i, info.vesselName);

				}			


				vesselListModel.addElement(info);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());	

		} 
		this.setModel(vesselListModel);

		log.info("vessel list init end");
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
				Vessel v1 =vesselService.selectDetail(vesselName);
				VesselInfo info = new VesselInfo();

				if(v1==null)
				{
					log.error("null vessel:"+vesselName);
					info.setExist(false);
					info.vesselName=vesselName;
					list.add(info);

				}			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return list;
	}

	public int getVesselSize() {
		return vesselSize;
	}


}

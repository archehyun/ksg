package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.KeyWordInfo;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.panel.KSGPanel;

public class PnKeyWord extends PnOption {
	private JList listKeyword;
	
	String selectedKeyword="vessel";
	
	private Font defaultfont;
	
	BaseService baseService;
	
	private KSGPropertis propertis = KSGPropertis.getIntance();
	
	private JComboBox cbxKeyword;
	public PnKeyWord(PreferenceDialog preferenceDialog) {
		super(preferenceDialog);
		
		this.setName("Keyword형식");
		
		baseService = new BaseServiceImpl();
		
		
		this.setLayout(new BorderLayout());
		this.add(buildCenter(),BorderLayout.CENTER);
	}
	
	private KSGPanel buildCenter()
	{
		
		listKeyword = new JList();
		
		KSGPanel pnKeyWordTypeOption = new KSGPanel();
		pnKeyWordTypeOption.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnKeyWordTypeOption.add(new JLabel("키워드 타입: "));
		
		cbxKeyword = new JComboBox();
		cbxKeyword.setPreferredSize(new Dimension(200,25));
		cbxKeyword.addItem("Vessel");
		cbxKeyword.addItem("Voyage");
		cbxKeyword.addItem("Vessel&Voyage");
		cbxKeyword.addActionListener(this);
		cbxKeyword.setSelectedIndex(0);
	
		pnKeyWordTypeOption.add(cbxKeyword);
		JButton butOption = new JButton("Vessel&Voyage옵션");
		butOption.setActionCommand("옵션");
		butOption.addActionListener(new KeywordOptionDialog());
		pnKeyWordTypeOption.add(butOption);


		JButton butADD = new JButton("Key Word  추가");
		butADD.addActionListener(this);
		JButton butDel = new JButton("Key Word  삭제");
		butDel.addActionListener(this);


		butADD.setFont(defaultfont);
		butDel.setFont(defaultfont);

		
		Box pnBox =Box.createVerticalBox();
		
		pnBox.add(pnKeyWordTypeOption);
		
		KSGPanel pnKeyList = new KSGPanel();
		pnKeyList.setLayout(new BorderLayout());
		pnKeyList.add(new JScrollPane(listKeyword));
		Box pnKeyControl = Box.createVerticalBox();
		
		pnKeyControl.add(butADD);		
		pnKeyControl.add(Box.createGlue());
		pnKeyControl.add(butDel);
		pnKeyControl.add(Box.createGlue());
		
		KSGPanel pn1 = new KSGPanel();
		pn1.add(pnKeyControl);
		
		pnKeyList.add(pn1,BorderLayout.EAST);

		pnBox.add(pnKeyList);
		
		KSGPanel pnMain=new KSGPanel(new BorderLayout());
		
		pnMain.add(pnBox);
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,15, 5,5));
		return pnMain;
		
	}
	
	private final class KeywordOptionDialog implements ActionListener {
		private JComboBox cbxCount;
		private JComboBox cbxDivider;
	

		public void actionPerformed(ActionEvent arg0) {
			
			final JDialog dialog =  new JDialog(preferenceDialog);
			dialog.setTitle("Vessel&Voy Keyword 옵션" );
			KSGPanel pnControl = new KSGPanel();
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton butSubOk = new JButton("확인");
			butSubOk.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					String divider=(String)cbxDivider.getSelectedItem();
					int cc=(Integer)cbxCount.getSelectedItem();

					propertis.setProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_DIVIDER, divider);
					propertis.setProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT, String.valueOf(cc));
					propertis.store();
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			butSubOk.setFont(defaultfont);
			JButton butSubCancel = new JButton("취소");
			butSubCancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			butSubCancel.setFont(defaultfont);
			pnControl.add(butSubOk);
			pnControl.add(butSubCancel);
			KSGPanel pnMain = new KSGPanel();
			pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel lblDivider =new JLabel("구분자 : ");
			lblDivider.setFont(defaultfont);
			lblDivider.setPreferredSize(new Dimension(60,25));
			cbxDivider = new JComboBox();

			cbxDivider.setPreferredSize(new Dimension(60,25));
			cbxDivider.setToolTipText("Vessel과 Voyage를 구분할 구분자를 지정");
			cbxDivider.setFont(defaultfont);
			cbxDivider.addItem("공백");
			cbxDivider.addItem("/");
			cbxDivider.addItem("Enter");

			cbxCount = new JComboBox();
			cbxCount.setToolTipText("구분자의 위치를 지정");
			cbxCount.setPreferredSize(new Dimension(60,25));
			cbxCount.setFont(defaultfont);
			cbxCount.addItem(1);
			cbxCount.addItem(2);
			cbxCount.addItem(3);

			JLabel lblCount =new JLabel("위치 : ");
			lblCount.setFont(defaultfont);
			pnMain.add(lblDivider);
			pnMain.add(cbxDivider);
			pnMain.add(lblCount);
			pnMain.add(cbxCount);
			String doubleKey = propertis.getProperty(KSGPropertis.PROPERTIES_DOUBLEKEY);
			StringTokenizer dst =  new StringTokenizer(doubleKey,"|");

			
			System.out.println(Integer.parseInt(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT).toString()));
			cbxCount.setSelectedItem(Integer.parseInt(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT).toString()));
			
			cbxDivider.setSelectedItem(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_DIVIDER));				


			dialog.getContentPane().add(pnMain);
			dialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
			dialog.setSize(250,120);
			ViewUtil.center(dialog, false);
			dialog.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try {
		if(command.equals("comboBoxChanged"))
		{
			JComboBox box = (JComboBox) e.getSource();
			Object obj = box.getSelectedItem();
			if(obj.equals("Vessel"))
			{
				selectedKeyword="vessel";
//				String keyList =propertis.getProperty("xlskey.vessel");
				
					updateKeyWordList(baseService.getKeywordList("VESSEL"));
				
			}else if(obj.equals("Voyage"))
			{
				selectedKeyword=command;
//				String keyList =propertis.getProperty("xlskey.vessel");
				try {
					updateKeyWordList(baseService.getKeywordList("VOYAGE"));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(obj.equals("Vessel&Voyage"))
			{
//				String keyList =propertis.getProperty("xlskey.vessel");
				try {
					updateKeyWordList(baseService.getKeywordList("BOTH"));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
		
		else if(command.equals("Key Word  추가"))
		{
			String result=JOptionPane.showInputDialog(preferenceDialog, cbxKeyword.getSelectedItem()+"타입 Keyword를 입력하세요");
			if(result!=null&&result.length()>0)
			{
				KeyWordInfo insert = new KeyWordInfo();
				insert.setKey_name(result);
				Object selectedKeytyp=cbxKeyword.getSelectedItem();
				
				if(selectedKeytyp.equals("Vessel"))
				{
					insert.setKey_type("VESSEL");
				}else if(selectedKeytyp.equals("Voyage"))
				{
					insert.setKey_type("VOYAGE");
				}
				else if(selectedKeytyp.equals("Vessel&Voyage"))
				{
					insert.setKey_type("BOTH");	
				}
				try {
					baseService.insertKeyword(insert);
					DefaultListModel model =(DefaultListModel) listKeyword.getModel();

					model.addElement(result);
				} catch (SQLException e1) {
					if(e1.getErrorCode()==2627)
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "존재하는 키워드 입니다.");
					}

					e1.printStackTrace();
				}
			}else
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "키워드를 입력하세요");
			}
		}
		else if(command.equals("Key Word  삭제"))
		{
			if(listKeyword.getSelectedValue()!=null)
			{
				int result=JOptionPane.showConfirmDialog(preferenceDialog, listKeyword.getSelectedValue()+" 항목을 삭제 하시겠습니까?","Key Word 삭제",JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.OK_OPTION)
				{
					try {
						KeyWordInfo info = new KeyWordInfo();
						info.setKey_name(listKeyword.getSelectedValue().toString());
						baseService.deleteKeyword(info);
						DefaultListModel model = (DefaultListModel) listKeyword.getModel();
						model.removeElement(listKeyword.getSelectedValue());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}else
			{
				JOptionPane.showMessageDialog(preferenceDialog, "선택된 Key Word가 없습니다");
			}
		}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
			e1.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private void updateKeyWordList(List keyList) {

		Iterator ite = keyList.iterator();
		DefaultListModel listModel = new DefaultListModel();
		while(ite.hasNext())
		{
			listModel.addElement(ite.next());
		}


		listKeyword.setModel(listModel);
	}
	

	public void saveAction() {
		
	}
}


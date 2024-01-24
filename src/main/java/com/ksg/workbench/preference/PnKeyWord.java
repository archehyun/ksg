package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
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

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.KeyWordInfo;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;

public class PnKeyWord extends PnOption {

	private JList listKeyword;

	private String selectedKeyword="vessel";

	private Font defaultfont;

	private KSGPropertis propertis = KSGPropertis.getIntance();

	private JComboBox cbxKeyword;

	private JButton butOption;

	private JButton butADD;

	private JButton butDel;

	public PnKeyWord(PreferenceDialog preferenceDialog) {
		super(preferenceDialog);

		this.setName("Keyword형식");

		this.setController(new CodeController());

		initComp();

		this.setLayout(new BorderLayout());

		this.add(buildCenter(),BorderLayout.CENTER);
	}

	private void initComp()
	{
		listKeyword = new JList();
		cbxKeyword = new JComboBox();
		cbxKeyword.setPreferredSize(new Dimension(200,25));
		cbxKeyword.addItem("Vessel");
		cbxKeyword.addItem("Voyage");
		cbxKeyword.addItem("Vessel&Voyage");
		cbxKeyword.addActionListener(this);
		cbxKeyword.setSelectedIndex(0);
		butOption = new JButton("Vessel&Voyage옵션");
		butOption.setActionCommand("옵션");
		butOption.addActionListener(this);

		butADD = new KSGGradientButton("Key Word  추가");
		butDel = new KSGGradientButton("Key Word  삭제");
		butADD.addActionListener(this);
		butDel.addActionListener(this);
		butADD.setFont(defaultfont);
		butDel.setFont(defaultfont);
	}

	private KSGPanel buildCenter()
	{
		KSGPanel pnKeyWordTypeOption = new KSGPanel();

		pnKeyWordTypeOption.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnKeyWordTypeOption.add(new JLabel("키워드 타입: "));

		pnKeyWordTypeOption.add(cbxKeyword);

		pnKeyWordTypeOption.add(butOption);

		Box pnBox =Box.createVerticalBox();

		pnBox.add(pnKeyWordTypeOption);

		KSGPanel pnKeyList = new KSGPanel(new BorderLayout());

		pnKeyList.add(new JScrollPane(listKeyword));

		Box pnKeyControl = Box.createVerticalBox();	

		pnKeyControl.add(butADD);

		pnKeyControl.add(Box.createVerticalStrut(15));

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

	private String getKeyType()
	{
		Object selectedKeytyp=cbxKeyword.getSelectedItem();

		if(selectedKeytyp.equals("Vessel"))
		{
			return "VESSEL"; 

		}else if(selectedKeytyp.equals("Voyage"))
		{
			return "VOYAGE";
		}
		else
		{
			return "BOTH";
		}
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("comboBoxChanged"))
		{
			fnSearch();
		}

		else if(command.equals("Key Word  추가"))
		{
			String result=JOptionPane.showInputDialog(preferenceDialog, cbxKeyword.getSelectedItem()+"타입 Keyword를 입력하세요");

			if(result!=null&&result.length()>0)
			{
				CommandMap param = new CommandMap();

				param.put("key_name", result);

				param.put("key_type", getKeyType());

				callApi("pnKeyWord.insertKeyWord", param);

			}else
			{
				NotificationManager.showNotification("키워드를 입력하세요");
			}
		}
		else if(command.equals("Key Word  삭제"))
		{

			if(listKeyword.getSelectedValue()==null)return;
			
			int result=JOptionPane.showConfirmDialog(preferenceDialog, listKeyword.getSelectedValue()+" 항목을 삭제 하시겠습니까?","Key Word 삭제",JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.OK_OPTION)
			{
				CommandMap param = new CommandMap();

				param.put("key_name", listKeyword.getSelectedValue());

				param.put("key_type", getKeyType());

				callApi("pnKeyWord.deleteKeyWord", param);

			}else
			{
				JOptionPane.showMessageDialog(preferenceDialog, "선택된 Key Word가 없습니다");
			}
		}
		else if(command.equals("옵션"))
		{
			KeywordOptionDialog  dailog = new KeywordOptionDialog(preferenceDialog);
			
			dailog.createAndUpdateUI();
		}
	}
	
	private void fnSearch()
	{
		Object obj = cbxKeyword.getSelectedItem();

		CommandMap param = new CommandMap();

		param.put("key_type", getKeyType() );

		callApi("pnKeyWord.selectKeyWordInfoListByCondition", param);	
	}

	public void saveAction() {

	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("pnKeyWord.selectKeyWordInfoListByCondition".equals(serviceId)) {

			List<KeyWordInfo> data = (List) result.get("data");

			DefaultListModel listModel = new DefaultListModel();

			Iterator<KeyWordInfo> iter = data.iterator();

			while(iter.hasNext())
			{
				listModel.addElement(iter.next().getKey_name());
			}
			
			listKeyword.setModel(listModel);

		}else if("pnKeyWord.insertKeyWord".equals(serviceId)) {
			NotificationManager.showNotification("추가 되었습니다.");
			fnSearch();
		}
		
		else if("pnKeyWord.deleteKeyWord".equals(serviceId)) {
			NotificationManager.showNotification("삭제 되었습니다.");
			fnSearch();
		}
		
		
	}
	class KeywordOptionDialog extends KSGDialog implements ActionListener
	{
		private JComboBox cbxCount;

		private JComboBox cbxDivider;

		private JButton butSubOk;

		private JButton butSubCancel;

		public KeywordOptionDialog(JDialog dialog)
		{
			super(dialog);

			initComp();

			this.addComponentListener(this);
		}

		private void initComp()
		{
			butSubOk = new JButton("확인");
			butSubOk.addActionListener(this);
			butSubOk.setFont(defaultfont);
			butSubCancel = new JButton("취소");
			butSubCancel.addActionListener(this);
			butSubCancel.setFont(defaultfont);

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
		}


		public void createAndUpdateUI()
		{
			KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
			pnControl.add(butSubOk);
			pnControl.add(butSubCancel);
			KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel lblDivider =new JLabel("구분자 : ");
			lblDivider.setFont(defaultfont);
			lblDivider.setPreferredSize(new Dimension(60,25));


			JLabel lblCount =new JLabel("위치 : ");
			lblCount.setFont(defaultfont);
			pnMain.add(lblDivider);
			pnMain.add(cbxDivider);
			pnMain.add(lblCount);
			pnMain.add(cbxCount);


			getContentPane().add(pnMain);

			getContentPane().add(pnControl,BorderLayout.SOUTH);

			setSize(250,120);

			ViewUtil.center(this, false);

			setVisible(true);
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if("확인".equals(command)) {

				String divider	= (String)cbxDivider.getSelectedItem();

				int cc			= (Integer)cbxCount.getSelectedItem();

				propertis.setProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_DIVIDER, divider);

				propertis.setProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT, String.valueOf(cc));
				try {
					propertis.store();
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
					NotificationManager.showNotification(ee.getMessage());
					
				}
				

				setVisible(false);

				dispose();
			}
			else if("취소".equals(command)) {
				KeywordOptionDialog.this.setVisible(false);
				KeywordOptionDialog.this.dispose();
			}
		}

		@Override
		public void componentShown(ComponentEvent e) {

			String doubleKey = propertis.getProperty(KSGPropertis.PROPERTIES_DOUBLEKEY);

			StringTokenizer dst =  new StringTokenizer(doubleKey,"|");

			cbxCount.setSelectedItem(Integer.parseInt(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT).toString()));

			cbxDivider.setSelectedItem(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_DIVIDER));		
		}
	}
}
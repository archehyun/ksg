package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;

/**
 * @author archehyun
 * inland ������ ���� ���� ��ȸ
 */
public class PnInland extends JPanel implements ActionListener{
	private InlandScheduleTable _tblInlandScheduleList;
	private JComboBox cbxInlandInOut;
	private JComboBox cbxInlandSearch;
	private JTextField txfInlandSearch;
	private JLabel lblInlandCount;

	public PnInland()
	{
		
		this.setLayout(new BorderLayout());
		_tblInlandScheduleList = new InlandScheduleTable();
		
		JPanel pnInlandSearchMain = new JPanel(new BorderLayout());

		JPanel pnInlandSearch = new JPanel(new FlowLayout(FlowLayout.LEADING));
		cbxInlandInOut = new JComboBox();
		cbxInlandInOut.addItem("��ü");
		cbxInlandInOut.addItem("Inbound");
		cbxInlandInOut.addItem("Outbound");

		cbxInlandSearch = new JComboBox();
		cbxInlandSearch.addItem("��ü");
		cbxInlandSearch.addItem("���̺� ���̵�");
		cbxInlandSearch.addItem("�����");
		cbxInlandSearch.addItem("������Ʈ");
		cbxInlandSearch.addItem("���ڸ�");
		cbxInlandSearch.addItem("Voyage��ȣ");
		cbxInlandSearch.addItem("�����");
		cbxInlandSearch.addItem("������");
		cbxInlandSearch.addItem("������");

		txfInlandSearch = new JTextField(15);
		JButton butInlandSearch = new JButton("�˻�");
		butInlandSearch.setActionCommand("Inland �˻�");
		butInlandSearch.addActionListener(this);


		pnInlandSearch.add(new JLabel("����:"));
		pnInlandSearch.add(cbxInlandInOut);		
		pnInlandSearch.add(new JLabel("�׸�:"));
		pnInlandSearch.add(cbxInlandSearch);
		pnInlandSearch.add(txfInlandSearch);
		pnInlandSearch.add(butInlandSearch);
		
		
		JPanel pnInlandSearchEast =new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblInlandCount = new JLabel();
		lblInlandCount.setText("0");
		pnInlandSearchEast.add(lblInlandCount);
		pnInlandSearchEast.add(new JLabel("��"));
		
		
		pnInlandSearchMain.add(pnInlandSearch,BorderLayout.WEST);
		pnInlandSearchMain.add(pnInlandSearchEast,BorderLayout.EAST);


		

		add(pnInlandSearchMain,BorderLayout.NORTH);
		add(new JScrollPane(_tblInlandScheduleList));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ScheduleData data = new ScheduleData();
		data.setGubun(ShippersTable.GUBUN_INLAND);
		data.setInOutType(cbxInlandInOut.getSelectedIndex()==1?"I":"O");
		String searchOption  = txfInlandSearch.getText();

		switch (cbxInlandSearch.getSelectedIndex()) {
		// case 0: ��ü
		case 1: // ���̺� ���̵�
			data.setTable_id(searchOption);	
			break;
		case 2: // �����
			data.setCompany_abbr(searchOption);	
			break;					
		case 3: // ������Ʈ
			data.setAgent(searchOption);	
			break;
		case 4: // ���� ��
			data.setVessel(searchOption);	
			break;					
		case 5: // Voyage��ȣ
			data.setVoyage_num(searchOption);	
			break;
		case 6: // �����
			data.setFromPort(searchOption);	
			break;
		case 7: // ������
			data.setPort(searchOption);	
			break;
		case 8: // ������
			data.setInland_port(searchOption);	
			break;					
		default:
			break;
		}

		try {
			lblInlandCount.setText(String.valueOf(_tblInlandScheduleList.updateTable(data)));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		
	}
	class InlandScheduleTable extends KSGScheduleTable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public InlandScheduleTable() {
			super("inland");
			initTable(colums);
		}
		
		
		private String colums[] = {"","I/O","���̺� ID","�����","������Ʈ","���ڸ�","��³�¥","Voyage��ȣ","�����","DateF","DateT","������","����","TS Port","�����ڵ�","������","��������"};
		protected TableColumnModel updateTableRender() {
			TableColumnModel fvcolmodel =getColumnModel();
			for(int i=0;i<fvcolmodel.getColumnCount();i++)
			{
				TableColumn namecol =fvcolmodel.getColumn(i);
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				

				switch (i) {
				case 0:
					renderer.setHorizontalAlignment(SwingConstants.CENTER);

					namecol.setCellRenderer(renderer);
					namecol.setMaxWidth(20);
					namecol.setMinWidth(20);
				case 1: // I/O
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setPreferredWidth(45);
					break;
				case 2: // ���̺� ���̵�
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);

					break;				
				case 3: // �����
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setPreferredWidth(125);
					break;
				case 4: // ������Ʈ
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setPreferredWidth(125);
					break;

				case 5: // ���ڸ�
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(120);
				case 6: // ��³�¥
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);	

				case 7: // Voyage ��ȣ
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);				
					break;


				case 8: // 	�����
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(120);
					break;
				case 9: // �����
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(75);
					namecol.setMaxWidth(75);
					break;				

				case 10: // ������
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(75);
					namecol.setMaxWidth(75);

				case 11: // ������
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(120);

					break;
				case 12: // ����
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(60);
					break;
				case 13: // TS ��Ʈ
					renderer.setHorizontalAlignment(SwingConstants.CENTER);

					namecol.setCellRenderer(renderer);
					namecol.setMinWidth(150);
					break;				
				case 14:
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					break;	
				case 17: // TS ��Ʈ
					renderer.setHorizontalAlignment(SwingConstants.CENTER);

					namecol.setCellRenderer(renderer);

					break;		



				default:
					break;
				}
				namecol.setHeaderRenderer(new IconHeaderRenderer());

			}
			return fvcolmodel;
		}

		public int updateTable(ScheduleData op) throws SQLException {
			List li = scheduleService.getInlandScheduleList(op);
			logger.debug("table size:"+li.size());

			DefaultTableModel defaultTableModel = new DefaultTableModel();
			for(int i=0;i<colums.length;i++)
			{
				defaultTableModel.addColumn(colums[i]);
			}


			int j=0;

			for(int i=0;i<li.size();i++)
			{
				ScheduleData data = (ScheduleData) li.get(i);
				defaultTableModel.addRow(new Object[]{"",
						data.getInOutType(),
						data.getTable_id(),													
						data.getCompany_abbr(),
						data.getAgent(),
						data.getVessel(),
						data.getDate_issue(),
						data.getVoyage_num(),

						data.getFromPort(),
						data.getDateF(),
						data.getDateT(),
						data.getPort(),
						data.getGubun(),
						data.getPort(),

						data.getArea_code(),
						data.getInland_port(),
						data.getInland_date()
						
				});

			}
			setModel(defaultTableModel);
			updateTableRender();
			return li.size();
		}

	}

}

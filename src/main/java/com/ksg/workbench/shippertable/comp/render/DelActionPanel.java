package com.ksg.workbench.shippertable.comp.render;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ksg.view.comp.table.cell.ActionButton;
import com.ksg.workbench.shippertable.comp.AdvertiseTable;

public class DelActionPanel extends JPanel
{
	private JButton jButton;
	
	JTable table;

	public DelActionPanel(JTable table)
	{
		this.table = table;
		initComponent();
	}

	public void initEvent(int row)
	{
		jButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int rowc=row;

				if(rowc==-1) return;

				try{

					if(rowc<DelActionPanel.this.table.getRowCount())
					{
						DefaultTableModel model=(DefaultTableModel) DelActionPanel.this.table.getModel();

						int col = model.getColumnCount();

						for(int i=0;i<col-1;i++)
						{
							model.setValueAt("", rowc, i);
						}

						//							AdvertiseTable.this.setModel(model);

						DelActionPanel.this.table.updateUI();

					}
				}catch(Exception ee)
				{
					JOptionPane.showMessageDialog(null, "error:"+ee.getMessage());
					ee.printStackTrace();
				}
			}
		});
	}

	private void initComponent()
	{
		jButton = new ActionButton();

		jButton.setIcon( new javax.swing.ImageIcon(getClass().getResource("/com/ksg/view/comp/table/cell/delete.png")));

		//			setBorder(BorderFactory.createEmptyBorder());
		//			
		//			 javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		//		        
		//		        this.setLayout(layout);
		//		        
		//		        layout.setHorizontalGroup(
		//		            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		//		            .addGroup(layout.createSequentialGroup()
		//		                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		////		                .addComponent(cmdEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//		                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
		//		                .addComponent(jButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//		                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
		////		                .addComponent(cmdView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//		                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		//		        );
		//		        
		//		        
		//		        layout.setVerticalGroup(
		//		            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		//		            .addGroup(layout.createSequentialGroup()
		//		                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		//		                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		////		                    .addComponent(cmdView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//		                    .addComponent(jButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		//		                    .addComponent(jButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
		//		                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		//		        );
		add(jButton);
	}

	public void setButVisible(boolean b) {
		jButton.setVisible(b);

	}
}

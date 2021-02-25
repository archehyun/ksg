package com.ksg.common.comp;

import javax.swing.table.TableColumn;

/**

  * @FileName : KSGTableColumn.java

  * @Date : 2021. 2. 24. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : ����� ���� ���̺� �÷�

  */
@SuppressWarnings("serial")
public class KSGTableColumn extends TableColumn{
	
	public int size = 150;

	public String columnName;

	public String columnField;

	public int cloumIndex;
	
	public KSGTableColumn() {

	}
	public KSGTableColumn(String columnField, String columnName) {
		this.columnField = columnField;
		this.columnName = columnName;
	}

	public KSGTableColumn(String columnField, String columnName, int size) {
		this.columnField = columnField;
		this.columnName = columnName;
		this.size = size;
	}



	@Override
	public String toString() {
		return columnName;
	}

}

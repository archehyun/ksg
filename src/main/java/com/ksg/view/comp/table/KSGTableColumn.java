package com.ksg.view.comp.table;

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
	
	public int size = 0;
	
	public int minSize = 0;
	
	public int maxSize = 0;

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
	
	public Object getValue(Object obj)
	{
		return obj;
	}


	@Override
	public String toString() {
		return columnName;
	}

}

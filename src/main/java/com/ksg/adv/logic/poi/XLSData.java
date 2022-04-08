/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.adv.logic.poi;


/**

  * @FileName : XLSData.java

  * @Project : KSG2

  * @Date : 2021. 12. 6. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class XLSData 
{
	private String data="";
	
	private int start;
	
	private int row;
	
	private int col;
	
	private int index;
	
	private String[][] dataArray;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setDataArray(String[][] dataArray) {
		this.dataArray=dataArray;
		
	}
	public String[][] getDataArray() {
		return dataArray;
	}
	

}

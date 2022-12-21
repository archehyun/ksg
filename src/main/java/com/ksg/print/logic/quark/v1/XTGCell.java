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
package com.ksg.print.logic.quark.v1;

public class XTGCell extends XTGDoc{
	
	String data;
	String prefix;
	String endfix;
	public XTGCell(String data) {
		this.setData(data);
	}

	public void setData(String data) {
		this.data=data.trim();
		if(this.data.startsWith("<"))
		{
			int ind=this.data.indexOf(">");
			
			this.setPreFix(this.data.substring(0,ind+1));
			this.data=this.data.substring(ind+1);
		}
		
		if(this.data.endsWith(">"))
		{
			int ind=this.data.lastIndexOf("<");
			endfix=this.data.substring(ind,this.data.length());
			this.data=this.data.substring(0,ind);
		}
	}

	@Override
	public String getPreFix() {
		if(prefix==null )
			return "";
		return prefix;
	}

	@Override	
	public String parseDoc() {
		if(!this.getEndfix().equals("<\\c>"))
		{
			return this.getPreFix()+this.getData()+this.getEndfix();
			
		}else
		{
			return this.getPreFix()+this.getData();
		}
	}

	@Override
	public void setADVData(String data) {
		
	}

	@Override
	public void setPreFix(String preFix) {
		this.prefix=preFix;
		
	}

	@Override
	public void setQuarkData(String data) {
		
	}
	public String getData()
	{
		return data;
	}

	public String getEndfix() {
		if(endfix==null)
			return "";
		return endfix;
	}

	public void setEndfix(String endfix) {
		this.endfix = endfix;
	}

}

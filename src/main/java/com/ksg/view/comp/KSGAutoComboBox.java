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
package com.ksg.view.comp;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;

public class KSGAutoComboBox extends JComboBox{

	
	
	BaseService baseService;
	class ComboBoxTest extends PlainDocument {
		JComboBox comboBox;
		ComboBoxModel model;
		JTextComponent editor;
		// flag to indicate if setSelectedItem has been called
		// subsequent calls to remove/insertString should be ignored
		boolean selecting=false;

		public ComboBoxTest(final JComboBox comboBox) {
			this.comboBox = comboBox;
			model = comboBox.getModel();
			editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
		}

		public void remove(int offs, int len) throws BadLocationException {
			// return immediately when selecting an item
			if (selecting) return;

			super.remove(offs, len);
			Object item = lookupItem(getText(0, getLength()));
			setSelectedItem(item);

			// select the completed part
			//highlightCompletedText(offs+str.length());
		}

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// return immediately when selecting an item
			if (selecting) return;
			// insert the string into the document
			super.insertString(offs, str, a);
			// lookup and select a matching item
			//
			//Object item = lookupItem(getText(0, getLength()));
			
			String strs=getText(0, getLength());
			//setSelectedItem(item);
			try{
				
				System.out.println("strs:"+strs);
				
				searchItem(strs);	
				setText(strs);
			}catch(Exception e)
			{
				comboBox.setPopupVisible(false);
				e.printStackTrace();
			}
			// select the completed part
			highlightCompletedText(offs+str.length());
			
		}

		private void setText(String text) throws BadLocationException {
			// remove all text and insert the completed string
			super.remove(0, getLength());
			super.insertString(0, text, null);
		}

		private void highlightCompletedText(int start) {
			editor.setSelectionStart(start);
			editor.setSelectionEnd(getLength());
		}

		private void setSelectedItem(Object item) {
			selecting = true;
			model.setSelectedItem(item);
			selecting = false;
		}

		/**
		 * @param patten
		 */
		private void searchItem(String patten)
		{
			try {
				
				List li = baseService.getPortListByPatten(patten);
				comboBox.removeAllItems();
				System.out.println(li.size());
				Iterator iter = li.iterator();
				
				while(iter.hasNext())
				{
					selecting=true;
					comboBox.addItem(iter.next().toString());
				}
				selecting=false;
				
				comboBox.setPopupVisible(true);
			} catch (SQLException e) {
				e.printStackTrace();
			} 

		}

		private Object lookupItem(String pattern) {
			// iterate over all items
			for (int i=0, n=model.getSize(); i < n; i++) 
			{
				Object currentItem = model.getElementAt(i);
				// current item starts with the pattern?
				if (startsWithIgnoreCase(currentItem.toString(), pattern)) {
					System.out.println("'" + currentItem + "' matches pattern '" + pattern + "'");
					comboBox.setPopupVisible(true);
					return currentItem;
				}
			}
			comboBox.setPopupVisible(false);
			// no item starts with the pattern => return null
			return null;
		}

		// checks if str1 starts with str2 - ignores case
		private boolean startsWithIgnoreCase(String str1, String str2) {
			return str1.toUpperCase().startsWith(str2.toUpperCase());
		}
	}

	public KSGAutoComboBox() {
		this.setEditable(true);
		// get the combo boxes editor component
		JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
		// change the editor's document
		editor.setDocument(new ComboBoxTest(this));
		baseService = new BaseServiceImpl();

	}



}

package com.ksg.common.view.comp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class LookAheadTextField extends JTextField {

	String init_text;
	boolean focus_lost=true;
	public LookAheadTextField() {
		this(0, null);
	}

	public LookAheadTextField(int columns) {
		this(columns, null);
	}
	public LookAheadTextField(String text,int colums) 
	{
		super(text, colums);
	}

	public LookAheadTextField(int columns, TextLookAhead lookAhead) {
		super(columns);
		setLookAhead(lookAhead);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Remove any existing selection
				setCaretPosition(getDocument().getLength());
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent evt) {
			}

			public void focusLost(FocusEvent evt) {
				if (evt.isTemporary() == false) {
					// Remove any existing selection
					setCaretPosition(getDocument().getLength());
				}
			}
		});

	}
	public LookAheadTextField(String text,int columns, TextLookAhead lookAhead) {
		super(text, columns);
		this.setForeground(Color.lightGray);
		init_text = text;
		setLookAhead(lookAhead);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Remove any existing selection
				setCaretPosition(getDocument().getLength());
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent evt) {
			}

			public void focusLost(FocusEvent evt) {
				if (evt.isTemporary() == false) {
					// Remove any existing selection
					setCaretPosition(getDocument().getLength());
				}
			}
		});
		addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) 
			{
			
					LookAheadTextField.this.setForeground(Color.black);
					LookAheadTextField.this.setText("");
			}


			public void focusLost(FocusEvent e) {
				if(focus_lost)
				{
				LookAheadTextField.this.setForeground(Color.lightGray);
				LookAheadTextField.this.setText(init_text);
				}

			}});
	}

	public void setLookAhead(TextLookAhead lookAhead) {
		this.lookAhead = lookAhead;
	}

	public TextLookAhead getLookAhead() {
		return lookAhead;
	}

	public void replaceSelection(String content) {
		super.replaceSelection(content);


		if (isEditable() == false || isEnabled() == false) {
			return;
		}

		Document doc = getDocument();
		if (doc != null && lookAhead != null) {
			try {
				String oldContent = doc.getText(0, doc.getLength());
				String newContent = lookAhead.doLookAhead(oldContent);

				if (newContent != null) {

					setText(newContent);		
					setCaretPosition(newContent.length());
					moveCaretPosition(oldContent.length());
				}
			} catch (BadLocationException e) {
				// Won't happen
			}
		}
	}

	protected TextLookAhead lookAhead;

	// The TextLookAhead interface
	public interface TextLookAhead {
		public String doLookAhead(String key);
	}

	public void setFocus_lost(boolean focus_lost) {
		this.focus_lost = focus_lost;
	}
}

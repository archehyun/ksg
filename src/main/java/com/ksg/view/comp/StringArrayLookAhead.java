package com.ksg.view.comp;


/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class StringArrayLookAhead implements LookAheadTextField.TextLookAhead{
	public StringArrayLookAhead() {
		values = new String[0];
	}

	public StringArrayLookAhead(String[] values) {
		this.values = values;
	}
	public StringArrayLookAhead(String value) {
		this.value = value;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String[] getValues() {
		return values;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String doLookAhead(String key) {
		// Look for a string that starts with the key
			if (value.startsWith(key) == true) {
				return value;
			}

		// No match found - return null
		return null;
	}

	protected String[] values;
	protected String value;
}

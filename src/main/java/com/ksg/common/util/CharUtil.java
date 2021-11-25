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
package com.ksg.common.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**

  * @FileName : CharUtil.java

  * @Project : KSG2

  * @Date : 2021. 11. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class CharUtil {
	public static Hashtable<Character,List<Character>> BOKJAUM;
	// 복모음
	public static Hashtable<Character,List<Character>> BOKMOUM;
	
	
	static {
		// 복자음

		List<Character> chars = null;
		BOKJAUM = new Hashtable<Character,List<Character>>();
		BOKMOUM = new Hashtable<Character,List<Character>>();
	}
	
	// 초성
	public static final char[] CHOSEONG = { 
		//ㄱ  ㄲ  ㄴ  ㄷ  ㄸ  ㄹ  ㅁ  ㅂ  ㅃ  ㅅ
		0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
		//ㅆ  ㅇ  ㅈ  ㅉ  ㅊ  ㅋ  ㅌ  ㅍ  ㅎ
		0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e 
	};

	// 중성
	public static final char[] JUNGSEONG = { 
		//ㅏ  ㅐ  ㅑ  ㅒ  ㅓ  ㅔ  ㅕ  ㅖ  ㅗ  ㅘ
		0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
		//ㅙ  ㅚ  ㅛ  ㅜ  ㅝ  ㅞ  ㅟ  ㅠ  ㅡ  ㅢ
		0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
		//ㅣ
		0x3163
	};
	// 종성
	public static final char[] JONGSEONG = {
		//채움 ㄱ  ㄲ  ㄳ  ㄴ  ㄵ  ㄶ  ㄷ  ㄹ  ㄺ
		0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
		//ㄻ  ㄼ  ㄽ  ㄾ  ㄿ  ㅀ  ㅁ  ㅂ  ㅄ  ㅅ
		0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
		//ㅆ  ㅇ  ㅈ  ㅊ  ㅋ  ㅌ  ㅍ  ㅎ
		0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e 
	};

	
	private static String charsToString( List<Character> chars ) {
		String t = "";
		for( Character tc : chars ) {
			t += tc.toString();
		}
		return t;
	}
	public static String split(String s) {
		if (s == null)
			return null;

		char c;
		String t = "";
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			t += charsToString( split(c) );
		}
		return t;
	}
	
	private static List<Character> convertBokMoum(char c) {
		List<Character> chars = null;
		if( BOKMOUM.containsKey(c) ) {
			chars = BOKMOUM.get(c);
		} else {
			chars = new ArrayList<Character>();
			chars.add(c);
		}
		return chars;
	}
	
	public static List<Character> split(char c) {
		List<Character> chars = new ArrayList<Character>();
		if (c >= 0xAC00 && c <= 0xD7A3) {
			int i1, i2, i3;

			i3 = c - 0xAC00;
			i1 = i3 / (21 * 28);
			i3 = i3 % (21 * 28);
			i2 = i3 / 28;
			i3 = i3 % 28;

			chars.add(CHOSEONG[i1]);
			chars.addAll( convertBokMoum( JUNGSEONG[i2] ));
			if (i3 != 0x0000)
				chars.addAll( convertBokJaum( JONGSEONG[i3] ));
		} else {
			chars.add(c);
		}
		return chars;
	}
	
	private static List<Character> convertBokJaum(char c) {  
		List<Character> chars = null;
		if( BOKJAUM.containsKey(c) ) {
			chars = BOKJAUM.get(c);
		} else {
			chars = new ArrayList<Character>();
			chars.add(c);
		}
		return chars;
	}
}

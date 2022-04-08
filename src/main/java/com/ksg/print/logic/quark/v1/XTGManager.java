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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.util.KSGPropertis;


public class XTGManager {
	XTGDoc doc;
	FileChannel channel = null;
	protected Logger logger = LogManager.getLogger(this.getClass());
	private String INCODE_KEY;

	public XTGPage createDefaultPage() {
		logger.debug("DefaultXTGPage을 생성합니다.");
		return new XTGPage();
	}

	public void createXTGFile(XTGDoc page,String filename) {
		logger.debug("xtg 파일을 생성합니다.");
		doc = page;


		String source = doc.parseDoc();
		logger.debug("doc:\n"+source);
		char buffer[] = new char[source.length()];
		source.getChars(0,source.length(),buffer,0);

		try {
			FileWriter f0 = new FileWriter(filename);
			for(int i=0;i<buffer.length;i++)
			{
				f0.write(buffer[i]);
			}
			f0.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 기존 소스20140805
	public void createXTGFile(String  data,String filename) {
		logger.debug("create file");
		INCODE_KEY = KSGPropertis.getIntance().getProperty("selected_incode");
		logger.debug("xtg 파일을 생성합니다."+INCODE_KEY);

		File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
		if(!fo.exists())
		{
			System.out.println("폴더 생성");
			fo.mkdir();
		}
		String source = data;
		char buffer[] = new char[source.length()];
		source.getChars(0,source.length(),buffer,0);
		FileChannel channel = null;
		try {

			Charset charset = Charset.forName(INCODE_KEY);
			CharsetEncoder encoder = charset.newEncoder();
			encoder.onMalformedInput(CodingErrorAction.REPORT);


			byte[] srcData = source.getBytes(); 


			ByteBuffer buff = ByteBuffer.allocate(5);    
			File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+filename);
			FileOutputStream out = new FileOutputStream(file);  
			channel = out.getChannel();      
			int count = 0; 
			// srcData를 처리한 바이트 수 
			int len = 0;  
			while(count < srcData.length)
			{  
				len = buff.remaining(); 
				if (srcData.length - count < len)
				{       
					len = srcData.length - count;        
				}   
				buff.put(srcData, count, len);    
				buff.flip();     
				channel.write(buff);  
				buff.clear();    
				count += len;   
			}
			/* 
			 * 
			 * 
			 * 유현경 수정 시작 - 개행문자 부분
			 * 
			 * 
			 * 
			 * 
			 */

			File file2 = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+filename);
			InputStream infile = new FileInputStream(file2);
			// String rlt=filename.substring(1,length(filename)-4)+"_c.txt";
			String rlt=filename.substring(0,filename.length()-4)+"_c.txt";

			// String rlt = filename;
			//String rlt2=filename.split(".");
			//String result=rlt2+"_c.txt";

			File file3 = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+rlt);
			OutputStream outfile = new FileOutputStream(file3);

			int readLen;
			byte buf[]=new byte[1000000];
			byte bb[]=new byte[1];
			bb[0]=13;

			while(true)
			{
				readLen=infile.read(buf);
				for(int i=0;i<readLen;i++)
				{
					if(buf[i]==10)
					{
						if(buf[i+1]==13 && buf[i+2]==10){
							outfile.write(buf[i+1]);
							outfile.write(buf[i+2]);
							i=i+2;
						}
						else { //if(buf[i+1]==13 && buf[i+2]==10)

							if(buf[i-1]==13){
								outfile.write(buf[i]);
							}
						}
					}
					else { //if(buf[i]==10)
						outfile.write(buf[i]);
					}
				}
				if(readLen==-1)
					break;
				//							out.write(buf,0,readLen);
			}
			infile.close();
			outfile.close();
			System.out.println("rlt 파일 생성");


			/* 
			 * 
			 * 유현경 수정 끝 - 개행문자 부분
			 * 
			 * 
			 * 
			 * 
			 * */
		} catch(IllegalCharsetNameException ex) {   
			logger.error("잘못된 캐릭터셋 이름: " +INCODE_KEY);      
		}
		catch(UnsupportedCharsetException ex)

		{
			JOptionPane.showMessageDialog(null, "지원하지 않는 캐릭터셋: " +INCODE_KEY);
		} 
		catch(IOException ex) { 
			logger.error("입출력 예외: " + ex.getMessage());     
		} 

		finally
		{
			if (channel != null) try { channel.close(); } 
			catch(IOException ex) {}        
		}
		logger.info("end create file");
	}
	public void createXTGFile_NEW(Vector<String> dataList,String filename) {
		logger.info("create file");
		INCODE_KEY = KSGPropertis.getIntance().getProperty("selected_incode");
		logger.info("xtg 파일을 생성합니다."+INCODE_KEY);

		File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
		if(!fo.exists())
		{
			logger.info("폴더 생성");
			fo.mkdir();
		}
		try {


			//'[', ']' 제거
			String source = dataList.toString().substring(1, dataList.toString().length()-1);
			logger.debug(source);

			String xtgs[] =new String[dataList.size()];
			xtgs = dataList.toArray(xtgs);
			File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+filename);
			FileWriter fw = new FileWriter(file);
			for (int i = 0; i < xtgs.length; i++) {
				fw.write(xtgs[i]);
			}
			fw.close();

			/* 
			 * 
			 * 
			 * 유현경 수정 시작 - 개행문자 부분
			 * 
			 * 
			 * 
			 * 
			 */

			File file2 = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+filename);
			InputStream infile = new FileInputStream(file2);

			String rlt=filename.substring(0,filename.length()-4)+"_c.txt";


			File file3 = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+rlt);
			OutputStream outfile = new FileOutputStream(file3);

			int readLen;
			byte buf[]=new byte[1000000];
			byte bb[]=new byte[1];
			bb[0]=13;

			while(true)
			{
				readLen=infile.read(buf);
				for(int i=0;i<readLen;i++)
				{
					if(buf[i]==10)
					{
						if(buf[i+1]==13 && buf[i+2]==10){
							outfile.write(buf[i+1]);
							outfile.write(buf[i+2]);
							i=i+2;
						}
						else { 

							if(buf[i-1]==13){
								outfile.write(buf[i]);
							}
						}
					}
					else { 
						outfile.write(buf[i]);
					}
				}
				if(readLen==-1)
					break;

			}
			infile.close();
			outfile.close();
			logger.info("rlt 파일 생성");


			/* 
			 * 
			 * 유현경 수정 끝 - 개행문자 부분
			 * 
			 * 
			 * 
			 * 
			 * */
		} catch(IllegalCharsetNameException ex) {   
			logger.error("잘못된 캐릭터셋 이름: " +INCODE_KEY);      
		}
		catch(UnsupportedCharsetException ex)

		{
			JOptionPane.showMessageDialog(null, "지원하지 않는 캐릭터셋: " +INCODE_KEY);
		} 
		catch(IOException ex) { 
			logger.error("입출력 예외: " + ex.getMessage());     
		} 

		finally
		{
			if (channel != null) try { channel.close(); } 
			catch(IOException ex) {}        
		}
		logger.info("end create file");
	}	






}

package com.ksg.view.util;

import java.io.*;

public final class FileUtil
{
	public final static byte[] load(String fileName)
	{
		try { 
			FileInputStream fin=new FileInputStream(fileName);
			return load(fin);
		}
		catch (Exception e) {

			return new byte[0];
		}
	}

	public final static byte[] load(File file)
	{
		try { 
			FileInputStream fin=new FileInputStream(file);
			return load(fin);
		}
		catch (Exception e) {

			return new byte[0];
		}
	}

	public final static byte[] load(FileInputStream fin)
	{
		byte readBuf[] = new byte[512*1024];

		try { 
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			int readCnt = fin.read(readBuf);
			while (0 < readCnt) {
				bout.write(readBuf, 0, readCnt);
				readCnt = fin.read(readBuf);

				System.out.println(readCnt);
			}

			fin.close();

			return bout.toByteArray();
		}
		catch (Exception e) {

			return new byte[0];
		}
	}
	public static void main(String[] args) {
		FileUtil fileUtil = new FileUtil();
		File f = new File("SAVE1");
		if(f.isFile())
		{
			fileUtil.load(new File("inputdata/SAVE1"));
			
		}else
		{
			System.out.println("faile");
		}
		
	}


}


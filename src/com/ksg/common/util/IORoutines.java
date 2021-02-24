package com.ksg.common.util;

import java.io.*;

/**
 * @author J. H. S.
 */
public class IORoutines {
  public static final byte[] LINE_BREAK_BYTES = { (byte) 13, (byte) 10 }; 
  
  public static String loadAsText(InputStream in, String encoding) throws IOException {
    return loadAsText(in, encoding, 4096);
  }
  
  public static String loadAsText(InputStream in, String encoding, int bufferSize) throws IOException {
    InputStreamReader reader = new InputStreamReader(in, encoding);
      char[] buffer = new char[bufferSize];
      int offset = 0;
      for(;;) {
        int remain = buffer.length - offset;
        if(remain <= 0) {
          char[] newBuffer = new char[buffer.length * 2];
          System.arraycopy(buffer, 0, newBuffer, 0, offset);                    
          buffer = newBuffer;
          remain = buffer.length - offset;
        }
          int numRead = reader.read(buffer, offset, remain);
          if(numRead == -1) {
            break;
          }
        offset += numRead;
      }
      return new String(buffer, 0, offset);
  }
  
    public static byte[] load(File file) throws IOException {
        long fileLength = file.length();
        if(fileLength > Integer.MAX_VALUE) {
            throw new IOException("File '" + file.getName() + "' too big");
        }
        InputStream in = new FileInputStream(file);
        try {
          return loadExact(in, (int) fileLength);
        } finally {
            in.close();
        }
    }
    
    public static byte[] load(InputStream in) throws IOException {
        return load(in, 4096);
    }
    public static byte[] load(InputStream in, int initialBufferSize) throws IOException {
      if(initialBufferSize == 0) {
        initialBufferSize = 1;
      }
      byte[] buffer = new byte[initialBufferSize];
      int offset = 0;
      for(;;) {
        int remain = buffer.length - offset;
        if(remain <= 0) {
          int newSize = buffer.length * 2;
          byte[] newBuffer = new byte[newSize];
          System.arraycopy(buffer, 0, newBuffer, 0, offset);                    
          buffer = newBuffer;
          remain = buffer.length - offset;
        }
          int numRead = in.read(buffer, offset, remain);
          if(numRead == -1) {
            break;
          }
        offset += numRead;
      }
      if(offset < buffer.length) {
        byte[] newBuffer = new byte[offset];
        System.arraycopy(buffer, 0, newBuffer, 0, offset);                    
        buffer = newBuffer;
      }
      return buffer;
    }
    
    public static byte[] loadExact(InputStream in, int length) throws IOException {
      byte[] buffer = new byte[length];
      int offset = 0;
      for(;;) {
        int remain = length - offset;
        if(remain <= 0) {
          break;
        }
        int numRead = in.read(buffer, offset, remain);
        if(numRead == -1) {
          throw new IOException("Reached EOF, read " + offset + " expecting " + length);
        }
      offset += numRead;
      }
      return buffer;
    }

    public static boolean equalContent(File file, byte[] content) throws IOException {
      long length = file.length();
      if(length > Integer.MAX_VALUE) {
        throw new IOException("File '" + file + "' too big");
      }
      InputStream in = new FileInputStream(file);
      try {
        byte[] fileContent = loadExact(in, (int) length);
        return java.util.Arrays.equals(content, fileContent);
      } finally {
        in.close();
      }
    }
    
    public static void save(File file, byte[] content) throws IOException {
      FileOutputStream out = new FileOutputStream(file);
      try {
        out.write(content);
      } finally {
        out.close();
      }
    }
    
    /**
     * Reads line without buffering.
     */
    public static String readLine(InputStream in) throws IOException {
      int b;
      StringBuffer sb = null;
      OUTER:
      while((b = in.read()) != -1) {
        if(sb == null) {
          sb = new StringBuffer();
        }
        switch(b) {
          case (byte) '\n':
            break OUTER;
          case (byte) '\r':
            break;
          default:
            sb.append((char) b);
            break;
        }
      }
      return sb == null ? null : sb.toString();
    }
    
    public static void touch(File file) {
      file.setLastModified(System.currentTimeMillis());
    }
    
    public static void saveStrings(File file, java.util.Collection list) throws IOException {
      BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
      try {
        PrintWriter writer = new PrintWriter(bout);
        java.util.Iterator i = list.iterator();
        while(i.hasNext()) {
          String text = (String) i.next();
          writer.println(text);
        }
        writer.flush();
      } finally {
        bout.close();
      }
    }
    
    public static java.util.List loadStrings(File file) throws IOException {
      java.util.List list = new java.util.LinkedList();
      InputStream in = new FileInputStream(file);
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while((line = reader.readLine()) != null) {
          list.add(line);
        }
        return list;
      } finally {
        in.close();
      }
    }
    public static void main(String[] args) throws IOException {
		IORoutines ioRoutines = new IORoutines();
		IORoutines ioRoutines2 = new IORoutines();
		byte[] a=ioRoutines.load(new File("inputdata/SAVE1"));
		byte[] b=ioRoutines2.load(new File("inputdata/A2010-03-01.txt"));
		for(int i=0;i<a.length;i++)
		{
			System.out.println(a[i]+",");
			System.out.print(b[i]);
		}
		
		
		
	}
    
}

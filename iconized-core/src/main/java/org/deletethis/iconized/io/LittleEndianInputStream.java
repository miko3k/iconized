/*
 * LittleEndianInputStream.java
 *
 * Created on 07 November 2006, 08:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.deletethis.iconized.io;

import java.io.*;

/**
 * Reads little-endian data from a source <tt>InputStream</tt> by reversing byte ordering.
 * @author Ian McDonagh
 */
public class LittleEndianInputStream extends DataInputStream implements DataInput {
	
  /**
   * Creates a new instance of <tt>LittleEndianInputStream</tt>, which will read from the specified source.
   * @param in the source <tt>InputStream</tt>
   */
  public LittleEndianInputStream(InputStream in) {
    super(in);
  }
  
  public int skip(int count, boolean strict) throws IOException {
	  return IOUtils.skip(this, count, strict);
  }
  
  /**
   * Reads a little-endian <tt>short</tt> value
   * @throws java.io.IOException if an error occurs
   * @return <tt>short</tt> value with reversed byte order
   */
  public short readShortLE() throws IOException {
    
    int b1 = read();
    int b2 = read();
    
    if (b1 < 0 || b2 < 0) {
      throw new EOFException();
    }
    
    short ret = (short) ((b2 << 8) + (b1 << 0));
    
    return ret;
  }
  
  /**
   * Reads a little-endian <tt>int</tt> value.
   * @throws java.io.IOException if an error occurs
   * @return <tt>int</tt> value with reversed byte order
   */
  public int readIntLE() throws IOException {
    int b1 = read();
    int b2 = read();
    int b3 = read();
    int b4 = read();
    
    if (b1 < -1 || b2 < -1 || b3 < -1 || b4 < -1) {
      throw new EOFException();
    }
    
    int ret = (b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0);
    
    return ret;
  }
  
}

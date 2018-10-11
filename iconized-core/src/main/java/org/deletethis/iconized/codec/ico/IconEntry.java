package org.deletethis.iconized.codec.ico;

import java.io.IOException;
import org.deletethis.iconized.io.LittleEndianInputStream;
import org.deletethis.iconized.io.LittleEndianOutputStream;

/**
 * Represents an <tt>IconEntry</tt> structure, which contains information about an ICO image.
 * @author Ian McDonagh
 */
public class IconEntry {
  /**
   * The width of the icon image in pixels.
   * <tt>0</tt> specifies a width of 256 pixels.
   */
  final int bWidth;
  /**
   * The height of the icon image in pixels.
   * <tt>0</tt> specifies a height of 256 pixels.
   */
  final int bHeight;
  /**
   * The number of colours, calculated from {@link #sBitCount sBitCount}.
   * <tt>0</tt> specifies a colour count of &gt;= 256.
   */
  final int bColorCount;
  /**
   * Unused.  Should always be <tt>0</tt>.
   */
  final byte bReserved;
  /**
   * Number of planes, which should always be <tt>1</tt>.
   */
  final short sPlanes;
  /**
   * Colour depth in bits per pixel.
   */
  final short sBitCount;
  /**
   * Size of ICO data, which should be the size of (InfoHeader + AND bitmap + XOR bitmap).
   */
  final int iSizeInBytes;
  /**
   * Position in file where the InfoHeader starts.
   */
  final int iFileOffset;
  
  /**
   * Creates an <tt>IconEntry</tt> structure from the source input
   * @param in the source input
   * @throws java.io.IOException if an error occurs
   */ 
  public IconEntry(LittleEndianInputStream in) throws IOException {
    //Width 	1 byte 	Cursor Width (16, 32, 64, 0 = 256)
    bWidth = in.readUnsignedByte();
    //Height 	1 byte 	Cursor Height (16, 32, 64, 0 = 256 , most commonly = Width)
    bHeight = in.readUnsignedByte();
    //ColorCount 	1 byte 	Number of Colors (2,16, 0=256)
    bColorCount = in.readUnsignedByte();
    //Reserved 	1 byte 	=0
    bReserved = in.readByte();
    //Planes 	2 byte 	=1
    sPlanes = in.readShortLE();
    //BitCount 	2 byte 	bits per pixel (1, 4, 8)
    sBitCount = in.readShortLE();
    //SizeInBytes 	4 byte 	Size of (InfoHeader + ANDbitmap + XORbitmap)
    iSizeInBytes = in.readIntLE();
    //FileOffset 	4 byte 	FilePos, where InfoHeader starts
    iFileOffset = in.readIntLE();
  }

  /**
   * A string representation of this <tt>IconEntry</tt> structure.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("width=");
    sb.append(bWidth);
    sb.append(",height=");
    sb.append(bHeight);
    sb.append(",bitCount=");
    sb.append(sBitCount);
    sb.append(",colorCount="+bColorCount);
    return sb.toString();
  }
}

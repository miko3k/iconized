# iconized

A multiplatform `.ico` reading library.

## Overview

The `iconized` library allows you to read certain Microsoft icon format (1, 4, 8, 24 and 32 bit, XP uncompressed, Vista compressed).

Any `.ico` file which can be read by any version of Windows and not this library will considered a bug. 
Please submit a bug report if you have such a file.

Project is split into multiple artifacts:
* `iconized-core` - core decoding routines, mostly coming from `image4j`
* `iconized-awt` - decoder which returns `java.awt.image.BufferedImage`
* `iconized-andorid` - decoder which returns `android.graphics.Bitmap`
* `iconized-test` - utility classes for unit testing, not intended for public use

We always use native PNG decoder (ImageIO or `android.graphics.BitmapFactory`). We also attempt not to
copy data around and parsing is always done in single pass. 


## Purpose

This project aims to provide:

* an open source library for handling ICO formats in Java
* with a commercial-friendly license
* using only Java code, ie. without using any JNI hacks
* with no dependencies on third-party libraries (where possible)
* be as much much unit and field tested as possible

## Usage

API is the same on Android and AWT, one always uses `IcoParser.getInstance().getIcons(...)`.
The package of `IcoParser` and returned type is platform specific.

AWT
```java
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.list;
import org.deletethis.iconized.awt.IcoParser;

class Main {
    public static void main(String[] args) throws IOException{
        try(InputStream stream = Foo.class.getResourceAsStream("bundled_icon.ico")) {
            List<BufferedImage> images = IcoParser.getInstance().getIcons(stream);
            // do something with images
        }
    }    
}

```

Android
```java
import java.io.InputStream;
import java.util.list;
import android.graphics.Bitmap;
import android.app.Activity;
import org.deletethis.iconized.android.IcoParser;

class MyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try(InputStream stream = getResources().openRawResource(R.raw.resource_id)) {
            List<Bitmap> images = IcoParser.getInstance().getIcons(stream);
            // do something with images
        } catch(IOException ex) {
            throw new IllegalStateException("Cannot open resource");
        }
    }
} 
```


## License

The `iconized` library is licensed under the GNU LGPL v2.1 so you are free to use it in your Free Software and Open Source projects, as well as commercial projects, under the terms of the LGPL v2.1.

## History

This is a fork of [image4j](https://github.com/imcdonagh/image4j). I changed the code beyond recognition, added a battery 
of unit tests, ditched many parts including encoding and general purpose BMP decoder. 


## Credits

* [image4j](https://github.com/imcdonagh/image4j)
* The [File Formats page at DaubNET](https://www.daubnet.com/en/file-formats) for information on various image formats.
* GIMP, which we use for editing images

## Disclaimer

To my or original author knowledge, there are no patents on either the BMP or ICO formats.

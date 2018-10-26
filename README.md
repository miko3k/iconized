# mejico

The chingón `.ico` reading library.

## Overview

The `mejico` a small library to read Microsoft ICO format in pure java.

* a commercial-friendly license
* no dependencies on third-party libraries
* comprehensively unit tested
* fully compatible with .ico format

All versions should be supported. Hopefully. Please file a bug and help to improve this library
if you find an `.ico` file in the wild, which can be read by any version of Windows 
and this library fails to do so. 

### Structure

Project is split into multiple artifacts:
* `mejico-core` - core decoding routines, mostly coming from `image4j`
* `mejico-awt` - decoder which returns `java.awt.image.BufferedImage`
* `mejico-andorid` - decoder which returns `android.graphics.Bitmap`
* `mejico-test` - utility classes for unit testing, not intended for public use

Loading of PNG data is delegated to native facilities. This is the main reason why we have
separate Android and AWT artifacts. 

## Usage

API is the same on Android and AWT, one always uses `IconParser.getInstance().getIcons(...)`.
The package of `IconParser` and returned type is platform specific.

AWT
```java
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.deletethis.mejico.awt.IconParser;

class Main {
    public static void main(String[] args) throws IOException {
        InputStream stream = Main.class.getResourceAsStream("bundled_icon.ico");
        List<BufferedImage> images = IconParser.getInstance().getIcons(Main.class.getResourceAsStream("bundled_icon.ico"));
        // do something with images
    }    
}

```

Android
```java
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import android.graphics.Bitmap;
import android.app.Activity;
import android.os.Bundle;
import org.deletethis.mejico.android.IconParser;

class MyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InputStream stream = getResources().openRawResource(R.raw.resource_id);
            List<Bitmap> images = IconParser.getInstance().getIcons(stream);
            
            // do something with images
            
        } catch(IOException ex) {
            throw new IllegalStateException("Cannot open resource");
        }
    }
} 
```

## License

The `mejico` library is licensed under the GNU LGPL v2.1 so you are free to use it in
 your Free Software and Open Source projects, as well as commercial projects, 
 under the terms of the LGPL v2.1.

## History

This is a fork of [image4j](https://github.com/imcdonagh/image4j). I changed the code
beyond recognition, added a battery of unit tests, ditched many parts including
encoding and general purpose BMP decoder.

## Future

I plan to maintain this library. More or less.

If you encounter bugs, please do not hesitate to report them.

## Credits

* [image4j](https://github.com/imcdonagh/image4j)
* The [File Formats page at DaubNET](https://www.daubnet.com/en/file-formats) for information 
  on various image formats
* GIMP, which we use for editing images

## Disclaimer

To my or original author knowledge, there are no patents on either the BMP or ICO formats.

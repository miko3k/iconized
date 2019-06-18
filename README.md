# mejico

A library to read `.ico`/`.cur` files in Java. Chingón.

## Overview

The `mejico` a small library to read Microsoft `.ico` or `.cur` format in java.

* a commercial-friendly license
* no dependencies on third-party libraries
* comprehensively unit tested
* fully compatible with .ico format

All versions should be supported. Hopefully. Please file a bug and help to improve this library
if you find an `.ico` or `.cur` file in the wild, which can be read by any version of Windows 
and this library fails to do so. 

### Artifcats

I decided to stop publishing to Maven Central, coz it is annoying. 

Simply snatch this from it from [jitpack](https://jitpack.io/#miko3k/mejico/26b5f1d8c4). Do not forget to
select correct subproject in the dropdown menu!

Should this project find heavy use, I might consider publishing to Maven Central again.


### Structure

This project is split into multiple subprojects. Your application should depend on one of the first two.
* `mejico-awt` - decoder which returns `java.awt.image.BufferedImage`
* `mejico-andorid` - decoder which returns `android.graphics.Bitmap`
* `mejico-core` - core decoding routines, mostly coming from `image4j`
* `mejico-test` - utility classes for unit testing, not intended for public use

Loading of PNG data is delegated to native facilities. This is the main reason why we have
separate Android and AWT artifacts. You might want to depend on `mejico-core` only, if you want
to supply your own PNG (or even BMP) decoder.

## Usage

There are two classes with static factory methods to obtain an [`IconParser`](mejico-core/src/main/java/org/deletethis/mejico/IconParser.java), depending on the platform:
* [`AwtMejico.getIconParser()`](mejico-awt/src/main/java/org/deletethis/mejico/awt/AwtMejico.java), returns [`IconParser<BufferedImage>`](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html)
* [`AndroidMejico.getIconParser()`](mejico-android/src/main/java/org/deletethis/mejico/android/AndroidMejico.java), returns [`IconParser<Bitmap>`](https://developer.android.com/reference/kotlin/android/graphics/Bitmap)


Check [`IconParser`](mejico-core/src/main/java/org/deletethis/mejico/IconParser.java)
to see the high level API. It can return a `List` of images contained in `.ico` file. 

Lower level API is in the [`IconReader`](mejico-core/src/main/java/org/deletethis/mejico/IconReader.java) 
class and allows to retrieve image metadata first (including the hotspot position for `.cur` files) and decode 
selected images afterwards.
  

### Examples

```java
// AWT example
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.deletethis.mejico.awt.AwtMejico;

class Main {
    public static void main(String[] args) throws IOException {
        InputStream stream = Main.class.getResourceAsStream("bundled_icon.ico");
        List<BufferedImage> images = AwtMejico.getIconParser().getIcons(Main.class.getResourceAsStream("bundled_icon.ico"));
        // do something with images
    }    
}
```

```java
// Android example
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import android.graphics.Bitmap;
import android.app.Activity;
import android.os.Bundle;
import org.deletethis.mejico.android.AndroidMejico;

class MyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InputStream stream = getResources().openRawResource(R.raw.resource_id);
            List<Bitmap> images = AndroidMejico.getIconParser().getIcons(stream);
            
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

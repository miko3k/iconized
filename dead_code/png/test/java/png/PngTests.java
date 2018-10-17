package png;

import org.deletethis.iconized.Colors;
import org.deletethis.iconized.Pixmap;
import org.deletethis.iconized.PngDecoder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Iterator;


@RunWith(Parameterized.class)
public class PngTests {
    @Rule
    public TestName name= new TestName();

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<File> data() throws URISyntaxException {
        if(false) {
            return Arrays.asList(new File("iconized-core\\out\\test\\resources\\png\\basn0g08.png"));
        }

        URL resource = PngTests.class.getResource("_here");
        if(resource == null) {
            throw new IllegalArgumentException("directory not found");
        }
        File[] files = new File(resource.toURI()).getParentFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.getName().toLowerCase().endsWith(".png"))
                    return false;
                if (pathname.getName().startsWith("."))
                    return false;
                return true;
            }
        });
        return Arrays.asList(files);
    }
    @Parameterized.Parameter public File file;

    private ImageReader getPngReader() {
        Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
        if (itr.hasNext()) {
            return itr.next();
        } else {
            return null;
        }
    }

    private String dataToString(int [] data) {
        StringBuilder bld = new StringBuilder();
        for(int i=0;i<data.length;++i) {
            if(i > 0)
                bld.append(' ');

            bld.append(Colors.toString(data[i]));
        }
        return bld.toString();
    }

    private Pixmap decodeByLib(File file) throws IOException {
        Pixmap pm1;
        try(InputStream in = new FileInputStream(file)) {
            PngDecoder pngDecoder = new PngDecoder(in);
            int width = pngDecoder.getWidth();
            int height = pngDecoder.getHeight();
            pm1 = new Pixmap(width, height);
            IntBuffer bb = IntBuffer.wrap(pm1.getData());
            pngDecoder.decode(bb, width);
            return pm1;
        }
    }

    private Pixmap readRaw(File file, int w, int h) throws IOException {
        String s = file.toString();
        file = new File(s.substring(0, s.length()-4) + ".rgba");
        Pixmap result = new Pixmap(w,h);
        try(FileInputStream in = new FileInputStream(file)) {
            for(int y=0;y<h;++y) {
                for(int x=0;x<w;++x) {
                    byte r = (byte)in.read();
                    byte g = (byte)in.read();
                    byte b = (byte)in.read();
                    byte a = (byte)in.read();

                    result.setRGB(x,y,Colors.create(r,g,b,a));
                }

            }

        }
        return result;
    }

    private Pixmap decodeByImageIo(File file) throws IOException {
        Pixmap pm2;
        try(InputStream in = new FileInputStream(file)) {
            ImageReader imageReader = getPngReader();
            try(ImageInputStream input = ImageIO.createImageInputStream(in)) {
                imageReader.setInput(input);
                BufferedImage image = imageReader.read(0);

                pm2 = new Pixmap(image.getWidth(), image.getHeight());
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), pm2.getData(), 0, image.getWidth());
            }
        }
        return pm2;
    }


    //@Test
    public void testImageIo() throws IOException {
        decodeByImageIo(file);
    }

//    @Test
    public void testLib() throws IOException {
        decodeByLib(file);
    }

    @Test
    public void testEq() throws IOException {
        System.out.println(name.getMethodName());

        Pixmap pm2 = decodeByLib(file);
        //Pixmap pm1 = readRaw(file, pm2.getWidth(), pm2.getHeight());
        Pixmap pm1 = decodeByImageIo(file);

        String s1 = dataToString(pm1.getData());
        String s2 = dataToString(pm2.getData());

        System.out.println("rgba:  " + s1);
        System.out.println("intrn: " + s2);

        Assert.assertEquals(s1, s2);
    }

}

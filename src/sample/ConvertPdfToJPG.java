package sample;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ConvertPdfToJPG {
    public void convertPdfJpg(String namePdf) throws IOException {
//        namePdf = "PDF/"+namePdf;

        //  load a pdf from a file
//        String dir = "/home/vova/Documents/";
        System.out.println(namePdf);
        File file;
        try {


            file = new File(namePdf);


            RandomAccessFile raf = new RandomAccessFile(file, "r");
            ReadableByteChannel ch = Channels.newChannel(new FileInputStream(file));

            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,
                    0, channel.size());
            PDFFile pdffile = new PDFFile(buf);


            //   get number of pages
            int jumlahhalaman = pdffile.getNumPages();

            //  iterate through the number of pages
            for (int i = 1; i <= jumlahhalaman; i++) {
                System.out.println("i = " + i + " stage = " + jumlahhalaman);
                PDFPage page = pdffile.getPage(i);

                //  create new image
                Rectangle rect = new Rectangle(0, 0,
                        (int) page.getBBox().getWidth(),
                        (int) page.getBBox().getHeight());

                java.awt.Image img = page.getImage(
                        rect.width, rect.height, //width & height
                        rect, // clip rect
                        null, // null for the ImageObserver
                        true, // fill background with white
                        true // block until drawing is done
                );

                BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
                Graphics g = bufferedImage.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();

                File asd = new File("out/production/Printer_PDF_DOC/JPG/" + i + ".jpg");
                if (asd.exists()) {
                    asd.delete();
                }
                ImageIO.write(bufferedImage, "jpg", asd);
            }
            System.out.print("okokokokoko");
        }catch (Exception e){

        }

    }
}

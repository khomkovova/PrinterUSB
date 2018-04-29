package sample;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
public class PrinterSend {

     public void print() {
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(1));
        PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, pras);
        if (pss.length == 0) System.out.println("No printer services available.");
        PrintService ps = pss[0];
        System.out.println("Printing to " + ps);
        DocPrintJob job = ps.createPrintJob();
         FileInputStream fin = null;
         try {
             fin = new FileInputStream("YOurImageFileName.JPG");
         } catch (FileNotFoundException e) {
             System.out.println("not found image");
             e.printStackTrace();
         }
         Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.JPEG, null);
         try {
             job.print(doc, pras);
//             fin.close();
         } catch (PrintException e) {
             e.printStackTrace();
             System.out.println("not found 1");
         }
         try {
             fin.close();
         } catch (IOException e) {
             e.printStackTrace();
             System.out.println("not found 2");
         }
     }
}

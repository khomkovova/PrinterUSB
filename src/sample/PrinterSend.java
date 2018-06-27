package sample;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PrinterSend {

     public void print_jpg() {
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(1));
        PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, pras);
        if (pss.length == 0) System.out.println("No printer services available.");
        PrintService ps = pss[0];
        System.out.println("Printing to " + ps);
        DocPrintJob job = ps.createPrintJob();
         FileInputStream fin = null;
         try {
             fin = new FileInputStream("pdf.jpg");
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

     public void print_file(String name) throws IOException, InterruptedException {
         System.out.println("name = " + name);
         String command = null;
         command = "soffice -p FilePrint/" + name;

         System.out.println(command);
         Process proc = Runtime.getRuntime().exec(command);
         proc.waitFor();
         System.out.println(proc.getInputStream().read());

     }
}

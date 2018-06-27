package sample;


import java.io.*;

public class WorldToPDF {

    public void conwertWorldPdf(String nameDoc) throws InterruptedException, IOException {
        String command = "soffice --headless --convert-to pdf --outdir PDF/ " +  nameDoc ;
        System.out.println(command);
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();
    }
}
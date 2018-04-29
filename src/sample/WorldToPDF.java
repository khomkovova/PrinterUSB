package sample;


import java.io.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WorldToPDF {

    public void conwertWorldPdf(String nameDoc) throws InterruptedException, IOException {
        String command = "soffice --headless --convert-to pdf --outdir out/production/Printer_PDF_DOC/PDF/ " +  nameDoc ;
        System.out.println(command);
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();
    }
}
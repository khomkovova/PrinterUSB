package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ArduinoRead extends Thread implements SerialPortDataListener {


    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

    public int getPacketSize() { return 100; }

    public void serialEvent(SerialPortEvent event)
    {
        byte[] newData = event.getReceivedData();
        System.out.println("Received data of size: " + newData.length);
        for (int i = 0; i < newData.length; ++i)
            System.out.print((char)newData[i]);
        System.out.println("\n");
    }
    @Override
    public void run()	//Этот метод будет выполнен в побочном потоке
    {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        ArduinoRead listener = new ArduinoRead();
        while (true) {


            comPort.addDataListener(listener);
            InputStream inputStream = comPort.getInputStream();
            int str_asci;
            try {
                str_asci = inputStream.read();
                if(str_asci != 0) {
                    System.out.println("get string = " + str_asci);
                }
            }
            catch (Exception e ){
//                System.out.println("Exception");
            }


            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            comPort.removeDataListener();
        }
    }







}

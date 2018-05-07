package sample;



import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.*;

public class Arduino implements SerialPortDataListener {

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

public void sendComand(){
    SerialPort comPort = SerialPort.getCommPorts()[0];
    comPort.openPort();
    Arduino listener = new Arduino();
    comPort.addDataListener(listener);
    try { Thread.sleep(5000); } catch (Exception e) { e.printStackTrace(); }
    comPort.removeDataListener();
    comPort.closePort();
}
}


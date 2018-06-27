package sample;



import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.*;
import java.math.BigInteger;

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

public void sendComand(int numberPage) throws IOException, InterruptedException {
    try {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        System.out.println("Port = " + comPort.getSystemPortName());
        comPort.openPort();
        Arduino listener = new Arduino();
        comPort.addDataListener(listener);
        OutputStream outputStream = comPort.getOutputStream();
        System.out.println("send string = " + numberPage);

        while (numberPage > 9) {

            outputStream.write(9);
//            System.out.println("get string = " + (comPort.getInputStream().read() - 48));
            numberPage -= 9;
            Thread.sleep(50);
        }
        outputStream.write(numberPage);
//        System.out.println("get string = " + (comPort.getInputStream().read() - 48));

        comPort.removeDataListener();
        comPort.closePort();
    }
    catch (Exception ArrayIndexOutOfBoundsException){
        System.out.println("ArrayIndexOutOfBoundsException");
    }

}
}


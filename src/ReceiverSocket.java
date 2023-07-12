import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ReceiverSocket implements Runnable{
    private final int portNumber;
    private volatile boolean execute;
    private final Socket client;
    private List<DataTagBLE> beacons;
    private int count = 0;

    public ReceiverSocket(int portNumber, Socket client) {
        this.portNumber = portNumber;
        this.client = client;
        beacons = new ArrayList<DataTagBLE>();
        System.out.println("Connected");
    }


    @Override
    public void run() {
        execute = true;
        try {
            DataInputStream dIn = new DataInputStream(client.getInputStream());
            while(execute){
                int length = dIn.readInt();
                if (length > 0){
                    byte[] data = new byte[length];
                    dIn.readFully(data, 0, data.length);
                    setBeacons(data);
                    applyKalmanFilter();
                }
            }
        } catch (IOException e) {
            System.out.println("CONNECTION RESET");
        }
    }

    private void setBeacons(byte[] arr){
        if (this.beacons.isEmpty()){
            for (int i = 0; i < arr.length - 1; i+=2) {
                var beacon = new Beacon("A","A",arr[i]);
                var dataTag = new DataTagBLE(beacon, arr[i+1]);
                beacons.add(dataTag);
            }
            return;
        }
        for (int i = 1; i <= arr.length - 1; i+=2) {
            var beacon = beacons.get((i - 1) / 2);
            beacon.setRSSI(arr[i]);
        }



    }
    private void applyKalmanFilter(){
        if (beacons.isEmpty()){
            return;
        }
        System.out.println("Filtered RSSI Values Count = " + ++count);
        for (int i = 0; i < beacons.size(); i++) {
            var beacon = beacons.get(i);
            beacon.applyKalmanFilterToRssi();
            System.out.println(MessageFormat.format("B{0} = {1}",i,beacon.mFilteredRSSI));
        }
    }
    public void stop(){
        execute = false;
    }
}

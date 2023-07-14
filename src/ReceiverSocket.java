import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReceiverSocket implements Runnable{
    private final int portNumber;
    private volatile boolean execute;
    private final Socket client;
    private List<DataTagBLE> beacons;
    private List<Beacon> mockData = new ArrayList<Beacon>(Arrays.asList(new Beacon("00:2B:67:C7:20:A6", "A",30),
            new Beacon("02:50:F2:70:83:00","B",40), new Beacon("DE:3A:45:2D:48:1F", "C",45)));
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
            for (int i = 0; i < arr.length ; i+= arr.length / 3) {
                int rssi = arr[i];
                byte[] macAddressInBytes = new byte[6];
                int count = 0;
                for (int j = i + 1; j < i + (arr.length / 3) ; j++) {
                    macAddressInBytes[count] = arr[j];
                    count++;
                }
                var beacon = getBeaconByMac(decodeMacAddress(macAddressInBytes));
                if (beacon == null){
                    System.out.println("BEACON IS NOT DEFINED");
                    return;
                }
                var dataTag = new DataTagBLE(beacon, rssi);
                beacons.add(dataTag);
            }
            return;
        }
        for (int i = 0; i < arr.length ; i+= arr.length / 3) {
            var beacon = beacons.get(i / (arr.length / 3));
            beacon.setRSSI(arr[i]);
        }
    }
    private String decodeMacAddress(byte[] macAddressInBytes){
        List<String> parts = new ArrayList<String>();
        for(int i=0; i<6; i++) {
            parts.add(String.format("%02x", macAddressInBytes[i]));
            int hex = macAddressInBytes[i] & 0xFF;
        }
        return String.join(":", parts).toUpperCase();
    }
    private Beacon getBeaconByMac(String mac){
        var selectedBeacon = mockData.stream().filter(b -> b.getMAC().equals(mac)).findFirst().orElse(null);
        return selectedBeacon;
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

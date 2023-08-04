package Socket;

import Manager.BeaconManager;
import Models.Beacon;
import Models.DataTagBLE;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReceiverSocket implements Runnable{
    private final int portNumber;
    public int realLocationX;
    public int realLocationY;
    private volatile boolean execute;
    private final Socket client;
    private List<DataTagBLE> beacons;
    private List<Beacon> mockData = new ArrayList<Beacon>(Arrays.asList(new Beacon("00:2B:67:C7:20:A6", "A",-35,0,0),
            new Beacon("02:50:F2:70:83:00","B",-35,100,200), new Beacon("DE:3A:45:2D:48:1F", "C",-35,200,0)));
    private int count = 0;

    public ReceiverSocket(int portNumber, Socket client) throws IOException {
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
                    System.out.println(length);
                    dIn.readFully(data, 0, data.length);
                    setBeacons(data);
                    var location = applyKalmanFilter();
                }
            }
        } catch (IOException e) {
            System.out.println("CONNECTION RESET");
        }
    }
    private void setBeacons(byte[] arr){
        if (this.beacons.isEmpty()){
            var beaconData = new byte[21];
            for (int i = 0; i < beaconData.length ; i+= beaconData.length / 3) {
                int rssi = arr[i];
                byte[] macAddressInBytes = new byte[6];
                int count = 0;
                for (int j = i + 1; j < i + (beaconData.length / 3) ; j++) {
                    macAddressInBytes[count] = arr[j];
                    count++;
                }
                System.out.println(decodeMacAddress(macAddressInBytes));
                var beacon = getBeaconByMac(decodeMacAddress(macAddressInBytes));
                if (beacon == null){
                    System.out.println("BEACON IS NOT DEFINED");
                    return;
                }
                var dataTag = new DataTagBLE(beacon, rssi);
                beacons.add(dataTag);
            }
            var realLocationData = new byte[8];
            var counter = 0;
            for (int i = beaconData.length; i < arr.length ; i++) {
                realLocationData[counter] = arr[i];
                counter++;
            }
            realLocationX = ByteBuffer.wrap(Arrays.copyOfRange(realLocationData, 0, 4)).getInt();
            realLocationY = ByteBuffer.wrap(Arrays.copyOfRange(realLocationData, 4, 8)).getInt();
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
    private byte[] applyKalmanFilter(){
        if (beacons.isEmpty()){
            return null;
        }
        byte[] result = new byte[2];
        var positions = new double[3][2];
        var distances = new double[3];
        System.out.println("Filtered RSSI Values Count = " + ++count);
        for (int i = 0; i < beacons.size(); i++) {
            var beacon = beacons.get(i);
            beacon.applyKalmanFilterToRssi();
            positions[i][0] = beacon.getTag().getLocationX();
            positions[i][1] = beacon.getTag().getLocationY();
            distances[i] = beacon.getDistanceFiltered();
        }
        var location = BeaconManager.getLocationWithTrilateration(positions,distances);
        result[0] = (byte)location[0];
        result[1] = (byte)location[1];
        System.out.println(location[0]);
        System.out.println(location[1]);

        return result;
    }
    public void stop(){
        execute = false;
    }
}

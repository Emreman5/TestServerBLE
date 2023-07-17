import Manager.BeaconManager;
import Models.Beacon;
import Models.DataTagBLE;
import Socket.ReceiverSocket;

import java.io.IOException;
import java.net.ServerSocket;

public class Test {
    public static void main(String[] args) {
        for (var item : BeaconManager.getLocationWithTrilateration()) {
            System.out.println(item);
        }

        serverApp();
    }
    private static void A(){
        Beacon b1 = new Beacon("A","B1",20);
        Beacon b2 = new Beacon("B","B2",25);
        Beacon b3 = new Beacon("C","B3",30);

        DataTagBLE dataTagBLE = new DataTagBLE(b1, 30);
        DataTagBLE dataTagBLE2 = new DataTagBLE(b2, 30);
        DataTagBLE dataTagBLE3 = new DataTagBLE(b3, 30);
        System.out.println(dataTagBLE.getDistanceFiltered());
        System.out.println(dataTagBLE.getDistance());
        System.out.println("------------------");
        dataTagBLE.setRSSI(30);
        System.out.println(dataTagBLE.getDistanceFiltered());
        System.out.println(dataTagBLE.getDistance());
        System.out.println("------------------");
        dataTagBLE.setRSSI(80);
        System.out.println(dataTagBLE.getDistanceFiltered());
        System.out.println(dataTagBLE.getDistance());
        System.out.println("------------------");
        dataTagBLE.setRSSI(10);
        System.out.println(dataTagBLE.getDistanceFiltered());

        System.out.println(dataTagBLE.getDistance());
        System.out.println("------------------");
        dataTagBLE.setRSSI(30);
        System.out.println(dataTagBLE.getDistanceFiltered());
        System.out.println(dataTagBLE.getDistance());
        System.out.println("------------------");
    }
    private static void serverApp(){
        int portNumber = 30;
        try {
            System.out.println("Listening on PORT : " + portNumber);
            var serverSocket = new ServerSocket(portNumber);
            while (true){
                new Thread(new ReceiverSocket(portNumber, serverSocket.accept())).start();
            }
        } catch (IOException e) {
            System.out.println("SERVER ERROR");
            throw new RuntimeException(e);
        }
    }
}

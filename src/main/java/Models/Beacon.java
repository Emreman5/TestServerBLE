package Models;
//TODO
// MAP INTEGRASYONU YAPACALAK

public class Beacon {
    public static final String FIELD_ID = "id";
    public static final String FIELD_MAC = FIELD_ID;
    private String id;
    private String mName;
    private int locationX;
    private int locationY;


    private int mTxPower;

    public Beacon(){}

    public Beacon(String mac, String name, int txPower){
        id = mac;
        mName = name;
        mTxPower = txPower;
    }

    public Beacon(Beacon tag){
        id = tag.getMAC();
        mName = tag.getName();
        mTxPower = tag.getTxPower();
    }

    public String getId(){
        return id;
    }

    public String getMAC() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public int getTxPower() {
        return mTxPower;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setTxPower(int txPower) {
        mTxPower = txPower;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public double getDistance(int rssi){
        return getDistance((double) rssi);
    }

    public double getDistance(double rssi){
        return Math.pow(10, ((mTxPower - rssi) * 1.0) / 20);
    }

    @Override
    public String toString() {
        return "TagBLE{" +
                "id='" + id + '\'' +
                ", mName='" + mName + '\'' +
                ", mTxPower=" + mTxPower +
                '}';
    }

}
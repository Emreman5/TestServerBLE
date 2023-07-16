package Models;

import Filter.KalmanFilter;

import java.util.Date;
import java.util.UUID;

public class DataTagBLE {

    // Field Names
    public static final String FIELD_ID = "id";

    // Kalman R & Q
    private static final double KALMAN_R = 0.125d;
    private static final double KALMAN_Q = 0.5d;


    private String id;
    private Beacon mTag;
    private Date mAcquired;

    public int mRSSI;

    public double mFilteredRSSI;

    private KalmanFilter mKalmanFilter;

    private double mDistance;

    public DataTagBLE() {
    }

    public DataTagBLE(Beacon tag) {
        id = UUID.randomUUID().toString();
        mTag = tag;
        mAcquired = new Date();
        mRSSI = 0x0;
        mFilteredRSSI = 0x0;
        mKalmanFilter = new KalmanFilter(KALMAN_R, KALMAN_Q);
    }

    public DataTagBLE(Beacon tag, int rssi) {
        id = UUID.randomUUID().toString();
        mTag = tag;
        mAcquired = new Date();
        mRSSI = rssi;
        mKalmanFilter = new KalmanFilter(KALMAN_R, KALMAN_Q);
        applyKalmanFilterToRssi();
    }


    public Beacon getTag() {
        return mTag;
    }

    public Date getAcquired() {
        return mAcquired;
    }

    public int getRSSI() {
        return mRSSI;
    }

    public double getFilteredRSSI() {
        return this.mFilteredRSSI;
    }

    public KalmanFilter getKalmanFilter() {
        return mKalmanFilter;
    }

    public void setTag(Beacon tag) {
        mTag = tag;
    }

    public void setAcquired(Date acquired) {
        this.mAcquired = acquired;
    }

    public void setRSSI(int rssi) {
        mRSSI = rssi;
        applyKalmanFilterToRssi();
    }

    public void setFilteredRSSI(int rssi) {
        this.mFilteredRSSI = rssi;
    }

    public void setKalmanFilter(KalmanFilter kalmanFilter) {
        this.mKalmanFilter = kalmanFilter;
    }


    public String getTagMac() {
        if (mTag != null) {
            return mTag.getMAC();
        } else {
            return null;
        }
    }


    public void setTagNameAndTxPower(String tagName, int txPower) {
        if (mTag != null) {
            mTag.setName(tagName);
            mTag.setTxPower(txPower);
        }
    }

    public void generateNewID() {
        id = UUID.randomUUID().toString();
    }

    public void onNewDataTagAcquired(DataTagBLE dataTagFound) {
        setRSSI(dataTagFound.getRSSI());
        applyKalmanFilterToRssi();
        Beacon tagFound = dataTagFound.getTag();
        if (tagFound != null) {
            setTagNameAndTxPower(tagFound.getName(), tagFound.getTxPower());
        }
        setAcquired(new Date());
    }


    public double getDistanceFiltered() {
        return mTag.getDistance(mFilteredRSSI);
    }

    public double getDistance() {
        return mTag.getDistance(mRSSI);
    }


    public void applyKalmanFilterToRssi() {
        mFilteredRSSI = mKalmanFilter.applyFilter(mRSSI);
    }

    @Override
    public String toString() {
        return "DataTagBLE{" +
                "id='" + id + '\'' +
                ", mTag=" + mTag +
                ", mAcquired=" + mAcquired +
                ", mRSSI=" + mRSSI +
                ", mFilteredRSSI=" + mFilteredRSSI +
                ", mKalmanFilter=" + mKalmanFilter +
                ", mDistance=" + mDistance +
                '}';
    }
}



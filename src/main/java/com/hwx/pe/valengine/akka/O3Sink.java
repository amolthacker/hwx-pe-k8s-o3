package com.hwx.pe.valengine.akka;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneClientFactory;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class O3Sink {

    private static OzoneClient ozClient = null;
    private static ObjectStore ozStore = null;
    private static String omHost = "om-0.om";
    private static final String OZONE_USER = "oz";

    //private AtomicBoolean initialized = new AtomicBoolean();

    private static final Logger LOG = LoggerFactory.getLogger(O3Sink.class);

    private O3Sink(){}

    private static class Holder {
        static final O3Sink INSTANCE = new O3Sink();
    }

    public static O3Sink instance(String om) throws IOException {
        omHost = om;
        ozClient = OzoneClientFactory.getRpcClient(omHost);
        ozStore = ozClient.getObjectStore();
        return Holder.INSTANCE;
    }

    /*
    public void init(String volume) throws IOException {
        try {
            if(initialized.compareAndSet(false, true)){
                LOG.info("Initializing O3 Sink at {} ...", omHost);
                LOG.info("Creating S3 buckets ...");
                for (ValMetric metric : ValMetric.values()){
                    ozStore.createS3Bucket(OZONE_USER, lower(metric));
                }
            }
        } catch(IOException ioe) {
            LOG.error("Cannot initiate O3 Sink - {}", ioe.getMessage());
        }
    }
    */

    public void info(){
        /*
        if(!initialized.get()){
            LOG.warn("O3 not yet initialized with buckets");
            return;
        }
        */
        try{
                LOG.info("Ozone Volume(s) and Buckets");
                Iterator<? extends OzoneVolume> volIter = ozStore.listVolumesByUser(OZONE_USER, null, null);
                if(volIter != null){
                    while(volIter.hasNext()){
                        OzoneVolume ozVol = volIter.next();
                        LOG.info("Ozone Volume: {}", ozVol.getName());
                        Iterator<? extends OzoneBucket> bktIter = ozVol.listBuckets(null);
                        if(bktIter != null){
                            while(bktIter.hasNext()){
                                OzoneBucket ozBkt = bktIter.next();
                                LOG.info("Ozone Bucket for Volume {}: {}",ozVol.getName(), ozBkt.getName());
                            }
                        }
                    }
                }
                LOG.info("Ozone S3 Buckets");
                Iterator<? extends OzoneBucket> s3BktIter = ozStore.listS3Buckets(OZONE_USER, null);
                if(s3BktIter != null){
                    while(s3BktIter.hasNext()){
                        OzoneBucket ozS3Bkt = s3BktIter.next();
                        LOG.info("Ozone S3 Bucket: {}", ozS3Bkt.getName());
                    }
                }
                /*
                LOG.info("Ozone S3 Bucket -> Ozone Bucket Mapping");
                for (ValMetric metric : ValMetric.values()){
                    String s3BucketName = lower(metric);
                    LOG.info("[{} -> {}|{}]", s3BucketName, ozStore.getOzoneVolumeName(s3BucketName), ozStore.getOzoneBucketName(s3BucketName));
                }
                */
        } catch(IOException ioe){
            LOG.error(ioe.getMessage());
        }
    }

    public void write(String bucket, String key, String value) {
        try {    
            OzoneBucket ozBucket = getBucket(bucket);
            if(ozBucket == null){
                LOG.warn("Bucket {} not found ... returning");
                return;
            }
            String ozBucketName = ozBucket.getName();
            LOG.info("Writing [{} -> {}] to bucket {} ...", key, value, ozBucketName);
            OzoneOutputStream oos = ozBucket.createKey(key, value.getBytes().length, ReplicationType.STAND_ALONE, ReplicationFactor.ONE, new HashMap<>());
            //OzoneOutputStream oos = ozBucket.createKey(key, value.getBytes().length);
            oos.write(value.getBytes());
            oos.flush();
            oos.close();
            // verify
            try {
                Thread.sleep(100);
            } catch(InterruptedException ie){}
            byte[] valueBytes = new byte[value.getBytes().length];
            ozBucket.readKey(key).read(valueBytes);
            LOG.info("Saved - [{} -> {}] to bucket {}", ozBucket.getKey(key).getName(), new String(valueBytes), ozBucketName);
        } catch (IOException ioe) {
            LOG.error("Failed writing to O3 - {}", ioe.getMessage());
        }
    }

    public void write(Trade trade){
        String bucket = trade.getValuation().getValMetric().toString().toLowerCase();
        String key = bucket  + "_" + trade.getTradeId() + "_" + trade.getExecTime() + ".json";
        String value = trade.toString();
        //write(bucket, key, value);
        write("valuations", key, value);
    }

    private void write2(Trade trade){
        String bucket = trade.getValuation().getValMetric().toString().toLowerCase();
        String filenamePfx = bucket  + "_" + trade.getTradeId() + "_" + trade.getExecTime();
        String filename = filenamePfx + ".json";
        
        File file = null;
        Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        try{
            file = new File(filename);
            gson.toJson(trade, new FileWriter(file));
        } catch(IOException ioe) {
            LOG.error("Failed to write to file {}", filename);
        }
    }

    public void shutdown(){
        if(ozClient != null){
            try {
                ozClient.close();
            } catch (IOException ioe) {
                LOG.error("Error closing OzoneClient - {}", ioe.getMessage());
            }
        }
    }

    /*
    private String lower(ValMetric metric){
        return metric.toString().toLowerCase();
    }
    private OzoneVolume ozVolFromS3BktName(String s3BktName) throws IOException {
        return ozStore.getVolume(ozStore.getOzoneVolumeName(s3BktName));
    }
    private OzoneBucket ozBktFromS3BktName(String s3BktName) throws IOException {
        return ozVolFromS3BktName(s3BktName).getBucket(s3BktName);
    }
    */

    private OzoneBucket getBucket(String bucketName) throws IOException {
        OzoneBucket ozoneBucket = null;
        Iterator<? extends OzoneBucket> iter = ozStore.listVolumesByUser(OZONE_USER, null, null).next().listBuckets(null);
        while(iter.hasNext()){
            OzoneBucket ozBucket = iter.next();
            if(ozBucket.getName().equals(bucketName)) return ozBucket;
        }
        return ozoneBucket;
    }

}
package com.hwx.pe.valengine.akka;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
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
    private static String o3Bucket = "valuations";
    private static final String OZONE_USER = "oz";

    private static final Logger LOG = LoggerFactory.getLogger(O3Sink.class);

    private O3Sink(){}

    private static class Holder {
        static final O3Sink INSTANCE = new O3Sink();
    }

    public static O3Sink instance(String om, String bucket) throws IOException {
        omHost = om;
        o3Bucket = bucket;
        ozClient = OzoneClientFactory.getRpcClient(omHost);
        ozStore = ozClient.getObjectStore();
        return Holder.INSTANCE;
    }

    public void info(){
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
        } catch(IOException ioe){
            LOG.error(ioe.getMessage());
        }
    }

    public void write(String bucket, String key, String value, boolean verify) {
        try {    
            OzoneBucket ozBucket = getBucket(bucket);
            if(ozBucket == null){
                LOG.error("Bucket {} not found ... returning");
                return;
            }
            String ozBucketName = ozBucket.getName();
            LOG.info("Writing [{} -> {}] to bucket {} ...", key, value, ozBucketName);
            OzoneOutputStream oos = ozBucket.createKey(key, value.getBytes().length, ReplicationType.STAND_ALONE, ReplicationFactor.ONE, new HashMap<>());
            oos.write(value.getBytes());
            oos.flush();
            oos.close();
            // optionally verify
            if(verify){
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ie){}
                byte[] valueBytes = new byte[value.getBytes().length];
                ozBucket.readKey(key).read(valueBytes);
                LOG.info("Saved - [{} -> {}] to bucket {}", ozBucket.getKey(key).getName(), new String(valueBytes), ozBucketName);
            }
        } catch (IOException ioe) {
            LOG.error("Failed writing to O3 - {}", ioe.getMessage());
        }
    }

    public void write(String bucket, String key, String value) {
        write(o3Bucket, key, value, true);
    }

    public void write(final Trade trade){
        String metric = trade.getValuation().getValMetric().toString().toLowerCase();
        String key = metric  + "_" + trade.getTradeId() + "_" + trade.getExecTime() + ".json";
        String value = trade.toString();
        write(o3Bucket, key, value);
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
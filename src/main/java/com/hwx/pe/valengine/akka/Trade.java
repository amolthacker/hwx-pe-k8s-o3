package com.hwx.pe.valengine.akka;

import java.util.Random;

import com.hwx.pe.valengine.akka.Policy.Desk;
import com.hwx.pe.valengine.akka.Policy.Region;

public class Trade {

    private final int tradeId;
    private final TradeType tradeType;
    private final Valuation valuation;
    private final Policy policy;
    private final long execTime;

    public Trade(int tradeId, TradeType tradeType, Valuation valuation, Policy policy, long execTime){
        this.tradeId = tradeId;
        this.tradeType = tradeType;
        this.valuation = valuation;
        this.policy = policy;
        this.execTime = execTime;
    }

    public Trade(String jobId, ValMetric valMetric, double dealValue, int numDeals, long valTimeMillis){
        this(new Random().nextInt(1000000), 
             valMetric.equals(ValMetric.FwdRate) ? TradeType.FRA : (valMetric.equals(ValMetric.NPV) ? TradeType.SWAP : TradeType.OPTION), 
             new Valuation(jobId, valMetric, dealValue, numDeals, (dealValue *numDeals), valTimeMillis),
             new Policy(Region.getRandom(), Desk.getRandom()), 
             System.currentTimeMillis());
    }

    enum TradeType {
        FRA,
        SWAP,
        OPTION
    }

    public int getTradeId(){
        return this.tradeId;
    }

    public TradeType getTradeType(){
        return this.tradeType;
    }

    public Valuation getValuation() {
        return this.valuation;
    }

    public Policy getPolicy(){
        return this.policy;
    }

    public long getExecTime() {
        return this.execTime;
    }

    @Override
    public String toString(){
        return new com.google.gson.Gson().toJson(this);
    }

}
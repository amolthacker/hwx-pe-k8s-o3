package com.hwx.pe.valengine.akka;

public class Valuation {

    private final String valuationId;
    private final ValMetric valMetric;
    private final double dealValue;
    private final int numDeals;
    private final double tradeValue;
    private final long valTimeMillis;

    public Valuation(String valuationId, ValMetric valMetric, Double dealValue, Integer numDeals, double tradeValue, long valTimeMillis){
        this.valuationId = valuationId;
        this.valMetric = valMetric;
        this.dealValue = dealValue;
        this.numDeals = numDeals;
        this.tradeValue = tradeValue;
        this.valTimeMillis = valTimeMillis;
    }

    public String getValuationId() {
        return this.valuationId;
    }

    public ValMetric getValMetric() {
        return this.valMetric;
    }

    public double getDealValue() {
        return this.dealValue;
    }

    public double getNumDeals() {
        return this.numDeals;
    }

    public double getTradeValue() {
        return this.tradeValue;
    }

    public long getValTimeMillis() {
        return this.valTimeMillis;
    }

    @Override
    public String toString(){
        return new com.google.gson.Gson().toJson(this);
    }
}
package com.hwx.pe.valengine.akka;

public class Policy{

    enum Region {
        NA,
        EMEA,
        APAC;

        public static Region getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    enum Desk {
        IFI,
        MACRO,
        SHORT_TERM;

        public static Desk getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    
        private final Region region;
        private final Desk desk;

        public Policy(Region region, Desk desk){
            this.region = region;
            this.desk = desk;
        }

        Region  geRegion(){
            return this.region;
        }

        Desk getDesk(){
            return this.desk;
        }

}
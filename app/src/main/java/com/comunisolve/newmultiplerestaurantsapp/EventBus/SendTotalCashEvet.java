package com.comunisolve.newmultiplerestaurantsapp.EventBus;

public class SendTotalCashEvet {

    private String cash;

    public SendTotalCashEvet(String cash) {
        this.cash = cash;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}

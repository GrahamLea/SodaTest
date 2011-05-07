package org.sodatest.api.java.reflection.test;

import scala.Option;

import java.math.BigDecimal;

public class EventWithSetters extends EventWithPublicFieldsSuperclass {

    private Amount amount;
    private Amount anotherAmount;
    private BigDecimal bigDecimal;
    private Option<String> stringOptionOne;
    private Option<String> stringOptionTwo;

    @Override
    protected void executeEvent() {
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public void setAnotherAmount(Amount anotherAmount) {
        this.anotherAmount = anotherAmount;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public void setStringOptionOne(Option<String> stringOptionOne) {
        this.stringOptionOne = stringOptionOne;
    }

    public void setStringOptionTwo(Option<String> stringOptionTwo) {
        this.stringOptionTwo = stringOptionTwo;
    }

    public Amount amount() {
        return amount;
    }

    public Amount anotherAmount() {
        return anotherAmount;
    }

    public BigDecimal bigDecimal() {
        return bigDecimal;
    }

    public Option<String> stringOptionOne() {
        return stringOptionOne;
    }

    public Option<String> stringOptionTwo() {
        return stringOptionTwo;
    }
}


package org.sodatest.api.java.reflection.test;

import org.sodatest.coercion.java.CoercionRegisterForJava;
import scala.Option;

import java.math.BigDecimal;

public class EventWithSetters extends EventWithPublicFieldsSuperclass {

    private final CoercionRegisterForJava coercionRegister = new CoercionRegisterForJava(new CustomStringCoercion());

    private Amount amount;
    private Amount anotherAmount;
    private BigDecimal bigDecimal;
    private Option<String> stringOptionOne;
    private Option<String> stringOptionTwo;
    private CustomString stringNeedingCoercion;

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

    public void setStringNeedingCoercion(CustomString stringNeedingCoercion) {
        this.stringNeedingCoercion = stringNeedingCoercion;
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

    public CustomString stringNeedingCoercion() {
        return stringNeedingCoercion;
    }
}


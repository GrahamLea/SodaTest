package org.sodatest.coercion.java;

import org.sodatest.coercion.CurrencyAmountCoercion;
import org.sodatest.coercion.CurrencyAmountCoercion$;
import scala.collection.JavaConversions;

import java.util.Collection;

/**
 * TODO: Document
 */
public class CurrencyAmountCoercionForJava<T> extends CurrencyAmountCoercion<T> {

    public CurrencyAmountCoercionForJava(Class<T> targetClass) {
        super(targetClass, CurrencyAmountCoercion$.MODULE$.patternsForSymbol('$'));
    }

    public CurrencyAmountCoercionForJava(Class<T> targetClass, Collection<String> decimalFormatPatterns) {
        super(targetClass, JavaConversions.asScalaIterable(decimalFormatPatterns));
    }
}

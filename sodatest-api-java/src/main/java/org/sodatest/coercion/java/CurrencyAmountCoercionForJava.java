package org.sodatest.coercion.java;

import org.sodatest.coercion.CurrencyAmountCoercion;
import org.sodatest.coercion.CurrencyAmountCoercion$;
import scala.collection.JavaConversions;

import java.util.Collection;
import java.util.List;

/**
 * Coerces strings that are currency amounts (e.g. "($25,000.00)") to a specified strong type for
 * storing such amounts.
 *
 * <b>Example</b>
 * {{{
 * public class MySodaEvent extends ReflectiveSodaEventForJava {
 *    var coercionRegister = new CoercionRegisterForJava(new CurrencyAmountCoercionForJava(classOf[MyMoneyClass]))
 *
 *    ...
 * }}}
 *
 * The target class for the Coercion must provide a public, one-parameter constructor that accepts
 * either a {@link scala.math.BigDecimal}, a {@link java.math.BigDecimal} or a String.
 *
 * By default, the patterns accepted by the coercion are:
 * #,##0.00
 * -#,##0.00
 * (#,##0.00)
 * $#,##0.00
 * -$#,##0.00
 * $-#,##0.00
 * ($#,##0.00)
 * $(#,##0.00)
 *
 * You can, if necessary, specify the patterns to be used by passing into the constructor the
 * decimalFormatPatterns parameter, which must be a list of patterns acceptable to
 * {@link java.text.DecimalFormat}.
 *
 * The {@link PatternFactory} class provides functions to aid in the construction of such pattern lists.
 *
 * @see PatternFactory
 * @see java.text.DecimalFormat
 * @see CurrencyAmountCoercion
 */
public class CurrencyAmountCoercionForJava<T> extends CurrencyAmountCoercion<T> {

    public CurrencyAmountCoercionForJava(Class<T> targetClass) {
        this(targetClass, PatternFactory.currencyPatterns());
    }

    public CurrencyAmountCoercionForJava(Class<T> targetClass, Collection<String> decimalFormatPatterns) {
        super(targetClass, JavaConversions.asScalaIterable(decimalFormatPatterns));
    }

    /**
     * A class of static methods to aid in the construction of lists of DecimalFormat patterns for
     * use with the {@link CurrencyAmountCoercionForJava}.
     *
     * The {@link #currencyPatterns()} function uses defaults for all parameters and produces the
     * following patterns:
     * <ul>
     * <li>#,##0.00</li>
     * <li>-#,##0.00</li>
     * <li>(#,##0.00)</li>
     * <li>$#,##0.00</li>
     * <li>-$#,##0.00</li>
     * <li>$-#,##0.00</li>
     * <li>($#,##0.00)</li>
     * <li>$(#,##0.00)</li>
     * </ul>
     *
     * Using the other variations of the functions allows you to replace the dollar sign (currencySymbol),
     * the comma (thousandSeparator), the period (decimalSeparator) and the number of decimal places
     * (numberOfDecimalPlaces), respectively. If numberOfDecimalPlaces is 0, the decimalSeparator
     * will not be used.
     */
    public static final class PatternFactory {
        public static Collection<String> currencyPatterns() {
            return currencyPatterns('$', ',', '.', 2);
        }

        public static Collection<String> currencyPatterns(char currencySymbol) {
            return currencyPatterns(currencySymbol, ',', '.', 2);
        }

        public static Collection<String> currencyPatterns(
                char currencySymbol, char thousandSeparator, char decimalSeparator) {
            return currencyPatterns(currencySymbol, thousandSeparator, decimalSeparator, 2);
        }

        public static Collection<String> currencyPatterns(
                char currencySymbol, char thousandSeparator, char decimalSeparator, int numberOfDecimalPlaces) {
            return JavaConversions.asJavaCollection(
                    CurrencyAmountCoercion$.MODULE$.currencyPatterns(
                        currencySymbol, thousandSeparator, decimalSeparator, numberOfDecimalPlaces));
        }
    }
}

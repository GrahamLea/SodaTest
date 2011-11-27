// Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
package org.sodatest.coercion.java;

import org.junit.Test;
import org.sodatest.coercion.Coercion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoercionForJavaTest {
    @Test
    public void shouldBeUseableByCoercionRegister() throws Exception {
        CoercionForJava coercionForJava = new CoercionForJava<TestTargetClass>(TestTargetClass.class) {
            @Override
            public TestTargetClass apply(String s) {
                return new TestTargetClass(s + "-TEST");
            }
        };
        CoercionRegisterForJava coercionRegisterForJava = new CoercionRegisterForJava(coercionForJava);
        TestTargetClass result = (TestTargetClass) Coercion.coerce("foo", TestTargetClass.class, coercionRegisterForJava);
        assertThat(result.value, is("foo-TEST"));
    }

    public static final class TestTargetClass {
        private final String value;

        public TestTargetClass(String value) {
            this.value = value;
        }
    }
}

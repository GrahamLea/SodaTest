// Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
package org.sodatest.coercion.java;

import org.sodatest.coercion.Coercion;
import scala.reflect.Manifest;

public abstract class CoercionForJava<T> extends Coercion<T> {
    @SuppressWarnings({"unchecked"})
    public CoercionForJava(Class<T> targetClass) {
        super((Manifest) scala.reflect.Manifest$.MODULE$.classType(targetClass));
    }

    @Override
    public abstract T apply(String s);
}

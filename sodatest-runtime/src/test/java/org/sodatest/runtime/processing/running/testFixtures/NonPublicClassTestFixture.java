// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.running.testFixtures;

import org.sodatest.api.SodaEvent;
import org.sodatest.api.SodaFixture;
import org.sodatest.api.SodaReport;

import scala.Option;

class NonPublicClassTestFixture implements SodaFixture {

    public NonPublicClassTestFixture() { }
    
    public Option<SodaEvent> createEvent(String name) {
        return null;
    }

    public Option<SodaReport> createReport(String name) {
        return null;
    }
}

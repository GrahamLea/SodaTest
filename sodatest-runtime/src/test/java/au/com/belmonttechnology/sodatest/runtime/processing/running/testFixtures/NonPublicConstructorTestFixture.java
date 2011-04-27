//  Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.running.testFixtures;

import au.com.belmonttechnology.sodatest.api.SodaEvent;
import au.com.belmonttechnology.sodatest.api.SodaFixture;
import au.com.belmonttechnology.sodatest.api.SodaReport;

import scala.Option;

public class NonPublicConstructorTestFixture implements SodaFixture {

    NonPublicConstructorTestFixture() { }
    
    public Option<SodaEvent> createEvent(String name) {
        return null;
    }

    public Option<SodaReport> createReport(String name) {
        return null;
    }
}

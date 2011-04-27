// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.data.results

import au.com.belmonttechnology.sodatest.runtime.data.SodaTest

class SodaTestResult(val test: SodaTest, val results: List[BlockResult[_]], val passed: Boolean)
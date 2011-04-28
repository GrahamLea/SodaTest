// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.data.results

import org.sodatest.runtime.data.SodaTest

class SodaTestResult(val test: SodaTest, val results: List[BlockResult[_]], val passed: Boolean)
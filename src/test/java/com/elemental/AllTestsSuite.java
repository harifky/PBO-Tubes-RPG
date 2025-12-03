package com.elemental;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Elemental Battle Arena
 * Runs all unit tests in the project
 */
@Suite
@SuiteDisplayName("Elemental Battle Arena Test Suite")
@SelectPackages({
    "com.elemental.model",
    "com.elemental.factory",
    "com.elemental.service",
    "com.elemental.decorator"
})
public class AllTestsSuite {
    // This class remains empty, used only as a holder for the above annotations
}


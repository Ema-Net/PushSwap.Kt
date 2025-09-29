package tests

import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(PermutationTest::class, SmallSortTest::class)
class MainSuite
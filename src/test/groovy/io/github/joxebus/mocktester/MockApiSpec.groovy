package io.github.joxebus.mocktester

import spock.lang.Specification

class MockApiSpec extends Specification {

    def "Sample test" () {
        expect:
        12 == 7+5
    }
}

package io.github.joxebus.mockapi.controller

import io.github.joxebus.mockapi.util.FileUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigurationControllerSpec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    @Value('${file.upload.folder}')
    private String fileUploadFolder

    def setup(){
        FileUtil.cleanFolder(fileUploadFolder)
    }

    def "Test create a configuration"() {
        given: "A configuration file in json format"
        String jsonApiConfig = FileUtil.getTextFromFile(fileName)

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)

        when: "The configuration is created"
        def entity = testRestTemplate.postForEntity("/config", data, Map)

        then:
        entity.statusCode               == HttpStatus.CREATED
        entity.body.config              == configName
        entity.body.endpoints.size()    == numberOfEndpoints

        where:
        fileName                        |   configName              |   numberOfEndpoints
        "configuration_secured.json"    |   "/config/secured-api"   |   4
        "configuration_unsecured.json"  |   "/config/unsecured-api" |   4

    }

    def "Test config/apiName returns the API configuration available"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_secured.json")

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)

        then:
        entity.statusCode == HttpStatus.OK
        entity.body.name == "secured-api"
        entity.body.description == "This is sample API to mock REST endpoint"
        entity.body.termsOfService == "http://example.com/terms/"
        entity.body.version == "1.0.1"
        entity.body.contact.name == "API Support"
        entity.body.contact.url == "http://www.example.com/support"
        entity.body.contact.email == "support@example.com"
        entity.body.license.name == "Apache 2.0"
        entity.body.license.url == "https://www.apache.org/licenses/LICENSE-2.0.html"
        entity.body.secured == true
        entity.body.authConfig == "am94ZWJ1czpNb2NrQVBJ"
        entity.body.paths.size() == 4
    }

    def "Verify config/apiName is not configured"() {
        given:

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)
        println entity

        then:
        entity.statusCode == HttpStatus.NOT_FOUND
        entity.body.message == "The configuration [secured-api] does not exist."

    }

}

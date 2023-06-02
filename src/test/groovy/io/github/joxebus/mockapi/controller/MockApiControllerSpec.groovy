package io.github.joxebus.mockapi.controller

import io.github.joxebus.mockapi.util.FileUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockApiControllerSpec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    @Value('${file.upload.folder}')
    private String fileUploadFolder

    def setup(){
        FileUtil.cleanFolder(fileUploadFolder)
    }

    def "Test api/unsecured-api"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_unsecured.json")
        String baseAPI = "/api/unsecured-api"

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def glossary = testRestTemplate.exchange("${baseAPI}/GLOSSARY", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)
        def menu = testRestTemplate.exchange("${baseAPI}/MENU", HttpMethod.PUT, new HttpEntity<>(requestHeaders), Map)
        def createPerson = testRestTemplate.postForEntity("${baseAPI}/CREATE_PERSON", new HttpEntity<>(requestHeaders), Map)
        def customError = testRestTemplate.exchange("${baseAPI}/FAILURE", HttpMethod.DELETE, new HttpEntity<>(requestHeaders), Map)


        then:
        glossary.statusCode == HttpStatus.OK
        glossary.body.ID == "SGML"
        glossary.body.SortAs == "SGML"
        glossary.body.GlossTerm == "Standard Generalized Markup Language"
        glossary.body.Acronym == "SGML"
        glossary.body.Abbrev == "ISO 8879:1986"
        glossary.body.GlossSee == "markup"
        glossary.body.GlossDef.para == "A meta-markup language, used to create markup languages such as DocBook."
        glossary.body.GlossDef.GlossSeeAlso == ["GML", "XML"]

        menu.statusCode == HttpStatus.ACCEPTED
        menu.body.menu.id == "file"
        menu.body.menu.value == "File"
        menu.body.menu.popup.menuitem.size() == 3

        createPerson.statusCode == HttpStatus.CREATED

        customError.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        customError.body.message == "This is a custom error configured on the Mock API"
    }

    def "Test api/secured-api only returns values when receive Authorization"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_secured.json")
        String baseAPI = "/api/secured-api"

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        requestHeaders.add("Authorization", "am94ZWJ1czpNb2NrQVBJ")
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def glossary = testRestTemplate.exchange("${baseAPI}/GLOSSARY", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)
        def menu = testRestTemplate.exchange("${baseAPI}/MENU", HttpMethod.PUT, new HttpEntity<>(requestHeaders), Map)
        def createPerson = testRestTemplate.postForEntity("${baseAPI}/CREATE_PERSON", new HttpEntity<>(requestHeaders), Map)
        def customError = testRestTemplate.exchange("${baseAPI}/FAILURE", HttpMethod.DELETE, new HttpEntity<>(requestHeaders), Map)

        then:
        glossary.statusCode == HttpStatus.OK
        glossary.body.ID == "SGML"
        glossary.body.SortAs == "SGML"
        glossary.body.GlossTerm == "Standard Generalized Markup Language"
        glossary.body.Acronym == "SGML"
        glossary.body.Abbrev == "ISO 8879:1986"
        glossary.body.GlossSee == "markup"
        glossary.body.GlossDef.para == "A meta-markup language, used to create markup languages such as DocBook."
        glossary.body.GlossDef.GlossSeeAlso == ["GML", "XML"]

        menu.statusCode == HttpStatus.ACCEPTED
        menu.body.menu.id == "file"
        menu.body.menu.value == "File"
        menu.body.menu.popup.menuitem.size() == 3

        createPerson.statusCode == HttpStatus.CREATED

        customError.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        customError.body.message == "This is a custom error configured on the Mock API"
    }

    def "Test api/secured-api not allow request without Authorization"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_secured.json")
        String baseAPI = "/api/secured-api"

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def glossary = testRestTemplate.exchange("${baseAPI}/GLOSSARY", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)

        then:
        glossary.statusCode == HttpStatus.UNAUTHORIZED
        glossary.body.message == "UNAUTHORIZED missing or wrong auth info for [GLOSSARY]"
    }
}

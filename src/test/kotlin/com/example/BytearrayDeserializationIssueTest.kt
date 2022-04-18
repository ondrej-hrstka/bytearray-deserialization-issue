package com.example

import com.fasterxml.jackson.databind.node.JsonNodeCreator
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.jackson.core.tree.TreeGenerator
import io.micronaut.jackson.databind.JacksonDatabindMapper
import io.micronaut.json.tree.JsonNode
import io.micronaut.kotlin.http.retrieveObject
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@MicronautTest
class BytearrayDeserializationIssueTest(
    @Client("/") private val client: HttpClient
) {

    //language=JSON
    private val payload = """
     {
       "content": "YWJj"
     }
    """.trimIndent() //content is "abc" in base64

    @ParameterizedTest
    @ValueSource(strings = ["deserialization", "objectMapper", "jsonMapper"])
    fun `deserialization`(path: String) {
        val response = client.toBlocking().retrieveObject<String>(HttpRequest.POST(path, payload))
        assertEquals("abc", response)
    }

    @Test
    fun `serialization`() { //this passes
        val response = client.toBlocking().retrieve("/serialization")
        assertEquals("""{"content":"YWJj"}""", response)
    }

}

@MicronautTest
class JacksonDatabindMapperTest(
    private val jacksonDatabindMapper: JacksonDatabindMapper
) {

    @Test
    fun test() {
        val jsonNode = JsonNode.createObjectNode(linkedMapOf("content" to JsonNode.createStringNode("YWJj")))
        val readValue = jacksonDatabindMapper.readValueFromTree(jsonNode, TestDto::class.java)
        assertEquals(TestDto(byteArrayOf(97,98,99)), readValue)
    }

}
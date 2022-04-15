package com.example

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.core.type.Argument
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.json.JsonMapper
import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.example")
        .start()
}

data class TestDto(
    val content: ByteArray
)

@Controller
class TestController(private val objectMapper: ObjectMapper, private val jsonMapper: JsonMapper) {

    @Post("deserialization")
    fun deserialization(@Body testDto: TestDto) = String(testDto.content)

    @Get("serialization")
    fun serialization() = TestDto("abc".toByteArray())


    @Post("objectMapper")
    fun objectMapper(@Body testDtoAsString: String): String {
        val testDto = objectMapper.readValue<TestDto>(testDtoAsString)
        return String(testDto.content)
    }

    @Post("jsonMapper")
    fun jsonMapper(@Body testDtoAsString: String): String {
        val testDto = jsonMapper.readValue(testDtoAsString, Argument.of(TestDto::class.java))
        return String(testDto.content)
    }
}

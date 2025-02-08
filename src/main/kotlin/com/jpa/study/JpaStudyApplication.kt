package com.jpa.study

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JpaStudyApplication

fun main(args: Array<String>) {
    runApplication<JpaStudyApplication>(*args)
}

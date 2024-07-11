package com.thirtytwonineteen.graql.lib.config

import graphql.GraphQL
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.time.Duration

@MicronautTest
class GraQLConfigurationPropertiesSpec(
    val beanContext: BeanContext,
    val graQLConfigurationProperties: GraQLConfigurationProperties
):BehaviorSpec({

    Given("a GraQL configuration") {
        When("we inspect graql configuration") {
            Then("we get our configuration") {
                graQLConfigurationProperties.schemaLocations.first() shouldBe  "classpath:custom-schema/schema.graphqls"
                graQLConfigurationProperties.queryCache.maxSize shouldBe 200
                graQLConfigurationProperties.queryCache.expireAfterAccess shouldBe Duration.ofMinutes(5)
            }
        }

        When("autowire is turned on") {
            Then("we get a graphQL bean automatically") {
                shouldNotThrow<Exception> {
                    beanContext.getBean(GraphQL::class.java)
                }
            }
        }
    }
})
package com.thirtytwonineteen.graql.lib.config

import graphql.GraphQL
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class GraQLConfigurationPropertiesSpec(
    val beanContext: BeanContext,
    val graQLConfigurationProperties: GraQLConfigurationProperties
):BehaviorSpec({

    Given("a GraQL configuration") {
        When("we inspect graql configuration") {
            Then("we get our configuration") {
                graQLConfigurationProperties.schemaLocations.first() shouldBe  "classpath:custom-schema/schema.graphqls"
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
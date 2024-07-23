package com.thirtytwonineteen.graql.lib

import com.thirtytwonineteen.graql.GraQL
import com.thirtytwonineteen.graql.lib.config.GraQLComponentScanner
import com.thirtytwonineteen.graql.lib.fixture.*
import graphql.schema.DataFetchingEnvironmentImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.instanceOf
import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class GraQLTest(
    private val graQL: GraQL,
    private val beanScanner: GraQLComponentScanner,
    private val beanContext: BeanContext
):BehaviorSpec({
    Given("a GraQLBeanScanner instance") {
        When("we ask for GraQL component definitions and resolve them to beans") {
            val resolvedComps = beanScanner.componentDefinitions.map{ beanContext.getBean( it ) }
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val notAFixtureComponent = beanContext.getBean(NotAGraQLFixtureComponent::class.java)

            Then("we don't find other beans") {
                resolvedComps shouldNotContain notAFixtureComponent
            }

            Then("we find our singleton fixture component") {
                resolvedComps shouldContain fixtureComponent
            }
        }

        When("we ask for GraQL query delegates we can find them and they have their correct names") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegates = graQL.queries

            Then("we find two annotated methods") {
                delegates.size shouldBe 3
            }
            Then("one should have a conventional name") {
                delegates.find{ it.name == "isAQuery" } shouldNotBe null
            }
            Then("one should have an explicit name") {
                delegates.find{ it.name == "isANamedQuery" } shouldNotBe null
            }
        }

        When("we look up a GraQL query delegate and run it with a DataFetchingEnvironment") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.queries.filter{ it.name == "isAQuery"}.first()
            val dfe = DataFetchingEnvironmentImpl
                .Builder()
                .arguments(
                    mutableMapOf<String, Any>(
                        "req" to mapOf("ding" to "bat")
                    )
                )
                .build()

            val res = delegate.get(dfe)

            Then("We get the appropriate response type") {
                res should beInstanceOf(GraQLFixtureResponseType::class)
            }
            Then("We get the correct response value") {
                (res as GraQLFixtureResponseType).dong shouldBe "BAT"
            }
        }

        When("we look up a GraQL query delegate with custom names and run it with a DataFetchingEnvironment") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.queries.filter{ it.name == "isANamedQueryWithInputName"}.first()
            val dfe = DataFetchingEnvironmentImpl
                .Builder()
                .arguments(
                    mutableMapOf<String, Any>(
                        "wackyKeyName" to mapOf("ding" to "bat")
                    )
                )
                .build()

            val res = delegate.get(dfe)

            Then("We get the appropriate response type") {
                res should beInstanceOf(GraQLFixtureResponseType::class)
            }
            Then("We get the correct response value") {
                (res as GraQLFixtureResponseType).dong shouldBe "BAT"
            }
        }
        
        When("we ask for GraQL mutation delegates we can find them and they have their correct names") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegates = graQL.mutations

            Then("we find two annotated methods") {
                delegates.size shouldBe 3
            }
            Then("one should have a conventional name") {
                delegates.find{ it.name == "isAMutation" } shouldNotBe null
            }
            Then("one should have an explicit name") {
                delegates.find{ it.name == "isANamedMutation" } shouldNotBe null
            }
        }

        When("we look up a GraQL mutation delegate and run it with a DataFetchingEnvironment") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.mutations.filter{ it.name == "isAMutation"}.first()
            val dfe = DataFetchingEnvironmentImpl
                .Builder()
                .arguments(
                    mutableMapOf<String, Any>(
                        "input" to mapOf("ding" to "bat")
                    )
                )
                .build()

            val res = delegate.get(dfe)

            Then("We get the appropriate response type") {
                res should beInstanceOf(GraQLFixtureResponseType::class)
            }
            Then("We get the correct response value") {
                (res as GraQLFixtureResponseType).dong shouldBe "BAT"
            }
        }

        When("we look up a GraQL mutation delegate with custom names and run it with a DataFetchingEnvironment") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.mutations.filter{ it.name == "isANamedMutationWithInputName"}.first()
            val dfe = DataFetchingEnvironmentImpl
                .Builder()
                .arguments(
                    mutableMapOf<String, Any>(
                        "wackyKeyName" to mapOf("ding" to "bat")
                    )
                )
                .build()

            val res = delegate.get(dfe)

            Then("We get the appropriate response type") {
                res should beInstanceOf(GraQLFixtureResponseType::class)
            }
            Then("We get the correct response value") {
                (res as GraQLFixtureResponseType).dong shouldBe "BAT"
            }
        }
        
        When("we look up a GraQL mapped data loader delegate and run it") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.mappedBatchLoaders.filter{ it.dataLoaderName == "things"}.first().createLoader()


            val stage = delegate.load(mutableSetOf(1, 2, 3, 4) as MutableSet<Any>)
            val stuff = stage.toCompletableFuture().join()

            Then("We get a result populated by the loader") {
                stuff.size shouldBe 4
            }

            val last = stuff.get(4.toLong())
            Then("We should get maps") {
                last shouldBe instanceOf<Map<Any,Any>>()
            }

            val lastEntry = last as Map<String, String>
            Then("And they have state") {
                lastEntry.get("name") shouldBe "Thing 4"
            }
        }

        When("we look up a named GraQL mapped data loader delegate and run it") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.mappedBatchLoaders.filter{ it.dataLoaderName == "otherThings"}.first().createLoader()


            val stage = delegate.load(mutableSetOf(1, 2, 3, 4) as MutableSet<Any>)
            val stuff = stage.toCompletableFuture().join()

            Then("We get a result populated by the loader") {
                stuff.size shouldBe 4
            }

            val last = stuff.get(4.toLong())
            Then("We should get maps") {
                last shouldBe instanceOf<Map<Any,Any>>()
            }

            val lastEntry = last as Map<String, String>
            Then("And they have state") {
                lastEntry.get("name") shouldBe "Thing 4"
            }
        }

        When("we look up a named-via-repetition GraQL mapped data loader delegate and run it") {
            val fixtureComponent = beanContext.getBean(GraQLFixtureComponent::class.java)
            val delegate = graQL.mappedBatchLoaders.filter{ it.dataLoaderName == "yetMoreThings"}.first().createLoader()


            val stage = delegate.load(mutableSetOf(1, 2, 3, 4) as MutableSet<Any>)
            val stuff = stage.toCompletableFuture().join()

            Then("We get a result populated by the loader") {
                stuff.size shouldBe 4
            }

            val last = stuff.get(4.toLong())
            Then("We should get maps") {
                last shouldBe instanceOf<Map<Any,Any>>()
            }

            val lastEntry = last as Map<String, String>
            Then("And they have state") {
                lastEntry.get("name") shouldBe "Thing 4"
            }
        }
    }

})

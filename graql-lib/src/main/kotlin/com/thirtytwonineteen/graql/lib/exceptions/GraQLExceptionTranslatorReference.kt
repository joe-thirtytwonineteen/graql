package com.thirtytwonineteen.graql.lib.exceptions

import graphql.GraphqlErrorBuilder
import java.lang.reflect.Method

class GraQLExceptionTranslatorReference(
    val target: Any,
    val method: Method
) {
    fun validateAndInvoke( t:Throwable, builder: GraphqlErrorBuilder<*> ) {
        method.invoke(target, t, builder)
    }
}
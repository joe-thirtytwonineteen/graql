package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import graphql.schema.DataFetcher

interface GraQLDelegatingQuery<T>:DataFetcher<T>, GraQLDelegate {
    val name:String
    val argumentName: String
    val requestType: Class<*>

}


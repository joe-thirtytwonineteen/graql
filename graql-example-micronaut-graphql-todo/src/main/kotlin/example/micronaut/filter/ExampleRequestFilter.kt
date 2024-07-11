package example.micronaut.filter

import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.RequestFilter
import io.micronaut.http.annotation.ServerFilter
import io.micronaut.core.propagation.MutablePropagatedContext

@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
class ExampleRequestFilter {

    companion object{
        var requests = 0L
    }

    @RequestFilter
    fun filter(req: HttpRequest<*>, ctx:MutablePropagatedContext ) {
        requests++
        ctx.add(
            ExamplePropagationContext(
                RequestInfo( requests )
            )
        )
    }
}


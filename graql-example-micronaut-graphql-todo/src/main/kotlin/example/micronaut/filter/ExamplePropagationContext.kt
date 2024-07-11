package example.micronaut.filter

import io.micronaut.core.propagation.ThreadPropagatedContextElement

class ExamplePropagationContext(val state:RequestInfo) : ThreadPropagatedContextElement<RequestInfo> {

    override fun updateThreadContext(): RequestInfo? {
        val oldState = RequestInfoThreadLocal.getValue()
        RequestInfoThreadLocal.setValue(state)
        return oldState
    }

    override fun restoreThreadContext(oldState: RequestInfo?) {
        if (oldState == null) {
            RequestInfoThreadLocal.unsetValue()
        } else {
            RequestInfoThreadLocal.setValue(oldState)
        }
    }
}

data class RequestInfo( val requestCount:Long) {
    val threadId: Long
    
    init {
        threadId = Thread.currentThread().id
    }
}

object RequestInfoThreadLocal {
    private val threadValue = ThreadLocal<RequestInfo>()

    fun setValue(v:RequestInfo) {
        threadValue.set(v)
    }

    fun unsetValue() {
        threadValue.remove()
    }

    fun getValue(): RequestInfo? {
        return threadValue.get()
    }
}
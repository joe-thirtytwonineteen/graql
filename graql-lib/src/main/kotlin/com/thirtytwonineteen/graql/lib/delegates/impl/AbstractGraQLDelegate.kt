package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.slf4j.LoggerFactory

abstract class AbstractGraQLDelegate(
    /*
     * Haven't found a good way around this yet. Not resorting to contexts/statics/holders/etc.
     */
    val exceptionHandler: GraQLGlobalExceptionHandler
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractGraQLDelegate::class.java)
    }

    fun withExceptionHandling( operation: ( () -> Any ), message: ( () -> String ) ) : Any? {
        try {
            return operation()
        } catch (ex: Throwable) {
            // If nobody's registered a handler for this exception, be kind to server-side folks: warn and log
            if ( !exceptionHandler.hasExceptionTranslatorFor( ex ) ) {
                LOG.warn(message(), ex)
            }

            // Ok, now let graphql-java do its thing.
            throw ex
        }
    }
}
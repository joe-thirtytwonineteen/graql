package com.thirtytwonineteen.graql.lib.event

import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLBeanScanner
import io.micronaut.context.event.BeanContextEvent

class GraQLScanningComplete( val results: GraQLBeanScanner.BeanScanningResults )
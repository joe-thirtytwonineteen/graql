package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import org.dataloader.BatchLoader

interface GraQLBatchLoaderFactory<K, V>: GraQLDelegate {
    fun createLoader():BatchLoader<K, V>
    val dataLoaderName:String
}


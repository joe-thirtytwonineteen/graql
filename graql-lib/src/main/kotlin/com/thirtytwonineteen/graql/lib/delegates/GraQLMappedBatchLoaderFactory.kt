package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import org.dataloader.MappedBatchLoader

interface GraQLMappedBatchLoaderFactory<K, V>: GraQLDelegate {
    fun createLoader():MappedBatchLoader<K, V>
    val dataLoaderName:String
}


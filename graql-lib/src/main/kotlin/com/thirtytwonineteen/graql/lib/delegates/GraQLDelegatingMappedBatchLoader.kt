package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import org.dataloader.MappedBatchLoader

interface GraQLDelegatingMappedBatchLoader<K, V>:MappedBatchLoader<K, V>, GraQLDelegate {
    val dataLoaderName:String
}


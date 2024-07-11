package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import org.dataloader.BatchLoader

interface GraQLDelegatingBatchLoader<K, V>:BatchLoader<K, V>, GraQLDelegate {
    val dataLoaderName:String
}


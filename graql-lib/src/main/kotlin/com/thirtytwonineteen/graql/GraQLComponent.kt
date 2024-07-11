package com.thirtytwonineteen.graql

import jakarta.inject.Qualifier
import jakarta.inject.Singleton

interface GraQLDelegate

@Qualifier
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Singleton
annotation class GraQLScalar(val name:String = "", val description:String = "")

@Qualifier
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Singleton
annotation class GraQLComponent()

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Singleton
annotation class GraQLFetch(val field:String = "", val type:String = "")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Singleton
annotation class GraQLQuery(val name:String = "", val input:String = "")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Singleton
annotation class GraQLMutation(val name:String = "", val input:String = "")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Singleton
annotation class GraQLBatchFetch(val dataLoaderName:String = "", val type:String="", val field:String="")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Singleton
annotation class GraQLDataLoader(val name:String = "")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Singleton
annotation class GraQLMappedDataLoader(val name:String = "")

@Qualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class GraQLExceptionHandler


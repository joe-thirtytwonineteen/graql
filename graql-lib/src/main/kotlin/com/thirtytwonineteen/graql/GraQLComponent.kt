package com.thirtytwonineteen.graql

import jakarta.inject.Qualifier
import jakarta.inject.Singleton

interface GraQLDelegate

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
annotation class GraQLMappedDataLoader(val name:String = "")
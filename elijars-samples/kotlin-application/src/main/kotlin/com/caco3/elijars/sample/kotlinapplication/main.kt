@file:JvmName("Main")

package com.caco3.elijars.sample.kotlinapplication

fun main() {
    println(Hello("Hello from Kotlin Application"))

    var classLoader: ClassLoader? = Hello::class.java.classLoader

    var clazz: Class<*>? = null

    while (classLoader != null) {
        try {
            val loadedClass = classLoader.loadClass("com.caco3.elijars.sample.kotlinapplication.Hello")
            if (clazz != null) {
                throw IllegalStateException(
                    "Duplicate class found. " +
                            "First class = $clazz, its classLoader = ${clazz.classLoader} " +
                            "and the second = $loadedClass, its classLoader = ${loadedClass.classLoader}"
                )
            }
            clazz = loadedClass
        } catch (ignore: ClassNotFoundException) {
        }
        classLoader = classLoader.parent
    }
    println(clazz)
}
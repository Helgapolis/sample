package com.kastapp.sample.data.model

abstract class KeyValue<out Key> {

    companion object {
        fun create(key: String, value: String): KeyValue<String> {
            return object : KeyValue<String>() {
                override fun getKey(): String {
                    return key
                }

                override fun getValue(): String {
                    return value
                }
            }
        }

        fun create(key: Int, value: String): KeyValue<Int> {
            return object : KeyValue<Int>() {
                override fun getKey(): Int {
                    return key
                }

                override fun getValue(): String {
                    return value
                }
            }
        }
    }

    abstract fun getKey(): Key
    abstract fun getValue(): String
}

fun List<KeyValue<*>>.findByKey(key: Int): KeyValue<*>? {
    return firstOrNull { it.getKey() == key }
}

fun List<KeyValue<*>>.findByKey(key: String): KeyValue<*>? {
    return firstOrNull { it.getKey() == key }
}

fun List<KeyValue<*>>.findByVal(value: String): KeyValue<*>? {
    return firstOrNull { it.getValue() == value }
}

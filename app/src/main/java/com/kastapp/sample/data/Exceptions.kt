package com.kastapp.sample.data

import java.io.IOException

class ServerException(
    val code: Int,
    msg: String
) : IOException(msg)

class NoNetworkException : IOException()

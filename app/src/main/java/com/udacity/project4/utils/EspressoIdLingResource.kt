package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdLingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

    inline fun <T> wrapEspressoIdLingResource(function: ()->T):T {
        EspressoIdLingResource.increment()
        return try {
            function()
        }finally {
          EspressoIdLingResource.decrement()
        }
    }

package com.mobeedev.library.gravity

enum class SlideGravity {

    LEFT {
        override fun createHelper(): GravityHelper {
            return LeftGravityHelper
        }
    },
    RIGHT {
        override fun createHelper(): GravityHelper {
            return RightGravityHelper
        }
    };

    abstract fun createHelper(): GravityHelper
}
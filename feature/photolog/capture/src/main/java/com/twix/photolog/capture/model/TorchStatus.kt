package com.twix.photolog.capture.model

enum class TorchStatus {
    On,
    Off,
    ;

    companion object {
        fun toggle(value: TorchStatus): TorchStatus =
            when (value) {
                On -> Off
                Off -> On
            }
    }
}

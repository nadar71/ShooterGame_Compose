package com.indiewalkabout.cosmoraiders.domain.model.audio

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AudioPlayer {
    fun playSound(index: Int)
}

val soundResList = listOf(
    "files/pop.mp3"
)
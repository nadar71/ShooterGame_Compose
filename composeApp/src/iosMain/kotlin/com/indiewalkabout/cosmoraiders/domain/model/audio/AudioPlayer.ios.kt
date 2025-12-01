package com.indiewalkabout.cosmoraiders.domain.model.audio


import kotlinx.cinterop.ExperimentalForeignApi
import cosmoraiders.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSURL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@OptIn(ExperimentalResourceApi::class)
actual class AudioPlayer /*actual constructor(context: Context)*/ {
    private val mediaItems = soundResList.map { path ->
        val uri = Res.getUri(path)
        NSURL.URLWithString(URLString = uri)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun playSound(index: Int) {
        val avAudioPlayer = AVAudioPlayer(mediaItems[index]!!, error = null)
        avAudioPlayer.prepareToPlay()
        avAudioPlayer.play()

    }
}

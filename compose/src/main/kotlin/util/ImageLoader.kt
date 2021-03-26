package util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URL

object ImageLoader {
    private val cache = mutableMapOf<String, ImageBitmap>()

    @Composable
    fun imageFromUrl(uri: URI): ImageBitmap {
        return imageFromUrl(uri.toURL())
    }

    @Composable
    fun imageFromUrl(url: String): ImageBitmap {
        return imageFromUrl(URL(url))
    }

    @Composable
    fun imageFromUrl(url: URL): ImageBitmap {
        val imageBitmap = remember { mutableStateOf(ImageBitmap(1, 1)) }

        val cacheKey = url.toString()

        LaunchedEffect(url) {
            imageBitmap.value = when (val cacheHit = cache[cacheKey]) {
                null -> {
                    withContext(Dispatchers.IO) {
                        val image = org.jetbrains.skija.Image.makeFromEncoded(url.readBytes())
                        val bitmap = image.asImageBitmap()
                        cache[cacheKey] = bitmap
                        bitmap
                    }
                }
                else -> cacheHit
            }
        }

        return imageBitmap.value
    }
}
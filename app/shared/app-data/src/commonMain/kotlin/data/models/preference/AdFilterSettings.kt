package me.him188.ani.app.data.models.preference

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class AdFilterSettings(
    val filterBySubjectId: Boolean = true,
    val filterByEmptyName: Boolean = true,
    val filterByDesc2: Boolean = true,
    val filterByAdImage: Boolean = true,
    @Suppress("PropertyName") @Transient val _placeholder: Int = 0,
) {
    companion object {
        @Stable
        val Default = AdFilterSettings()
    }
}

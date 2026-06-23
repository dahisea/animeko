/*
 * Copyright (C) 2024-2025 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.domain.episode

import kotlinx.coroutines.flow.first
import me.him188.ani.app.data.models.preference.AdFilterSettings
import me.him188.ani.app.data.network.SubjectService
import me.him188.ani.app.data.repository.user.SettingsRepository
import me.him188.ani.app.domain.usecase.UseCase
import me.him188.ani.utils.platform.Uuid


class SubjectRecommendation(
    val subjectId: Long?,
    val name: String,
    val nameCn: String?,
    val desc1: String,
    val desc2: String,
    val imageUrl: String,
    val uri: String?,
) {
    val uniqueId: String = Uuid.randomString()
}

fun interface GetSubjectRecommendationUseCase : UseCase {
    suspend operator fun invoke(subjectId: Int): List<SubjectRecommendation>
}

class GetSubjectRecommendationUseCaseImpl(
    private val service: SubjectService,
    private val settingsRepository: SettingsRepository,
) : GetSubjectRecommendationUseCase {
    override suspend fun invoke(subjectId: Int): List<SubjectRecommendation> {
        val adFilter = settingsRepository.themeSettings.flow.first().adFilter
        return service.getSubjectRecommendations(subjectId, 15).filter { it.matchesAdFilter(adFilter) }.map {
            SubjectRecommendation(
                subjectId = it.subjectId,
                name = it.subjectName,
                nameCn = it.subjectNameCn,
                desc1 = it.desc1,
                desc2 = it.desc2,
                imageUrl = it.imageUrl,
                uri = it.uri,
            )
        }
    }

    private fun AniSubjectRecommendation.matchesAdFilter(adFilter: AdFilterSettings): Boolean {
        val rawId = subjectId ?: return false
        val id = rawId.toInt()
        if (adFilter.filterBySubjectId && id <= 0) return false
        if (adFilter.filterByEmptyName && subjectName.isBlank()) return false
        if (adFilter.filterByDesc2 && desc2.contains("广告")) return false
        if (adFilter.filterByAdImage && imageUrl.contains("/ad-images/")) return false
        return true
    }
}

private typealias AniSubjectRecommendation = me.him188.ani.client.models.AniSubjectRecommendation
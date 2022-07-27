/**
 * Copyright (c) 2020-2022 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package io.github.samarium150.mirai.plugin.lolicon.util

import net.mamoe.mirai.message.data.ForwardMessage.DisplayStrategy
import net.mamoe.mirai.message.data.RawForwardMessage

internal object CustomDisplayStrategy : DisplayStrategy {

    override fun generateTitle(forward: RawForwardMessage): String = "神秘的聊天记录"

    override fun generateBrief(forward: RawForwardMessage): String = "[Lolicon]"

}

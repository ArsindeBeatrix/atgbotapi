package dev.inmo.tgbotapi.extensions.api

import korlibs.time.DateTime
import korlibs.time.TimeSpan
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.edit.location.live.editLiveLocation
import dev.inmo.tgbotapi.extensions.api.edit.location.live.stopLiveLocation
import dev.inmo.tgbotapi.requests.send.SendLiveLocation
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.business_connection.BusinessConnectionId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.location.LiveLocation
import dev.inmo.tgbotapi.types.location.StaticLocation
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.AccessibleMessage
import dev.inmo.tgbotapi.types.message.content.LocationContent
import dev.inmo.tgbotapi.utils.extensions.threadIdOrNull
import io.ktor.utils.io.core.Closeable
import korlibs.time.millisecondsLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil

val defaultLivePeriodDelayMillis = (livePeriodLimit.last - 60L) * 1000L

/**
 * @see startLiveLocation
 */
class LiveLocationProvider internal constructor(
    private val requestsExecutor: TelegramBot,
    scope: CoroutineScope,
    autoCloseTimeDelay: Double,
    initMessage: ContentMessage<LocationContent>
) : Closeable {
    private val doWhenClose = {
        scope.launch {
            requestsExecutor.stopLiveLocation(message)
        }
    }
    private val autoCloseTime = DateTime.now() + TimeSpan(autoCloseTimeDelay)
    val leftUntilCloseMillis: TimeSpan
        get() = autoCloseTime - DateTime.now()

    var isClosed: Boolean = false
        private set
        get() = field || leftUntilCloseMillis.millisecondsLong < 0L

    var message: ContentMessage<LocationContent> = initMessage
        private set
    val lastLocation: LiveLocation
        get() = message.content.location as LiveLocation

    /**
     * @param replyMarkup Some [InlineKeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard]
     * as a builder for that
     */
    suspend fun updateLocation(
        location: LiveLocation,
        replyMarkup: InlineKeyboardMarkup? = null
    ): LiveLocation {
        if (!isClosed) {
            message = requestsExecutor.editLiveLocation(
                message,
                location,
                replyMarkup
            )
            return lastLocation
        } else {
            error("LiveLocation is closed")
        }
    }

    override fun close() {
        if (isClosed) {
            return
        }
        isClosed = true
        doWhenClose()
    }
}

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend fun TelegramBot.startLiveLocation(
    scope: CoroutineScope,
    chatId: ChatIdentifier,
    latitude: Double,
    longitude: Double,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = chatId.threadId,
    businessConnectionId: BusinessConnectionId? = chatId.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    replyParameters: ReplyParameters? = null,
    replyMarkup: KeyboardMarkup? = null
): LiveLocationProvider {
    val liveTimeAsDouble = liveTimeMillis.toDouble()
    val locationMessage = execute(
        SendLiveLocation(
            chatId,
            latitude,
            longitude,
            ceil(liveTimeAsDouble / 1000).toInt(),
            initHorizontalAccuracy,
            initHeading,
            initProximityAlertRadius,
            threadId,
            businessConnectionId,
            disableNotification,
            protectContent,
            replyParameters,
            replyMarkup
        )
    )

    return LiveLocationProvider(
        this,
        scope,
        liveTimeAsDouble,
        locationMessage
    )
}

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend fun TelegramBot.startLiveLocation(
    scope: CoroutineScope,
    chat: Chat,
    latitude: Double,
    longitude: Double,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = chat.id.threadId,
    businessConnectionId: BusinessConnectionId? = chat.id.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    replyParameters: ReplyParameters? = null,
    replyMarkup: KeyboardMarkup? = null
): LiveLocationProvider = startLiveLocation(
    scope,
    chat.id,
    latitude,
    longitude,
    liveTimeMillis,
    initHorizontalAccuracy,
    initHeading,
    initProximityAlertRadius,
    threadId,
    businessConnectionId,
    disableNotification,
    protectContent,
    replyParameters,
    replyMarkup
)

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend fun TelegramBot.startLiveLocation(
    scope: CoroutineScope,
    chatId: IdChatIdentifier,
    location: StaticLocation,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = chatId.threadId,
    businessConnectionId: BusinessConnectionId? = chatId.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    replyParameters: ReplyParameters? = null,
    replyMarkup: KeyboardMarkup? = null
): LiveLocationProvider = startLiveLocation(
    scope,
    chatId,
    location.latitude,
    location.longitude,
    liveTimeMillis,
    initHorizontalAccuracy,
    initHeading,
    initProximityAlertRadius,
    threadId,
    businessConnectionId,
    disableNotification,
    protectContent,
    replyParameters,
    replyMarkup
)

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend fun TelegramBot.startLiveLocation(
    scope: CoroutineScope,
    chat: Chat,
    location: StaticLocation,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = chat.id.threadId,
    businessConnectionId: BusinessConnectionId? = chat.id.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    replyParameters: ReplyParameters? = null,
    replyMarkup: KeyboardMarkup? = null
): LiveLocationProvider = startLiveLocation(
    scope,
    chat.id,
    location.latitude,
    location.longitude,
    liveTimeMillis,
    initHorizontalAccuracy,
    initHeading,
    initProximityAlertRadius,
    threadId,
    businessConnectionId,
    disableNotification,
    protectContent,
    replyParameters,
    replyMarkup
)

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend inline fun TelegramBot.replyWithLiveLocation(
    to: AccessibleMessage,
    scope: CoroutineScope,
    latitude: Double,
    longitude: Double,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = to.threadIdOrNull,
    businessConnectionId: BusinessConnectionId? = to.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = startLiveLocation(
    scope,
    to.chat,
    latitude,
    longitude,
    liveTimeMillis,
    initHorizontalAccuracy,
    initHeading,
    initProximityAlertRadius,
    threadId,
    businessConnectionId,
    disableNotification,
    protectContent,
    ReplyParameters(to.metaInfo, allowSendingWithoutReply = allowSendingWithoutReply),
    replyMarkup
)

/**
 * @param replyMarkup Some of [KeyboardMarkup]. See [dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard] or
 * [dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard] as a builders for that param
 */
suspend inline fun TelegramBot.replyWithLiveLocation(
    to: AccessibleMessage,
    scope: CoroutineScope,
    location: StaticLocation,
    liveTimeMillis: Long = defaultLivePeriodDelayMillis,
    initHorizontalAccuracy: Meters? = null,
    initHeading: Degrees? = null,
    initProximityAlertRadius: Meters? = null,
    threadId: MessageThreadId? = to.threadIdOrNull,
    businessConnectionId: BusinessConnectionId? = to.businessConnectionId,
    disableNotification: Boolean = false,
    protectContent: Boolean = false,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = startLiveLocation(
    scope,
    to.chat,
    location,
    liveTimeMillis,
    initHorizontalAccuracy,
    initHeading,
    initProximityAlertRadius,
    threadId,
    businessConnectionId,
    disableNotification,
    protectContent,
    ReplyParameters(to.metaInfo, allowSendingWithoutReply = allowSendingWithoutReply),
    replyMarkup
)

package dev.inmo.tgbotapi.utils.extensions

import dev.inmo.tgbotapi.types.message.*
import dev.inmo.tgbotapi.types.message.abstracts.*
import dev.inmo.tgbotapi.types.message.content.MediaGroupCollectionContent
import dev.inmo.tgbotapi.types.message.content.MediaGroupContent
import dev.inmo.tgbotapi.types.message.content.MediaGroupPartContent
import dev.inmo.tgbotapi.utils.RiskFeature

@RiskFeature("This API is experimental and can be changed without any notice, use with caution")
fun <T : MediaGroupPartContent> List<PossiblySentViaBotCommonMessage<T>>.asMediaGroupMessage(): PossiblySentViaBotCommonMessage<MediaGroupContent<T>> {
    val sourceMessage = first()
    val content = MediaGroupContent(
        map { MediaGroupCollectionContent.PartWrapper(it.messageId, it.content, it) },
        sourceMessage.mediaGroupId ?: error("Can't create media group message with the first message without media group id")
    )
    return when (sourceMessage) {
        is ChannelContentMessage -> ChannelContentMessageImpl(
            sourceMessage.messageId,
            sourceMessage.chat,
            content,
            sourceMessage.date,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.forwardOrigin,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is BusinessContentMessage -> BusinessContentMessageImpl(
            sourceMessage.messageId,
            sourceMessage.user,
            sourceMessage.chat,
            sourceMessage.businessConnectionId,
            content,
            sourceMessage.date,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.forwardOrigin,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            sourceMessage.senderBot,
            sourceMessage.mediaGroupId,
            sourceMessage.senderBusinessBot,
            sourceMessage.fromOffline
        )
        is PrivateContentMessage -> PrivateContentMessageImpl(
            sourceMessage.messageId,
            sourceMessage.user,
            sourceMessage.chat,
            content,
            sourceMessage.date,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.forwardOrigin,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            sourceMessage.senderBot,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is AnonymousGroupContentMessage -> AnonymousGroupContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.messageId,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is CommonGroupContentMessage -> CommonGroupContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.messageId,
            sourceMessage.user,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.mediaGroupId,
            sourceMessage.senderBoostsCount,
            sourceMessage.fromOffline
        )
        is ConnectedFromChannelGroupContentMessage -> ConnectedFromChannelGroupContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.channel,
            sourceMessage.messageId,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is UnconnectedFromChannelGroupContentMessage -> UnconnectedFromChannelGroupContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.channel,
            sourceMessage.messageId,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is AnonymousForumContentMessage -> AnonymousForumContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.messageId,
            sourceMessage.threadId,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
        is CommonForumContentMessage -> CommonForumContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.messageId,
            sourceMessage.threadId,
            sourceMessage.user,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.mediaGroupId,
            sourceMessage.senderBoostsCount,
            sourceMessage.fromOffline
        )
        is FromChannelForumContentMessage -> FromChannelForumContentMessageImpl(
            sourceMessage.chat,
            sourceMessage.channel,
            sourceMessage.messageId,
            sourceMessage.threadId,
            sourceMessage.date,
            sourceMessage.forwardOrigin,
            sourceMessage.editDate,
            sourceMessage.hasProtectedContent,
            sourceMessage.replyInfo,
            sourceMessage.replyMarkup,
            content,
            sourceMessage.senderBot,
            sourceMessage.authorSignature,
            sourceMessage.mediaGroupId,
            sourceMessage.fromOffline
        )
    }
}

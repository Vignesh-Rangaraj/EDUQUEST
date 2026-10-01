package com.eduquest.ai.dto;

import java.util.List;

public class ChatHistoryPage {
    private final List<ChatMessageDto> messages;
    private final Long nextBeforeId;
    private final boolean hasMore;

    public ChatHistoryPage(List<ChatMessageDto> messages, Long nextBeforeId, boolean hasMore) {
        this.messages = messages;
        this.nextBeforeId = nextBeforeId;
        this.hasMore = hasMore;
    }
    public List<ChatMessageDto> getMessages() { return messages; }
    public Long getNextBeforeId() { return nextBeforeId; }
    public boolean isHasMore() { return hasMore; }
}

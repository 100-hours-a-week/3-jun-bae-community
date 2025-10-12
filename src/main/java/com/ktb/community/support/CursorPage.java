package com.ktb.community.support;

import java.util.List;

public class CursorPage<T> {

    private final List<T> contents;
    private final Long nextCursor;
    private final boolean hasNext;

    public CursorPage(List<T> contents, Long nextCursor, boolean hasNext) {
        this.contents = contents;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    public List<T> getContents() {
        return contents;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}

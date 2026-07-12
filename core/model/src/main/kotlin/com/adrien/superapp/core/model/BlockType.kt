package com.adrien.superapp.core.model

/**
 * A subset of the brief's full block-type list (see docs/DATA_MODEL.md). `IMAGE`, `FILE`, `LINK`,
 * `TASK_REFERENCE`, `PAGE_REFERENCE`, and `CANVAS_REFERENCE` are deferred to the phases that add
 * the features they'd reference (attachments, tasks, canvas, cross-page linking).
 */
enum class BlockType {
    PARAGRAPH,
    HEADING_1,
    HEADING_2,
    HEADING_3,
    BULLETED_LIST,
    NUMBERED_LIST,
    CHECKLIST,
    QUOTE,
    CALLOUT,
    DIVIDER,
    CODE,
}

package com.adrien.superapp.core.model

/**
 * A subset of the brief's full property-type list. `FILE` and `RELATION` are deferred (need an
 * attachment picker and a cross-collection entry picker, respectively); `AGGREGATION`/`FORMULA`
 * are explicitly "ultérieure" in the brief itself; `LAST_EDITED_BY` needs edit-attribution
 * tracking `Page` doesn't have yet.
 */
enum class CollectionPropertyType {
    TEXT,
    NUMBER,
    SELECT,
    MULTI_SELECT,
    STATUS,
    DATE,
    CHECKBOX,
    URL,
    EMAIL,
    PHONE,
    PERSON,
    CREATED_AT,
    UPDATED_AT,
    CREATED_BY,
}

/** True for types whose value always mirrors a [Page]'s own field — never stored as a property value row. */
val CollectionPropertyType.isSystemProperty: Boolean
    get() = this == CollectionPropertyType.CREATED_AT ||
        this == CollectionPropertyType.UPDATED_AT ||
        this == CollectionPropertyType.CREATED_BY

/** True for types backed by a fixed option list ([CollectionPropertyOption]). */
val CollectionPropertyType.hasOptions: Boolean
    get() = this == CollectionPropertyType.SELECT ||
        this == CollectionPropertyType.MULTI_SELECT ||
        this == CollectionPropertyType.STATUS

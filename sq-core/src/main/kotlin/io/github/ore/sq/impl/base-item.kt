package io.github.ore.sq.impl

import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqOwnedColumn


open class SqOwnedColumnImpl<JAVA, DB: Any>(
    override val owner: SqItem,
    override val name: String,
    override val reader: SqDataTypeReader<JAVA, DB>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
) : SqOwnedColumn<JAVA, DB>

package io.github.ore.sq.impl

import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqDataTypeWriter
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqTableColumn


open class SqTableColumnImpl<JAVA, DB: Any>(
    owner: SqItem,
    name: String,
    reader: SqDataTypeReader<JAVA, DB>,
    override val writer: SqDataTypeWriter<JAVA & Any, DB>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
): SqOwnedColumnImpl<JAVA, DB>(owner, name, reader, commentAtStart, commentAtEnd), SqTableColumn<JAVA, DB>

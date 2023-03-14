package me.ore.sq


class SqByteArray(val value: ByteArray)


fun ByteArray.toSqArray(): SqByteArray = SqByteArray(this)

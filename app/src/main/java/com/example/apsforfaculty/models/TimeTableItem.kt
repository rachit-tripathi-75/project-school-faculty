package com.example.apsforfaculty.models


sealed class TimetableItem(
    open val text: String,
    open val row: Int,
    open val column: Int
) {
    data class Corner(
        override val text: String,
        override val row: Int,
        override val column: Int
    ) : TimetableItem(text, row, column)

    data class Header(
        override val text: String,
        override val row: Int,
        override val column: Int
    ) : TimetableItem(text, row, column)

    data class Day(
        override val text: String,
        override val row: Int,
        override val column: Int
    ) : TimetableItem(text, row, column)

    data class Class(
        override val text: String,
        override val row: Int,
        override val column: Int,
        val room: String,
        val teacher: String,
        val backgroundColor: String,
        val textColor: String
    ) : TimetableItem(text, row, column)

    data class Empty(
        override val text: String,
        override val row: Int,
        override val column: Int
    ) : TimetableItem(text, row, column)

    data class Free(
        override val text: String,
        override val row: Int,
        override val column: Int
    ) : TimetableItem(text, row, column)
}
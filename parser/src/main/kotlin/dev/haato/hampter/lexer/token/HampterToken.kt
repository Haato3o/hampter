package dev.haato.hampter.lexer.token

import dev.haato.hampter.metadata.FileMetadata

data class HampterToken(
    val value: String,
    val metadata: FileMetadata
)

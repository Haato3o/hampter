package dev.haato.sarah.lexer.token

import dev.haato.sarah.metadata.FileMetadata

data class SarahToken(
    val value: String,
    val metadata: FileMetadata
)

package dev.finio.auth.data.mapper

import dev.finio.auth.data.dto.AuthResponseDto
import dev.finio.auth.data.dto.UserDto
import dev.finio.auth.domain.model.User

fun AuthResponseDto.toDomain(): User = User(
    id = user.id,
    name = user.name,
    email = user.email
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email
)
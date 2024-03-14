package kr.foundcake.role_auth.dto

data class UserDto(
	val serverId: Long,
	val number: Int,
	val name: String,
	val role: Long
)

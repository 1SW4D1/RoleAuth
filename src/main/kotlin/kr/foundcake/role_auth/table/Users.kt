package kr.foundcake.role_auth.table

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {

	val number = integer("number")

	val name = varchar("name", 20)

	val role = long("roleId")

	val serverId = long("serverId")
}
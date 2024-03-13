package kr.foundcake.role_auth.table

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {

	val number = integer("number")

	val name = varchar("name", 20)

	val role = long("roleId")

	override val primaryKey = PrimaryKey(number)
}
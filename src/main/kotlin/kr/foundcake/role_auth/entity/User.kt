package kr.foundcake.role_auth.entity

import kr.foundcake.role_auth.table.Users
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<User>(Users)

	var number by Users.number

	var name by Users.name

	var role by Users.role

	var serverId by Users.serverId

	val nickName: String
		get() {
			var shortNumber = number / 10000
			shortNumber %= 100
			return "${shortNumber}_$name"
		}
}
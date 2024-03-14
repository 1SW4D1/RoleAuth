package kr.foundcake.role_auth.database

import kr.foundcake.role_auth.entity.User

enum class SaveResult {
	INSERT,
	UPDATE,
	FAILED;

	private var user: User? = null

	fun setUser(user: User): SaveResult {
		this.user = user
		return this
	}

	fun getUser(): User? {
		return user
	}
}
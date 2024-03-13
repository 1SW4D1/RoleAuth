package kr.foundcake.role_auth.dto

data class User(
	val number: Int,
	val name: String,
	val role: Long
) {
	val nickName: String
		get() {
			var shortNumber = number / 10000
			shortNumber %= 100
			return "${shortNumber}_$name"
		}
}

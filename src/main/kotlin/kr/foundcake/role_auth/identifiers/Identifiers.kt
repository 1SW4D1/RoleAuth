package kr.foundcake.role_auth.identifiers

enum class Identifiers(val id: String) {
	AUTH_BUTTON("role_auth_btn"),
	AUTH_MODAL("role_auth_modal"),
	AUTH_MODAL_INPUT_NUMBER("number"),
	AUTH_MODAL_INPUT_NAME("name");

	infix fun not(id: String): Boolean = this.id != id
}
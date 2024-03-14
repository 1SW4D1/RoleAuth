package kr.foundcake.role_auth.command

enum class Commands(val cmdName: String, val description: String) {
	SETUP_USER("유저설정", "인증가능한 유저를 추가합니다."),
	REMOVE_USER("유저제거", "인증가능한 유저에서 제외시킵니다."),
	CREATE_BUTTON("인증버튼생성", "인증하기 버튼을 생성합니다.")
}
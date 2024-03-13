package kr.foundcake.role_auth.extension

private val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

private fun String.checkTextJosa(josa1: String, josa2: String): String {
	val lastChar: Char = this.last()
	return when {
		lastChar.code in 0xAC00..0xD7A3 -> {
			if ((lastChar.code - 0xAC00) % 28 > 0) josa1 else josa2
		}

		lastChar.lowercaseChar() in VOWELS -> {
			josa2
		}

		lastChar.isLetter() -> {
			josa1
		}

		else -> {
			"($josa1/$josa2)"
		}
	}
}

/**
 * 주어진 단어에 적절한 조사를 붙여 반환합니다.
 * @param josa1 받침이 있는 경우에 붙일 조사
 * @param josa2 받침이 없는 경우에 붙일 조사
 * @return 조사가 붙은 새로운 문자열
 */
fun String.josa(josa1: String, josa2: String): String = "$this${this.checkTextJosa(josa1, josa2)}"

/**
 * 주어진 단어에 적절한 조사를 붙여 반환합니다.
 * @param standard 자신이 아닌 주어진 단어를 기준으로 합니다.
 * @param josa1 받침이 있는 경우에 붙일 조사
 * @param josa2 받침이 없는 경우에 붙일 조사
 * @return 조사가 붙은 새로운 문자열
 */
fun String.josa(standard: String, josa1: String, josa2: String): String = "$this${standard.checkTextJosa(josa1, josa2)}"

/**
 * 주어진 숫자에 적절한 조사를 붙여 반환합니다.
 * @param josa1 받침이 있는 경우에 붙일 조사
 * @param josa2 받침이 없는 경우에 붙일 조사
 * @return 조사가 붙은 새로운 문자열
 */
fun Number.josa(josa1: String, josa2: String): String {
	val str = this.toString().last()
	return if (str in listOf('2', '4', '5', '9')) {
		"$this$josa2"
	} else {
		"$this$josa1"
	}
}
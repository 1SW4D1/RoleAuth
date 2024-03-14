package kr.foundcake.role_auth.handler

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import kr.foundcake.role_auth.database.DBManager
import kr.foundcake.role_auth.entity.User
import kr.foundcake.role_auth.extension.getValue
import kr.foundcake.role_auth.identifiers.Identifiers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

fun handleAuthModal(jda: JDA) {
	jda.listener<ModalInteractionEvent> {
		if (Identifiers.AUTH_MODAL not it.modalId) {
			return@listener
		}
		val number: Int? = it.getValue(Identifiers.AUTH_MODAL_INPUT_NUMBER).asString.toIntOrNull()
		if (number === null) {
			it.reply("학번은 숫자로만 이루어져 있습니다.")
				.setEphemeral(true)
				.queue()
			return@listener
		}
		val name: String = it.getValue(Identifiers.AUTH_MODAL_INPUT_NAME).asString

		val user: User? = DBManager.UserRepo.find(it.guild!!.idLong, number, name)
		if (user === null) {
			it.reply("학번 또는 이름이 잘못되었습니다.")
				.setEphemeral(true)
				.queue()
			return@listener
		}

		val role: Role? = it.guild!!.getRoleById(user.role)
		if (role === null) {
			it.reply(
				"역할이 부여에 실패하였습니다.\n" +
						"관리자에게 문의해주세요."
			)
				.setEphemeral(true)
				.queue()
			return@listener
		}

		it.guild!!.addRoleToMember(it.user, role).await()
		it.member!!.modifyNickname(user.nickName).await()
		it.reply("인증되었습니다.")
			.setEphemeral(true)
			.queue()
	}
}
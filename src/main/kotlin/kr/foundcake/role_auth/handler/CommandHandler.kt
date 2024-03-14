package kr.foundcake.role_auth.handler

import dev.minn.jda.ktx.coroutines.await
import kr.foundcake.role_auth.command.Commands
import kr.foundcake.role_auth.database.DBManager
import kr.foundcake.role_auth.database.SaveStatus
import kr.foundcake.role_auth.dto.User
import kr.foundcake.role_auth.extension.josa
import kr.foundcake.role_auth.extension.onCommand
import kr.foundcake.role_auth.identifiers.Identifiers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CommandHandler(private val jda: JDA) {

	private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)

	fun registerAddUser() {
		jda.onCommand(Commands.SETUP_USER) {
			val number: Int = it.getOption("학번")!!.asInt
			val name: String = it.getOption("이름")!!.asString
			val role: Role = it.getOption("역할")!!.asRole

			val user = User(number, name, role.idLong)
			val result: SaveStatus = DBManager.UserRepo.save(user)

			when (result) {
				SaveStatus.INSERT -> it.reply(
					"${number.josa("은", "는")} ${
						role.asMention.josa(
							role.name,
							"이",
							"가"
						)
					} 부여되도록 설정하였습니다."
				).queue()

				SaveStatus.UPDATE -> it.reply(
					"${number.josa("은", "는")} ${
						role.asMention.josa(
							role.name,
							"이",
							"가"
						)
					} 부여되도록 변경하였습니다."
				).queue()

				SaveStatus.FAILED -> it.reply(
					"${number.josa("은", "는")} 이미 설정되어있습니다.\n" +
							"기존 이름과 일치하던가 삭제 후 다시 시도해주세요"
				).queue()
			}

			val isSync: Boolean = it.getOption("동기화")?.asBoolean == true
			if(result === SaveStatus.UPDATE && isSync) {
				val members = it.guild!!.loadMembers().await()
				for (m in members) {
					if (m.nickname == user.nickName) {
						it.guild!!.modifyMemberRoles(m, role).queue()
						break
					}
				}
			}
		}
	}

	fun registerRemoveUser() {
		jda.onCommand(Commands.REMOVE_USER) {
			val number: Int = it.getOption("학번")!!.asInt
			val name: String = it.getOption("이름")!!.asString

			val user: User? = DBManager.UserRepo.find(number)
			if (user === null) {
				it.reply("등록되지 않은 유저 입니다.")
					.setEphemeral(true)
					.queue()
				return@onCommand
			} else if (user.name != name) {
				it.reply("학번과 이름이 일치하지 않습니다.")
					.setEphemeral(true)
					.queue()
				return@onCommand
			}

			var comment = "(킥 실패)"
			val isKick: Boolean = it.getOption("킥여부")?.asBoolean == true
			if (isKick) {
				val members = it.guild!!.loadMembers().await()
				for (m in members) {
					println(m.nickname)
					if (m.nickname == user.nickName) {
						m.kick().queue()
						comment = ""
						break
					}
				}
			}

			DBManager.UserRepo.delete(user)
			it.reply("삭제 되었습니다${comment}").queue()
			logger.info("remove $user")
		}
	}

	fun registerCreateButton() {
		jda.onCommand(Commands.CREATE_BUTTON) {
			it.messageChannel.sendMessage("")
				.addActionRow(
					Button.success(Identifiers.AUTH_BUTTON.id, "인증하기")
				)
				.queue()
			it.reply("생성되었습니다.")
				.setEphemeral(true)
				.queue()
		}
	}
}
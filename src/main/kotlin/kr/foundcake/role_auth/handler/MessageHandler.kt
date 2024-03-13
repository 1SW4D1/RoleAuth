package kr.foundcake.role_auth.handler

import dev.minn.jda.ktx.events.listener
import kotlinx.coroutines.runBlocking
import kr.foundcake.role_auth.database.DBManager
import kr.foundcake.role_auth.database.SaveStatus
import kr.foundcake.role_auth.dto.User
import kr.foundcake.role_auth.extension.josa
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

fun handleMessage(jda: JDA) {
	jda.listener<MessageReceivedEvent> {
		if(it.member?.hasPermission(Permission.MANAGE_ROLES) != true) {
			return@listener
		}

		val message = it.message
		val content = message.contentRaw

		if (message.attachments.count() != 1 || content != "<유저전체추가>") {
			return@listener
		}
		val attachment = message.attachments[0]
		if (attachment.fileExtension != "csv") {
			return@listener
		}

		val file = File.createTempFile("temp", null)
		attachment.proxy.downloadToFile(file).thenAccept { _ ->
			BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
				reader.lineSequence().drop(1).forEach { line ->
					val (number, name, roleId) = line.split(",")

					val num: Int? = number.toIntOrNull()
					if(num ===  null) {
						message.reply("${number.josa("은", "는")} 정수가 아닙니다.").queue()
						return@forEach
					}

					val role: Role? = it.guild.getRolesByName(roleId.trim(), true).firstOrNull()
					if(role === null) {
						message.reply("${roleId.josa("은", "는")} 존재 하지 않는 역할 입니다.($number 등록 실패)").queue()
						return@forEach
					}

					val user = User(num, name, role.idLong)
					runBlocking {
						val result: SaveStatus = DBManager.UserRepo.save(user)
						if(result === SaveStatus.FAILED) {
							message.reply("이미 등록되어있습니다.($number 등록 실패)").queue()
						}
					}
				}
			}
		}
		file.deleteOnExit()

		message.reply("작업을 완료 하였습니다.").queue()
	}
}
package kr.foundcake.role_auth.handler

import dev.minn.jda.ktx.coroutines.await
import kotlinx.coroutines.*
import kr.foundcake.role_auth.database.DBManager
import kr.foundcake.role_auth.database.SaveResult
import kr.foundcake.role_auth.dto.UserDto
import kr.foundcake.role_auth.extension.josa
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.NumberFormat

private const val FILE_EXTENSION = "csv"
private const val MESSAGE_COMMAND = "<유저전체추가>"
private const val MESSAGE_COMMAND_OF_SYNC = "$MESSAGE_COMMAND(동기화)"

suspend fun handleMessage(it: MessageReceivedEvent): Boolean {
	if(it.member?.hasPermission(Permission.MANAGE_ROLES) != true) {
		return false
	}

	val message: Message = it.message
	val content: String = message.contentRaw

	if (message.attachments.count() != 1 || (content != MESSAGE_COMMAND && content != MESSAGE_COMMAND_OF_SYNC)) {
		return false
	}

	val attachment = message.attachments[0]
	if (attachment.fileExtension != FILE_EXTENSION) {
		return false
	}

	val mention: String = message.author.asMention
	message.delete().queue()

	val file: File = withContext(Dispatchers.IO) {
		File.createTempFile("temp", null)
	}
	attachment.proxy.downloadToFile(file).await()

	val stream: FileInputStream= withContext(Dispatchers.IO) {
		FileInputStream(file)
	}

	val startTime: Long = System.currentTimeMillis()
	val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
	reader.lineSequence().drop(1).forEach { line ->
		val (number: String, name: String, roleId: String) = line.split(",")

		val num: Int? = number.toIntOrNull()
		if(num ===  null) {
			it.channel.sendMessage("${number.josa("은", "는")} 정수가 아닙니다.").queue()
			return@forEach
		}

		val role: Role? = it.guild.getRolesByName(roleId.trim(), true).firstOrNull()
		if(role === null) {
			it.channel.sendMessage("${roleId.josa("은", "는")} 존재 하지 않는 역할 입니다.($number 등록 실패)").queue()
			return@forEach
		}

		val result: SaveResult = DBManager.UserRepo.save(
			UserDto(
				serverId = it.guild.idLong,
				number = num,
				name = name,
				role = role.idLong
			)
		)
		if (result === SaveResult.FAILED) {
			it.channel.sendMessage("이미 존재하는 데이터와 일치 하지 않습니다.($number 등록 실패)").queue()
		} else if (content == MESSAGE_COMMAND_OF_SYNC) {
			val members: List<Member> = it.guild.loadMembers().await()
			for (m: Member in members) {
				if (m.nickname == result.getUser()!!.nickName) {
					it.guild.modifyMemberRoles(m, role).queue()
					break
				}
			}
		}
	}
	val endTime: Long = System.currentTimeMillis()
	val elapsedTime: String = NumberFormat.getNumberInstance().format(endTime - startTime)

	file.deleteOnExit()

	message.reply("$mention  작업을 완료 하였습니다.($elapsedTime ms)").queue()
	return true
}
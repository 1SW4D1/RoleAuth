package kr.foundcake.role_auth

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.restrict
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.intents
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kr.foundcake.role_auth.command.Commands
import kr.foundcake.role_auth.database.DBManager
import kr.foundcake.role_auth.extension.listener
import kr.foundcake.role_auth.extension.slash
import kr.foundcake.role_auth.handler.CommandHandler
import kr.foundcake.role_auth.handler.handleAuthButton
import kr.foundcake.role_auth.handler.handleAuthModal
import kr.foundcake.role_auth.handler.handleMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.requests.GatewayIntent
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
	val dbSetup: Deferred<Unit> = async { DBManager.init() }

	val token: String = System.getenv("TOKEN")
	val jda: JDA = default(token, true) {
		intents += listOf(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
	}

	setupCommand(jda)

	jda.listener(::handleAuthButton)
	jda.listener(::handleAuthModal)
	jda.listener(30.seconds, ::handleMessage)

	dbSetup.await()

	jda.awaitReady()

	return@runBlocking
}

fun setupCommand(jda: JDA) {
	jda.updateCommands {
		slash(Commands.SETUP_USER) {
			restrict(true, Permission.MANAGE_ROLES)
			option<Int>("학번", "학번", true)
			option<String>("이름", "이름", true)
			option<Role>("역할", "지급할 역할", true)
			option<Boolean>("동기화", "유저가 서버에 있을 경우 동기화를 시도합니다.")
		}
		slash(Commands.REMOVE_USER) {
			restrict(true, Permission.KICK_MEMBERS)
			option<Int>("학번", "학번", true)
			option<String>("이름", "이름", true)
			option<Boolean>("킥여부", "서버에 있을 경우 킥을 시도합니다.")
		}
		slash(Commands.CREATE_BUTTON) {
			restrict(true, Permission.MANAGE_ROLES)
		}
	}.queue()

	val handler = CommandHandler(jda)
	handler.registerAddUser()
	handler.registerRemoveUser()
	handler.registerCreateButton()
}
package kr.foundcake.role_auth.handler

import dev.minn.jda.ktx.events.listener
import kr.foundcake.role_auth.extension.TextInput
import kr.foundcake.role_auth.extension.createModal
import kr.foundcake.role_auth.identifiers.Identifiers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

fun handleAuthButton(jda: JDA) {
	jda.listener<ButtonInteractionEvent> {
		if (it.componentType !== Component.Type.BUTTON || Identifiers.AUTH_BUTTON not it.componentId) {
			return@listener
		}
		it.replyModal(
			createModal(
				Identifiers.AUTH_MODAL,
				title = "인증"
			)
				.addActionRow(
					TextInput(
						id = Identifiers.AUTH_MODAL_INPUT_NUMBER,
						label = "학번",
						style = TextInputStyle.SHORT,
						required = true,
						requiredLength = IntRange(8, 8)
					)
				)
				.addActionRow(
					TextInput(
						id = Identifiers.AUTH_MODAL_INPUT_NAME,
						label = "이름",
						style = TextInputStyle.SHORT,
						required = true,
						requiredLength = IntRange(2, 20)
					)
				)
				.build()
		).queue()
	}
}


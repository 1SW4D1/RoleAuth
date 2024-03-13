package kr.foundcake.role_auth.extension

import kr.foundcake.role_auth.identifiers.Identifiers
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.interactions.modals.ModalMapping
import dev.minn.jda.ktx.interactions.components.TextInput as TextInputComponent

fun createModal(identifier: Identifiers, title: String): Modal.Builder = Modal.create(identifier.id, title)

fun TextInput(
	id: Identifiers,
	label: String,
	style: TextInputStyle,
	required: Boolean = false,
	placeholder: String? = null,
	requiredLength: IntRange? = null
): TextInput = TextInputComponent(id.id, label, style, required, null, placeholder, requiredLength)

fun ModalInteractionEvent.getValue(identifier: Identifiers): ModalMapping = this.getValue(identifier.id)!!
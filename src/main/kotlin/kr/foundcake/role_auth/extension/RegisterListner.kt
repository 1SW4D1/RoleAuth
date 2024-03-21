package kr.foundcake.role_auth.extension

import dev.minn.jda.ktx.events.listener as ktxlistener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.time.Duration

inline fun <reified T : GenericEvent> JDA.listener(crossinline handler: suspend (T) -> Boolean) {
	this.listener(null, handler)
}

inline fun <reified T : GenericEvent> JDA.listener(timeout: Duration? = null, crossinline handler: suspend (T) -> Boolean) {
	this.ktxlistener<T>(timeout) {
		if(handler(it)){
			cancel()
		}
	}
}
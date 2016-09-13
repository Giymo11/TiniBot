package rip.hansolo.discord.tini.brain


import rip.hansolo.discord.tini.resources.LocalSettings

import scala.collection.mutable
import scala.collection.concurrent.TrieMap

/**
	* Created by Giymo11 on 9/12/2016 at 5:16 PM.
	*/
object SettingsBrain {

	// TODO: read settings from firebase!
	// TODO: persist settings to firebase!
	// TODO: listen for changes of settings from firebase! (for future web-use)

	val map: mutable.Map[String, LocalSettings] = TrieMap[String, LocalSettings]().withDefault(id => LocalSettings(id))

	def getFor(id: String) = map(id)
	def getForPrivate(id: String) = map(id)

	def update(value: LocalSettings) = map += (value.id -> value)
}

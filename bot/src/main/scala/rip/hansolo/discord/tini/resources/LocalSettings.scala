package rip.hansolo.discord.tini.resources

import rip.hansolo.discord.tini.resources.LocalSettings.Defaults


/**
	* Created by Giymo11 on 9/12/2016 at 2:55 PM.
	*/
case class LocalSettings (
	id: String,
	is8ball: Boolean = Defaults.is8Ball,
	isShowingTags: Boolean = Defaults.isShowingTags,
	tiniPrefix: String = Defaults.tiniPrefix,
	isSelfAnnouncing: Boolean = Defaults.isSelfAnnouncing,
	minimumRepeatDurationMins: Int = Defaults.minimumRepeatDurationMins,
	embedRssLinks: Boolean = Defaults.embedRssLinks,
	numberOfRssEntries: Int = Defaults.numberOfRssEntries
)

object LocalSettings {

	object Defaults {
		val is8Ball = true
		val isShowingTags = false
		val tiniPrefix = "!"
		val isSelfAnnouncing = false
		val minimumRepeatDurationMins = Reference.repeatMinimumDuration
		val embedRssLinks = Reference.embedRssLinks
		val numberOfRssEntries = Reference.numberOfRssEntries
	}

	def fromMap(id: String, values: Map[String, Object]): LocalSettings = {
		LocalSettings(
			id,
			values.get("is8ball").map(_.asInstanceOf[Boolean]).getOrElse(Defaults.is8Ball),
			values.get("isShowingTags").map(_.asInstanceOf[Boolean]).getOrElse(Defaults.isShowingTags),
			values.get("tiniPrefix").map(_.asInstanceOf[String]).getOrElse(Defaults.tiniPrefix),
			values.get("isSelfAnnouncing").map(_.asInstanceOf[Boolean]).getOrElse(Defaults.isSelfAnnouncing),
			values.get("minimumRepeatDurationMins").map(_.asInstanceOf[Int]).getOrElse(Defaults.minimumRepeatDurationMins),
			values.get("embedRssLinks").map(_.asInstanceOf[Boolean]).getOrElse(Defaults.embedRssLinks),
			values.get("numberOfRssEntries").map(_.asInstanceOf[Int]).getOrElse(Defaults.numberOfRssEntries)
		)
	}
}
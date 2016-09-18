package rip.hansolo.discord.tini.resources


import scala.collection.immutable.HashMap

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
) {
	import LocalSettings.Strings
	def toMap: Map[String, AnyRef] = HashMap[String, AnyRef](
		Strings.is8Ball -> is8ball.asInstanceOf[AnyRef],
		Strings.isShowingTags -> isShowingTags.asInstanceOf[AnyRef],
		Strings.tiniPrefix -> tiniPrefix.asInstanceOf[AnyRef],
		Strings.isSelfAnnouncing -> isSelfAnnouncing.asInstanceOf[AnyRef],
		Strings.minimumRepeatDurationMins -> minimumRepeatDurationMins.asInstanceOf[AnyRef],
		Strings.embedRssLinks -> embedRssLinks.asInstanceOf[AnyRef],
		Strings.numberOfRssEntries -> numberOfRssEntries.asInstanceOf[AnyRef]
	)
}

object LocalSettings {

	object Strings {
		val is8Ball = "is8ball"
		val isShowingTags = "isShowingTags"
		val tiniPrefix = "tiniPrefix"
		val isSelfAnnouncing = "isSelfAnnouncintg"
		val minimumRepeatDurationMins = "minimumRepeatDurationMins"
		val embedRssLinks = "embedRssLinks"
		val numberOfRssEntries = "numberOfRssEntries"
	}

	object Defaults {
		val is8Ball = false
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
			values.get(Strings.is8Ball).map(_.asInstanceOf[Boolean]).getOrElse(Defaults.is8Ball),
			values.get(Strings.isShowingTags).map(_.asInstanceOf[Boolean]).getOrElse(Defaults.isShowingTags),
			values.get(Strings.tiniPrefix).map(_.asInstanceOf[String]).getOrElse(Defaults.tiniPrefix),
			values.get(Strings.isSelfAnnouncing).map(_.asInstanceOf[Boolean]).getOrElse(Defaults.isSelfAnnouncing),
			values.get(Strings.minimumRepeatDurationMins).map(_.asInstanceOf[Long].toInt).getOrElse(Defaults.minimumRepeatDurationMins),
			values.get(Strings.embedRssLinks).map(_.asInstanceOf[Boolean]).getOrElse(Defaults.embedRssLinks),
			values.get(Strings.numberOfRssEntries).map(_.asInstanceOf[Long].toInt).getOrElse(Defaults.numberOfRssEntries)
		)
	}
}
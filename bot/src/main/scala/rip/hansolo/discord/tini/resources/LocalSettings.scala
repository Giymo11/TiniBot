package rip.hansolo.discord.tini.resources

import rip.hansolo.discord.tini.brain.SettingsBrain

/**
	* Created by Giymo11 on 9/12/2016 at 2:55 PM.
	*/
case class LocalSettings (
	                         id: String,
	                         is8ball: Boolean = false,
	                         isShowingTags: Boolean = false,
	                         tiniPrefix: String = "!",
	                         isSelfAnnouncing: Boolean = false,
	                         minimumRepeatDurationMins: Int = Reference.repeatMinimumDuration,
	                         embedRssLinks: Boolean = Reference.embedRssLinks,
	                         numberOfRssEntries: Int = Reference.numberOfRssEntries
)
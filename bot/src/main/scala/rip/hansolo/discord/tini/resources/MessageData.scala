package rip.hansolo.discord.tini.resources


import net.dv8tion.jda.entities.{Message, MessageChannel, User}


/**
	* Created by Giymo11 on 9/28/2016 at 10:52 PM.
	*/
class MessageData(
	val getChannel: MessageChannel,
	val getAuthor: User,
	val getMentionedUsers: java.util.List[User],
	val getRawContent: String,
	val id: String) {


}

object MessageData {
	def from(message: Message) = new MessageData(
		message.getChannel,
		message.getAuthor,
		message.getMentionedUsers,
		message.getRawContent,
		message.getId)
}

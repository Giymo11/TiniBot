package rip.hansolo.discord.tini.resources

import com.typesafe.config.ConfigFactory

import ammonite.ops._

/**
  * Created by Giymo11 on 08.08.2016.
  */
object Reference {
  private val config = ConfigFactory.parseString(read ! cwd / "config" / "application.conf")

  val token: String = config.getString("secret.discord.token") //System.getenv("TINI_TOKEN")
  val authorPassword: String = config.getString("secret.password") //System.getenv("TINI_PASSWORD")

  val logPath: String = config.getString("config.log-dir")

  val gdriveFolderName = config.getString("config.gdrive.folder")
  val gdriveIgnore = config.getString("config.gdrive.ignore")
  val gdriveCredentialsDir = config.getString("config.gdrive.credentials-dir")

  val gdriveClientID = config.getString("secret.gdrive.client-id")
  val gdriveSecret = config.getString("secret.gdrive.secret")

  val firebaseJson = config.getString("config.firebase.json")
  val firebaseUrl = config.getString("secret.firebase.database-url")

  val malUser = config.getString("secret.mal.user")
  val malPass = config.getString("secret.mal.password")

  val repeatMinimumDuration = config.getInt("config.repeat.min")

  val shitTiniSays = ConfigFactory.parseString(read ! cwd / "config" / "shitTiniSays.conf").resolve()

  object Permissions {
    val CREATE_INSTANT_INVITE	= 0x00000001	//Allows creation of instant invites
    val KICK_MEMBERS = 0x00000002  // *	Allows kicking members
    val BAN_MEMBERS = 0x00000004  // *	Allows banning members
    val ADMINISTRATOR = 0x00000008  // *	Allows all permissions and bypasses channel permission overwrites
    val MANAGE_CHANNELS = 0x00000010  // *	Allows management and editing of channels
    val MANAGE_GUILD = 0x00000020  // *	Allows management and editing of the guild
    val READ_MESSAGES = 0x00000400	// Allows reading messages in a channel. The channel will not appear for users without this permission
    val SEND_MESSAGES = 0x00000800	// Allows for sending messages in a channel.
    val SEND_TTS_MESSAGES = 0x00001000	// Allows for sending of /tts messages
    val MANAGE_MESSAGES = 0x00002000 // *	Allows for deletion of other users messages
    val EMBED_LINKS	= 0x00004000	// Links sent by this user will be auto-embedded
    val ATTACH_FILES = 0x00008000	// Allows for uploading images and files
    val READ_MESSAGE_HISTORY = 0x00010000	// Allows for reading of message history
    val MENTION_EVERYONE = 0x00020000	// Allows for using the @everyone tag to notify all users in a channel, and the @here tag to notify all online users in a channel
    val CONNECT = 0x00100000	// Allows for joining of a voice channel
    val SPEAK = 0x00200000	// Allows for speaking in a voice channel
    val MUTE_MEMBERS = 0x00400000	// Allows for muting members in a voice channel
    val DEAFEN_MEMBERS = 0x00800000	// Allows for deafening of members in a voice channel
    val MOVE_MEMBERS = 0x01000000	// Allows for moving of members between voice channels
    val USE_VAD = 0x02000000	// Allows for using voice-activity-detection in a voice channel
    val CHANGE_NICKNAME = 0x04000000	// Allows for modification of own nickname
    val MANAGE_NICKNAMES = 0x08000000	// Allows for modification of other users nicknames
    val MANAGE_ROLES = 0x10000000  // *	Allows management and editing of roles


    val usedPermission = READ_MESSAGES ^ SEND_MESSAGES ^ CONNECT ^ SPEAK ^ SEND_TTS_MESSAGES
    // wanted = 3152896
    // https://discordapp.com/oauth2/authorize?client_id=211993132529614849&scope=bot&permissions=3152896

  }
}

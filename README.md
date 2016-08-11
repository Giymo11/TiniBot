Tini is back!
==================

This is a little discord Bot, aiming to be gimmicky and fun. Maybe even a bit useful!

Features not considered (at least for now) are:
* Anything that can be done faster the "proper" way
  - Yes, this means google search
* Anything that has no long term use
  - Funny gimmicks that wear off after a very short time are just not worth it
* Music playing 
  - there are special bots for this


Contribution Guide
-----------------

1. [Download Typesafe Activator](http://typesafe.com/platform/getstarted)
2. Extract the zip and run the `activator` or `activator.bat` script from a non-interactive shell
3. Your browser should open to the Activator UI: [http://localhost:8888](http://localhost:8888)

or

1. Download sbt
2. (Optional) Open the project in IntelliJ using `File > New > Project from Existing Sources > Model: SBT`

then:

Create a new application in your discord developer console (only the Name is needed).
Make this a Bot user.

Create two environment variables:
* TINI_TOKEN - the bot token from your discord authorization site.
* TINI_PASSWORD - the password with which you want to kill the bot.

You can use the following ways to run the bot:
* To run it locally: `sbt bot/run`
* To assemble a jar-file: `sbt bot/assembly` and then `java -jar bot/target/scala-2.11/TiniBot.jar`
* To build a docker image: `sbt bot/docker` and then `docker run -d -e "TINI_TOKEN=xxx" -e "TINI_PASSWORD=xxx" giymo11/tinibot`

Finally, to make your bot join, modify and then open this link: `https://discordapp.com/oauth2/authorize?client_id=<CLIENT_ID from discord>&scope=bot&permissions=3152896`
Example: `https://discordapp.com/oauth2/authorize?client_id=211993132529614849&scope=bot&permissions=3152896`

Usage
-----------------

To make the bot join, click [here](https://discordapp.com/oauth2/authorize?client_id=211993132529614849&scope=bot&permissions=3152896)
Until now, Tini can:
* `!help` - Display help Dialog
* `!catfacts` - Display a cat fact.
* respond to everything with an opinion.
* `!shutup` - not do that anymore.
* `!8ballmode` - do that again.
* `!roll <lower> <upper>`, example: `!roll 1 10`
* `!roll <count>d<sides>`, example: `!roll 2d6`
* commit seppuku when you PM it `!kill <password>` where you substitute the <password> with the one in your `TINI_PASSWORD` environment variable.

Wishlist
-----------------

* Repeat a command (every x minutes, etc)! <-- amazing technology!
* Subscribe to MAL profiles for announcements of activity
  - should only updates been shown? i guess so
  - but then i have to store the already-known state. in DB? in memory?
  - can i re-use the repeat command for this? how to differentiate?
* Subscribe to Subreddits for announcements of activity
* Subscribe to Github profiles for announcemnets of activity
* Subscribe to Youtube profiles for announcements of activity
* Subscribe to ... you get the point.
* Maybe implement minigames like 
  - !heist.
  - 8ball
  - etc
* Display random files from my private EpicCollecshun(TM) (request by genre)
* Scrape and save the lewd pictures that get posted.
* Check if a reddit thread exists for a given link, and post it.
* Track the last time a given nick was in the channel, and the last time they spoke.
* !russian-roulette
* Play yandere simulator
* Subscribe to RSS feeds?
* Collect stats about the channels/guilds
* insult/insult-add
* sometimes just change its status to inappropriate stuff.
* Play silly sounds (from collection?)
* Linking discord and other profiles e.g. Steam and Batlle.net

Afterthoughts:
* custom "choose one of a list" commands using gist or pastebin (like catfacts, insult, etc) and access them via `!tell add <name> <url>` and then  `!tell <name>`
  - maybe even over-engineer it an do "tell add" and "tell set" to differentiating adding e.g. additional car facts. This could also be done via just message, i guess.
* setting to change the bot-escape-char for one channel, e.g. `!setchar *` to now require `*command` all the time.
* add points like NadekoFlowers to gamble in minigames
  - heist!
  - trivia game
  - and more like that
* at every command: notify users via PM if permissions are missing
* instead of always typing the admin PW, be able to tell the bot that you are admin and then do your stuff
* enable settings for if the bot should repspond via PM, via @mention, or just normally

From Nadekobot issues:
* google calendar integration
* give members a group based on which game they are currently playing
  - could then be used to have channels only for one game
* macro commands
  - to have custom command "scripts"
* bridge functionality
  - to IRC
  - to channels between servers
  - to Slack or even hangouts
* !remindme
  - email?
  - pm?
  - default = in current channel
* CARDS AGAINS HUMANITY! 
  - you get PMs for your white cards
  - the result is always shown in the text channel where it started


Version 2.0:
* A web dashboard with all kinds of awesome stuff.
* Like, I don't even want to start writing all this stuff down.

Moonshot:
* Replace the firebase database backend with a Postgres & Kafka setup.

Libraries used
-----------------

* [JDA](https://github.com/DV8FromTheWorld/JDA) // Way better than Discord4J. TODO: investigate multithreading efficiency.
* [monix](https://github.com/monixio/monix) // amazing! 
* [cats](https://github.com/typelevel/cats/) // Not used much yet. The Xor is nice I guess.
* [scalatest](https://github.com/scalatest/scalatest) // My favourite testing lib. Not that I write many, tho.


Plugins used
-----------------

* [sbt-updates](https://github.com/rtimush/sbt-updates)
* [sbt-assembly](https://github.com/sbt/sbt-assembly)
* [sbt-docker](https://github.com/marcuslonnberg/sbt-docker)
Tini is back!
==================

This is a little discord Bot, aiming to be gimmicky and fun. No serious features allowed here!


Setup
-----

1. [Download Typesafe Activator](http://typesafe.com/platform/getstarted)
2. Extract the zip and run the `activator` or `activator.bat` script from a non-interactive shell
3. Your browser should open to the Activator UI: [http://localhost:8888](http://localhost:8888)

or

1. Simply use IntelliJ to import the project from SBT source model. I just wanted to give activator a try.


Credentials
-----------------

Create two environment variables:
* TINI_TOKEN - the bot token from your discord authorization site.
* TINI_PASSWORD - the password with which you want to kill the bot.


Usage
-----------------

To make the bot join, click [here](https://discordapp.com/oauth2/authorize?client_id=211993132529614849&scope=bot&permissions=3152896)
Until now, Tini can:
* respond to everything with agreement.
* commit seppuku when you PM it `!kill <password>` where you substitute the <password> with the one in your `TINI_PASSWORD` environment variable.


Libraries used
-----------------

* [Discord4J](https://github.com/austinv11/Discord4J) // will probably be swapped for [JDA](https://github.com/DV8FromTheWorld/JDA) asap.
* [monix](https://github.com/monixio/monix) // amazing!
* [cats](https://github.com/typelevel/cats/) // not much used yet.


Plugins used
-----------------

* [sbt-updates](https://github.com/rtimush/sbt-updates)
* [sbt-assembly](https://github.com/sbt/sbt-assembly)
* [sbt-docker](https://github.com/marcuslonnberg/sbt-docker) // not used yet.
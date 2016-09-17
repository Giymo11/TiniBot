package rip.hansolo.discord.tini.brain


import com.google.firebase.database.{ChildEventListener, DataSnapshot, DatabaseError, ValueEventListener}

import scala.collection.mutable
import scala.collection.concurrent.TrieMap
import rip.hansolo.discord.tini.resources.{LocalSettings, Reference}


/**
	* Created by Giymo11 on 9/12/2016 at 5:16 PM.
	*/
object SettingsBrain {

	def init(): Unit = {

		def useData(dataSnapshot: DataSnapshot) = {
			import scala.collection.JavaConverters._
			println("In useData")
			println(dataSnapshot)
			val id = dataSnapshot.getKey
			val valueMap: Map[String, Object] = dataSnapshot.getValue().asInstanceOf[java.util.Map[String, Object]].asScala.toMap
			println(valueMap)
			println(valueMap.values.map(x => x.getClass))
			val settings = LocalSettings.fromMap(id, valueMap)
			println(settings)
			update(settings)
		}

		println("WHADDAP")

		TiniBrain.settings.addChildEventListener(new ChildEventListener {
			override def onChildRemoved(dataSnapshot: DataSnapshot) = update(LocalSettings(dataSnapshot.getKey))

			override def onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String) = ()

			override def onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String) = useData(dataSnapshot)

			override def onCancelled(databaseError: DatabaseError) = ()

			override def onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String) = useData(dataSnapshot)
		})
	}


	// TODO: read settings from firebase!
	// TODO: persist settings to firebase!
	// TODO: listen for changes of settings from firebase! (for future web-use)

	val map: mutable.Map[String, LocalSettings] = TrieMap[String, LocalSettings]().withDefault(id => LocalSettings(id))

	def getFor(id: String): LocalSettings = map(id)
	def getForPrivate(id: String): LocalSettings = map(id)

	def update(value: LocalSettings): map.type = {
		map += (value.id -> value)
	}
}

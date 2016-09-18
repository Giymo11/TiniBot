package rip.hansolo.discord.tini.brain


import scala.collection.concurrent.TrieMap

import com.google.firebase.database._

import rip.hansolo.discord.tini.resources._


/**
	* Created by Giymo11 on 9/12/2016 at 5:16 PM.
	*/
object SettingsBrain {

	def init(): Unit = {

		def useData(dataSnapshot: DataSnapshot) = {
			import scala.collection.JavaConverters._
			val id = dataSnapshot.getKey
			val valueMap: Map[String, Object] = dataSnapshot.getValue().asInstanceOf[java.util.Map[String, Object]].asScala.toMap
			val settings = LocalSettings.fromMap(id, valueMap)
			updateMap(settings)
		}

		println("WHADDAP")

		TiniBrain.settings.addChildEventListener(new ChildEventListener {
			override def onChildRemoved(dataSnapshot: DataSnapshot) = updateMap(LocalSettings(dataSnapshot.getKey))

			override def onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String) = ()

			override def onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String) = useData(dataSnapshot)

			override def onCancelled(databaseError: DatabaseError) = ()

			override def onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String) = useData(dataSnapshot)
		})
	}

	// TODO: listen for changes of settings from firebase! (for future web-use)

	import scala.collection.mutable
	val map: mutable.Map[String, LocalSettings] = TrieMap[String, LocalSettings]().withDefault(id => LocalSettings(id))

	def getFor(id: String): LocalSettings = map(id)
	def getForPrivate(id: String): LocalSettings = map(id)

	def update(value: LocalSettings): Unit = {
		import scala.collection.JavaConverters._

		val map = value.toMap
		val defaults = LocalSettings(value.id).toMap
		val differenceToDefaults: Map[String, Object] = map.toSeq.diff(defaults.toSeq).toMap
		val same: Map[String, Object] = map.toSeq.intersect(defaults.toSeq).toMap.mapValues(_ => null)

		println(map)
		println(defaults)
		println(differenceToDefaults)

		TiniBrain.settings.child(value.id).updateChildren((differenceToDefaults ++ same).asJava)
	}

	def updateMap(value: LocalSettings): map.type = {
		map += (value.id -> value)
	}
}

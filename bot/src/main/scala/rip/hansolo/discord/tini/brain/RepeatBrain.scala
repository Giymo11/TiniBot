package rip.hansolo.discord.tini.brain


import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.language.postfixOps

import monix.execution.Scheduler.Implicits.global
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.atomic.Atomic

import cats.data.Xor

import rip.hansolo.discord.tini.resources.{LocalSettings, MessageData}


/**
	* Created by Giymo11 on 9/29/2016 at 12:51 AM.
	*/
object RepeatBrain {

	private val repeatTasks = new TrieMap[String, ListBuffer[CancelableFuture[Unit]]]()

	def cancelAll(): Unit = for {
		(id, list) <- repeatTasks
		listEntry <- list
	} listEntry.cancel()

	def stopRepeat(channelId: String): Unit = repeatTasks.get(channelId) match {
		case Some(channel) => channel foreach { _.cancel() }
		case _ =>
	}

	def startRepeatTask(
    args: String,
    messageData: MessageData,
    count: Int,
    interval: Int,
    startTimestamp: String = null)
		(implicit brain: LocalSettings): Unit = {

			val channelId = messageData.getChannel.getId
			val now = ZonedDateTime.now
			val start = Xor.catchNonFatal(ZonedDateTime.parse(startTimestamp)).getOrElse(now)

			println("got jda")

			val task = Task {
				TextBrainRegion.exec(args.split(" ").toList, messageData)
			}.flatMap(_ => {
				val atomicCount = Atomic(count)
				Task {
					TextBrainRegion.exec(args.split(" ").toList, messageData)
				}
				.delayExecution(interval seconds)
				.restartUntil(_ => atomicCount.getAndDecrement(1) == 1 ) // because this gets the value before updating it
			})

			val taskWithInitialDelay: Task[Unit] = if(now.isAfter(start)) {
				// this means we are late!
				messageData.getChannel.sendMessageAsync("Sorry, we had an outage!", null)
				task
			} else {
				val timeToWait = ChronoUnit.SECONDS.between(now, start)
				task.delayExecution(timeToWait seconds)
			}

			val cancelableFuture = taskWithInitialDelay.runAsync
			val y = cancelableFuture andThen { case _ => repeatTasks(channelId) -= cancelableFuture } // can't use onFinish from Task cuz variable is not fully set

			val tasks = repeatTasks.getOrElseUpdate(messageData.getChannel.getId, new ListBuffer[CancelableFuture[Unit]])
			tasks += cancelableFuture
		}
}

package rip.hansolo.discord.tini.audio.util.mpegts

import java.nio.ByteBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 29.08.2016
  */
class MTSPacket(buffer: ByteBuffer) {
  private val  MTS_STATIC_HEADER = 4
  val MTS_PACKET_SIZE = 188


  /* mts magic byte */
  val magic: Int                        = buffer.get() & 0xFF

  /* internal buffer structure, do not touch! */
  private val `2_and_3_Byte`            = buffer.getShort()
  private val `4_Byte`                  = buffer.get() & 0xFF

  /* data starts here */
  val transportError                    = ( `2_and_3_Byte` & 0x8000 ) != 0
  val payloadStart                      = ( `2_and_3_Byte` & 0x4000 ) != 0
  val priority                          = ( `2_and_3_Byte` & 0x2000 ) != 0

  val pid: Int                          =   `2_and_3_Byte` & 0x1FFF
  val scrambling: Int                   =   `4_Byte`       & 0xFF

  val adaptionPresent                   = ( `4_Byte`       & 0xC0   ) != 0
  val payloadPresent                    = ( `4_Byte`       & 0x10   ) != 0

  val counter: Int                      =   `4_Byte`       & 0x0F

  /* adaption field ... ignore it, don't need it */
  private val adaptionFieldLen          =   buffer.get()   & 0xFF
  val payload                           =   buffer.array().drop( MTS_STATIC_HEADER + adaptionFieldLen )

}
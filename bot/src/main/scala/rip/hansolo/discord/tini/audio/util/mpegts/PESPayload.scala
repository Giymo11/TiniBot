package rip.hansolo.discord.tini.audio.util.mpegts

import java.nio.ByteBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 24.09.2016
  */
class PESPayload(buffer: ByteBuffer) {
  val startCode                       = buffer.getInt
  def streamID                        = startCode & 0xFF

  val packetLength                    = buffer.getShort // length after this field

  /* optional header may not be present! */
  private val PES_DATA_1              = buffer.get
  def markerBitsValid                 =   PES_DATA_1 & 0xC0
  def dataScrambled                   = ( PES_DATA_1 & 0x30 ) != 0
  def priority                        = ( PES_DATA_1 & 0x08 ) != 0
  def dataAlignmentIndicator          = ( PES_DATA_1 & 0x04 ) != 0
  def copyright                       = ( PES_DATA_1 & 0x02 ) != 0
  def original                        = ( PES_DATA_1 & 0x01 ) != 0

  private val PES_DATA_2              = buffer.get
  def pts_dts_indicator               = ( PES_DATA_2 & 0xC0 ) >>> 6 // 11 = both present, 01 is forbidden, 10 = only PTS, 00 = no PTS or DTS
  def escrFlag                        = ( PES_DATA_2 & 0x20 ) != 0
  def esRateFlag                      = ( PES_DATA_2 & 0x10 ) != 0
  def dsmTrickMode                    = ( PES_DATA_2 & 0x08 ) != 0
  def additionalCopyInfo              = ( PES_DATA_2 & 0x04 ) != 0
  def crcFlag                         = ( PES_DATA_2 & 0x02 ) != 0
  def extensionFlag                   = ( PES_DATA_2 & 0x01 ) != 0

  System.err.println("StartCode: " + startCode)
  System.err.println("packetLength: " + packetLength)
  System.err.println("markerBitsValid: " + markerBitsValid)
  System.err.println("dataScrambled: " + dataScrambled)
  System.err.println("priority: " + priority)
  System.err.println("dataAlignmentIndicator: " + dataAlignmentIndicator)
  System.err.println("copyright: " + copyright)
  System.err.println("original: " + original)



  val optionalFieldsLength            = buffer.get
  val optionalFields: Array[Byte]     = new Array[Byte]( optionalFieldsLength )
  buffer.get( optionalFields )        // not sure if i ever will need them

  //System.err.println("Pes Packet size: " + packetLength)
  //System.err.println("Pes Optional Fields: " + optionalFieldsLength)

  val payload                         = ByteBuffer.wrap( buffer.array().drop( MPEGTSPacket.MTS_STATIC_HEADER + packetLength + 4) )    // rest of the payload in packet
  val bodyPacketSize                  = Math.ceil( packetLength / (MPEGTSPacket.MTS_PACKET_SIZE - MPEGTSPacket.MTS_STATIC_HEADER) ).toInt
}

object PESPayload {

  def isPESPayload(mpegtsPacket: MPEGTSPacket) =
                                                mpegtsPacket.payloadUnitStartIndicator &&
                                                mpegtsPacket.payload.array()(0) == 0x0 &&
                                                mpegtsPacket.payload.array()(1) == 0x0 &&
                                                mpegtsPacket.payload.array()(2) == 0x1

  def parsePES(mPEGTSPacket: MPEGTSPacket): Option[PESPayload] = {
    if( isPESPayload(mPEGTSPacket) ) Some( new PESPayload(mPEGTSPacket.payload) )
    else None
  }
}
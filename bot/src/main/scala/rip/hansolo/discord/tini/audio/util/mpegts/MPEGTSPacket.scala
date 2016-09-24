package rip.hansolo.discord.tini.audio.util.mpegts

import java.io.InputStream
import java.nio.ByteBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 29.08.2016
  *
  * source: https://en.wikipedia.org/wiki/MPEG_transport_stream#Packet
  */
class MPEGTSPacket(buffer: ByteBuffer) {
  val MTS_PACKET_SIZE = 188
  //System.err.println("\nMTS-PACKET:")

  /* mts magic byte */
  val magic: Int                        = buffer.get() & 0xFF   // this is the first byte

  /* internal buffer structure, do not touch! */
  private val `2_and_3_Byte`            = buffer.getShort()
  private val `4_Byte`                  = buffer.get() & 0xFF

  /* data starts here */
  def transportError                    = ( `2_and_3_Byte` & 0x8000 ) != 0      //  Set when a demodulator can't correct errors from FEC data
  def payloadUnitStartIndicator         = ( `2_and_3_Byte` & 0x4000 ) != 0      //	Set when a PES, PSI, or DVB-MIP packet begins immediately following the header.
  def priority                          = ( `2_and_3_Byte` & 0x2000 ) != 0      //  Set when the current packet has a higher priority than other packets with the same PID

  def pid: Int                          =   `2_and_3_Byte` & 0x1FFF
  def scrambling: Int                   =   `4_Byte`       & 0xC0

  def adaptionValue                     =  `4_Byte`       & 0x30              // 01 – no adaptation field, payload only, 10 – adaptation field only, no payload,11 – adaptation field followed by payload,00 - RESERVED for future use
  def adaptionFieldPresent              = ( `4_Byte`       & 0x20   ) != 0
  def payloadPresent                    = ( `4_Byte`       & 0x10   ) != 0

  def counter: Int                      =   `4_Byte`       & 0x0F

  private val adaptionFieldLen          =   if( adaptionFieldPresent ) buffer.get & 0xFF else 0
  class AdaptionField {
    /* internal buffer */
    private val `5_Byte`                = if( adaptionFieldLen > 0 ) buffer.get & 0xFF else 0x0

    def discontinuityIndicator          = ( `5_Byte`       & 0x80 ) != 0
    def randomAccessIndicator           = ( `5_Byte`       & 0x40 ) != 0
    def streamPriorityIndicator         = ( `5_Byte`       & 0x20 ) != 0

    def pcrPresent                      = ( `5_Byte`       & 0x10 ) != 0
    def opcrPresent                     = ( `5_Byte`       & 0x08 ) != 0

    def splicingPointFlag               = ( `5_Byte`       & 0x04 ) != 0
    def privateTransportFlag            = ( `5_Byte`       & 0x02 ) != 0
    def isAdaptionFieldExtensionPresent = ( `5_Byte`       & 0x01 ) != 0

    class PCR {
      private val pcrData: Array[Byte]  = new Array[Byte]( MPEGTSPacket.PCR_DATA_LENGHT )
      buffer.get( pcrData )

       private val pcrDataBits          = ( (pcrData(0) & 0xffL) << 40 ) |
                                          ( (pcrData(1) & 0xffL) << 32 ) |
                                          ( (pcrData(2) & 0xffL) << 24 ) |
                                          ( (pcrData(3) & 0xffL) << 16 ) |
                                          ( (pcrData(4) & 0xffL) << 8  ) |
                                            (pcrData(5) & 0xffL)

      def base: Long                  = ( pcrDataBits   & 0xFFFFFFFF8000L ) >> 15
      def reserved: Byte              = ( ( pcrDataBits & 0x7E00 ) >> 9 ).asInstanceOf[Byte]
      def extension: Int              = ( pcrDataBits & 0x1FFL ).asInstanceOf[Int]

      def getValue: Long              = base * 300 + extension // magic from spec
    }
    val pcr: Option[PCR]                = if( pcrPresent  && adaptionFieldLen > MPEGTSPacket.PCR_DATA_LENGHT) Some(new PCR) else None
    val opcr: Option[PCR]               = if( opcrPresent && adaptionFieldLen > MPEGTSPacket.PCR_DATA_LENGHT) Some(new PCR) else None

    val spliceCountdown                   = if( splicingPointFlag ) buffer.get else 0
    val transportData:Option[Array[Byte]] = if( privateTransportFlag ) Some( extractData ) else None
    val extension :Option[Array[Byte]]    = if( isAdaptionFieldExtensionPresent ) Some( extractData ) else None

    private[this] def extractData: Array[Byte] = {
      val dataLen                       = buffer.get & 0xFF
      val data                          = new Array[Byte]( dataLen )

      if( dataLen > buffer.remaining() ) {
        System.err.println("ERR: will crash")
        buffer.array().foreach( x => System.err.print( (x & 0xFF).toHexString + " " ) )
        System.err.println()

        val d = buffer.remaining()
        buffer.get(d)
      } else {
        buffer.get(data)
      }

      data
    }
  }
  val adaptionField: Option[AdaptionField] = if( adaptionFieldPresent ) Some( new AdaptionField ) else None

  val payload: ByteBuffer                  = ByteBuffer.wrap( buffer.array().drop( MPEGTSPacket.MTS_STATIC_HEADER + adaptionFieldLen + 1) )

  //System.err.println( "PAYLOAD: " + payload.array()(0).toHexString + " " + payload.array()(1).toHexString + " " + payload.array()(2).toHexString + " ")
}

object MPEGTSPacket {
  val MTS_PACKET_SIZE = 188
  val MTS_SYNC_BYTE: Byte = 0x47
  val MTS_STATIC_HEADER = 4
  val PCR_DATA_LENGHT   = 6

  def canParse(data: Array[Byte]) = data(0) == MTS_SYNC_BYTE
  def findPacket(data: Array[Byte]) = data.dropWhile( _ != MTS_SYNC_BYTE )

  /* only corrects once! */
  def readPacket(in: InputStream): Option[MPEGTSPacket] = {
    val mtsRaw: Array[Byte] = new Array[Byte]( MTS_PACKET_SIZE )

    val bytesRead = in.read( mtsRaw, 0, MTS_PACKET_SIZE )
    if( bytesRead != MTS_PACKET_SIZE ) return None //End of Stream or so

    //System.err.println( "bytsRead: " + bytesRead + " / data: " )
    //mtsRaw.foreach( x => System.err.print( (x & 0xFF).toHexString.padTo(2,"0").mkString + " ") )
    //System.err.println()

    if( canParse(mtsRaw) ) Some( new MPEGTSPacket(ByteBuffer.wrap(mtsRaw)) )
    else { // must correct result
      val newMtsRaw = findPacket(mtsRaw)

      System.err.println("Must correct result")

      if( newMtsRaw.length == 0 ) None // no sync byte found in packet -> not there?
      else {
        val mtsCorrectedRaw: Array[Byte] = new Array[Byte]( MTS_PACKET_SIZE - newMtsRaw.length )
        val bytesRead = in.read( mtsRaw, 0, MTS_PACKET_SIZE )
        if( bytesRead != MTS_PACKET_SIZE ) return None //End of Stream or so

        //System.err.println( "Corrected Result: bytsRead: " + bytesRead + " / data: " )
        //(newMtsRaw ++ mtsCorrectedRaw).foreach( x => System.err.print(x.toHexString + " ") )

        Some( new MPEGTSPacket( ByteBuffer.wrap( newMtsRaw ++ mtsCorrectedRaw ) ) )
      }
    }
  }
}
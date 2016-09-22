package rip.hansolo.discord.tini.audio.util.mpegts

import java.nio.ByteBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 29.08.2016
  */
class MTSPacket(buffer: ByteBuffer) {
  private val MTS_STATIC_HEADER = 4
  private val PCR_DATA_LENGHT   = 6

  val MTS_PACKET_SIZE = 188

  /* mts magic byte */
  val magic: Int                        = buffer.get() & 0xFF   // this is the first byte

  /* internal buffer structure, do not touch! */
  private val `2_and_3_Byte`            = buffer.getShort()
  private val `4_Byte`                  = buffer.get() & 0xFF

  /* data starts here */
  def transportError                    = ( `2_and_3_Byte` & 0x8000 ) != 0
  def payloadStart                      = ( `2_and_3_Byte` & 0x4000 ) != 0
  def priority                          = ( `2_and_3_Byte` & 0x2000 ) != 0

  def pid: Int                          =   `2_and_3_Byte` & 0x1FFF
  def scrambling: Int                   =   `4_Byte`       & 0xC0

  def adaptionPresent                   = ( `4_Byte`       & 0x30   ) > 0x8
  def payloadPresent                    = ( `4_Byte`       & 0x10   ) != 0

  def counter: Int                      =   `4_Byte`       & 0x0F

  class AdaptionField {
    def adaptionFieldLen                =   buffer.get()   & 0xFF

    /* internal buffer */
    private val `5_Byte`                =   buffer.get()   & 0xFF

    def discontinuityIndicator          = ( `5_Byte`       & 0x80 ) != 0
    def randomAccessIndicator           = ( `5_Byte`       & 0x40 ) != 0
    def streamPriorityIndicator         = ( `5_Byte`       & 0x20 ) != 0

    def pcrPresent                      = ( `5_Byte`       & 0x10 ) != 0
    def opcrPresent                     = ( `5_Byte`       & 0x08 ) != 0

    def splicingPointFlag               = ( `5_Byte`       & 0x04 ) != 0
    def privateTransportFlag            = ( `5_Byte`       & 0x02 ) != 0
    def isAdaptionFieldExtensionPresent = ( `5_Byte`       & 0x01 ) != 0

    class PCR {
      private val pcrData: Array[Byte]  = new Array[Byte]( PCR_DATA_LENGHT )
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
    val pcr: Option[PCR]                = if( pcrPresent ) Some(new PCR) else None
    val opcr: Option[PCR]               = if( pcrPresent ) Some(new PCR) else None

    val spliceCountdown                 = if( splicingPointFlag ) buffer.get else 0
    val transportData:Option[Array[Byte]] = if( privateTransportFlag ) Some( extractData ) else None
    val extension :Option[Array[Byte]]    = if( isAdaptionFieldExtensionPresent ) Some( extractData ) else None

    private[this] def extractData: Array[Byte] = {
      val dataLen                       = 48//buffer.get & 0xFF
      val data                          = new Array[Byte]( dataLen )

      if( dataLen > buffer.remaining() ) {
        System.err.print("ERR: will crash")
      }

      buffer.get(data)
      data
    }
  }
  val adaptionField: Option[AdaptionField] = if( adaptionPresent ) Some(new AdaptionField) else None
  val payload: Option[ByteBuffer]       = if( payloadPresent ) Some( buffer.slice() ) else None
}
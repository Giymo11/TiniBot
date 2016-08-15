package rip.hansolo.discord.tiniweb.generic


import scala.language.postfixOps

import scalacss._
import scalacss.Defaults._


/**
  * Created by Giymo11 on 15.08.2016.
  */
object MyMaterialDesignTheme extends StyleSheet.Inline {
  import dsl._

  /*
  object DiscordColors {
    val blurple = c"#7289DA"
    val white = c"#FFFFFF"
    val greyple = c"#99AAB5"
    val dark = c"#2C2F33"
    val veryDark = c"#23272A"
  }
  */

  /*
    val m100 = c"#FFCDD2"
    val m300 = c"#E57373"
    val m500 = c"#F44336"
    val m700 = c"#D32F2F"
    val m900 = c"#B71C1C"
    */ // Red

  /*
  val a100 = c"#F4FF81"
  val a200 = c"#EEFF41"
  val a400 = c"#C6FF00"
  val a700 = c"#AEEA00"
  */ // Lime

  object GreyOrange {

    // grey m800 and m900
    val background = c"#212121"
    val backgroundAccented = c"#424242"

    // Deep Orange
    val a100 = c"#FF9E80"
    val a200 = c"#FF6E40"
    val a400 = c"#FF3D00"
    val a700 = c"#DD2C00"

    val textPrimary = c"#FFF"
    val textSecondary = c"#DDD"
  }

  object MaterialFonts {

    val lato = mixin(fontFamily :=! "'Lato', sans-serif")
    val sourceSansPro = mixin(fontFamily :=! "'Source Sans Pro', sans-serif")
    val roboto = mixin(fontFamily :=! "'Roboto', sans-serif")

    object Weight {
      val normal = "400"
      val light = "300"
      val medium = "500" // no 500 in Source Sans Pro
      val bold = "700"
    }

    type FontStyle = StyleS
    /**
      * Should only be single line!
      */
    val display3: FontStyle = mixin(
      fontSize(56 px),
      fontWeight :=! Weight.normal,
      letterSpacing :=! "-0.005em"
    )
    val display1: FontStyle = mixin(
      fontSize(34 px),
      fontWeight :=! Weight.normal,
      lineHeight(40 px)
    )
    val headline: FontStyle = mixin(
      fontSize(24 px),
      fontWeight :=! Weight.normal,
      lineHeight(32 px)
    )
    /**
      * App-bar title. Not headline!
      */
    val title: FontStyle = mixin(
      fontSize(20 px),
      fontWeight :=! Weight.medium,
      letterSpacing :=! "0.005em"
    )
    val body1: FontStyle = mixin(
      fontSize(14 px),
      fontWeight :=! Weight.normal,
      letterSpacing :=! "0.01em",
      lineHeight(20 px),
      maxWidth(720 px),
      marginBottom(16 px)
    )
    val button: FontStyle = mixin(
      fontSize(14 px),
      fontWeight :=! Weight.medium,
      textTransform.uppercase,
      letterSpacing :=! "0.01em"
    )
  }
}

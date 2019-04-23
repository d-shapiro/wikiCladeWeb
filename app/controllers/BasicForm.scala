package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class BasicForm(inputs: String, verbosity: Int)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  val form: Form[BasicForm] = Form(
    mapping(
      "inputs" -> nonEmptyText,
      "verbosity" -> number(min = 0, max = 100)
    )(BasicForm.apply)(BasicForm.unapply)
  )
}

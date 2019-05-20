package controllers

import play.api.data._
import play.api.data.Forms._

case class WikiCladeForm(inputs: String, verbosity: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object WikiCladeForm {
  val form: Form[WikiCladeForm] = Form(
    mapping(
      "inputs" -> nonEmptyText,
      "verbosity" -> nonEmptyText
    )(WikiCladeForm.apply)(WikiCladeForm.unapply)
  )
}

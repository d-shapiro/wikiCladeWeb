package controllers

import guru.nidi.graphviz.engine.{Graphviz, GraphvizEngine, GraphvizV8Engine}
import javax.inject._
import play.api.Logger
import play.api.mvc._
import play.twirl.api.Html

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {
  val logger: Logger = Logger(this.getClass)

  val paraphylyNote = "* Input groups marked with an asterisk may be paraphyletic/polyphyletic, meaning that their members dont share an immediate common ancestor. The clade that such a group has been associated with is the smallest one that contains all members of the group (although it also contains organisms that are not part of the group)."

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
//  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
//  }
//
//  def hello(name: String) = Action {
//    Ok(views.html.hello(name))
//  }
//
  def wikiCladeForm() = Action {  implicit request: Request[AnyContent] =>
    Ok(views.html.wikiCladeForm(WikiCladeForm.form.fill(WikiCladeForm("Cat, Dog, Pigeon", "normal"))))
  }

  def wikiCladeFormPost() = Action { implicit request: Request[AnyContent] =>
    WikiCladeForm.form.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.wikiCladeForm(formWithErrors))
      },
      formData => {
        val inpList: List[String] = formData.inputs.split(",").map(_.trim).toList
        Graphviz.useEngine(new GraphvizV8Engine())
        logger.info("Generating cladogram")
        val svg = cladograms.Main.svg(inpList, Some(formData.verbosity))
        logger.info("Generated cladogram")
        val footnote = if (svg.contains("*")) paraphylyNote else ""
        Ok(views.html.cladogram(Html(svg))(Html(footnote)))
      }
    )
  }
}

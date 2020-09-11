package controllers

import akka.actor.ActorSystem
import guru.nidi.graphviz.engine.{Graphviz, GraphvizEngine, GraphvizV8Engine}
import javax.inject._
import play.api.Logger
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) with play.api.i18n.I18nSupport {
  val logger: Logger = Logger(this.getClass)

  val paraphylyNote = "* Input groups marked with an asterisk may be paraphyletic/polyphyletic, meaning that their members don't share an immediate common ancestor. The clade that such a group has been associated with is the smallest one that contains all members of the group (although it also contains organisms that are not part of the group)."

  // Separate ExecutionContext for blocking wikiClade calls,
  // to prevent thread starvation.
  val cladogramExecutionContext: ExecutionContext =
    system.dispatchers.lookup("wikiclade-context")



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
  def wikiCladeForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.wikiCladeForm(WikiCladeForm.form.fill(WikiCladeForm("Cat, Dog, Pigeon", "normal"))))
  }

  def generateCladogram(inpList: List[String], verbosity: Option[String]): Future[String] = Future {
    logger.info("Generating cladogram")
    // TODO: Move Graphviz to client
    Graphviz.useEngine(new GraphvizV8Engine())
    val cladogramFuture = cladograms.Main.svg(inpList, verbosity)
    logger.info("Generated cladogram")
    cladogramFuture
  }(cladogramExecutionContext)

  def wikiCladeFormPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    WikiCladeForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.wikiCladeForm(formWithErrors))
      },
      formData => {
        val inpList: List[String] = formData.inputs.split(",").map(_.trim).toList

        generateCladogram(inpList, Some(formData.verbosity)).map{ svg =>
          val footnote = if (svg.contains("*")) paraphylyNote else ""
          Ok(views.html.cladogram(Html(svg))(Html(footnote)))
        }(cladogramExecutionContext)
      }
    )
  }


}

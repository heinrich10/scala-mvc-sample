package v1.user

import javax.inject.Inject

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(firstName: String, lastName: String, age: Int)

class UserController @Inject()(
  action: UserAction,
  handler: UserResourceHandler
  )(implicit ec: ExecutionContext) 
  extends Controller {
  
  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "firstName" -> text,
        "lastName" -> nonEmptyText,
        "age" -> number
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }
  
  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { users =>
        Ok(Json.toJson(users))
      }
    }
  }
  
  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonPost()
    }
  }
  
  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(id).map { user =>
        Ok(Json.toJson(user))
      }
    }
  }
  
   private def processJsonPost[A]()(
      implicit request: UserRequest[A]): Future[Result] = {
     
      def failure(badForm: Form[UserFormInput]) = {
        Future.successful(BadRequest)
      }

      def success(input: UserFormInput) = {
        handler.create(input).map { user =>
          Created(Json.toJson(user)).withHeaders(LOCATION -> user.id)
        }
      }

    form.bindFromRequest().fold(failure, success)
  }
}
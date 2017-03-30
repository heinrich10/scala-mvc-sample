package v1.user

import javax.inject.Inject

import play.api.http.HttpVerbs
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserRequest[A](request: Request[A], val messages: Messages)
  extends WrappedRequest(request)

class UserAction @Inject() (messagesApi: MessagesApi)
  (
    implicit ec: ExecutionContext
  ) extends ActionBuilder[UserRequest]
  with HttpVerbs {
  
  type UserRequestBlock[A] = UserRequest[A] => Future[Result]

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: UserRequestBlock[A]): Future[Result] = {
    if (logger.isTraceEnabled()) {
      logger.trace(s"invokeBlock: request = $request")
    }

    val messages = messagesApi.preferred(request)
    val future = block(new UserRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}
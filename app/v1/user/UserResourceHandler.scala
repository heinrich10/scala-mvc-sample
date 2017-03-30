package v1.user

import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class UserResource(id: String, firstName: String, lastName: String, age: Int)

object UserResource {
  implicit val implicitWrites = new Writes[UserResource] {
    def writes(user: UserResource): JsValue = {
      Json.obj(
          "id" -> user.id,
          "firstName" -> user.firstName,
          "lastName" -> user.lastName,
          "age" -> user.age
      )
    }
  }
}

class UserResourceHandler @Inject() (
    // routerProvider: Provider[UserRouter],
    userRepository: UserRepository
    )(implicit ec: ExecutionContext){
  
    def create(input: UserFormInput): Future[UserResource] = {
    val data = UserData(UserId("1"), input.firstName, input.lastName, input.age)
    // We don't actually create the post, so return what we have
    userRepository.create(data).map { id =>
      createUserResource(data)
    }
  }

  def lookup(id: String): Future[Option[UserResource]] = {
    val postFuture = userRepository.get(UserId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createUserResource(postData)
      }
    }
  }

  def find: Future[Iterable[UserResource]] = {
    userRepository.list().map { postDataList =>
      postDataList.map(postData => createUserResource(postData))
    }
  }

  private def createUserResource(user: UserData): UserResource = {
    UserResource(user.id.toString(), user.firstName, user.lastName, user.age)
  }
}
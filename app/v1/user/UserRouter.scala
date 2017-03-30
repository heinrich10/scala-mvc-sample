package v1.user

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class UserRouter @Inject()(controller: UserController) 
  extends SimpleRouter {
  
  var prefix = "/v1/users"
  
  def link(id: UserId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }
  
  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)
       
      
  }
}
package v1.user

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer

final case class UserData(id: UserId, firstName: String, lastName: String, age: Int)

class UserId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object UserId {
  def apply(raw: String): UserId = {
    require(raw != null)
    new UserId(Integer.parseInt(raw))
  }
}

class UserDataList() {
  var _list: ListBuffer[UserData] = new ListBuffer[UserData]();
  
  def add(data: UserData):String = {
    var counter: String = if (_list.size <= 0) "1" else (_list.size+1).toString()
    _list += new UserData(UserId(counter), data.firstName, data.lastName, data.age);
    counter;
  }
  
  def get = _list.toList;
  
}

trait UserRepository {
  def create(data: UserData): Future[UserId]
  def list(): Future[Iterable[UserData]]
  def get(id: UserId): Future[Option[UserData]]
}

@Singleton
class UserRepositoryImpl @Inject() extends UserRepository {
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  private val userList = new UserDataList();
  
  override def list(): Future[Iterable[UserData]] = {
    Future.successful {
      logger.trace(s"list: ")
      userList.get
    }
  }
  
  override def get(id: UserId): Future[Option[UserData]] = {
    Future.successful {
      logger.trace(s"get: id = $id")
       userList.get.find(user => user.id == id)
      
    }
  }

  def create(data: UserData): Future[UserId] = {
    Future.successful {
      logger.trace(s"create: data = $data")
      UserId(userList.add(data))
    }
  }
  
}

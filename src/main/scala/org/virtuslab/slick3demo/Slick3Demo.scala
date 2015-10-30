package org.virtuslab.slick3demo

import org.virtuslab.slick3demo.model.{User, Users}
import slick.dbio
import slick.dbio.Effect.{Read, Write}
import slick.profile.FixedSqlStreamingAction

//import slick.driver.PostgresDriver.api._

import slick.driver.PostgresDriver.api.{Session => _, _}
import slick.driver.PostgresDriver.backend.Session

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object Main extends App {
  val demo: Slick3Demo = new Slick3Demo()


  //demo.createUsersTable() //uncomment for first run
  demo.clearUsersTable()

  demo.printSeq(Seq("Start"))

  demo.slick2style()
  //  demo.slick3style()
}


class Slick3Demo {
  val db = Database.forConfig("mydb")

  def invokeAction[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])
                                                                                       (implicit session: Session): R = {
//    Invoker.invokeAction_runWithSession(action)
    Invoker.invokeAction_singleSessionDb(action)
  }

  def slick2style() = {
    db.withTransaction { implicit session =>
      println("autocommit: " + session.conn.getAutoCommit)
      insertUser(User(Some(1), "A1", "B1"))
      printSeq(selectAllUsers())
      insertUser(User(Some(2), "A2", "B2"))
      printSeq(selectAllUsers())
    }
  }

  def slick3style() = {
    val insertUser1Action = insertUserAction(User(Some(1), "A1", "B1"))
    val insertUser2Action = insertUserAction(User(Some(2), "A2", "B2"))
    val selectAllUsersAction1 = selectAllUsersAction()

    //    val mainAction =
    //      insertUser1Action
    //        .andThen(selectAllUsersAction1)
    //        .map (printSeq)
    //        .andThen(insertUser2Action)
    //        .andThen(selectAllUsersAction1)
    //        .map (printSeq)

    val mainAction =
      insertUser1Action
        .andThen(selectAllUsersAction1)
        .flatMap { users =>
          printSeq(users)
          insertUser2Action
            .andThen(selectAllUsersAction1)
            .map(printSeq)
        }

    Await.result(db.run(mainAction.transactionally), Duration.Inf)
  }

  // DBIOAction based database access

  def selectAllUsersAction(): FixedSqlStreamingAction[Seq[User], User, Read] = {
    Users.query.result
  }

  def insertUserAction(user: User): dbio.DBIOAction[Unit, NoStream, Write] = {
    DBIO.seq(Users.query += user)
  }

  // Slick 2 style calls. Wrap DBIOActions

  def selectAllUsers()(implicit session: Session): Seq[User] = {
    invokeAction(selectAllUsersAction())
  }

  def insertUser(user: User)(implicit session: Session): Unit = {
    invokeAction(insertUserAction(user))
  }

  // Other

  def printSeq[T](users: Seq[T]): Unit = {
    //breakpoint here
    users.foreach(println)
  }

  def createUsersTable(): Unit = {
    Await.result(db.run(Users.query.schema.create), Duration.Inf)
  }

  def clearUsersTable(): Int = {
    Await.result(db.run(Users.query.delete), Duration.Inf)
  }

}

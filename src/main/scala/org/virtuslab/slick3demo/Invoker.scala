package org.virtuslab.slick3demo

import slick.dbio.DBIOAction
import slick.jdbc.SingleSessionDb

//Following import causes an error:
/*
[error]  found   : slick.driver.PostgresDriver.api.Session
[error]     (which expands to)  slick.jdbc.JdbcBackend#SessionDef
[error]  required: _13.SessionDef where val _13: slick.jdbc.JdbcBackend
[error]     Await.result(db.runWithSession(action, session), queryTimeout)
 */
//import slick.driver.PostgresDriver.api._

//This works
import slick.driver.PostgresDriver.backend.Session

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Invoker {

  protected val queryTimeout = Duration.Inf

  def invokeAction_runWithSession[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])
                                                                       (implicit session: Session): R = {
    val db = session.database
    Await.result(db.runWithSession(action, session), queryTimeout)
  }

  def invokeAction_singleSessionDb[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])
                                                                       (implicit session: Session): R = {
    val db = session.database
    val singleSessionDb = SingleSessionDb.createFor(session, db.executor)
    Await.result(singleSessionDb.run(action), queryTimeout)
  }

}


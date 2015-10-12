package org.virtuslab.slick3demo.model

import slick.lifted.{TableQuery, Tag}

import slick.driver.PostgresDriver.api._

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey)
  def first = column[String]("first")
  def last = column[String]("last")
  def * = (id.?, first, last) <> (User.tupled, User.unapply)
}

object Users {
  val query = TableQuery[Users]
}
package guygo.chat.effects

sealed trait Page

object Page:
  case class Offset(value: Int, limit: Int = defaultLimit) extends Page

  val defaultLimit = 70

  val first = Offset(0)

sealed trait PagingMetadata

object PagingMetadata:
  case class Count(value: Int) extends PagingMetadata

object PaginationOps:
  extension [A](seq: Seq[A]) {
    def paginate(page: Page): Seq[A] =
      page match {
        case Page.Offset(value, limit) =>
          seq.slice(value, value + limit)
      }
  }


import cats.effect.IO
import cats.effect.unsafe.IORuntime

enum Permission {
  case View
  case Edit
  case Both(p1: Permission, p2: Permission)
}

trait Dsl {
  type T
  def requires(p: Permission): T
  def sequence(p1: T, p2: T): T
}

object syntax:
  def requires(p: Permission)(using dsl: Dsl): dsl.T = dsl.requires(p)
  extension [U](t: U)(using dsl: Dsl { type T = U }) def *>(p: U): U = dsl.sequence(t, p)

object Dsl {

  def compile(f: (dsl: Dsl) ?=> dsl.T): (dryRun: Boolean) => IO[Unit] = dryRun => {

    val describe = new Dsl:
      type T = String
      def requires(p: Permission): String = s"requires $p"
      def sequence(p1: String, p2: String): String = s"$p1, $p2"

    val run = new Dsl:
      type T = IO[Unit]
      def requires(p: Permission): IO[Unit] = IO.println(s"checking $p")
      def sequence(p1: IO[Unit], p2: IO[Unit]): IO[Unit] = p1 *> p2

    val x = f(
      using describe
    )
    val y = f(
      using run
    )

    if (dryRun)
      IO.println(x)
    else y
  }

}

@main
def run =
  Dsl
    .compile {
      import syntax.*

      requires(Permission.View)
        *> requires(Permission.Edit)

    }(dryRun = false)
    .unsafeRunSync()(IORuntime.global)

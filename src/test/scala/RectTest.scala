import jb.model.Rect
import org.scalatest.FunSuite

class RectTest extends FunSuite{

  test("Rect creation") {
    val rect = Rect(Array(0D), Array(1.5))
    assert(rect.mid(0) == 0.75)
  }

  test("Rect redefinition") {
    val rect = Rect(Array(0D), Array(1.5))
    rect.min = Array(0.5)
    assert(rect.mid(0) == 1)
  }

  test("Rect volume") {
    val rect = Rect(Array(0, 1, 2.5), Array(1, 3, 6.5))
    assert(rect.volume == 8)
  }

  test("Is within") {
    val rect = Rect(Array(-1, -1), Array(1, 1))
    assert(rect.isWithin(Array(-.2, -.3), Array(.4, .5)))
  }

}

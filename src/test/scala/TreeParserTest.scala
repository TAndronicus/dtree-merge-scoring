import jb.model.Rect
import jb.parser.TreeParser
import org.scalatest.FunSuite

class TreeParserTest extends FunSuite{

  test("labelCalculation") {
    // given
    val mins = Array(0D, 0D)
    val maxes = Array(2D, 2D)
    val rects = Array(
      Array(
        Rect(Array(0, 0), Array(1, 1)), // is within, 0
        Rect(Array(0, 1), Array(3, 2)), // is within, 0
        Rect(Array(0, 0), Array(2, 2), 1) // is within, 1
      ),
      Array(
        Rect(Array(0, 0), Array(2, 2)), //is within, 0
        Rect(Array(0, 0), Array(2, 2)), //is within, 0
        Rect(Array(1.5, 0), Array(2.5, 2), 1) // is not within 1
      ),
      Array(
        Rect(Array(0, 0), Array(2, 2), 1),
        Rect(Array(0, 0), Array(2, 2), 1),
      )
    )
    val res = TreeParser.calculateLabel(mins, maxes, rects)
    assert(res == 0D)
  }

  // TODO: Test for empties

}

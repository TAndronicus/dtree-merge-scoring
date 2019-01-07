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
        Rect(Array(0, 0), Array(1, 1))
      )
    )
    TreeParser.calculateLabel(mins, maxes, rects)
  }

}

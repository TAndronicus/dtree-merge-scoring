package jb.model

import org.scalatest.FunSuite

class IntegratedDecisionTreeModelTest extends FunSuite {

  test("point dist 2d") {
    val model = new IntegratedDecisionTreeModel(null, null)
    assert(model.pointDist(Array(1, 1), Array(4, 5)) == 5)
  }

  test("dist unsigned x") {
    // given
    val p = Array(1.5, 1.5)
    val edge = Edge(Array(0, -.5), Array(2, -.5))
    val model = new IntegratedDecisionTreeModel(null, null)

    // when
    val dist = model.distUnsigned(edge, p)

    // then
    assert(dist == 2)
  }

  test("dist unsigned y") {
    // given
    val p = Array(1.5, 1.5)
    val edge = Edge(Array(2, -.5), Array(2, 5))
    val model = new IntegratedDecisionTreeModel(null, null)

    // when
    val dist = model.distUnsigned(edge, p)

    // then
    assert(dist == .5)
  }

  test("dist unsigned non-overlapping") {
    // given
    val p = Array(1.5, 1.5)
    val edge = Edge(Array(5.5, -5), Array(5.5, -1.5))
    val model = new IntegratedDecisionTreeModel(null, null)

    // when
    val dist = model.distUnsigned(edge, p)

    // then
    assert(dist == 5)
  }

  test("min dist") {
    // given
    val p = Array(1.5, 1.5)
    val edges = Array(
      Edge(Array(5.5, -5), Array(5.5, -1.5)),
      Edge(Array(2, -.5), Array(2, 5)),
      Edge(Array(0, -.5), Array(2, -.5))
    )
    val model = new IntegratedDecisionTreeModel(null, null)

    // when
    val dist = model.minDistUnsigned(edges, p)

    // then
    assert(dist == .5)
  }

}

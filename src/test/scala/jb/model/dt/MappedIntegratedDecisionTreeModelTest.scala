package jb.model.dt

import jb.model.Edge
import org.scalatest.FunSuite

class MappedIntegratedDecisionTreeModelTest extends FunSuite {

  test("point dist") {
    val model = new MappedIntegratedDecisionTreeModel(null, null, null, null)
    val (x, y) = (2, 3.5)
    assert(model.pointDist(Array(x, y), Array(x + 3, y + 4)) == 5)
  }

  test("edge overlapping") {
    val model = new MappedIntegratedDecisionTreeModel(null, null, null, null)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(0, 1), Array(0, 2))
    val point = Array(1D, 0)

    assert(model.edgeOvelaps(edge0, point, 0))
    assert(!model.edgeOvelaps(edge0, point, 1))
    assert(!model.edgeOvelaps(edge1, point, 0))
    assert(!model.edgeOvelaps(edge1, point, 1))
  }

  test("distance from point to edge") {
    val model = new MappedIntegratedDecisionTreeModel(null, null, null, null)
    val point = Array(1D, 1)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(3, 0), Array(3, 5))
    val edge2 = Edge(Array(point(0) + 3, point(1) + 4), Array(point(0) + 3, point(1) + 6))
    
    assert(model.distUnsigned(edge0, point) == 1)
    assert(model.distUnsigned(edge1, point) == 2)
    assert(model.distUnsigned(edge2, point) == 5)
  }

  test("min distance from point to edge") {
    val model = new MappedIntegratedDecisionTreeModel(null, null, null, null)
    val point = Array(1D, 1)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(3, 0), Array(3, 5))
    val edge2 = Edge(Array(point(0) + 3, point(1) + 4), Array(point(0) + 3, point(1) + 6))
    assert(model.minDistUnsigned(Array(edge0, edge1, edge2), point) == 1)
  }

}

package com.daltao.template;

import java.util.*;

public class GeometryUtils {
    private static final double PREC = 1e-8;
    private static final double INF = 1e30;

    public static double valueOf(double x) {
        return x > -PREC && x < PREC ? 0 : x;
    }

    public static class Point2D {
        final double x;
        final double y;
        static final Point2D ORIGIN = new Point2D(0, 0);

        public Point2D(double x, double y) {
            this.x = valueOf(x);
            this.y = valueOf(y);
        }

        public double distance2Between(Point2D another) {
            double dx = x - another.x;
            double dy = y - another.y;
            return valueOf(dx * dx + dy * dy);
        }

        public double distanceBetween(Point2D another) {
            return valueOf(Math.sqrt(distance2Between(another)));
        }

        /**
         * 以自己为起点，判断线段a和b的叉乘
         */
        public double cross(Point2D a, Point2D b) {
            return GeometryUtils.cross(a.x - x, a.y - y, b.x - x, b.y - y);
        }
    }


    public static class Line2D {
        final Point2D a;
        final Point2D b;
        final Point2D d;
        static final Comparator<Line2D> SORT_BY_ANGLE = new Comparator<Line2D>() {
            @Override
            public int compare(Line2D a, Line2D b) {
                if (a.d.y > 0 && b.d.y > 0) {
                    return a.onWhichSide(b);
                }
                if (a.d.y < 0 && b.d.y < 0) {
                    return a.onWhichSide(b);
                }
                if (a.d.y == b.d.y) {
                    return Double.compare(a.d.x, b.d.x);
                }
                return -Double.compare(a.d.y, b.d.y);
            }
        };


        public Line2D(Point2D a, Point2D b) {
            this.a = a;
            this.b = b;
            d = new Point2D(b.x - a.x, b.y - a.y);
        }

        /**
         * 判断a处于b的哪个方向，返回1，表示处于逆时针方向，返回-1，表示处于顺时针方向。0表示共线。
         */
        public int onWhichSide(Line2D b) {
            return Double.compare(cross(d.x, d.y, b.d.x, b.d.y), 0);
        }

        /**
         * 判断pt处于自己的哪个方向，返回1，表示处于逆时针方向，返回-1，表示处于顺时针方向。0表示共线。
         */
        public int whichSideIs(Point2D pt) {
            return Double.compare(a.cross(b, pt), 0);
        }

        public double getSlope() {
            return a.y / a.x;
        }

        public double getB() {
            return a.y - getSlope() * a.x;
        }

        public Point2D intersect(Line2D another) {
            double m11 = b.x - a.x;
            double m01 = another.b.x - another.a.x;
            double m10 = a.y - b.y;
            double m00 = another.a.y - another.b.y;

            double div = valueOf(m00 * m11 - m01 * m10);
            if (div == 0) {
                return null;
            }

            double v0 = (another.a.x - a.x) / div;
            double v1 = (another.a.y - a.y) / div;

            double alpha = m00 * v0 + m01 * v1;
            return getPoint(alpha);
        }

        /**
         * 获取与线段的交点，null表示无交点或有多个交点
         */
        public Point2D getPoint(double alpha) {
            return new Point2D(a.x + d.x * alpha, a.y + d.y * alpha);
        }
    }

    public static class Segment2D extends Line2D {
        public Segment2D(Point2D a, Point2D b) {
            super(a, b);
        }

        /**
         * 判断p是否落在线段section上
         */
        public boolean contain(Point2D p) {
            return cross(p.x - a.x, p.y - a.y, d.x, d.y) == 0
                    && valueOf(p.x - Math.min(a.x, b.x)) >= 0 && valueOf(p.x - Math.min(a.x, b.x)) <= 0
                    && valueOf(p.y - Math.min(a.y, b.y)) >= 0 && valueOf(p.y - Math.min(a.y, b.y)) <= 0;
        }

        /**
         * 获取与线段的交点，null表示无交点或有多个交点
         */
        public Point2D intersect(Segment2D another) {
            Point2D point = super.intersect(another);
            return point != null && contain(point) ? point : null;
        }

    }

    /**
     * 计算两个向量的叉乘
     */
    public static double cross(double x1, double y1, double x2, double y2) {
        return valueOf(x1 * y2 - y1 * x2);
    }

    public static int signOf(double x) {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }

    public static class Area {
        public double areaOfRect(Line2D a, Line2D b) {
            return Math.abs(cross(a.d.x, a.d.y, b.d.x, b.d.y));
        }

        public double areaOfTriangle(Line2D a, Line2D b) {
            return areaOfRect(a, b) / 2;
        }
    }


    public static class GrahamScan {
        ConvexHull convex;

        public GrahamScan(List<Point2D> point2s) {
            final Point2D[] points = point2s.toArray(new Point2D[0]);
            int n = points.length;
            Memory.swap(points, 0, Memory.min(points, 0, n, new Comparator<Point2D>() {
                @Override
                public int compare(Point2D a, Point2D b) {
                    return a.y != b.y ? Double.compare(a.y, b.y) : Double.compare(a.x, b.x);
                }
            }));

            Comparator<Point2D> cmp = new Comparator<Point2D>() {
                @Override
                public int compare(Point2D o1, Point2D o2) {
                    return signOf(valueOf(-points[0].cross(o1, o2)));
                }
            };
            Arrays.sort(points, 1, n, cmp);

            int shrinkSize = 2;
            for (int i = 2; i < n; i++) {
                if (cmp.compare(points[i], points[shrinkSize - 1]) == 0) {
                    if (points[i].distance2Between(points[0]) > points[shrinkSize - 1].distance2Between(points[0])) {
                        points[shrinkSize - 1] = points[i];
                    }
                } else {
                    points[shrinkSize++] = points[i];
                }
            }

            n = shrinkSize;
            Deque<Point2D> stack = new ArrayDeque(n);
            stack.addLast(points[0]);
            for (int i = 1; i < n; i++) {
                while (stack.size() >= 2) {
                    Point2D last = stack.removeLast();
                    Point2D second = stack.peekLast();
                    if (valueOf(second.cross(points[i], last)) < 0) {
                        stack.addLast(last);
                        break;
                    }
                }
                stack.addLast(points[i]);
            }

            convex = new ConvexHull(new ArrayList(stack));
        }
    }

    public static class StaticHalfConvexHull {
        Deque<Line2D> deque;

        public StaticHalfConvexHull(List<Line2D> lineList) {
            Line2D[] lines = lineList.toArray(new Line2D[0]);
            int n = lines.length;
            Arrays.sort(lines, new Comparator<Line2D>() {
                @Override
                public int compare(Line2D a, Line2D b) {
                    return a.onWhichSide(b);
                }
            });
            deque = new ArrayDeque<>(n);
            for (int i = 0; i < n; i++) {
                Line2D line = lines[i];
                while (i + 1 < n && line.onWhichSide(lines[i]) == 0) {
                    if (line.whichSideIs(lines[i].b) < 0) {
                        line = lines[i];
                    }
                    i++;
                }
                while (deque.size() >= 2) {
                    Line2D last = deque.removeLast();
                    Point2D pt = last.intersect(deque.peekLast());
                    if (line.whichSideIs(pt) > 0) {
                        deque.addLast(last);
                        break;
                    }
                }
                deque.addLast(line);
            }
        }
    }

    public static class ConvexHull extends Polygon {
        private ConvexHull(List<Point2D> points) {
            super(points);
        }
    }

    public static class HalfPlaneIntersection {
        ConvexHull convex;

        public HalfPlaneIntersection(List<Line2D> lineList) {
            List<Line2D> topList = new ArrayList<>(lineList.size());
            List<Line2D> bottomList = new ArrayList<>(lineList.size());
            for (Line2D line : lineList) {
                if (line.d.y > 0) {
                    topList.add(line);
                } else if (line.d.y < 0) {
                    bottomList.add(line);
                } else if (line.d.x >= 0) {
                    topList.add(line);
                } else {
                    bottomList.add(line);
                }
            }
            StaticHalfConvexHull topConvexHull = new StaticHalfConvexHull(topList);
            StaticHalfConvexHull bottomConvexHull = new StaticHalfConvexHull(bottomList);
            while()
        }
    }

    public static class Polygon {
        List<Point2D> points;

        private Polygon(List<Point2D> points) {
            this.points = points;
        }
    }
}

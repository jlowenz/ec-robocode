package com.imaginaryday.util;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * One of Java's "missing features", the Tuple provides 1-5 tuple constructs
 * for use when returning multiple values.
 *
 * <p><table> <tr><td>User:</td><td>jlowens</td></tr> <tr><td>Date:</td><td>Apr 14,
 * 2006</td></tr> <tr><td>Time:</td><td>5:58:12 PM</td></tr> </table>
 */
public abstract class Tuple implements Serializable {
    private static final long serialVersionUID = 5444405677010616052L;

    public abstract <a> a get(int i);

	public static class One<a> extends Tuple
	{
		private a A;
        public One() { this.A = null; }
        public One(a obj) { this.A = obj; }
		public a first() { return A; }
        public a getFirst() { return A; } // FunctionalJ compatibility
        public <z> z get(int i)
		{
			try {
				Class c = getClass();
				Field f = c.getField(nameFromInt(i));
				f.setAccessible(true);
				return (z)f.get(this);
			} catch (NoSuchFieldException e)
			{
				throw new RuntimeException("Couldn't access index " + i + " of tuple " + this);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}

		private String nameFromInt(int i) { return Character.valueOf((char)('A' + (char)i)).toString(); }
	}

	public static class Two<a,b> extends One<a>
	{
		private b B;
        public Two() { this.B = null; }
        public Two(a a, b b) { super(a); B = b; }
		public b second() { return B; }
        public b getSecond() { return B; } // FunctionalJ compatibility
	}

	public static class Three<a,b,c> extends Two<a,b>
	{
		private c C;
		public Three(a a, b b, c c) { super(a,b); C = c; }
		public c third() { return C; }
        public c getThird() { return C; }  // FunctionalJ compatibility
    }

	public static class Four<a,b,c,d> extends Three<a,b,c>
	{
		private d D;
		public Four(a a, b b, c c, d d) { super(a,b,c); D = d; }
		public d fourth() { return D; }
        public d getFourth() { return D; }  // FunctionalJ compatibility
	}

	public static class Five<a,b,c,d,e> extends Four<a,b,c,d>
	{
		private e E;
		public Five(a a, b b, c c, d d, e e) { super(a,b,c,d); E = e; }
		public e fifth() { return E; }
	}

	public static <a> One<a> one(a A) { return new One<a>(A); }
	public static <a> One<a> tuple(a A) { return new One<a>(A); }

	public static <a,b> Two<a,b> two(a A, b B) { return new Two<a,b>(A,B); }
    public static <a,b> Two<a,b> pair(a A, b B) { return new Two<a,b>(A,B); }
    public static <a,b> Two<a,b> tuple(a A, b B) { return new Two<a,b>(A,B); }

	public static <a,b,c> Three<a,b,c> three(a A, b B, c C) { return new Three<a,b,c>(A,B,C); }
    public static <a,b,c> Three<a,b,c> triple(a A, b B, c C) { return new Three<a,b,c>(A,B,C); }
	public static <a,b,c> Three<a,b,c> tuple(a A, b B, c C) { return new Three<a,b,c>(A,B,C); }

	public static <a,b,c,d> Four<a,b,c,d> four(a A, b B, c C, d D) { return new Four<a,b,c,d>(A,B,C,D); }
	public static <a,b,c,d> Four<a,b,c,d> tuple(a A, b B, c C, d D) { return new Four<a,b,c,d>(A,B,C,D); }

	public static <a,b,c,d,e> Five<a,b,c,d,e> five(a A, b B, c C, d D, e E) { return new Five<a,b,c,d,e>(A,B,C,D,E); }
	public static <a,b,c,d,e> Five<a,b,c,d,e> tuple(a A, b B, c C, d D, e E) { return new Five<a,b,c,d,e>(A,B,C,D,E); }
}

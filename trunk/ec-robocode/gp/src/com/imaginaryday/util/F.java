package com.imaginaryday.util;

import javolution.util.FastList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Some functions (functional) utilities like anonymous functions and some
 * collection manipulation routines.
 *
 * <p><table> <tr><td>User:</td><td>jlowens</td></tr> <tr><td>Date:</td><td>Apr 14,
 * 2006</td></tr> <tr><td>Time:</td><td>10:04:52 PM</td></tr> </table>
 */
public abstract class F {

	public interface Callable<r>
	{
		r call(Object ... args);
	}

	public static abstract class lambda<r> implements Callable<r>
	{
		protected static final String ARGUMENT_SIZE_MISMATCH = "Argument size mismatch: you either have too many or too little arguments";
		protected static final String ARGUMENT_APPLICATION_OVERFLOW = "Can't apply more arguments than parameter count!";

		protected List _args = new ArrayList(5);
		protected int getNumArgs() { return 0; }
		public <l extends lambda<r>> l apply(Object[] o) {
			assert o.length > getNumArgs() : ARGUMENT_APPLICATION_OVERFLOW;
			_args = new ArrayList();
			for (Object arg : o) { _args.add(arg); }
			return (l)this;
		}
	}

	public static abstract class lambda1<r,a> extends lambda<r>
	{
		protected abstract r _call(a A);
		public r call(Object ... args) {
			assert (args.length + _args.size()) == 1 : ARGUMENT_SIZE_MISMATCH;
			if (_args.size() > 0) { return _call((a)_args.get(0)); }
			else { return _call((a)args[0]); }
		}
	}
	public static abstract class lambda2<r,a,b> extends lambda<r>
	{
		protected abstract r _call(a A, b B);
		public r call(Object ... args) {
			assert (args.length + _args.size()) == 2 : ARGUMENT_SIZE_MISMATCH;
			switch (_args.size()) {
				case 1: return _call((a)_args.get(0), (b)args[0]);
				case 2: return _call((a)_args.get(0), (b)_args.get(1));
				case 0:
				default: return _call((a)args[0], (b)args[1]);
			}
		}
	}
	public static abstract class lambda3<r,a,b,c> extends lambda<r>
	{
		protected abstract r _call(a A, b B, c C);
		public r call(Object ... args)
		{
			assert (args.length + _args.size()) == 3 : ARGUMENT_SIZE_MISMATCH;
			switch (_args.size()) {
				case 1: return _call((a)_args.get(0), (b)args[0], (c)args[1]);
				case 2: return _call((a)_args.get(0), (b)_args.get(1), (c)args[0]);
				case 3: return _call((a)_args.get(0), (b)_args.get(1), (c)_args.get(2));
				case 0:
				default: return _call((a)args[0], (b)args[1], (c)args[2]);
			}
		}
	}


	/**
	 * Add to collection and return added value.
	 * @param c
	 * @param elt
	 * @return the first element value added to the collection
	 */
	public static <r> r addr(Collection<r> c, r ... elt) {
		for (r e : elt) c.add(e);
		return elt[0];
	}

	public static <k,v> v putr(Map<k,v> m, k key, v val) {
		m.put(key,val);
		return val;
	}

	public static <t> void addarray(Collection<t> c, t[] arr)
	{
		for (t el : arr) c.add(el);
	}

	public static <a,r,c extends Collection<r>> List<a> map(lambda1<a,r> l, c c) {
		List<a> newc = new FastList<a>();
		for (r el : c) { newc.add(l.call(el)); }
		return newc;
	}

    public static <a,b,c extends List<a>> b fold(lambda2<b,a,b> f, c L, b y) {
        if (L.size() == 0) return y;
//        else return f.call(L.get(0), fold(f,L.subList(1,L.size()),y));
        else { // do it iteratively (i think its actually a foldr
            b val = f.call(y,L.get(0));
            for (a A : L.subList(1,L.size())) val = f.call(val,A);
            return val;
        }
    }

    public static <a extends Comparable<a>> a max(List<a> vals) {
        a v = null;
        for (a A : vals) {
            if (v == null) v = A;
            else if (A.compareTo(v) > 0) v = A;
        }
        return v;
    }

    public static <a, c extends Collection<a>> void foreach(c c, lambda1<?,a> lambda1) {
		for (a el : c) { lambda1.call(el); }
	}
}

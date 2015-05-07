package divergenceApproach;

import java.util.List;

/* Copyright (C) 2003 Univ. of Massachusetts Amherst, Computer Science Dept.
This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
http://www.cs.umass.edu/~mccallum/mallet
This software is provided under the terms of the Common Public License,
version 1.0, as published by http://www.opensource.org.  For further
information, see the file `LICENSE' included with this distribution. */

/**
* 
* 
* @author <a href="mailto:casutton@cs.umass.edu">Charles Sutton</a>
* @version $Id: ArrayUtils.java,v 1.1 2007/10/22 21:37:40 mccallum Exp $
*/
public class DivergenceUtil {
public static final double log2 = Math.log(2);
 /**
  * Returns the KL divergence, K(p1 || p2).
  *
  * The log is w.r.t. base 2. <p>
  *
  * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
  * is <tt>infinite</tt>. Limin changes it to zero instead of infinite. 
  * 
  */
 public static double klDivergence(List<Double> list, List<Double> list2) {
   double klDiv = 0.0;
   for (int i = 0; i < list.size(); ++i) {
     if (list.get(i) == 0) { continue; }
     if (list2.get(i) == 0.0) { continue; } // Limin

   klDiv += list.get(i) * Math.log( list.get(i) / list2.get(i) );
   }
//System.out.println("diver : " +klDiv / log2 ) ; 
   return klDiv / log2; // moved this division out of the loop -DM
 }
 
 public static double jsDivergence(List<Double> list, List<Double> list2, List<Double> avg) {
	 return (klDivergence(list, avg) + klDivergence(list2, avg))/2d;
 }
 

}
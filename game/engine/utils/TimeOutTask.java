/*
 * Copyright (c) 2008 University of Szeged
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package game.engine.utils;

import java.util.concurrent.Callable;

/**
 * Measures the elapsed time of a function that returns the specified type of 
 * object.
 * @param <R> type of result to be returned
 */
public interface TimeOutTask<R> extends Callable<R> {
  /**
   * Elapsed time for computing the result.
   * @return elapsed time
   */
  public long getElapsed();
}

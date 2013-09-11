/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.helger.jcodemodel;

/**
 * JClass for generating expressions containing operators
 */

abstract public class JOp
{

  private JOp ()
  {}

  /**
   * Determine whether the top level of an expression involves an operator.
   */
  static boolean hasTopOp (final JExpression e)
  {
    return (e instanceof UnaryOp) || (e instanceof BinaryOp);
  }

  /* -- Unary operators -- */

  static private class UnaryOp extends JExpressionImpl
  {

    protected String op;
    protected JExpression e;
    protected boolean opFirst = true;

    UnaryOp (final String op, final JExpression e)
    {
      this.op = op;
      this.e = e;
    }

    UnaryOp (final JExpression e, final String op)
    {
      this.op = op;
      this.e = e;
      opFirst = false;
    }

    public void generate (final JFormatter f)
    {
      if (opFirst)
        f.p ('(').p (op).g (e).p (')');
      else
        f.p ('(').g (e).p (op).p (')');
    }

  }

  public static JExpression minus (final JExpression e)
  {
    return new UnaryOp ("-", e);
  }

  /**
   * Logical not <tt>'!x'</tt>.
   */
  public static JExpression not (final JExpression e)
  {
    if (e == JExpr.TRUE)
      return JExpr.FALSE;
    if (e == JExpr.FALSE)
      return JExpr.TRUE;
    return new UnaryOp ("!", e);
  }

  public static JExpression complement (final JExpression e)
  {
    return new UnaryOp ("~", e);
  }

  static private class TightUnaryOp extends UnaryOp
  {

    TightUnaryOp (final JExpression e, final String op)
    {
      super (e, op);
    }

    @Override
    public void generate (final JFormatter f)
    {
      if (opFirst)
        f.p (op).g (e);
      else
        f.g (e).p (op);
    }

  }

  public static JExpression incr (final JExpression e)
  {
    return new TightUnaryOp (e, "++");
  }

  public static JExpression decr (final JExpression e)
  {
    return new TightUnaryOp (e, "--");
  }

  /* -- Binary operators -- */

  static private class BinaryOp extends JExpressionImpl
  {

    String op;
    JExpression left;
    JGenerable right;

    BinaryOp (final String op, final JExpression left, final JGenerable right)
    {
      this.left = left;
      this.op = op;
      this.right = right;
    }

    public void generate (final JFormatter f)
    {
      f.p ('(').g (left).p (op).g (right).p (')');
    }

  }

  public static JExpression plus (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("+", left, right);
  }

  public static JExpression minus (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("-", left, right);
  }

  public static JExpression mul (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("*", left, right);
  }

  public static JExpression div (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("/", left, right);
  }

  public static JExpression mod (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("%", left, right);
  }

  public static JExpression shl (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("<<", left, right);
  }

  public static JExpression shr (final JExpression left, final JExpression right)
  {
    return new BinaryOp (">>", left, right);
  }

  public static JExpression shrz (final JExpression left, final JExpression right)
  {
    return new BinaryOp (">>>", left, right);
  }

  public static JExpression band (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("&", left, right);
  }

  public static JExpression bor (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("|", left, right);
  }

  public static JExpression cand (final JExpression left, final JExpression right)
  {
    if (left == JExpr.TRUE)
      return right;
    if (right == JExpr.TRUE)
      return left;
    if (left == JExpr.FALSE)
      return left; // JExpr.FALSE
    if (right == JExpr.FALSE)
      return right; // JExpr.FALSE
    return new BinaryOp ("&&", left, right);
  }

  public static JExpression cor (final JExpression left, final JExpression right)
  {
    if (left == JExpr.TRUE)
      return left; // JExpr.TRUE
    if (right == JExpr.TRUE)
      return right; // JExpr.FALSE
    if (left == JExpr.FALSE)
      return right;
    if (right == JExpr.FALSE)
      return left;
    return new BinaryOp ("||", left, right);
  }

  public static JExpression xor (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("^", left, right);
  }

  public static JExpression lt (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("<", left, right);
  }

  public static JExpression lte (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("<=", left, right);
  }

  public static JExpression gt (final JExpression left, final JExpression right)
  {
    return new BinaryOp (">", left, right);
  }

  public static JExpression gte (final JExpression left, final JExpression right)
  {
    return new BinaryOp (">=", left, right);
  }

  public static JExpression eq (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("==", left, right);
  }

  public static JExpression ne (final JExpression left, final JExpression right)
  {
    return new BinaryOp ("!=", left, right);
  }

  public static JExpression _instanceof (final JExpression left, final JType right)
  {
    return new BinaryOp ("instanceof", left, right);
  }

  /* -- Ternary operators -- */

  static private class TernaryOp extends JExpressionImpl
  {

    String op1;
    String op2;
    JExpression e1;
    JExpression e2;
    JExpression e3;

    TernaryOp (final String op1, final String op2, final JExpression e1, final JExpression e2, final JExpression e3)
    {
      this.e1 = e1;
      this.op1 = op1;
      this.e2 = e2;
      this.op2 = op2;
      this.e3 = e3;
    }

    public void generate (final JFormatter f)
    {
      f.p ('(').g (e1).p (op1).g (e2).p (op2).g (e3).p (')');
    }

  }

  public static JExpression cond (final JExpression cond, final JExpression ifTrue, final JExpression ifFalse)
  {
    return new TernaryOp ("?", ":", cond, ifTrue, ifFalse);
  }

}
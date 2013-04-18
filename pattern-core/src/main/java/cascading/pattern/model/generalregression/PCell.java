/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.pattern.model.generalregression;

import java.io.Serializable;
import java.math.BigInteger;

/** This Class represents PCell inside ParamMatrix */
public class PCell implements Serializable
  {
  private String parameterName;
  private double beta;
  private BigInteger df;

  public PCell( String parameterName, double beta, BigInteger df )
    {
    this.parameterName = parameterName;
    this.beta = beta;
    this.df = df;
    }

  public BigInteger getDf()
    {
    return df;
    }

  public void setDf( BigInteger df )
    {
    this.df = df;
    }

  public double getBeta()
    {
    return beta;
    }

  public void setBeta( double beta )
    {
    this.beta = beta;
    }

  public String getParameterName()
    {
    return parameterName;
    }

  public void setParameterName( String parameterName )
    {
    this.parameterName = parameterName;
    }

  /** @return String */
  public String toString()
    {
    StringBuilder buf = new StringBuilder();
    buf.append( "parameterName = " );
    buf.append( parameterName ).append( "," );
    buf.append( "df = " );
    buf.append( df ).append( "," );
    buf.append( "beta = " );
    buf.append( beta );

    return buf.toString();
    }
  }
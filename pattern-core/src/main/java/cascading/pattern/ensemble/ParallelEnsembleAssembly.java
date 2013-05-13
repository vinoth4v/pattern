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

package cascading.pattern.ensemble;

import java.util.ArrayList;
import java.util.List;

import cascading.pattern.ensemble.function.InsertGUID;
import cascading.pattern.ensemble.selection.SelectionBuffer;
import cascading.pattern.model.ModelSchema;
import cascading.pattern.model.ModelScoringFunction;
import cascading.pattern.model.Spec;
import cascading.pattern.model.tree.TreeFunction;
import cascading.pattern.model.tree.TreeSpec;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.pipe.SubAssembly;
import cascading.pipe.assembly.Discard;
import cascading.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ParallelEnsembleAssembly extends SubAssembly
  {
  private static final Logger LOG = LoggerFactory.getLogger( ParallelEnsembleAssembly.class );

  public ParallelEnsembleAssembly( Pipe pipe, EnsembleSpec ensembleSpec )
    {
    if( !ensembleSpec.getSelectionStrategy().isParallel() )
      throw new IllegalArgumentException( "given selection strategy must support parallel models" );

    List<Spec> modelSpecs = ensembleSpec.getModelSpecs();

    if( modelSpecs.size() < 2 )
      throw new IllegalArgumentException( "ensembles must have more than 1 model" );

    ModelSchema modelSchema = ensembleSpec.getModelSchema();

    Fields predictedFields = modelSchema.getPredictedFields();
    Fields keyFields = modelSchema.getKeyFields();

    if( keyFields.isNone() )
      {
      keyFields = new Fields( "ensemble-primary-key", String.class );
      pipe = new InsertGUID( pipe, keyFields );
      }

    List<Pipe> pipes = new ArrayList<Pipe>();

    for( int i = 0; i < modelSpecs.size(); i++ )
      {
      Spec spec = modelSpecs.get( i );

      if( spec instanceof TreeSpec )
        pipes.add( createScoringPipe( i, pipe, modelSchema, new TreeFunction( (TreeSpec) spec, true, false ) ) );
      }

    pipe = new GroupBy( "vote", pipes.toArray( new Pipe[ pipes.size() ] ), keyFields );

    pipe = new Every( pipe, predictedFields, new SelectionBuffer( ensembleSpec ), Fields.SWAP );

    if( modelSchema.getKeyFields().isNone() )
      pipe = new Discard( pipe, keyFields );

    setTails( pipe );
    }

  private Each createScoringPipe( int ordinal, Pipe tail, ModelSchema ensembleSchema, ModelScoringFunction function )
    {
    Fields inputFields = ensembleSchema.getInputFields();
    Fields declaredFields = ensembleSchema.getDeclaredFields();

    if( LOG.isDebugEnabled() )
      {
      LOG.debug( "creating: {}", function.getSpec() );
      LOG.debug( "input: {}, output: {}", inputFields, declaredFields );
      }

    tail = new Pipe( "model-" + ordinal, tail );

    return new Each( tail, inputFields, function, Fields.ALL );
    }
  }
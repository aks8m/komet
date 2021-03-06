/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributions from 2013-2017 where performed either by US government 
 * employees, or under US Veterans Health Administration contracts. 
 *
 * US Veterans Health Administration contributions by government employees
 * are work of the U.S. Government and are not subject to copyright
 * protection in the United States. Portions contributed by government 
 * employees are USGovWork (17USC §105). Not subject to copyright. 
 * 
 * Contribution by contractors to the US Veterans Health Administration
 * during this period are contractually contributed under the
 * Apache License, Version 2.0.
 *
 * See: https://www.usa.gov/government-works
 * 
 * Contributions prior to 2013:
 *
 * Copyright (C) International Health Terminology Standards Development Organisation.
 * Licensed under the Apache License, Version 2.0.
 *
 */



package sh.isaac.model.semantic.version;

import java.util.Arrays;
import org.glassfish.hk2.api.MultiException;
import sh.isaac.api.DataSource;
import sh.isaac.api.DataTarget;
import sh.isaac.api.Get;
import sh.isaac.api.LookupService;
import sh.isaac.api.chronicle.Version;
import sh.isaac.api.chronicle.VersionType;
import sh.isaac.api.component.semantic.version.MutableLogicGraphVersion;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.api.logic.LogicalExpression;
import sh.isaac.api.logic.LogicalExpressionByteArrayConverter;
import sh.isaac.model.logic.LogicalExpressionImpl;
import sh.isaac.model.semantic.SemanticChronologyImpl;

/**
 * The Class LogicGraphVersionImpl.
 *
 * @author kec
 */
public class LogicGraphVersionImpl
        extends AbstractVersionImpl
         implements MutableLogicGraphVersion {

   private static LogicalExpressionByteArrayConverter converter;

   byte[][] graphData = null;
   @Override
   public StringBuilder toString(StringBuilder builder) {
      builder.append(" ")
              .append("{graphData: ").append(graphData).append(" ")
              .append(Get.stampService()
                      .describeStampSequence(this.getStampSequence())).append("}");
      return builder;
   }

   /**
    * Instantiates a new logic graph semantic impl.
    *
    * @param container the container
    * @param stampSequence the stamp sequence
    */
   public LogicGraphVersionImpl(SemanticChronologyImpl container,
                               int stampSequence) {
      super(container, stampSequence);
   }

   /**
    * Instantiates a new logic graph semantic impl.
    *
    * @param container the container
    * @param stampSequence the stamp sequence
    * @param data the data
    */
   public LogicGraphVersionImpl(SemanticChronologyImpl container,
                               int stampSequence,
                               ByteArrayDataBuffer data) {
      super(container, stampSequence);

      final int graphNodes = data.getInt();

      this.graphData = new byte[graphNodes][];

      for (int i = 0; i < graphNodes; i++) {
         try {
            this.graphData[i] = data.getByteArrayField();
         } catch (final ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(e);
         }
      }

      if (data.isExternalData()) {
         this.graphData = getExternalDataConverter().convertLogicGraphForm(this.graphData, DataTarget.INTERNAL);
      }
   }
   
   private LogicGraphVersionImpl(LogicGraphVersionImpl other, int stampSequence) {
      super(other.getChronology(), stampSequence);
      this.graphData = new byte[other.graphData.length][];
      for (int i = 0; i < this.graphData.length; i++) {
         this.graphData[i] = other.graphData[i].clone();
      }
   }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends Version> V makeAnalog(int stampSequence) {
      final LogicGraphVersionImpl newVersion = new LogicGraphVersionImpl(this, stampSequence);
      getChronology().addVersion(newVersion);
      return (V) newVersion;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      sb.append(getSemanticType().toString());

      final LogicalExpressionImpl lg = new LogicalExpressionImpl(this.graphData,
                                                                           DataSource.INTERNAL,
                                                                              getReferencedComponentNid());

      sb.append("\n ");
      sb.append(lg.toString());
      toString(sb);
      return sb.toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeVersionData(ByteArrayDataBuffer data) {
      super.writeVersionData(data);

      byte[][] temp = this.graphData;

      if (data.isExternalData()) {
         temp = getExternalGraphData();
      }

      data.putInt(temp.length);

      for (final byte[] graphDataElement: temp) {
         data.putByteArrayField(graphDataElement);
      }
   }

   /**
    * Gets the external data converter.
    *
    * @return the external data converter
    * @throws MultiException the multi exception
    */
   private static LogicalExpressionByteArrayConverter getExternalDataConverter()
            throws MultiException {
      if (converter == null) {
         converter = LookupService.get()
                                  .getService(LogicalExpressionByteArrayConverter.class);
      }

      return converter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[][] getExternalGraphData() {
      return getExternalDataConverter().convertLogicGraphForm(this.graphData, DataTarget.EXTERNAL);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[][] getGraphData() {
      return this.graphData;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setGraphData(byte[][] graphData) {
      if (this.graphData != null) {
         checkUncommitted();
      }

      this.graphData = graphData;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LogicalExpression getLogicalExpression() {
      return new LogicalExpressionImpl(this.graphData, DataSource.INTERNAL, getReferencedComponentNid());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VersionType getSemanticType() {
      return VersionType.LOGIC_GRAPH;
   }
   

   /**
    * {@inheritDoc}
    */
   @Override
   protected int editDistance3(AbstractVersionImpl other, int editDistance) {
      LogicGraphVersionImpl otherImpl = (LogicGraphVersionImpl) other;
      if (!Arrays.deepEquals(this.graphData, otherImpl.graphData)) {
         editDistance++;
      }
      return editDistance;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean deepEquals3(AbstractVersionImpl other) {
      if (!(other instanceof LogicGraphVersionImpl)) {
         return false;
      }
      LogicGraphVersionImpl otherImpl = (LogicGraphVersionImpl) other;
      return Arrays.deepEquals(this.graphData, otherImpl.graphData);
   }
}
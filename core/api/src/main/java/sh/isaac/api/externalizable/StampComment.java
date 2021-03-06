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



package sh.isaac.api.externalizable;

/**
 * The Class StampComment.
 *
 * @author kec
 */
public class StampComment
         implements IsaacExternalizable {
   /** The comment. */
   private String comment;

   /** The stamp sequence. */
   private int stampSequence;

   //~--- constructors --------------------------------------------------------

   /**
    * Instantiates a new stamp comment.
    *
    * @param in the in
    */
   public StampComment(ByteArrayDataBuffer in) {
      if (in.getObjectDataFormatVersion() == IsaacObjectType.STAMP_COMMENT.getDataFormatVersion()) {
         this.stampSequence = StampUniversal.get(in)
                                            .getStampSequence();
         this.comment       = in.getUTF();
      } else {
         throw new UnsupportedOperationException("Can't handle version: " + in.getObjectDataFormatVersion());
      }
   }

   /**
    * Instantiates a new stamp comment.
    *
    * @param comment the comment
    * @param stampSequence the stamp sequence
    */
   public StampComment(String comment, int stampSequence) {
      this.comment       = comment;
      this.stampSequence = stampSequence;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Put external.
    *
    * @param out the out
    */
   @Override
   public void putExternal(ByteArrayDataBuffer out) {
      IsaacObjectType.STAMP_COMMENT.writeTypeVersionHeader(out);
      StampUniversal.get(this.stampSequence)
                    .writeExternal(out);
      out.putUTF(this.comment);
   }

   /**
    * To string.
    *
    * @return the string
    */
   @Override
   public String toString() {
      return "StampComment{" + "comment='" + this.comment + '\'' + ", stampSequence=" + this.stampSequence + '}';
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets the comment.
    *
    * @return the comment
    */
   public String getComment() {
      return this.comment;
   }

   /**
    * Gets the ochre object type.
    *
    * @return the ochre object type
    */
   @Override
   public IsaacObjectType getIsaacObjectType() {
      return IsaacObjectType.STAMP_COMMENT;
   }

   /**
    * Gets the stamp sequence.
    *
    * @return the stamp sequence
    */
   public int getStampSequence() {
      return this.stampSequence;
   }
   
   @Override
   public boolean equals(Object obj) {
       if (obj == null) {
           return false;
       }
       if (getClass() != obj.getClass()) {
           return false;
       }

       final StampComment other = (StampComment) obj;
       return this.getComment().equals(other.getComment()) && this.getStampSequence() == other.getStampSequence();
   }
}


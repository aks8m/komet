/*
 * Copyright 2017 Organizations participating in ISAAC, ISAAC's KOMET, and SOLOR development include the
         US Veterans Health Administration, OSHERA, and the Health Services Platform Consortium..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sh.komet.gui.cell.treetable;

import javafx.scene.control.TreeTableRow;
import sh.isaac.api.coordinate.ManifoldCoordinate;
import sh.isaac.api.observable.ObservableCategorizedVersion;
import sh.komet.gui.control.property.ViewProperties;

/**
 *
 * @author kec
 */
public class TreeTableConceptCell extends KometTreeTableCell<Integer> {
   private final ManifoldCoordinate manifoldCoordinate;

   protected TreeTableConceptCell() {
       throw new UnsupportedOperationException(
               "Manifold must be set. No arg constructor not allowed");
   }
   public TreeTableConceptCell(ManifoldCoordinate manifoldCoordinate) {
      if (manifoldCoordinate == null) {
         throw new IllegalArgumentException("manifold cannot be null");
      }
      this.manifoldCoordinate = manifoldCoordinate;
      getStyleClass().add("komet-version-concept-cell");
      getStyleClass().add("isaac-version");
   }

   @Override
   protected void updateItem(TreeTableRow<ObservableCategorizedVersion> row, Integer cellValue) {
       if (cellValue!= null) {
           setText(manifoldCoordinate.getPreferredDescriptionText(cellValue));
       }
         
   }
   
}

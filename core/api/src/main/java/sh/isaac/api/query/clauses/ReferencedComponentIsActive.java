/*
 * Copyright 2018 Organizations participating in ISAAC, ISAAC's KOMET, and SOLOR development include the
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
package sh.isaac.api.query.clauses;

import sh.isaac.api.Get;
import sh.isaac.api.chronicle.Chronology;
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.component.semantic.SemanticChronology;
import sh.isaac.api.coordinate.StampFilter;
import sh.isaac.api.query.*;
import sh.isaac.api.query.properties.StampCoordinateClause;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author kec
 */
public class ReferencedComponentIsActive extends LeafClause implements StampCoordinateClause {

    /**
     * the manifold coordinate key.
     */
    LetItemKey stampCoordinateKey;

    //~--- constructors --------------------------------------------------------
    /**
     * Instantiates a new component is active clause.
     */
    public ReferencedComponentIsActive() {
    }

    /**
     * Instantiates a new component is active clause.
     *
     * @param enclosingQuery the enclosing query
     * @param stampCoordinateKey the manifold coordinate key
     */
    public ReferencedComponentIsActive(Query enclosingQuery, LetItemKey stampCoordinateKey) {
        super(enclosingQuery);
        this.stampCoordinateKey = stampCoordinateKey;
    }

    //~--- methods -------------------------------------------------------------
    /**
     * Compute possible components.
     *
     * @param possibleComponents the incoming possible components
     * @return the nid set
     */
    @Override
    public final Map<ConceptSpecification, NidSet> computePossibleComponents(Map<ConceptSpecification, NidSet> possibleComponents) {
        StampFilter stampCoordinate = getLetItem(stampCoordinateKey);

        NidSet possibleComponentSet = possibleComponents.get(getAssemblageForIteration());
        for (int nid : possibleComponentSet.asArray()) {
            final Optional<? extends Chronology> chronology
                    = Get.identifiedObjectService()
                            .getChronology(nid);
            if (chronology.isPresent() && chronology.get() instanceof SemanticChronology) {
                SemanticChronology semanticChronology = (SemanticChronology) chronology.get();
                Optional<? extends Chronology> referencedComponentChronology
                        = Get.identifiedObjectService()
                                .getChronology(semanticChronology.getReferencedComponentNid());
                if (referencedComponentChronology.isPresent()
                        && !referencedComponentChronology.get()
                                .isLatestVersionActive(stampCoordinate)) {
                    possibleComponentSet.remove(nid);
                }
            } else {
                possibleComponentSet.remove(nid);
            }
        }
        return possibleComponents;

    }

    //~--- get methods ---------------------------------------------------------
    /**
     * Gets the compute phases.
     *
     * @return the compute phases
     */
    @Override
    public EnumSet<ClauseComputeType> getComputePhases() {
        return ITERATION;
    }

    @Override
    public ClauseSemantic getClauseSemantic() {
        return ClauseSemantic.REFERENCED_COMPONENT_IS_ACTIVE;
    }

    /**
     * Gets the where clause.
     *
     * @return the where clause
     */
    @Override
    public WhereClause getWhereClause() {
        final WhereClause whereClause = new WhereClause();

        whereClause.setSemantic(ClauseSemantic.REFERENCED_COMPONENT_IS_ACTIVE);
        return whereClause;
    }

    @Override
    public LetItemKey getStampCoordinateKey() {
        return stampCoordinateKey;
    }

    @Override
    public void setStampCoordinateKey(LetItemKey stampCoordinateKey) {
        this.stampCoordinateKey = stampCoordinateKey;
    }
}

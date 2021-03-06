/**
 * Copyright 2008 West Virginia University
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
 *
 */

/*
 * ObjectFilterTransformer.java
 *
 * Created on May 5, 2007, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.wvu.ferl.runtime;

import javax.rules.ObjectFilter;

import org.apache.commons.collections15.Transformer;

/**
 * A transformer adapter for the {@link ObjectFilter} interface.  If the object filter is null, the input object is
 * simply returned as the output object.
 *
 * @author jbunting
 */
class ObjectFilterTransformer implements Transformer<Object, Object> {

  private ObjectFilter objectFilter;

  /**
   * Creates a new instance of ObjectFilterTransformer
   *
   * @param objectFilter the object filter to adapt
   */
  public ObjectFilterTransformer(ObjectFilter objectFilter) {
    this.objectFilter = objectFilter;
  }

  /**
   * Transforms {@code object} by invoking {@link ObjectFilter#filter} on the object filter.
   *
   * @param object the object to transform
   * @return the result of the filtering
   */
  public Object transform(Object object) {
    if(objectFilter != null) {
      return objectFilter.filter(object);
    } else {
      return object;
    }
  }


}

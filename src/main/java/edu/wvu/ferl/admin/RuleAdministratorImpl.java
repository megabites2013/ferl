/*
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
 * RuleAdministrator.java
 *
 * Created on May 5, 2007, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.wvu.ferl.admin;

import edu.wvu.ferl.RuleServiceProvider;
import edu.wvu.ferl.store.RuleStore;
import edu.wvu.ferl.store.StoredRuleExecutionSet;
import edu.wvu.ferl.store.impl.StoredRuleExecutionSetImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.rules.ConfigurationException;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An implementation of the RuleAdministrator interface for ferl.  This class
 * registers RuleExecutionSets by adding them to the RuleStore configured for
 * the RuleServiceProvider.
 *
 * @author jbunting
 */
public class RuleAdministratorImpl implements RuleAdministrator {

  @SuppressWarnings({"UnusedDeclaration"})
  private static final Log logger =
          LogFactory.getLog(RuleAdministratorImpl.class);

  RuleServiceProvider serviceProvider;

  /**
   * Creates a new instance of RuleAdministrator
   *
   * @param serviceProvider the RuleServiceProvider that this Administrator object is attached to
   */
  public RuleAdministratorImpl(RuleServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  /**
   * Provides an instance of RuleExecutionSetProviderImpl.
   *
   * @param properties a map of properties to be used for the provider - currently has no effect
   * @return an instance of RuleExecutionSetProviderImpl
   */
  public RuleExecutionSetProviderImpl getRuleExecutionSetProvider(Map properties) {
    return new RuleExecutionSetProviderImpl(this);
  }

  /**
   * Provides an instance of RuleExecutionSetProviderImpl.
   *
   * @param properties a map of properties to be used for the provider - currently has no effect
   * @return an instance of RuleExecutionSetProviderImpl
   */
  public LocalRuleExecutionSetProvider getLocalRuleExecutionSetProvider(Map properties) {
    return new RuleExecutionSetProviderImpl(this);
  }

  /**
   * Registers a RuleExecutionSet by loading it to the RuleStore.  Only
   * RuleExecutionSets generated by the RuleExecutionProviderImpl
   * (returned by getRuleExecutionSetProvider or getLocalRuleExecutionSetProvider
   * on this class) may be registered with this method.  Any others will cause
   * a RuleExecutionSetRegisterException to be thrown.  Also during this method all
   * RuleDescriptors included with this RuleExecutionSet are processed.
   * RuleReference objects are verified with the RuleStore, while
   * RuleDetailedDescriptor objects cause a new Rule to be loaded into the
   * RuleStore.
   *
   * @param uri              the uri of the new RuleExecutionSet
   * @param ruleExecutionSet the RuleExecutionSet
   * @param properties       a map of properties to be used for the provider - currently has no effect
   * @throws RuleExecutionSetRegisterException
   *                                  thrown if anything goes wrong during the registration process
   */
  public void registerRuleExecutionSet(String uri, RuleExecutionSet ruleExecutionSet, Map properties) throws RuleExecutionSetRegisterException {
    if(!(ruleExecutionSet instanceof RuleExecutionSetImpl)) {
      throw new IllegalClassException("Only RuleExecutionSets created by this RuleAdministrator can be registered...");
    }
    RuleExecutionSetImpl ruleExecutionSetImpl = (RuleExecutionSetImpl) ruleExecutionSet;

    try {
      RuleStore ruleStore = serviceProvider.getRuleRuntime().getRuleServiceProvider().getRuleStore();
      List<String> ruleUris = new ArrayList<String>();
      for(RuleDescriptor ruleDescriptor : ruleExecutionSetImpl.getRuleDescriptors()) {
        ruleUris.add(ruleDescriptor.generateRule(ruleStore));
      }
      ruleStore.storeRuleSet(this.createStoredRuleExecutionSet(uri == null ? ruleExecutionSet.getName() : uri, ruleExecutionSetImpl, ruleUris, properties));
    } catch(ConfigurationException ex) {
      throw new RuleExecutionSetRegisterException("Error registering the RuleExecutionSet...", ex);
    }
  }

  /**
   * Removes a RuleExecutionSet from the RuleStore.
   *
   * @param uri the uri of the new RuleExecutionSet
   * @param map a map of properties to be used for the provider - currently has no effect
   * @throws javax.rules.admin.RuleExecutionSetDeregistrationException
   *                                  thrown if anything goes wrong during the deregistration process
   * @throws java.rmi.RemoteException if there is an exception retrieving a remote resource
   */
  public void deregisterRuleExecutionSet(String uri, Map map) throws RuleExecutionSetDeregistrationException, RemoteException {
    try {
      RuleStore ruleStore = serviceProvider.getRuleRuntime().getRuleServiceProvider().getRuleStore();
      ruleStore.removeRuleSet(uri);
    } catch(ConfigurationException ex) {
      throw new RuleExecutionSetDeregistrationException("Error registering the RuleExecutionSet...", ex);
    }
  }

  /**
   * This method is meant to create a StoredRuleExecutionSet object from the
   * information provided and gathered during the registration process.  This
   * StoredRuleExecutionSet is what can be passed on to the RuleStore.
   *
   * @param uri                  the uri of the new RuleExecutionSet
   * @param ruleExecutionSetImpl the RuleExecutionSet that is to be used as a template
   * @param ruleUris             A list of the Rule uris that are attached to this Rule - these rules MUST be in
   *                             the RuleStore prior to this StoredRuleExecutionSet
   * @param properties           a map of properties to be used for the provider - currently has no effect
   * @return the StoredRuleExecutionSet that can be loaded to the RuleStore
   */
  protected StoredRuleExecutionSet createStoredRuleExecutionSet(String uri, RuleExecutionSetImpl ruleExecutionSetImpl, List<String> ruleUris, Map<?, ?> properties) {
    Map<Object, Object> localProperties = new HashMap<Object, Object>(ruleExecutionSetImpl.getProperties());
    localProperties.putAll(properties);
    return new StoredRuleExecutionSetImpl(uri,
            ruleExecutionSetImpl.getName(),
            ruleExecutionSetImpl.getDescription(),
            ruleUris,
            localProperties,
            ruleExecutionSetImpl.getDefaultObjectFilter());
  }

}

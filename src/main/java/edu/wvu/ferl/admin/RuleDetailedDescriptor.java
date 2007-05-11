/*
 * RuleDetailedDescriptor.java
 *
 * Created on May 7, 2007, 11:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.wvu.ferl.admin;

import edu.wvu.ferl.spi.RuleStore;
import edu.wvu.ferl.spi.StoredRule;
import edu.wvu.ferl.spi.StoredRuleExecutionSet;
import java.util.HashMap;
import java.util.Map;
import javax.rules.ConfigurationException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jbunting
 */
class RuleDetailedDescriptor implements RuleDescriptor {
  
  private String uri;
  private String name;
  private String description;
  private String language;
  private String script;
  private Map properties = new HashMap();
  
  /** Creates a new instance of RuleDetailedDescriptor */
  public RuleDetailedDescriptor(String uri) {
    this.setUri(uri);
  }

  public void generateRule(StoredRuleExecutionSet set, RuleStore ruleStore) throws ConfigurationException {
    StoredRule storedRule = new StoredRule();
    
    this.checkForNull(getUri(), "uri");
    this.checkForNull(getName(), "name");
    this.checkForNull(getDescription(), "description");
    this.checkForNull(getLanguage(), "language");
    this.checkForNull(getScript(), "script");
    
    storedRule.setUri(getUri());
    storedRule.setName(getName());
    storedRule.setDescription(getDescription());
    storedRule.setLanguage(getLanguage());
    storedRule.setScript(getScript());
  }

  public String getUri() {
    return uri;
  }

  private void setUri(String uri) {
    this.uri = uri;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  private void checkForNull(String input, String field) throws ConfigurationException {
    if(StringUtils.isBlank(input)) {
      throw new ConfigurationException(field + " cannot be empty...");
    }
  }

  public void generateRule(StoredRuleExecutionSet set) throws ConfigurationException {
  }

  public Object getProperty(Object key) {
    return this.properties.get(key);
  }

  public void setProperty(Object key, Object value) {
    this.properties.put(key, value);
  }
}
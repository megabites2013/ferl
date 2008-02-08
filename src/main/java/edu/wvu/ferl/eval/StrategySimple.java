/*
 * StrategySimple.java
 *
 * Created on May 5, 2007, 7:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.wvu.ferl.eval;

import edu.wvu.ferl.store.StoredRule;
import javax.rules.InvalidRuleSessionException;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A {@link Strategy} used for scripting engines that do not support compilation.  This {@code Strategy} will retrieve
 * a raw script from the {@link StoredRule} and invoke it.
 * @author jbunting
 */
class StrategySimple implements Strategy {
  
  /**
   * Creates a new instance.
   */
  public StrategySimple() {
  }

  /**
   * Evaluates the raw script using the script engine retrieved from the engine manager.
   * @param rule {@inheritDoc}
   * @param context {@inheritDoc}
   * @param engineManager {@inheritDoc}
   * @return the output of the rule invocation
   * @throws InvalidRuleSessionException {@inheritDoc}
   */
  public Object evaluateRule(StoredRule rule, ScriptContext context, ScriptEngineManager engineManager) throws InvalidRuleSessionException {
    ScriptEngine engine = engineManager.getEngineByName(rule.getLanguage());

    if(engine == null) {
      throw new InvalidRuleSessionException("ScriptEngine " + rule.getLanguage() + " specified by rule " + rule.getUri() + " is not available...");
    }
    try {
      return engine.eval(rule.getScript(), context);
    } catch (ScriptException ex) {
      throw new InvalidRuleSessionException("Error executing rule: " + rule.getUri(), ex);
    }
  }
  
}
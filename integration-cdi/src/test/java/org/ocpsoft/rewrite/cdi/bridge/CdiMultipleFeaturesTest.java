/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.cdi.bridge;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.cdi.bind.BindingBean;
import org.ocpsoft.rewrite.cdi.bind.ExpressionLanguageTestConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
/*
 * TODO for some reason only the first CDI test run functions. look in to this
 */
@RunWith(Arquillian.class)
public class CdiMultipleFeaturesTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest
               .getDeployment()
               .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml")
               .addClasses(BindingBean.class, ExpressionLanguageTestConfigurationProvider.class, MockBean.class,
                        RewriteLifecycleEventObserver.class, ServiceEnricherTestConfigProvider.class);
   }

   /*
    * RewriteProviderBridge
    */
   @Test
   public void testRewriteProviderBridgeAcceptsChanges() throws Exception
   {
      HttpAction action = get("/success");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   @Test
   public void testRewriteRedirect301() throws Exception
   {
      HttpAction action = get("/redirect-301");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/outbound-rewritten");
   }

   /*
    * CdiServiceEnricher
    */
   @Test
   public void testCdiServiceEnricherProvidesEnrichment() throws Exception
   {
      HttpAction action = get("/cdi/inject");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   /*
    * CdiExpressionLanguageProvider
    */
   @Test
   public void testParameterExpressionBinding() throws Exception
   {
      HttpAction action = get("/one/2");
      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/result/2/one");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   @Test
   public void testParameterRegexValidationIgnoresInvalidInput1() throws Exception
   {
      HttpAction action = get("/one/44");
      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/one/44");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

   @Test
   public void testParameterRegexValidationIgnoresInvalidInput2() throws Exception
   {
      HttpAction action = get("/one/two");
      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/one/two");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.prettyfaces.interaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RewriteEngineUrlMappingInteractionIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addClass(InteractionDynaViewBean.class)
               .addAsWebResource("interaction/interaction-page.xhtml", "page.xhtml")
               .addAsWebInfResource("interaction/interaction-pretty-config.xml", "pretty-config.xml");
   }

   /**
    * Accessing the page using the URL mapping
    */
   @Test
   public void testSimpleUrlMapping() throws Exception
   {
      HttpAction action = get("/page");
      assertThat(action.getCurrentURL()).endsWith("/page");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Accessing the page using a dynaview
    */
   @Test
   public void testDynaViewUrlMapping() throws Exception
   {
      HttpAction action = get("/dyna/page");
      assertThat(action.getCurrentURL()).endsWith("/dyna/page");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Rewrite rule forwards to the URL mapping
    */
   @Test
   public void testRewriteForwardsToUrlMapping() throws Exception
   {
      HttpAction action = get("/rewrite-forwards-to-page-mapping");
      assertThat(action.getCurrentURL()).endsWith("/rewrite-forwards-to-page-mapping");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Rewrite rule redirects to the URL mapping
    */
   @Test
   public void testRewriteRedirectsToUrlMapping() throws Exception
   {
      HttpAction action = get("/rewrite-redirects-to-page-mapping");
      assertThat(action.getCurrentURL()).endsWith("/page");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Rewrite rule forwards to the dynaview
    */
   @Test
   public void testRewriteForwardsToDynaviewMapping() throws Exception
   {
      HttpAction action = get("/rewrite-forwards-to-dynaview");
      assertThat(action.getCurrentURL()).endsWith("/rewrite-forwards-to-dynaview");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Rewrite rule redirects to the dynaview
    */
   @Test
   public void testRewriteRedirectsToDynaviewMapping() throws Exception
   {
      HttpAction action = get("/rewrite-redirects-to-dynaview");
      assertThat(action.getCurrentURL()).endsWith("/dyna/page");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

   /**
    * Directly accessing the view-id should redirect to the pretty URL
    */
   @Test
   public void testJsfViewIdRedirectsToMapping() throws Exception
   {
      HttpAction action = get("/page.jsf");
      assertThat(action.getCurrentURL()).endsWith("/page");
      assertThat(action.getResponseContent()).contains("The page rendered fine!");
   }

}

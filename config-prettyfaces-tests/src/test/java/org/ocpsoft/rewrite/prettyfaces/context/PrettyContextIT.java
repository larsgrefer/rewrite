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
package org.ocpsoft.rewrite.prettyfaces.context;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PrettyContextIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = PrettyFacesITBase.getDeployment()
               .addAsWebResource("context/index.xhtml", "index.xhtml")
               .addAsWebInfResource("context/pretty-config.xml", "pretty-config.xml");

      return deployment;
   }

   @Test
   public void testIsPrettyRequest() throws Exception
   {
      HttpAction action = get("/");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
   }

   @Test
   public void testIsNotPrettyRequest() throws Exception
   {
      HttpAction action = get("/index.jsf");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/index.jsf");
      assertThat(action.getResponseContent()).contains("prettyRequest=false");
   }

   @Test
   public void testJSessionIdRemovedAutomatically() throws Exception
   {
      HttpAction action = get("/;jsessionid=F97KJHsf9876sdf?foo=bar");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/");
      assertThat(action.getResponseContent()).contains("query=?foo=bar");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
   }

   @Test
   public void testJSessionIdRemovedAutomaticallyCluster() throws Exception
   {
      HttpAction action = get("/;jsessionid=2437ae534134eeae.server1?foo=bar");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/");
      assertThat(action.getResponseContent()).contains("query=?foo=bar");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
   }

   @Test
   public void testJSessionIdRemovedAutomaticallyGAE() throws Exception
   {
      HttpAction action = get("/;jsessionid=1E-y6jzfx53ou9wymGmcfw?foo=bar");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("url=/");
      assertThat(action.getResponseContent()).contains("query=?foo=bar");
      assertThat(action.getResponseContent()).contains("prettyRequest=true");
   }

}
